package org.javaexpert.lexer;

public record Location (String filePath, int row, int col) {
    @Override
    public String toString() {
        return "%s:%d:%d".formatted(filePath, row, col);
    }
}