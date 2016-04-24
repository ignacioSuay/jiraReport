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


    @Test
    public void testCreateWordDocument() throws Exception {
        List<Issue> issueList = getListIssues();
        reportService.createWordDocument(issueList, "templateWwarn.docx");
    }


    private List<Issue> getListIssues() throws Exception {
        File file = utilTest.loadFileFromResources("last2weeks.xml");
        FileInputStream f = new FileInputStream(file);
        List<Issue> issueList = issueService.jiraToIssueDTO(f);

        return issueList;
    }

    @Test
    public void createWordDocument() throws Exception {
        File file = utilTest.loadFileFromResources("last2weeks.xml");
        FileInputStream f = new FileInputStream(file);

        ReportDTO reportDTO = new ReportDTO("Title test", "Ignacio Suay");
        List<Section> sections = new ArrayList<>();
        Section epicSection = new Section(SectionName.EPIC_SUMMARY);
        epicSection.setFieldNames(Arrays.asList(FieldName.EPIC_LINK, FieldName.TIME_ESTIMATE));
        sections.add(epicSection);
        sections.add(new Section(SectionName.TASKS_PER_EPIC));
        sections.add(new Section(SectionName.TASKS_BY_ASSIGNEE));
        sections.add(new Section(SectionName.ALL_ISSUES));
        reportDTO.setSections(sections);

        reportService.createWordDocument(f, reportDTO, "template.docx");
    }
}
