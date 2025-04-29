package org.javaexpert;


import org.javaexpert.expert.Expert;
import org.javaexpert.ui.Quiz;
import org.javaexpert.ui.Result;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.IOException;
import java.util.Set;

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
        var manager = new QuizManager("Problema de Saúde Autodelimitado: Dismenorreia", Expert.fromFile("dismenorreia.ex"));

        manager.addOptionsQuestion("Quando as dores se iniciam?", "dor_inicio");

        // TODO: criar uma questão melhor
        // TODO: separar 'unilateral' em outra questão?
        manager.addOptionsQuestion("Caracteristicas da dor?", "caracteristica_dor");
        manager.addOptionsQuestion("Houve alteração do local da dor?", "alteracao_do_local");

        manager.addOptionsQuestion("Qual é a intensidade da dor?", "intesidade_dor");

        manager.addOptionsQuestion("Os sintomas estão associados à menstruação?", "dor_associada_menstruacao");

        // TODO: separar em 'possui outros sintomas?' e 'quais?'
        manager.addOptionsQuestion("Possui outros sintomas?", "possui_outros_sintomas");
        manager.addMultiOptionsQuestion("O paciente apresenta algum desses sintomas?", "outros_sintomas");

        manager.addOptionsQuestion("Quando suas cólicas iniciaram?", "primeira_dismenorreia");

        manager.addMultiOptionsQuestion("Possui alguma dessas condições?", "historico_clinico");
        manager.addMultiOptionsQuestion("Já fez tratamentos prévios?", "historico_farmacoterapeutico");

        manager.runQuiz();
    }

    private static void exemplAula() throws IOException {
        var manager = new QuizManager("Exemplo: Aula", Expert.fromFile("examplo_aula.ex"));
        manager.addOptionsQuestion("Você tem episódios de lapsos de memória?", "Lapsos");
        manager.addOptionsQuestion("Quando você descreve suas lembranças, qual o nível de detalhamento?", "Lembranças");
        manager.addOptionsQuestion("Como estão os movimentos do seu diafragma?", "Movimentos");
        manager.addOptionsQuestion("Como está a sua respiração?", "Respiração");
        manager.addOptionsQuestion("Como está a sua tosse?", "Tosse");
        manager.runQuiz();
    }

    private static void presenteExample() throws IOException {
        var manager = new QuizManager("Exemplo: Presente", Expert.fromFile("example_numerico.ex"));
        manager.addNumericQuestion("Qual a idade do aniversariante?", "idade");
        manager.addOptionsQuestion("Qual é o sexo do aniversáriante?", "sexo");
        manager.runQuiz();
    }

    private static void qualidadeProdutoExample() throws IOException {
        var manager = new QuizManager("Exemplo: Qualidade de Produto", Expert.fromFile("example.ex"));
        manager.addOptionsQuestion("Como foi o controle de qualidade?", "controle_qualidade");
        manager.addOptionsQuestion("Como está o acabamento do produto?", "acabamento");
        manager.addOptionsQuestion("Qual a qualidade da materia prima?", "materia_prima");
        manager.addOptionsQuestion("Qual o nível do processo de fabricação?", "processo_fabricacao");

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

        private QuizManager(String title, Expert expert) {
            this.expert = expert;
            this.quiz = new Quiz(title);

            quiz.onQuestionAsnwered((id, answer) -> {
                if (answer == null) return id;
                if (answer instanceof Set<?> answers) {
                    expert.removeFact(id);
                    answers.forEach(a -> expert.newFact(id, a));
                } else {
                    expert.newFact(id, answer);
                }
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
            quiz.newNumericQuestion(attr, text);
        }

        public void addOptionsQuestion(String text, String attr) {
            expert.addAskable(attr);
            quiz.newOptionQuestion(attr, text, expert.getAttributesValues(attr));
        }

        public void addMultiOptionsQuestion(String text, String attr) {
            expert.addAskable(attr);
            quiz.newMultiOptionQuestion(attr, text, expert.getAttributesValues(attr));
        }

        private void runResult() {
            var result = new Result(expert);
            result.onNew(this::runQuiz);
            result.createAndShowGUI();
        }

        public void runQuiz() {
            expert.clearMemory();
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