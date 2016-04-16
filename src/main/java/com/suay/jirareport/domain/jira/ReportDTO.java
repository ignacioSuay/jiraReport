package com.suay.jirareport.domain.jira;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by natxo on 16/04/16.
 */
public class ReportDTO {

    String title;

    String authors;

    List<Section> sections = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }
}
