package org.javaexpert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Expert {
    private final String name;
    private final Map<String, Rule> rules = new HashMap<>();
    private final Map<String, Set<Object>> attributes = new HashMap<>();
    private final Set<String> objectivesAttributes = new HashSet<>();
    private final Map<String, Fact> facts = new HashMap<>();

    public Expert(String name) {
        Objects.requireNonNull(name, "Name cannot be null");
        this.name = name;
    }

    public Set<Fact> think() {
        var allRules = new HashSet<>(rules.values());
        for (var rule: allRules) {
            if (rule.isConclusive(objectivesAttributes) && rule.evaluate(allRules, facts)) {
                return rule.getConclusions();
            }
        }

        return Set.of();
    }

    public void addAttributes(String attributeName, String ...values) {
        attributes.computeIfAbsent(attributeName, _ -> new HashSet<>())
                .addAll(Arrays.asList(values));
    }

    public void addAttribute(String attributeName) {
        attributes.put(attributeName, Set.of(true, false));
    }

    public Rule withRule(String name) {
        if (name != null && rules.containsKey(name)) {
            throw new RuntimeException("Rule %s is already created".formatted(name));
        }

        var rule = new Rule(name);
        rules.put(name, rule);
        return rule;
    }

    public void addObjectives(String ...values) {
        objectivesAttributes.addAll(Arrays.asList(values));
    }

    public void addFact(String name, String value) {
        facts.put(name, new StringFact(name, value));
    }

    public void addFact(String name, boolean value) {
        facts.put(name, new BooleanFact(name, value));
    }
}
