import org.javaexpert.expert.Expert;
import org.javaexpert.expert.fact.Fact;
import org.javaexpert.expert.fact.NumericFact;
import org.javaexpert.expert.fact.StringFact;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import static org.javaexpert.Asserts.assertTrue;

public class Runner {

    @SuppressWarnings("all")
    public static void main(String[] args) throws IOException {
        var testCases = Set.of(
                numerics(),
                strings(),
                qualidadeAceitavel()
        );

        var total = 0;
        var passed = 0;
        for (var test: testCases) {
            total += 1;
            try {
                setFacts(test);
                var ruleOpt = test.expert().think();
                assertTrue(ruleOpt.isPresent(), "expert could not conclude");
                var rule = ruleOpt.get();
                assertTrue(rule.name().equals(test.acceptedRule()), "expected '%s' found '%s'".formatted(test.acceptedRule(), rule.name()));
                test.expert().clearMemory();
                System.out.printf("[%d] %s: PASSED\n", total, test.name());
                passed += 1;
            } catch (Exception e) {
                System.out.printf("[%d] %s: FAILED\n", total, test.name());
                e.printStackTrace(System.out);
            }
        }

        System.out.printf("Tests summary: %d/%d\n", passed, total);
    }

    private static void setFacts(TestCase test) {
        for (var fact: test.facts()) {
            test.expert().newFact(fact.getName(), fact.getValue());
        }
    }

    private static TestCase numerics() throws IOException {
        var expert = Expert.fromFile("example_numerico.ex");

        return TestCase.builder()
                .expert(expert)
                .testName("Numeric values")
                .fact("idade", 66)
                .fact("sexo", "masculino")
                .ruleIs("PRESENTE_IDOSO_MASC")
                .build();
    }

    private static TestCase strings() throws IOException {
        var expert = Expert.fromFile("example.ex");

        return TestCase.builder()
                .expert(expert)
                .testName("String values")
                .fact("controle_qualidade","fraco")
                .fact("acabamento","defeituoso")
                .fact("materia_prima","baixa")
                .fact("processo_fabricacao","ruim")
                .ruleIs("R5")
                .build();
    }

    private static TestCase qualidadeAceitavel() throws IOException {
        var expert = Expert.fromFile("example.ex");

        return TestCase.builder()
                .expert(expert)
                .testName("String values")
                .fact("controle_qualidade","rigoroso")
                .fact("acabamento","excelente")
                .fact("materia_prima","alta")
                .fact("processo_fabricacao","otimo")
                .ruleIs("R6")
                .build();
    }

    private record TestCase(
            String name,
            Expert expert,
            Set<Fact<?>> facts,
            String acceptedRule
    ) {
        public static TestCaseBuilder builder() {
            return new TestCaseBuilder();
        }

        private static class TestCaseBuilder {
            private String testName;
            private Expert expert;
            private final Set<Fact<?>> facts = new TreeSet<>();
            private String acceptedRule;

            public TestCaseBuilder() { }

            public TestCaseBuilder testName(String name) {
                this.testName = name;
                return this;
            }

            public TestCaseBuilder expert(Expert expert) {
                this.expert = expert;
                return this;
            }

            public TestCaseBuilder fact(String name, String value) {
                facts.add(new StringFact(name, value));
                return this;
            }

            public TestCaseBuilder fact(String name, int value) {
                facts.add(new NumericFact(name, value));
                return this;
            }

            public TestCaseBuilder ruleIs(String rule) {
                acceptedRule = rule;
                return this;
            }

            public TestCase build() {
                return new TestCase(this.testName, this.expert, facts, acceptedRule);
            }
        }
    }
}
