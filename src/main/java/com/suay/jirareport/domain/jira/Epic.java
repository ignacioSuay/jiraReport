package com.suay.jirareport.domain.jira;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suay on 4/14/16.
 */
public class Epic {

    Issue epicIssue;

    List<Issue> subIsues = new ArrayList();

    List<Story> stories = new ArrayList<>();

    public Issue getEpicIssue() {
        return epicIssue;
    }

    public void setEpicIssue(Issue epicIssue) {
        this.epicIssue = epicIssue;
    }

    public List<Issue> getSubIsues() {
        return subIsues;
    }

    public void setSubIsues(List<Issue> subIsues) {
        this.subIsues = subIsues;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;

        Epic epic = (Epic) o;

        if (!epicIssue.equals(epic.epicIssue)) return false;
        if (subIsues != null ? !subIsues.equals(epic.subIsues) : epic.subIsues != null) return false;
        return stories != null ? stories.equals(epic.stories) : epic.stories == null;

    }

    @Override
    public int hashCode() {
        int result = epicIssue.hashCode();
        result = 31 * result + (subIsues != null ? subIsues.hashCode() : 0);
        result = 31 * result + (stories != null ? stories.hashCode() : 0);
        return result;
    }
}
