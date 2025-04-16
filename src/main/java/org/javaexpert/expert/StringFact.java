package org.javaexpert.expert;

public record StringFact(
        String name,
        String value
) implements Fact {

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String toString() {
        return "%s: %s".formatted(name, value);
    }
}
