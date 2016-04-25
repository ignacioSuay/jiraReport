package com.suay.jirareport.domain.jira;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by natxo on 16/04/16.
 */
public class Section {

    SectionName name;

    List<FieldName> columns = new ArrayList<>();

    List<FieldName> groupsBy = new ArrayList<>();

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

    public List<FieldName> getColumns() {
        return columns;
    }

    public void setColumns(List<FieldName> columns) {
        this.columns = columns;
    }

    public List<FieldName> getGroupsBy() {
        return groupsBy;
    }

    public void setGroupsBy(List<FieldName> groupsBy) {
        this.groupsBy = groupsBy;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Section section = (Section) o;

        if (columns != null ? !columns.equals(section.columns) : section.columns != null) return false;
        if (name != section.name) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (columns != null ? columns.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Section{" +
            "name=" + name +
            ", columns=" + columns +
            '}';
    }
}
