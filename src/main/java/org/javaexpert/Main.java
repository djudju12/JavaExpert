package org.javaexpert;


import org.javaexpert.expert.Expert;
import org.javaexpert.ui.Quiz;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
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
        var questionsAttrs = new HashMap<Integer, String>();

        questionsAttrs.put(quiz.newQuestion(
            "Como foi o controle de qualidade?",
            expert.getAttributesValues("controle_qualidade")
        ), "controle_qualidade");

        questionsAttrs.put(quiz.newQuestion(
            "Como está o acabamento do produto?",
            expert.getAttributesValues("acabamento")
        ), "acabamento");

        questionsAttrs.put(quiz.newQuestion(
            "Qual a qualidade da materia prima?",
            expert.getAttributesValues("materia_prima")
        ), "materia_prima");

        questionsAttrs.put(quiz.newQuestion(
            "Qual o nível do processo de fabricação?",
            expert.getAttributesValues("processo_fabricacao")
        ), "processo_fabricacao");

        quiz.onFinished((answers) -> {
            answers.forEach((id, attrValue) -> {
                var attrName = questionsAttrs.get(id);
                expert.newFact(attrName, attrValue);

            });

            var rule = expert.think();
            System.out.println(expert.print());
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

    public static void setUIFont(FontUIResource f){
        var keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            var key = keys.nextElement();
            var value = UIManager.get (key);
            if (value instanceof FontUIResource)
                UIManager.put(key, f);
        }
    }
}