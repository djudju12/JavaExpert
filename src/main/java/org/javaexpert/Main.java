package org.javaexpert;


import org.javaexpert.expert.Expert;
import org.javaexpert.expert.attribute.StringAttribute;
import org.javaexpert.ui.Quiz;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException {
//        var quiz = simpleExample();
        var quiz = qualidadeProdutoExample();

        quiz.createAndRunGUI();
    }

    private static Quiz qualidadeProdutoExample() throws IOException {
        var quiz = new Quiz();
        var expert = Expert.fromFile("example.ex");
        var attrs = expert.getAttributes();

        var questionsAttrs = new HashMap<Integer, String>();

        questionsAttrs.put(quiz.newQuestion(
            "Como foi o controle de qualidade?",
            ((StringAttribute) attrs.get("controle_qualidade")).values()
        ), "controle_qualidade");

        questionsAttrs.put(quiz.newQuestion(
            "Como está o acabamento do produto?",
            ((StringAttribute) attrs.get("acabamento")).values()
        ), "acabamento");

        questionsAttrs.put(quiz.newQuestion(
            "Qual a qualidade da materia prima?",
            ((StringAttribute) attrs.get("materia_prima")).values()
        ), "materia_prima");

        questionsAttrs.put(quiz.newQuestion(
            "Qual o nível do processo de fabricação?",
            ((StringAttribute) attrs.get("processo_fabricacao")).values()
        ), "processo_fabricacao");

        quiz.onFinished((answers) -> {
            answers.forEach((id, attrValue) -> {
                var attrName = questionsAttrs.get(id);
                expert.newFact(attrName, attrValue);

            });

            var rule = expert.think();
            System.out.println(rule);
        });


        return quiz;
    }

    private static Quiz simpleExample() {
        var quiz = new Quiz();

        quiz.newQuestion(
            "What are your favorite programming languages?",
            Set.of("Java", "Python", "JavaScript", "C++")
        );

        quiz.newQuestion(
            "Hello",
            Set.of("World", "Saylor")
        );

        quiz.newQuestion("idade?");

        quiz.onFinished(System.out::println);

        return quiz;
    }
}