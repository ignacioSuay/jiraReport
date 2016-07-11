package com.suay.jirareport.domain.jira;

/**
 * Created by suay on 1/13/16.
 */
public enum FieldName {
    TITLE("title", "Title"),
    ITEM("item", "Item"),
    SUMMARY("summary", "Summary"),
    PROJECT("project", "Project"),
    LINK("link", "Link"),
    KEY("key", "Key"),
    TYPE("type", "Type"),
    PRIORITY("priority", "Priority"),
    STATUS("status", "Status"),
    RESOLUTION("resolution", "Resolution"),
    ASSIGNEE("assignee", "Assignee"),
    REPORTER("reporter", "Reporter"),
    CREATED("created", "Created"),
    UPDATED("updated", "Updated"),
    TIME_ORIGINAL_ESTIMATE("timeoriginalestimate", "Time original estimate"),
    TIME_ESTIMATE("timeestimate", "Time estimate"),
    TIME_SPENT("timespent", "Time spent"),
    CUSTOM_FIELDS("customfields", "Custom fields"),
    CUSTOM_FIELD("customfield", "Custom field"),
    CUSTOM_FIELD_NAME("customfieldname", "Custom field name"),
    CUSTOM_FIELD_VALUE("customfieldvalue", "Custom field value"),
    SPRINT("Sprint", "Sprint"),
    EPIC_LINK("Epic Link", "Epic Link"),
    SECONDS("seconds", "Seconds"),
    PARENT("parent", "Parent"),
    NUMBER_ISSUES("numberIssues", "Number issues");


    private String jiraName;

    private String columnName;

    FieldName(String jiraName, String columnName) {
        this.jiraName = jiraName;
        this.columnName = columnName;
    }

    public String getJiraName() {
        return jiraName;
    }

    public String getColumnName(){return columnName;}
}
