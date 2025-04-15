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
    private final Map<String, Set<String>> attributes = new HashMap<>();
    private final Set<String> objectivesAttributes = new HashSet<>();

    public Expert(String name) {
        Objects.requireNonNull(name, "Name cannot be null");
        this.name = name;
    }

    public Optional<Rule> think(Map<String, String> facts) {
        var allRules = new HashSet<>(rules.values());
        for (var rule: allRules) {
            if (rule.isConclusive(objectivesAttributes) && rule.evaluate(allRules, facts)) {
                return Optional.of(rule);
            }
        }

        return Optional.empty();
    }

    public void addAttributes(String attributeName, String ...values) {
        attributes.computeIfAbsent(attributeName, _ -> new HashSet<>())
                .addAll(Arrays.asList(values));
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

}
