package org.javaexpert.parser;

public class Token {

    private final Location location;
    private final TokenType type;

    public Token(Location location, TokenType type) {
        this.location = location;
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public TokenType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "(%s, )".formatted(type);
    }

    public enum TokenType {
        STR,
        RULE,
        SE,
        OU,
        E,
        ENTAO,
        COMMA,
        OPEN_PAR,
        CLOSE_PAR,
        OBJECTIVES,
        ATTR_NUMERIC,
        NUM,
        ATTRIBUTE,
        LOGIC_OPERATOR
    }
}
