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

    List<FieldName> fieldNames = new ArrayList<>();

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

    public List<FieldName> getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(List<FieldName> fieldNames) {
        this.fieldNames = fieldNames;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Section section = (Section) o;

        if (fieldNames != null ? !fieldNames.equals(section.fieldNames) : section.fieldNames != null) return false;
        if (name != section.name) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (fieldNames != null ? fieldNames.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Section{" +
            "name=" + name +
            ", fieldNames=" + fieldNames +
            '}';
    }
}
