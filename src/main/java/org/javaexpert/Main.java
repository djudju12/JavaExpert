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

        decisaoProblemaSaudeAutodelimitado_desmenorreia();


//        qualidadeProdutoExample();
//        presenteExample();
//        exemplAula();
    }

    private static void decisaoProblemaSaudeAutodelimitado_desmenorreia() throws IOException {
        var manager = new QuizManager(Expert.fromFile("dismenorreia.ex"));
        // TODO: criar variaveis univalodaras

        manager.addMultiOptionsQuestion("Quando as dores se iniciam?", "dor_inicio");

        // TODO: criar uma questão melhor
        // TODO: separar 'unilateral' em outra questão?
        manager.addMultiOptionsQuestion("Caracteristicas da dor?", "caracteristica_dor");
        manager.addMultiOptionsQuestion("Houve alteração do local da dor?", "alteracao_do_local");

        manager.addMultiOptionsQuestion("Qual é a intensidade da dor?", "intesidade_dor");

        manager.addMultiOptionsQuestion("Os sintomas estão associados à menstruação?", "dor_associada_menstruacao");

        // TODO: separar em 'possui outros sintomas?' e 'quais?'
        // TODO: podemos usar variaveis multivaloradas aqui
        manager.addMultiOptionsQuestion("Possui outros sintomas?", "outros_sintomas");

        manager.addMultiOptionsQuestion("Quando suas cólicas iniciaram?", "primeira_dismenorreia");

        // TODO: podemos usar variaveis multivaloradas aqui
        manager.addMultiOptionsQuestion("Possui alguma dessas condições?", "historico_clinico");

        // TODO: podemos usar variaveis multivaloradas aqui
        manager.addMultiOptionsQuestion("Já fez tratamentos prévios?", "historico_farmacoterapeutico");

        manager.runQuiz();
    }

    private static void exemplAula() throws IOException {
        var manager = new QuizManager(Expert.fromFile("examplo_aula.ex"));
        manager.addMultiOptionsQuestion("Você tem episódios de lapsos de memória?", "Lapsos");
        manager.addMultiOptionsQuestion("Quando você descreve suas lembranças, qual o nível de detalhamento?", "Lembranças");
        manager.addMultiOptionsQuestion("Como estão os movimentos do seu diafragma?", "Movimentos");
        manager.addMultiOptionsQuestion("Como está a sua respiração?", "Respiração");
        manager.addMultiOptionsQuestion("Como está a sua tosse?", "Tosse");
        manager.runQuiz();
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
            var result = new Result(expert);
            result.onNew(() -> {
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