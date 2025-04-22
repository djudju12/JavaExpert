package org.javaexpert;


import org.javaexpert.expert.Expert;
import org.javaexpert.ui.Quiz;
import org.javaexpert.ui.Result;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        setUIFont(new javax.swing.plaf.FontUIResource("Monospaced", Font.PLAIN, 14));

//        qualidadeProdutoExample();
        presenteExample();
    }

    private static void presenteExample() throws IOException {
        var manager = new QuizManager(Expert.fromFile("example_numerico.ex"));
        manager.addNumericQuestion("Qual a idade do aniversariante?", "idade");
        manager.addMultiOptionsQuestion("Qual é o sexo do aniversáriante?", "sexo");
        manager.runQuiz();
    }

    private static void qualidadeProdutoExample() throws IOException {
        var manager = new QuizManager(Expert.fromFile("example.ex"));
        manager.addMultiOptionsQuestion("Como foi o controle de qualidade?", "controle_qualidade");
        manager.addMultiOptionsQuestion("Como está o acabamento do produto?", "acabamento");
        manager.addMultiOptionsQuestion("Qual a qualidade da materia prima?", "materia_prima");
        manager.addMultiOptionsQuestion("Qual o nível do processo de fabricação?", "processo_fabricacao");

        manager.runQuiz();
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

    private static class QuizManager {
        private final Expert expert;
        private final Quiz quiz;

        private QuizManager(Expert expert) {
            this.expert = expert;
            this.quiz = new Quiz();

            quiz.onQuestionAsnwered((id, answer) -> {
                if (answer == null) return id;
                expert.newFact(id, answer);
                var questionOpt = expert.thinkIfNotConclusiveAskQuestion();
                if (questionOpt.isEmpty()) {
                    runResult();
                    return null;
                }

                return questionOpt.get();
            });

        }

        public void addNumericQuestion(String text, String attr) {
            expert.addAskable(attr);
            quiz.newQuestion(attr, text);
        }

        public void addMultiOptionsQuestion(String text, String attr) {
            expert.addAskable(attr);
            quiz.newQuestion(attr, text, expert.getAttributesValues(attr));
        }

        private void runResult() {
            var result = new Result(quiz, expert.getFacts(), expert.getObjectivesConclusions(), expert.print(), expert.getSystem());
            result.onNew(() -> {
                result.close();
                expert.clearMemory();
                runQuiz();
            });

            result.createAndShowGUI();
        }

        public void runQuiz() {
            var firstQuestionOpt = expert.thinkIfNotConclusiveAskQuestion();
            if (firstQuestionOpt.isPresent()) {
                quiz.setFirstQuestion(firstQuestionOpt.get());
                quiz.runGui();
            } else {
                runResult();
            }
        }
    }
}