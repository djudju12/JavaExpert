package org.javaexpert;

public record BooleanFact(
        String name,
        boolean value
) implements Fact {

    @Override
    public String getName() {
        return name();
    }

    @Override
    public boolean isTrue(Fact predicate) {
        if (predicate instanceof BooleanFact stringPredicate) {
            return stringPredicate.equals(this);
        }

        return false;
    }

    @Override
    public String toString() {
        return "%s: %s".formatted(name, value);
    }
}
