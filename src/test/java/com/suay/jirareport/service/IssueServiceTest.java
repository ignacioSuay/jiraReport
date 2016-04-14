package com.suay.jirareport.service;

import com.suay.jirareport.domain.jira.Issue;
import com.suay.jirareport.domain.jira.JiraNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class IssueServiceTest {

    @Autowired
    IssueService issueService;

    @Test
    public void testJiraToIssueDTO() throws Exception {
        File file = new File("/home/suay/dev/jiraReportGenerator/src/test/resources/sprint7.xml");
        FileInputStream f = new FileInputStream(file);
        List<Issue> issueList = issueService.jiraToIssueDTO(f);
        assertNotNull(issueList);
        assertTrue(issueList.size() > 0);
    }

    @Test
    public void testGroupIssuesBy() throws Exception {
        List<Issue> issueList = getListIssues();
        Map<String, List<Issue>> stringListMap = issueService.groupIssuesBy(issueList, JiraNode.ASSIGNEE);
        assertNotNull(stringListMap);
    }

    private List<Issue> getListIssues() throws Exception {
        File file = new File("/home/suay/dev/jiraReportGenerator/src/test/resources/sprint7.xml");
        FileInputStream f = new FileInputStream(file);
        List<Issue> issueList = issueService.jiraToIssueDTO(f);
        return issueList;
    }

    @Test
    public void getDomainModel throws Exception{
        File file = new File("/home/suay/ignacioSuay/jiraReport/src/test/resources/last2weeks.xml");

    }
}
