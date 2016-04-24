package com.suay.jirareport.service;

import com.suay.jirareport.JiraReportApp;
import com.suay.jirareport.UtilTest;
import com.suay.jirareport.domain.jira.Epic;
import com.suay.jirareport.domain.jira.FieldName;
import com.suay.jirareport.domain.jira.Issue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNotNull;
import static org.assertj.core.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JiraReportApp.class)
@WebAppConfiguration
public class IssueServiceTest {

    @Autowired
    IssueService issueService;

    @Inject
    UtilTest utilTest;

    @Test
    public void testJiraToIssueDTO() throws Exception {
        File file = utilTest.loadFileFromResources("sprint7.xml");
        FileInputStream f = new FileInputStream(file);
        List<Issue> issueList = issueService.jiraToIssueDTO(f);
        assertNotNull(issueList);
        assertTrue(issueList.size() > 0);
    }

    @Test
    public void testGroupIssuesBy() throws Exception {
        List<Issue> issueList = getListIssues();
        Map<String, List<Issue>> stringListMap = issueService.groupIssuesBy(issueList, FieldName.ASSIGNEE);
        assertNotNull(stringListMap);
    }

    private List<Issue> getListIssues() throws Exception {
        File file = utilTest.loadFileFromResources("sprint7.xml");
        FileInputStream f = new FileInputStream(file);
        List<Issue> issueList = issueService.jiraToIssueDTO(f);
        return issueList;
    }

    @Test
    public void getDomainModel() throws Exception{
        File file = utilTest.loadFileFromResources("last2weeks.xml");
        FileInputStream f = new FileInputStream(file);
        List<Issue> issueList = issueService.jiraToIssueDTO(f);
        Set<Epic> dataModel = issueService.getDataModel(issueList);
        assertThat(dataModel.size()).isGreaterThan(0);
    }


}
