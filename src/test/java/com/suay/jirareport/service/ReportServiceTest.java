package com.suay.jirareport.service;

import com.suay.jirareport.JiraReportApp;
import com.suay.jirareport.UtilTest;
import com.suay.jirareport.domain.jira.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by suay on 1/15/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JiraReportApp.class)
@WebAppConfiguration
@IntegrationTest
public class ReportServiceTest {

    @Autowired
    IssueService issueService;

    @Autowired
    ReportService reportService;

    @Inject
    UtilTest utilTest;


    private List<Issue> getListIssues() throws Exception {
        File file = utilTest.loadFileFromResources("last2weeks.xml");
        FileInputStream f = new FileInputStream(file);
        List<Issue> issueList = issueService.jiraToIssueDTO(f);

        return issueList;
    }

    @Test
    public void createWordDocument() throws Exception {
        File file = utilTest.loadFileFromResources("sprint8.xml");
        FileInputStream f = new FileInputStream(file);
        ReportDTO reportDTO = createReportDTO();
        reportService.createWordDocument(f, reportDTO, "testSprint8");
    }

    @Test
    public void testNoEpicsWordDocument() throws Exception {
        File file = utilTest.loadFileFromResources("onlyTasks.xml");
        FileInputStream f = new FileInputStream(file);
        ReportDTO reportDTO = createReportDTO();
        reportService.createWordDocument(f, reportDTO, "onlyTasks");
    }

    @Test
    public void createWordDocumentSummaryStories() throws Exception {
        File file = utilTest.loadFileFromResources("last2weeks.xml");
        FileInputStream f = new FileInputStream(file);

        ReportDTO reportDTO = new ReportDTO("Story Summary", "Ignacio Suay");
        List<Section> sections = new ArrayList<>();
        Section storySection = new Section(SectionName.STORY_SUMMARY);
        storySection.setColumns(Arrays.asList(FieldName.TITLE, FieldName.STATUS, FieldName.TIME_ORIGINAL_ESTIMATE));
        storySection.setGroupsBy(Arrays.asList(FieldName.TIME_ORIGINAL_ESTIMATE, FieldName.NUMBER_ISSUES));
        sections.add(storySection);
        reportDTO.setSections(sections);

        reportService.createWordDocument(f, reportDTO, "template.docx");
    }

    private ReportDTO createReportDTO(){
        ReportDTO reportDTO = new ReportDTO("Report Template", "Ignacio Suay");
        List<Section> sections = new ArrayList<>();

        List<FieldName> fieldNames = Arrays.asList(FieldName.KEY, FieldName.TITLE, FieldName.ASSIGNEE, FieldName.TIME_SPENT);
        List<IssueType> allTypes= new ArrayList<>(Arrays.asList(IssueType.EPIC, IssueType.STORY, IssueType.BUG, IssueType.SUB_TASK, IssueType.TASK));

        Section epicSection = new Section(SectionName.EPIC_SUMMARY);
        epicSection.setColumns(Arrays.asList(FieldName.EPIC_LINK, FieldName.STATUS));
        epicSection.setGroupsBy(Arrays.asList(FieldName.SUM_TIME_ORIGINAL_ESTIMATE, FieldName.NUMBER_ISSUES));
        sections.add(epicSection);

        Section tasksPerEpic = new Section(SectionName.ISSUES_EPIC);
        tasksPerEpic.setColumns(fieldNames);
        tasksPerEpic.setInclude(allTypes);
        sections.add(tasksPerEpic);

        Section tasksPerAssignee = new Section(SectionName.ISSUES_ASSIGNEE);
        tasksPerAssignee.setColumns(fieldNames);
        tasksPerAssignee.setInclude(allTypes);
        sections.add(tasksPerAssignee);

        Section allIssues = new Section(SectionName.ALL_ISSUES);
        allIssues.setColumns(fieldNames);
        allIssues.setInclude(new ArrayList<>(Arrays.asList(IssueType.TASK)));
        sections.add(allIssues);

        reportDTO.setSections(sections);
        return reportDTO;

    }

}
