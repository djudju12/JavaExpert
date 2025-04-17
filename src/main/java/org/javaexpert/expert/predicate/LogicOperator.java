package org.javaexpert.expert.predicate;

public enum LogicOperator {
    GT(">"), GTE(">="), LT("<"), LTE("<="), EQ("=");

    private final String format;

    LogicOperator(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return format;
    }
}
