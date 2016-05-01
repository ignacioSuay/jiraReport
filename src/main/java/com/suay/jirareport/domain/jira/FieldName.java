package com.suay.jirareport.domain.jira;

/**
 * Created by suay on 1/13/16.
 */
public enum FieldName {
    TITLE("title"),
    ITEM("item"),
    SUMMARY("summary"),
    PROJECT("project"),
    LINK("link"),
    KEY("key"),
    TYPE("type"),
    PRIORITY("priority"),
    STATUS("status"),
    RESOLUTION("resolution"),
    ASSIGNEE("assignee"),
    REPORTER("reporter"),
    CREATED("created"),
    UPDATED("updated"),
    TIME_ORIGINAL_ESTIMATE("timeoriginalestimate"),
    TIME_ESTIMATE("timeestimate"),
    TIME_SPENT("timespent"),
    CUSTOM_FIELDS("customfields"),
    CUSTOM_FIELD("customfield"),
    CUSTOM_FIELD_NAME("customfieldname"),
    CUSTOM_FIELD_VALUE("customfieldvalue"),
    SPRINT("Sprint"),
    EPIC_LINK("Epic Link"),
    SECONDS("seconds"),
    PARENT("parent"),
    NUMBER_ISSUES("numberIssues");


    private String jiraName;

    FieldName(String jiraName) {
        this.jiraName = jiraName;
    }

    public String getJiraName() {
        return jiraName;
    }
}
