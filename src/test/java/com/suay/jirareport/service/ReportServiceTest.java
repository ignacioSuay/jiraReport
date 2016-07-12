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

        ReportDTO reportDTO = new ReportDTO("Report Template", "Ignacio Suay");
        List<Section> sections = new ArrayList<>();
        Section epicSection = new Section(SectionName.EPIC_SUMMARY);
        epicSection.setColumns(Arrays.asList(FieldName.EPIC_LINK, FieldName.STATUS));
        epicSection.setGroupsBy(Arrays.asList(FieldName.TIME_ORIGINAL_ESTIMATE, FieldName.NUMBER_ISSUES));
        sections.add(epicSection);
        List<FieldName> fieldNames = Arrays.asList(FieldName.KEY, FieldName.TITLE, FieldName.ASSIGNEE, FieldName.TIME_SPENT);

        Section tasksPerEpic = new Section(SectionName.TASKS_PER_EPIC);
        tasksPerEpic.setColumns(fieldNames);
        sections.add(tasksPerEpic);

        Section tasksPerAssignee = new Section(SectionName.TASKS_BY_ASSIGNEE);
        tasksPerAssignee.setColumns(fieldNames);
        sections.add(tasksPerAssignee);

        Section allIssues = new Section(SectionName.ALL_ISSUES);
        allIssues.setColumns(fieldNames);
        sections.add(allIssues);

        reportDTO.setSections(sections);

        reportService.createWordDocument(f, reportDTO, "template.docx");
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
}
