package org.javaexpert.lexer;

public class TokenStr extends Token {
    private final String value;

    public TokenStr(Location location, String value) {
        super(location, TokenType.STR);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "(%s, %s)".formatted(getType(), value);
    }
}
