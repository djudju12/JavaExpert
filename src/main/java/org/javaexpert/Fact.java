package org.javaexpert;

public record Fact (
        String name,
        String value
) {

    public static FactBuilder builder() {
        return new FactBuilder();
    }

    public static class FactBuilder {
        private String attribute;
        private String value;

        public FactBuilder attribute(String attr) {
            this.attribute = attr;
            return this;
        }

        public FactBuilder value(String value) {
            this.value = value;
            return this;
        }

        public Fact build() {
            return new Fact(attribute, value);
        }
    }

}
