package com.suay.jirareport.service;

import com.suay.jirareport.domain.jira.*;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by suay on 1/13/16.
 */
@Component
public class ReportService {

    @Autowired
    IssueService issueService;

    Set<Issue> epics;

    Set<Issue> stories;


    public void createWordDocument(InputStream file, ReportDTO reportDTO, String template) throws IOException, SAXException {
        List<Issue> issues = issueService.jiraToIssueDTO(file);

        loadData(issues);
        Resource resource = new ClassPathResource(template);
        XWPFDocument doc = new XWPFDocument(resource.getInputStream());

        changeTitle(doc, reportDTO.getTitle());
        changeAuthors(doc, reportDTO.getAuthors());

        for(Section section: reportDTO.getSections()){
            if(SectionName.ALL_ISSUES == section.getName()){
                addSection(doc, "List of all issues");
                List<FieldName> fields = Arrays.asList(FieldName.TITLE, FieldName.ASSIGNEE, FieldName.CREATED, FieldName.SPRINT, FieldName.EPIC_LINK);
                createTableByFields(issues, fields, doc);
            }else if(SectionName.EPIC_SUMMARY == section.getName()){
                addSection(doc, "Epic Summary");
                createEpicSummaryTable(issues, doc, section);
            }else if(SectionName.TASKS_PER_EPIC == section.getName()){
                addSection(doc, "Tasks completed by Epic");
                createEpicTables(issues, doc);
            }else if(SectionName.TASKS_BY_ASSIGNEE == section.getName()){
                addSection(doc, "Tasks completed by Assignee");
                createAssigneeTable(issues, doc);
            }else if(SectionName.STORY_SUMMARY == section.getName()){
                addSection(doc, "Story Summary");
                createStorySummaryTable(issues, doc, section);
            }
        }
        FileOutputStream out = new FileOutputStream("simple.docx");
        doc.write(out);
        out.close();
    }



    public void createWordDocument(List<Issue> issues, String template) throws IOException {
        loadData(issues);
        Resource resource = new ClassPathResource(template);
        XWPFDocument doc = new XWPFDocument(resource.getInputStream());

        changeTitle(doc, "Sprint 8");

        addSection(doc, "Epic Summary");
        createSummaryTable(issues,doc);

        addSection(doc, "Tasks completed by Epic");
        createEpicTables(issues, doc);

        addSection(doc, "Tasks completed by Assignee");
        createAssigneeTable(issues, doc);

        addSection(doc, "List of all issues");
        List<FieldName> fields = Arrays.asList(FieldName.TITLE, FieldName.ASSIGNEE, FieldName.CREATED, FieldName.SPRINT, FieldName.EPIC_LINK);
        createTableByFields(issues, fields, doc);

        FileOutputStream out = new FileOutputStream("simple.docx");
        doc.write(out);
        out.close();

    }

    private void loadData(List<Issue> issues) {
        epics = issueService.getEpics(issues);
        stories = issueService.getStories(issues);
    }

    private void changeTitle(XWPFDocument doc, String title) {
        replaceText(doc, "templateTitle", title);
    }

