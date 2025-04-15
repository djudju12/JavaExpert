package org.javaexpert;

public sealed interface Fact permits BooleanFact, StringFact {
    String getName();

    boolean isTrue(Fact predicate);

    static FactBuilder builder() {
        return new FactBuilder();
    }

    class FactBuilder {
        private String attribute;

        public FactBuilder attribute(String attr) {
            this.attribute = attr;
            return this;
        }

        public StringFact value(String value) {
            return new StringFact(this.attribute, value);
        }

        public BooleanFact value(boolean value) {
            return new BooleanFact(this.attribute, value);
        }
    }
}