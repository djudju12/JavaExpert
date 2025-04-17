package org.javaexpert.lexer;

public class TokenNum extends Token {
    private final int value;

    public TokenNum(Location location, int value) {
        super(location, TokenType.NUM);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "(%s, %d)".formatted(getType(), value);
    }
}
