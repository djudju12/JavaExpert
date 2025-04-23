package org.javaexpert.parser;

import org.javaexpert.expert.predicate.LogicConnector;
import org.javaexpert.expert.predicate.LogicOperator;

public record Token (Location location, TokenType type, Object value) {

    public String valueStr() {
        return (String) value;
    }

    public int valueInt() {
        return (int) value;
    }

    public LogicOperator valueLogicOp() {
        return (LogicOperator) value;
    }

    public LogicConnector valueLogicConn() {
        return (LogicConnector) value;
    }

    public boolean isAND() {
        return value.equals(LogicConnector.AND);
    }

    @Override
    public String toString() {
        return "(%s, )".formatted(type);
    }

    public enum TokenType {
        STR,
        RULE,
        SE,
        ENTAO,
        COMMA,
        OPEN_PAR,
        CLOSE_PAR,
        OBJECTIVES,
        ATTR_NUMERIC,
        NUM,
        ATTRIBUTE,
        LOGIC_CONNECTOR,
        LOGIC_OPERATOR
    }
}
