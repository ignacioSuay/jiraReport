package com.suay.jirareport.service;

import com.suay.jirareport.domain.jira.*;
import com.suay.jirareport.web.rest.ReportResource;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * Created by suay on 1/13/16.
 */
@Service
public class ReportService {

    @Autowired
    IssueService issueService;

    Set<Issue> epics;

    Set<Issue> stories;


    public String createWordDocument(InputStream file, ReportDTO reportDTO, String uuid) throws IOException, SAXException {
        List<Issue> issues = loadData(file);
        XWPFDocument doc = createWordDocument();
        buildDocument(reportDTO, issues, doc);
        String outputFilename = writeOutputToFile(uuid, doc);
        return  outputFilename;
    }

    private List<Issue> loadData(InputStream file) throws IOException, SAXException {
        List<Issue> issues = issueService.jiraToIssueDTO(file);
        epics = issueService.getEpics(issues);
        stories = issueService.getStories(issues);
        return issues;
    }

    private XWPFDocument createWordDocument() throws IOException {
        //        Resource resource = new ClassPathResource(template);
        File fileTemplate = new File("/var/jiraReport/template.docx");
        InputStream inputStream = new BufferedInputStream(new FileInputStream(fileTemplate));
        return new XWPFDocument(inputStream);

    }

    private void buildDocument(ReportDTO reportDTO, List<Issue> issues, XWPFDocument doc){
        WordService.changeTitle(doc, reportDTO.getTitle());
        WordService.changeAuthors(doc, reportDTO.getAuthors());
        for(Section section: reportDTO.getSections()){
            if(SectionName.ALL_ISSUES == section.getName()){
                WordService.addSection(doc, "List of all issues");
                createTableByFields(issues, section, doc);
            }else if(SectionName.EPIC_SUMMARY == section.getName()){
                WordService.addSection(doc, "Epic Summary");
                createEpicSummaryTable(issues, doc, section);
            }else if(SectionName.ISSUES_EPIC == section.getName()){
                WordService.addSection(doc, "Tasks completed by Epic");
                createEpicTables(issues, doc, section);
            }else if(SectionName.ISSUES_ASSIGNEE == section.getName()){
                WordService.addSection(doc, "Tasks completed by Assignee");
                createAssigneeTable(issues, doc, section);
            }else if(SectionName.STORY_SUMMARY == section.getName()){
                WordService.addSection(doc, "Story Summary");
                createStorySummaryTable(issues, doc, section);
            }
        }
    }

    private String writeOutputToFile(String uuid, XWPFDocument doc ) throws IOException {
        String outputFilename = uuid + ".docx";
        String outputFile = ReportResource.REPORT_PATH + outputFilename;
        FileOutputStream out = new FileOutputStream(outputFile);
        doc.write(out);
        out.close();
        return outputFile;
    }



    public void createTableByFields(List<Issue> allIssues, Section section, XWPFDocument doc) {

        List<Issue> issues = filterIssuesByType(allIssues, section);
        XWPFTable table = doc.createTable(issues.size()+1, section.getTotalColumns().size());
        table.setStyleID("LightShading-Accent12");
        table.getCTTbl().getTblPr().unsetTblBorders();

        WordService.addColumnsToTable(table, section);

        for (int i = 0; i < issues.size(); i++){
            Issue issue = issues.get(i);
            int row = i + 1;
            for(int cols = 0; cols < section.getColumns().size(); cols++){
                table.getRow(row).getCell(cols).setText(issue.getValueByNode(section.getColumns().get(cols)));
            }
        }
    }


