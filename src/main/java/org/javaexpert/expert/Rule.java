package org.javaexpert.expert;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record Rule(String name, Predicate predicate, Set<Fact> conclusions) {
    public boolean isTrue(Set<Rule> rules, Map<String, Fact> facts) {
        var otherRules = rules.stream().filter(rule -> !rule.equals(this)).collect(Collectors.toSet());
        return predicate.isTrue(otherRules, facts);
    }
}
