package com.suay.jirareport.domain;

/**
 * Created by suay on 1/15/16.
 */
public class CustomField {
    String name;

    String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CustomField{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