    private void changeAuthors(XWPFDocument doc, String authors) {
        replaceText(doc, "templateAuthors", authors);
    }
    private void replaceText(XWPFDocument doc, String textToFind, String textToReplace) {
        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains(textToFind)) {
                        text = text.replace(textToFind, textToReplace);
                        r.setText(text, 0);
                    }
                }
            }
        }
    }



    public void createTableByFields(List<Issue> issues, List<FieldName> fields, XWPFDocument doc) throws IOException {

        XWPFTable table = doc.createTable(issues.size()+1, fields.size());
        table.setStyleID("LightShading-Accent1");
        table.getCTTbl().getTblPr().unsetTblBorders();

        for(int cols = 0; cols < fields.size(); cols++){
            XWPFParagraph p1 = table.getRow(0).getCell(cols).getParagraphs().get(0);
            XWPFRun r1 = p1.createRun();
            r1.setBold(true);
            r1.setText(fields.get(cols).getJiraName().toUpperCase());
            r1.setItalic(true);
        }

        for (int i = 0; i < issues.size(); i++){
            Issue issue = issues.get(i);
            int row = i + 1;
            for(int cols = 0; cols < fields.size(); cols++){
                table.getRow(row).getCell(cols).setText(issue.getValueByNode(fields.get(cols)));
            }
        }
    }


    public void createSummaryTable(List<Issue> issues, XWPFDocument doc){

        Map<String, Integer> collect = issues.stream().filter(i-> !i.isEpic() && !i.isStoryUnresolved()).collect(Collectors.groupingBy(i -> i.getValueByNode(FieldName.EPIC_LINK),
                Collectors.summingInt(Issue::getTimeOriginalEstimateInSeconds)));

        XWPFTable table = doc.createTable(collect.keySet().size()+1, 2);
        table.setStyleID("LightShading-Accent1");
        table.getCTTbl().getTblPr().unsetTblBorders();

        table.getRow(0).getCell(0).setText("Epic title");
        table.getRow(0).getCell(1).setText("Time");

        int row = 1;
        for(String key: collect.keySet()){
            String epicTitle = getEpicTitle(issues, key);
            table.getRow(row).getCell(0).setText(epicTitle);
            int timeInSeconds = collect.get(key);
            table.getRow(row).getCell(1).setText(secondsToDDHH(timeInSeconds));
            row++;
        }
    }

    public void createEpicSummaryTable(List<Issue> issues, XWPFDocument doc, Section section){

        Set<Epic> epics = issueService.getDataModel(issues);
        Map<String, Integer> epicByOriginalTimeEstimate = null;
        Map<String, Integer> epicByTimeEstimate = null;
        Map<String, Integer> epicByTimeSpent = null;

        if(section.getGroupsBy().contains(FieldName.TIME_ORIGINAL_ESTIMATE)) {
            ToIntFunction<Issue> orginalEstimateFunc = (issue) -> issue.getTimeOriginalEstimateInSeconds();
            epicByOriginalTimeEstimate = collectIssues(issues, FieldName.EPIC_LINK, orginalEstimateFunc);
        }

        if(section.getGroupsBy().contains(FieldName.TIME_ESTIMATE)) {
            ToIntFunction<Issue> timeEstimateFunc = (issue) -> issue.getTimeEstimateInSeconds();
            epicByTimeEstimate = collectIssues(issues, FieldName.EPIC_LINK, timeEstimateFunc);
        }

        if(section.getGroupsBy().contains(FieldName.TIME_SPENT)){
            ToIntFunction<Issue> timeSpentFunc = (issue) -> issue.getTimeSpentInSeconds();
            epicByTimeSpent = collectIssues(issues, FieldName.EPIC_LINK, timeSpentFunc);
        }

        XWPFTable table = doc.createTable(epics.size()+1, section.getTotalNumColumns());
        table.setStyleID("LightShading-Accent1");
        table.getCTTbl().getTblPr().unsetTblBorders();

        addColumnsToTable(table, section);

        int row = 1;
        for(Epic epic : epics) {
            int col = 0;
            if (epic.getEpicIssue() == null) {
                continue;
            }
            String epicKey = epic.getEpicIssue().getKey();
            for (FieldName column : section.getTotalColumns()) {
                if (column.equals(FieldName.EPIC_LINK)) {
                    String epicTitle = getEpicTitle(issues, epicKey);
                    table.getRow(row).getCell(col).setText(epicTitle);
                } else if (column.equals(FieldName.TIME_ORIGINAL_ESTIMATE)) {
                    if(epicByOriginalTimeEstimate.get(epicKey) != null)
                        table.getRow(row).getCell(col).setText(secondsToDDHH(epicByOriginalTimeEstimate.get(epicKey)));
                    else
                        table.getRow(row).getCell(col).setText("");
                } else if (column.equals(FieldName.TIME_ESTIMATE)) {
                    table.getRow(row).getCell(col).setText(secondsToDDHH(epicByTimeEstimate.get(epicKey)));
                } else if (column.equals(FieldName.TIME_SPENT)) {
                    table.getRow(row).getCell(col).setText(secondsToDDHH(epicByTimeSpent.get(epicKey)));
                } else if (column.equals(FieldName.NUMBER_ISSUES)) {
                    table.getRow(row).getCell(col).setText(Integer.toString(epic.getSubIsues().size()));
                }else{
                    String columnValue = epic.getEpicIssue().getValueByNode(column);
                    table.getRow(row).getCell(col).setText(columnValue);
                }
                col++;
            }
            row++;
        }
    }


    public void createStorySummaryTable(List<Issue> issues, XWPFDocument doc, Section section){
        Set<Epic> epics = issueService.getDataModel(issues);
        Set<Story> stories = new HashSet<>();
        epics.stream().forEach(e -> stories.addAll(e.getStories()));

        XWPFTable table = doc.createTable(stories.size()+1, section.getTotalNumColumns());
        table.setStyleID("LightShading-Accent1");
        table.getCTTbl().getTblPr().unsetTblBorders();

        addColumnsToTable(table, section);

        int row = 1;
        for(Story story : stories) {
            int col = 0;
            for (FieldName column : section.getColumns()) {
                String columnValue = story.getStoryIssue().getValueByNode(column);
                table.getRow(row).getCell(col).setText(columnValue);
                col++;
            }
            //groupby columns
            for (FieldName column : section.getGroupsBy()) {
                ToIntFunction<Issue> sumFunc = null;
                String columnValue = null;
                if (column.equals(FieldName.TIME_ORIGINAL_ESTIMATE)) {
                    sumFunc = (issue) -> issue.getTimeOriginalEstimateInSeconds();
                }else if(column.equals(FieldName.TIME_ESTIMATE)){
                    sumFunc = (issue) -> issue.getTimeEstimateInSeconds();
                }else if(column.equals(FieldName.TIME_SPENT)){
                    sumFunc = (issue) -> issue.getTimeSpentInSeconds();
                }else if (column.equals(FieldName.NUMBER_ISSUES)) {
                    columnValue = Integer.toString(story.getSubTasks().size());
                }

                if(column.equals(FieldName.TIME_ORIGINAL_ESTIMATE) || column.equals(FieldName.TIME_ESTIMATE) || column.equals(FieldName.TIME_SPENT)) {
                    int totalTime = story.getSubTasks().stream().collect(Collectors.summingInt(sumFunc));
                    columnValue = secondsToDDHH(totalTime);
                }
                table.getRow(row).getCell(col).setText(columnValue);
                col++;
            }
            row++;
        }
    }


    private Map<String, Integer> collectIssues(List<Issue> issues, FieldName fieldToGroup, ToIntFunction<Issue> sumFunction){
        return  issues.stream()
            .filter(i -> !i.isEpic() && !i.isStoryUnresolved())
            .collect(Collectors.groupingBy(i -> i.getValueByNode(fieldToGroup),
            Collectors.summingInt(sumFunction)));
    }

    private void addColumnsToTable(XWPFTable table, Section section){

        int i = 0;
        for(FieldName column: section.getTotalColumns()){
            table.getRow(0).getCell(i).setText(column.name());
            i++;
        }
    }

    public void createAssigneeTable(List<Issue> issues, XWPFDocument doc) {
        Map<String, List<Issue>> collect = issues.stream()
                .filter(i -> !i.isEpic() && !i.isStoryUnresolved())
                .collect(Collectors.groupingBy(i -> i.getValueByNode(FieldName.ASSIGNEE)));


        for(String assignee: collect.keySet()){
            addSubSection(doc, assignee + " tasks");
            XWPFTable table = doc.createTable(collect.get(assignee).size()+2, 3);
            table.getCTTbl().getTblPr().unsetTblBorders();
            table.setStyleID("LightShading-Accent1");

            table.getRow(0).getCell(0).setText("Epic");
            table.getRow(0).getCell(1).setText("Task");
            table.getRow(0).getCell(2).setText("Estimated Time");
            int row = 1;
            List<Issue> issuesPerAssignee = collect.get(assignee);
            for(Issue issue: issuesPerAssignee){
                String epicTitle = getEpicTitle(issues, issue.getValueByNode(FieldName.EPIC_LINK));
                table.getRow(row).getCell(0).setText(epicTitle);
                table.getRow(row).getCell(1).setText(issue.getTitleName());
                table.getRow(row).getCell(2).setText(issue.getTimeOriginalEstimate());
                row++;
            }
            table.getRow(row).getCell(0).setText("Total");
            table.getRow(row).getCell(1).setText("");
            Integer totalTime = issuesPerAssignee.stream().collect(Collectors.summingInt(Issue::getTimeOriginalEstimateInSeconds));
            table.getRow(row).getCell(2).setText(secondsToDDHH(totalTime));

        }
    }

    public void createEpicTables(List<Issue> issues, XWPFDocument doc){
        Map<String, List<Issue>> collect = issues.stream()
            .filter(i -> !i.isEpic() && !i.isStory())
            .collect(Collectors.groupingBy(i -> i.getValueByNode(FieldName.EPIC_LINK)));

        for(String epic: collect.keySet()){
            String epicTitle = getEpicTitle(issues, epic);

            addSubSection(doc, epicTitle + " tasks");
            XWPFTable table = doc.createTable(collect.get(epic).size()+2, 3);
            table.getCTTbl().getTblPr().unsetTblBorders();
            table.setStyleID("LightShading-Accent1");

            table.getRow(0).getCell(0).setText("Epic");
            table.getRow(0).getCell(1).setText("Task");
            table.getRow(0).getCell(2).setText("Estimated Time");
            int row = 1;
            List<Issue> issuesPerEpic = collect.get(epic);
            for(Issue issue: issuesPerEpic){
                table.getRow(row).getCell(0).setText(issue.getKey());
                table.getRow(row).getCell(1).setText(issue.getTitleName());
                table.getRow(row).getCell(2).setText(issue.getTimeOriginalEstimate());
                row++;
            }
            table.getRow(row).getCell(0).setText("Total");
            table.getRow(row).getCell(1).setText("");
            Integer totalTime = issuesPerEpic.stream().collect(Collectors.summingInt(Issue::getTimeOriginalEstimateInSeconds));
            table.getRow(row).getCell(2).setText(secondsToDDHH(totalTime));
        }
    }

    private String getEpicTitle(List<Issue> issues, String key) {
        if("not found".equals(key))
            return "unassigned";

        return epics.stream()
                .filter(i -> i.getKey().equals(key))
                .map(Issue::getTitleName)
                .findFirst()
                .orElse(key);
    }

    private String secondsToDDHH(int seconds){
        long totalHours = TimeUnit.SECONDS.toHours(seconds);
        int day = (int) (totalHours / 8);
        long hours = totalHours % 8;

        StringBuilder stringBuilder = new StringBuilder();
        if(day>0) stringBuilder.append(day + " days ");
        if(hours>0) stringBuilder.append(hours + " hours ");
        return stringBuilder.toString();

    }

    public void addSection(XWPFDocument doc, String title){
        XWPFParagraph p = doc.createParagraph();
        p.setStyle("Heading1");
        XWPFRun r1 = p.createRun();
        r1.setText(title);
        r1.addBreak();
    }

    public void addSubSection(XWPFDocument doc, String title){
        XWPFParagraph p = doc.createParagraph();
        p.setStyle("Heading2");
        XWPFRun r1 = p.createRun();
        r1.setText(title);
    }

}
