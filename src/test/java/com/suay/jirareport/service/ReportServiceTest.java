package com.suay.jirareport.service;

import com.suay.jirareport.JiraReportApp;
import com.suay.jirareport.domain.jira.Issue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.FileInputStream;
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


    @Test
    public void testCreateWordDocument() throws Exception {
        List<Issue> issueList = getListIssues();
        reportService.createWordDocument(issueList, "templateWwarn.docx");
    }


    private List<Issue> getListIssues() throws Exception {
        File file = new File("/home/suay/ignacioSuay/jiraReport/src/test/resources/last2weeks.xml");
        FileInputStream f = new FileInputStream(file);
        List<Issue> issueList = issueService.jiraToIssueDTO(f);
        return issueList;
    }

}
