package org.javaexpert.expert.predicate;

import org.javaexpert.TreeLogger;
import org.javaexpert.expert.Rule;
import org.javaexpert.expert.fact.Fact;

import java.util.Map;
import java.util.Set;

public sealed interface Predicate permits SimplePredicate, CompoundPredicate {

    boolean isTrue(Set<Rule> rules, Map<String, Fact<?>> facts, TreeLogger.Node parent);

}
