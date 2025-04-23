package org.javaexpert.parser;

public class UnexpectedTokenException extends RuntimeException {
    public UnexpectedTokenException(Token token) {
        super("%s: unexpected token %s".formatted(token.location(), token));
    }
}
