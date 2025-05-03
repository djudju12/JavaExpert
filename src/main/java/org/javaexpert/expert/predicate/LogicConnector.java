package org.javaexpert.expert.predicate;

public enum LogicConnector {
    AND("E"), OR("OU");

    private final String format;

    LogicConnector(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return format;
    }
}