    public void createEpicSummaryTable(List<Issue> issues, XWPFDocument doc, Section section){

        Set<Epic> epics = issueService.getDataModel(issues);
        Map<String, Integer> epicByOriginalTimeEstimate = null;
        Map<String, Integer> epicByTimeEstimate = null;
        Map<String, Integer> epicByTimeSpent = null;

        if(epics.size() < 1){
            WordService.addParagraph(doc, "There are no epics in the submitted file.");
            return;
        }

        if(section.getGroupsBy().contains(FieldName.SUM_TIME_ORIGINAL_ESTIMATE)) {
            ToIntFunction<Issue> orginalEstimateFunc = (issue) -> issue.getTimeOriginalEstimateInSeconds();
            epicByOriginalTimeEstimate = collectIssues(issues, FieldName.EPIC_LINK, orginalEstimateFunc);
        }

        if(section.getGroupsBy().contains(FieldName.SUM_TIME_ESTIMATE)) {
            ToIntFunction<Issue> timeEstimateFunc = (issue) -> issue.getTimeEstimateInSeconds();
            epicByTimeEstimate = collectIssues(issues, FieldName.EPIC_LINK, timeEstimateFunc);
        }

        if(section.getGroupsBy().contains(FieldName.SUM_TIME_SPENT)){
            ToIntFunction<Issue> timeSpentFunc = (issue) -> issue.getTimeSpentInSeconds();
            epicByTimeSpent = collectIssues(issues, FieldName.EPIC_LINK, timeSpentFunc);
        }

        XWPFTable table = doc.createTable(epics.size()+1, section.getTotalNumColumns());
        table.setStyleID("LightShading-Accent12");
        table.getCTTbl().getTblPr().unsetTblBorders();

        WordService.addColumnsToTable(table, section);

        int row = 1;
        for(Epic epic : epics) {
            int col = 0;
            if (epic.getEpicIssue() == null) {
                continue;
            }
            String epicKey = epic.getEpicIssue().getKey();
            for (FieldName column : section.getColumns()) {
                if (column.equals(FieldName.EPIC_LINK)) {
                    String epicTitle = getEpicTitle(issues, epicKey);
                    table.getRow(row).getCell(col).setText(epicTitle);
                }else{
                    String columnValue = epic.getEpicIssue().getValueByNode(column);
                    table.getRow(row).getCell(col).setText(columnValue);
                }
                col++;
            }

            for (FieldName column : section.getGroupsBy()) {
                if (column.equals(FieldName.SUM_TIME_ORIGINAL_ESTIMATE)) {
                    if(epicByOriginalTimeEstimate.get(epicKey) != null)
                        table.getRow(row).getCell(col).setText(secondsToDDHH(epicByOriginalTimeEstimate.get(epicKey)));
                    else
                        table.getRow(row).getCell(col).setText("");
                } else if (column.equals(FieldName.SUM_TIME_ESTIMATE)) {
                    table.getRow(row).getCell(col).setText(secondsToDDHH(epicByTimeEstimate.get(epicKey)));
                } else if (column.equals(FieldName.SUM_TIME_SPENT)) {
                    table.getRow(row).getCell(col).setText(secondsToDDHH(epicByTimeSpent.get(epicKey)));
                } else if (column.equals(FieldName.NUMBER_ISSUES)) {
                    table.getRow(row).getCell(col).setText(Integer.toString(epic.getSubIsues().size()));
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

        if(stories.size() < 1){
            WordService.addParagraph(doc, "There are no stories in the submitted file.");
            return;
        }


        XWPFTable table = doc.createTable(stories.size()+1, section.getTotalNumColumns());
        table.setStyleID("LightShading-Accent12");
        table.getCTTbl().getTblPr().unsetTblBorders();

        WordService.addColumnsToTable(table, section);

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
                if (column.equals(FieldName.SUM_TIME_ORIGINAL_ESTIMATE)) {
                    sumFunc = (issue) -> issue.getTimeOriginalEstimateInSeconds();
                }else if(column.equals(FieldName.SUM_TIME_ESTIMATE)){
                    sumFunc = (issue) -> issue.getTimeEstimateInSeconds();
                }else if(column.equals(FieldName.SUM_TIME_SPENT)){
                    sumFunc = (issue) -> issue.getTimeSpentInSeconds();
                }else if (column.equals(FieldName.NUMBER_ISSUES)) {
                    columnValue = Integer.toString(story.getSubTasks().size());
                }

                if(column.equals(FieldName.SUM_TIME_ORIGINAL_ESTIMATE) || column.equals(FieldName.SUM_TIME_ESTIMATE) || column.equals(FieldName.SUM_TIME_SPENT)) {
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


    public void createAssigneeTable(List<Issue> allIssues, XWPFDocument doc, Section section) {
        List<Issue> issues = filterIssuesByType(allIssues, section);

        Map<String, List<Issue>> collect = issues.stream()
                .collect(Collectors.groupingBy(i -> i.getValueByNode(FieldName.ASSIGNEE)));

        for(String assignee: collect.keySet()){
            WordService.addSubSection(doc, assignee + " tasks");
            XWPFTable table = doc.createTable(collect.get(assignee).size()+2, section.getTotalNumColumns());
            table.getCTTbl().getTblPr().unsetTblBorders();
            table.setStyleID("LightShading-Accent12");

            WordService.addColumnsToTable(table, section);

            int row = 1;
            List<Issue> issuesPerAssignee = collect.get(assignee);
            for(Issue issue: issuesPerAssignee){
                int col = 0;
                String columnValue;
                for (FieldName column : section.getTotalColumns()) {
                    if(column.equals(FieldName.EPIC_LINK)){
                        columnValue = getEpicTitle(issues, issue.getValueByNode(FieldName.EPIC_LINK));
                    }else {
                        columnValue = issue.getValueByNode(column);
                    }
                    table.getRow(row).getCell(col).setText(columnValue);
                    col++;
                }
                row++;
            }
        }
    }

    public void createEpicTables(List<Issue> allIssues, XWPFDocument doc, Section section){
        List<Issue> issues = filterIssuesByType(allIssues, section);

        Map<String, List<Issue>> collect = issues.stream()
            .collect(Collectors.groupingBy(i -> i.getValueByNode(FieldName.EPIC_LINK)));

        for(String epic: collect.keySet()){
            String epicTitle = getEpicTitle(issues, epic);

            WordService.addSubSection(doc, epicTitle + " tasks");
            XWPFTable table = doc.createTable(collect.get(epic).size()+2, section.getTotalNumColumns());
            table.getCTTbl().getTblPr().unsetTblBorders();
            table.setStyleID("LightShading-Accent12");

            WordService.addColumnsToTable(table,section);

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


    private List<Issue> filterIssuesByType(List<Issue> issues, Section section){
        if(section.getInclude() == null)
            return issues;

        List<Issue> issueList = new ArrayList<>(issues);
        if(!section.getInclude().contains(IssueType.EPIC)){
            issueList = issueList.stream().filter(i -> !i.isEpic()).collect(Collectors.toList());
        }
        if(!section.getInclude().contains(IssueType.STORY)){
            issueList = issueList.stream().filter(i -> !i.isStory()).collect(Collectors.toList());
        }
        if(!section.getInclude().contains(IssueType.TASK)){
            issueList = issueList.stream().filter(i -> !i.isTask()).collect(Collectors.toList());
        }
        if(!section.getInclude().contains(IssueType.SUB_TASK)){
            issueList = issueList.stream().filter(i -> !i.isSubTask()).collect(Collectors.toList());
        }
        if(!section.getInclude().contains(IssueType.BUG)){
            issueList = issueList.stream().filter(i -> !i.isBug()).collect(Collectors.toList());
        }

        return issueList;
    }

}
