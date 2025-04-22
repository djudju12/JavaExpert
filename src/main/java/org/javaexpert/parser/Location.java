package org.javaexpert.parser;

public record Location (String filePath, int row, int col) {
    @Override
    public String toString() {
        return "%s:%d:%d".formatted(filePath, row, col);
    }
}