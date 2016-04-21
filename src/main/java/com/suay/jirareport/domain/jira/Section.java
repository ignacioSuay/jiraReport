package com.suay.jirareport.domain.jira;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by natxo on 16/04/16.
 */
public class Section {

    SectionName name;

    List<ColumnName> columns = new ArrayList<>();

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

    public List<ColumnName> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnName> columns) {
        this.columns = columns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;

        Section section = (Section) o;

        if (name != section.name) return false;
        return columns != null ? columns.equals(section.columns) : section.columns == null;

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
