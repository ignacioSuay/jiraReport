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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by suay on 2/2/16.
 */
@Component
public class IssueService {

    public List<Issue> jiraToIssueDTO(FileInputStream inputStream) throws IOException, SAXException {
        List<Issue> issues = new ArrayList<Issue>();
        InputSource inputSource = new InputSource(inputStream);
        DOMParser parser = new DOMParser();
        parser.parse(inputSource);
        Document xmlDoc = parser.getDocument();

        final Element root= xmlDoc.getDocumentElement();
        NodeList items = root.getElementsByTagName(JiraNode.ITEM.getName());

        for (int i = 0; i < items.getLength(); i++){
            Element item = (Element)items.item(i);
            issues.add(XmlElementToIssue(item));
        }
        return issues;
    }

    private Issue XmlElementToIssue(Element item){
        Issue issue = new Issue();
        issue.setTitle(getNodeValue(item, JiraNode.TITLE.getName()).trim());
        issue.setLink(getNodeValue(item, JiraNode.LINK.getName()).trim());
        issue.setProject(getNodeValue(item, JiraNode.PROJECT.getName()).trim());
        issue.setSummary(getNodeValue(item, JiraNode.SUMMARY.getName()).trim());
        issue.setKey(getNodeValue(item, JiraNode.KEY.getName()).trim());
        issue.setType(getNodeValue(item, JiraNode.TYPE.getName()).trim());
        issue.setPriority(getNodeValue(item, JiraNode.PRIORITY.getName()).trim());
        issue.setStatus(getNodeValue(item, JiraNode.STATUS.getName()).trim());
        issue.setResolution(getNodeValue(item, JiraNode.RESOLUTION.getName()).trim());
        issue.setAssignee(getNodeValue(item, JiraNode.ASSIGNEE.getName()).trim());
        issue.setReporter(getNodeValue(item, JiraNode.REPORTER.getName()).trim());
        issue.setTimeEstimate(getNodeValue(item, JiraNode.TIME_ESTIMATE.getName()).trim());
        issue.setTimeOriginalEstimate(getNodeValue(item, JiraNode.TIME_ORIGINAL_ESTIMATE.getName()).trim());
        issue.setParent(getNodeValue(item, JiraNode.PARENT.getName()).trim());
        String estimateInSec = getAttributeValue(item, JiraNode.TIME_ORIGINAL_ESTIMATE.getName(), JiraNode.SECONDS.getName());
        if(!estimateInSec.isEmpty()) issue.setTimeEstimateInSeconds(Integer.parseInt(estimateInSec));

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ");
            issue.setCreated(formatter.parse(getNodeValue(item, JiraNode.CREATED.getName())));
            issue.setUpdated(formatter.parse(getNodeValue(item, JiraNode.UPDATED.getName())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        issue.setCustomFields(getCustomFields(item));
        return issue;
    }

    private List<CustomField> getCustomFields(Element item){
        NodeList customFieldsList = item.getElementsByTagName(JiraNode.CUSTOM_FIELD.getName());
        List<CustomField> customFields = new ArrayList<>();
        for (int i = 0; i < customFieldsList.getLength(); i++){
            Element cf = (Element)customFieldsList.item(i);
            CustomField customField = new CustomField();
            customField.setName(getNodeValue(cf, JiraNode.CUSTOM_FIELD_NAME.getName()));
            customField.setValue(getNodeValue(cf, JiraNode.CUSTOM_FIELD_VALUE.getName()));
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

    public Map<String, List<Issue>> groupIssuesBy(List<Issue>issues, JiraNode jiraNodeName){
        return issues.stream().collect(Collectors.groupingBy(i -> i.getValueByNode(jiraNodeName)));
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
            .collect(Collectors.groupingBy(i -> i.getValueByNode(JiraNode.EPIC_LINK)));

        for(String epicLink : epicIssues.keySet()){
            Optional<Issue> epicIssue = allEpics.stream().filter(e -> e.getKey().equals(epicLink)).findAny();
            if(epicIssue.isPresent()){
                Epic epic = new Epic();
                epic.setEpicIssue(epicIssue.get());
                List<Issue> issuesPerEpic = epicIssues.get(epicLink);
                epic.setSubIsues(issuesPerEpic);
                Set<Issue> stories = getStories(issuesPerEpic);
                for(Issue story : stories){
                    Story newStory = new Story();
                    newStory.setEpic(epicIssue.get());
                    newStory.setStoryIssue(story);
                    newStory.setSubTasks(issuesPerEpic.stream().filter(i -> "Sub-task".equals(i.getType()) && story.getKey().equals(i.getParent())).collect(Collectors.toList()));
                    epic.getStories().add(newStory);
                }
                epics.add(epic);
            }
        }
        return epics;
    }
}
