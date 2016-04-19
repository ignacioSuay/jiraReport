package com.suay.jirareport.domain.jira;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by natxo on 16/04/16.
 */
public class Section {

    SectionName name;

    Map<String, Boolean> columns = new HashMap<>();

    public Section(){}

    public Section(SectionName name) {
        this.name = name;
    }

    public SectionName getName() {
        return name;
    }

    public void setName(SectionName name) {
        this.name = name;
    }

    public Map<String, Boolean> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, Boolean> columns) {
        this.columns = columns;
    }
}
