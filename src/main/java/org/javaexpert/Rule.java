package org.javaexpert;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Rule {
    private final String name;
    private final Set<Fact> conclusions = new HashSet<>();
    private Predicate predicate = null;
    private LogicConnector lastConnector = null;
    private Fact.FactBuilder builder;
    private boolean concluding = false;

    public Rule(String name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    public Set<Fact> getConclusions() {
        return conclusions;
    }

    public boolean isConclusive(Set<String> conclusiveAttributes) {
        return conclusions.stream().anyMatch(fact -> conclusiveAttributes.contains(fact.name()));
    }

    public boolean evaluate(Set<Rule> allRules, Map<String, String> facts) {
        var otherRules = allRules.stream().filter(other -> !other.equals(this)).collect(Collectors.toSet());
        return predicate.evaluate(otherRules, facts);
    }

    private void chainPredicate(SimplePredicate predicate, LogicConnector connector) {
        Objects.requireNonNull(this.predicate, "Cannot chain predicate with a null predicate");
        this.predicate = new CompoundPredicate(this.predicate, predicate, connector);
    }

    public Rule iff(String attribute) {
        builder = newBuilder(attribute);
        return this;
    }

    public Rule is(String value) {
        assertNotNull(this.builder, "'is' without attribute");

        if (lastConnector != null) {
            chainPredicate(new SimplePredicate(builder.value(value).build()), lastConnector);
            lastConnector = null;
        } else if (concluding) {
            this.conclusions.add(builder.value(value).build());
        } else {
            this.predicate = new SimplePredicate(builder.value(value).build());
        }

        builder = null;
        return this;
    }

    public Rule and(String attribute) {
        assertNull(this.builder, "must finish last predicate before starting a new one");

        if (!concluding) {
            this.lastConnector = LogicConnector.AND;
        }

        builder = newBuilder(attribute);
        return this;
    }

    public Rule or(String attribute) {
        assertNull(this.lastConnector, "cannot chain logic connectors");
        assertFalse(this.concluding, "cannot or conclusion. Use 'then' or 'and'");

        this.lastConnector = LogicConnector.OR;
        builder = newBuilder(attribute);
        return this;
    }

    public Rule then(String attribute) {
        assertNull(this.builder, "must finish predicate before conclusion start");

        concluding = true;
        builder = newBuilder(attribute);
        return this;
    }

    private static Fact.FactBuilder newBuilder(String attr) {
        return Fact.builder().attribute(attr);
    }

    private static void assertNull(Object o, String msg) {
        if (o != null) {
            throw new IllegalStateException(msg);
        }
    }

    private static void assertNotNull(Object o, String msg) {
        if (o == null) {
            throw new IllegalStateException(msg);
        }
    }

    private static void assertFalse(boolean b, String msg) {
        if (b) {
            throw new IllegalStateException(msg);
        }
    }

    @Override
    public String toString() {
        return "Rule{" +
                "name='" + name + '\'' +
                ", predicate=" + predicate +
                ", conclusion=" + conclusions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(name, rule.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
