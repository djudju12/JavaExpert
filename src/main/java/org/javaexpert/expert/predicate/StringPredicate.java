package org.javaexpert.expert.predicate;

import org.javaexpert.TreeLogger;
import org.javaexpert.expert.fact.Fact;

public record StringPredicate(
        String name,
        String value,
        LogicOperator operator
) implements SimplePredicate {

    private final static TreeLogger logger = TreeLogger.instance();

    @Override
    public String toString() {
        return "%s: %s".formatted(name, value);
    }

    @Override
    public boolean validateFact(Fact<?> fact, TreeLogger.Node parent) {
        var ret =  value().equals(fact.getValue());

        logger.appendf(parent, "'%s': '%s' %s '%s'? %s", name(), fact.getValue(), operator(), value(), ret ? "~>[VERDADEIRO]" : "~>[FALSO]");
        return ret;
    }
}
