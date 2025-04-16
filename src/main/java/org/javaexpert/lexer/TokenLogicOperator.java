package org.javaexpert.lexer;

public class TokenLogicOperator extends Token {
    private final LogicOperator value;

    public TokenLogicOperator(Location location, LogicOperator value) {
        super(location, TokenType.LOGIC_OPERATOR);
        this.value = value;
    }

    public LogicOperator getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "(%s, %s)".formatted(getType(), value);
    }
}
