package com.suay.jirareport.service;

import com.suay.jirareport.domain.jira.*;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by suay on 2/2/16.
 */
@Component
public class IssueService {

    public List<Issue> jiraToIssueDTO(InputStream inputStream) throws IOException, SAXException {
        List<Issue> issues = new ArrayList<Issue>();
        InputSource inputSource = new InputSource(inputStream);
        DOMParser parser = new DOMParser();
        parser.parse(inputSource);
        Document xmlDoc = parser.getDocument();

        final Element root= xmlDoc.getDocumentElement();
        NodeList items = root.getElementsByTagName(FieldName.ITEM.getJiraName());

        for (int i = 0; i < items.getLength(); i++){
            Element item = (Element)items.item(i);
            issues.add(XmlElementToIssue(item));
        }
        return issues;
    }

    private Issue XmlElementToIssue(Element item){
        Issue issue = new Issue();
        issue.setTitle(getNodeValue(item, FieldName.TITLE.getJiraName()).trim());
        issue.setLink(getNodeValue(item, FieldName.LINK.getJiraName()).trim());
        issue.setProject(getNodeValue(item, FieldName.PROJECT.getJiraName()).trim());
        issue.setSummary(getNodeValue(item, FieldName.SUMMARY.getJiraName()).trim());
        issue.setKey(getNodeValue(item, FieldName.KEY.getJiraName()).trim());
        issue.setType(getNodeValue(item, FieldName.TYPE.getJiraName()).trim());
        issue.setPriority(getNodeValue(item, FieldName.PRIORITY.getJiraName()).trim());
        issue.setStatus(getNodeValue(item, FieldName.STATUS.getJiraName()).trim());
        issue.setResolution(getNodeValue(item, FieldName.RESOLUTION.getJiraName()).trim());
        issue.setAssignee(getNodeValue(item, FieldName.ASSIGNEE.getJiraName()).trim());
        issue.setReporter(getNodeValue(item, FieldName.REPORTER.getJiraName()).trim());
        issue.setTimeEstimate(getNodeValue(item, FieldName.TIME_ESTIMATE.getJiraName()).trim());
        issue.setTimeOriginalEstimate(getNodeValue(item, FieldName.TIME_ORIGINAL_ESTIMATE.getJiraName()).trim());
        issue.setTimeSpent(getNodeValue(item, FieldName.TIME_SPENT.getJiraName()).trim());
        issue.setParent(getNodeValue(item, FieldName.PARENT.getJiraName()).trim());
        String oriEstimateInSec = getAttributeValue(item, FieldName.TIME_ORIGINAL_ESTIMATE.getJiraName(), FieldName.SECONDS.getJiraName());
        if(!oriEstimateInSec.isEmpty()) issue.setTimeOriginalEstimateInSeconds(Integer.parseInt(oriEstimateInSec));
        String estimateInSec = getAttributeValue(item, FieldName.TIME_ESTIMATE.getJiraName(), FieldName.SECONDS.getJiraName());
        if(!estimateInSec.isEmpty()) issue.setTimeEstimateInSeconds(Integer.parseInt(estimateInSec));
        String spentInSec = getAttributeValue(item, FieldName.TIME_SPENT.getJiraName(), FieldName.SECONDS.getJiraName());
        if(!spentInSec.isEmpty()) issue.setTimeSpentInSeconds(Integer.parseInt(spentInSec));

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ");
            issue.setCreated(formatter.parse(getNodeValue(item, FieldName.CREATED.getJiraName())));
            issue.setUpdated(formatter.parse(getNodeValue(item, FieldName.UPDATED.getJiraName())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        issue.setCustomFields(getCustomFields(item));
        return issue;
    }

    private List<CustomField> getCustomFields(Element item){
        NodeList customFieldsList = item.getElementsByTagName(FieldName.CUSTOM_FIELD.getJiraName());
        List<CustomField> customFields = new ArrayList<>();
        for (int i = 0; i < customFieldsList.getLength(); i++){
            Element cf = (Element)customFieldsList.item(i);
            CustomField customField = new CustomField();
            customField.setName(getNodeValue(cf, FieldName.CUSTOM_FIELD_NAME.getJiraName()));
            customField.setValue(getNodeValue(cf, FieldName.CUSTOM_FIELD_VALUE.getJiraName()));
            customFields.add(customField);
        }
        return customFields;
    }

    private String getNodeValue(Element record, String tagName){
        if(record.getElementsByTagName(tagName).getLength() >0)
            return record.getElementsByTagName(tagName).item(0).getFirstChild().getNodeValue();

        return "";
    }

    private String getAttributeValue(Element record, String tagName, String attribute){
        if(record.getElementsByTagName(tagName).getLength() >0)
            return record.getElementsByTagName(tagName).item(0).getAttributes().getNamedItem(attribute).getNodeValue();

        return "";
    }

    public Map<String, List<Issue>> groupIssuesBy(List<Issue>issues, FieldName fieldNameName){
        return issues.stream().collect(Collectors.groupingBy(i -> i.getValueByNode(fieldNameName)));
    }

    public Set<Issue> getEpics(List<Issue> issues){
        return issues.stream().filter(i -> "Epic".equals(i.getType())).collect(Collectors.toSet());
    }

    public Set<Issue> getStories(List<Issue> issues){
        return issues.stream().filter(i -> "Story".equals(i.getType())).collect(Collectors.toSet());
    }

    /**
     * structure the list of issues into Epics and Stories
     * @param issues
     * @return
     */
    public Set<Epic> getDataModel(List<Issue> issues){
        Set<Epic> epics = new HashSet<>();
        Set<Issue> allEpics = getEpics(issues);
        Map<String, List<Issue>> epicIssues = issues.stream()
            .filter(i -> !i.isEpic())
            .collect(Collectors.groupingBy(i -> i.getValueByNode(FieldName.EPIC_LINK)));

        for(String epicLink : epicIssues.keySet()){
            Optional<Issue> epicIssueOptional = allEpics.stream().filter(e -> e.getKey().equals(epicLink)).findAny();
            Issue epicIssue;
            Epic epic = new Epic();
            if(epicIssueOptional.isPresent()) {
                epicIssue = epicIssueOptional.get();
                epic.setEpicIssue(epicIssue);
            }else{
                Issue issue = new Issue();
                issue.setKey(epicLink);
                issue.setTitle(epicLink);
                epicIssue = issue;
                epic.setEpicIssue(issue);
            }
            List<Issue> issuesPerEpic = epicIssues.get(epicLink);
            epic.setSubIsues(issuesPerEpic);
            Set<Issue> stories = getStories(issuesPerEpic);
            for(Issue story : stories){
                Story newStory = new Story();
                newStory.setEpic(epicIssue);
                newStory.setStoryIssue(story);
                newStory.setSubTasks(issues.stream().filter(i -> "Sub-task".equals(i.getType()) && story.getKey().equals(i.getParent())).collect(Collectors.toList()));
                epic.getStories().add(newStory);
            }
            epics.add(epic);
        }

        addEpicsWithoutTasks(allEpics, epicIssues, epics);
        return epics;
    }

    private void addEpicsWithoutTasks(Set<Issue> allEpics, Map<String, List<Issue>> epicIssues, Set<Epic> epics) {
        Set<Issue> newEpics = allEpics.stream().filter(e -> !epicIssues.containsKey(e.getKey())).collect(Collectors.toSet());

        for (Issue newEpic : newEpics) {
            Epic epic = new Epic();
            epic.setEpicIssue(newEpic);
            epics.add(epic);
        }

    }
}
