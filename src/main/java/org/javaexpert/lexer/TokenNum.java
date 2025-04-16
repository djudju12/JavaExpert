package org.javaexpert.lexer;

public class TokenNum extends Token {
    private final float value;

    public TokenNum(Location location, float value) {
        super(location, TokenType.NUM);
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "(%s, %f)".formatted(getType(), value);
    }
}
