package org.javaexpert.lexer;

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

    public record Location (String filePath, int row, int col) {
        @Override
        public String toString() {
            return "%s:%d:%d".formatted(filePath, row, col);
        }
    }

    public enum TokenType {
        STR,
        RULE,
        SE,
        OU,
        E,
        ENTAO,
        EQUAL,
        COMMA,
        OPEN_PAR,
        CLOSE_PAR,
        OBJECTIVES,
        ATTRIBUTE
    }
}
