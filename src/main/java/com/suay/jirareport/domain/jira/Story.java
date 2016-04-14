package com.suay.jirareport.domain.jira;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suay on 4/14/16.
 */
public class Story {

    Issue storyIssue;

    List<Issue> subTasks = new ArrayList<>();

    Issue epic;

    public Issue getStoryIssue() {
        return storyIssue;
    }

    public void setStoryIssue(Issue storyIssue) {
        this.storyIssue = storyIssue;
    }

    public List<Issue> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Issue> subTasks) {
        this.subTasks = subTasks;
    }

    public Issue getEpic() {
        return epic;
    }

    public void setEpic(Issue epic) {
        this.epic = epic;
    }


    public boolean isResolved(){
        return "Done".equals(storyIssue.getResolution());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Story)) return false;

        Story story = (Story) o;

        if (!storyIssue.equals(story.storyIssue)) return false;
        if (subTasks != null ? !subTasks.equals(story.subTasks) : story.subTasks != null) return false;
        return epic != null ? epic.equals(story.epic) : story.epic == null;

    }

    @Override
    public int hashCode() {
        int result = storyIssue.hashCode();
        result = 31 * result + (subTasks != null ? subTasks.hashCode() : 0);
        result = 31 * result + (epic != null ? epic.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Story{" +
            "storyIssue=" + storyIssue +
            ", subTasks=" + subTasks +
            ", epic=" + epic +
            '}';
    }


}
