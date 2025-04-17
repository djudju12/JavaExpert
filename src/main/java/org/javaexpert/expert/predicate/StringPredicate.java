package org.javaexpert.expert.predicate;

import org.javaexpert.TreeLogger;
import org.javaexpert.expert.fact.Fact;

public record StringPredicate(
        String name,
        String value,
        LogicOperator operator
) implements SimplePredicate {

    @Override
    public String toString() {
        return "%s: %s".formatted(name, value);
    }

    @Override
    public boolean validateFact(Fact<?> fact, TreeLogger tree, TreeLogger.Node parent) {
        var ret =  value().equals(fact.getValue());

        tree.appendf(
                parent,
                "'%s': '%s' %s '%s'? %s",
                name(), fact.getValue(), operator(), value(),
                ret ? "~>[VERDADEIRO]" : "~>[FALSO]"
        );
        return ret;
    }
}
