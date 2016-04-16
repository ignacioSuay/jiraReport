package com.suay.jirareport.domain.jira;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by natxo on 16/04/16.
 */
public class Section {

    String name;

    Map<String, Boolean> columns = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Boolean> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, Boolean> columns) {
        this.columns = columns;
    }
}
