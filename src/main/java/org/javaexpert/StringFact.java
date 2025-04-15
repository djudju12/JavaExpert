package org.javaexpert;

public record StringFact(
        String name,
        String value
) implements Fact {

    @Override
    public String getName() {
        return name();
    }

    @Override
    public boolean isTrue(Fact predicate) {
        if (predicate instanceof StringFact stringPredicate) {
            return stringPredicate.equals(this);
        }

        return false;
    }

    @Override
    public String toString() {
        return "%s: %s".formatted(name, value);
    }
}
