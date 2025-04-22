package org.javaexpert.ui;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Quiz {
    private JFrame frame;

    private final JPanel cards = new JPanel(new CardLayout());
    private final List<Question> questions = new ArrayList<>();
    private final JButton backButton = new JButton("Anterior");
    private final JButton nextButton = new JButton("Próximo");

    private String firstQuestion;
    private final Map<String, Object> answers = new HashMap<>();
    private final List<String> hist = new ArrayList<>();
    private int current = 0;

    private BiFunction<String, Object, String> onQuestionAnswered;

    public Quiz() { }

    public void setFirstQuestion(String question) {
        firstQuestion = question;
    }

    public void newQuestion(String id, String text) {
        var question = new NumericQuestion(id, text);
        questions.add(question);
        cards.add(question, id);
    }

    public void newQuestion(String id, String text, Set<String> options) {
        var question = new OptionQuestion(id, text, options);
        questions.add(question);
        cards.add(question, id);
    }

    public void runGui() {
        if (frame == null) {
            frame = createFrame();
        }

        resetState();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JFrame createFrame() {
        frame = new JFrame("Quiz");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setResizable(false);
        addComponentToPane(frame.getContentPane());
        frame.pack();
        return frame;
    }

    private void resetState() {
        questions.forEach(Question::reset);
        answers.clear();
        hist.clear();
        if (firstQuestion == null) {
            firstQuestion = questions.getFirst().id;
        }

        hist.add(firstQuestion);
        current = 0;
        showCurrentCard();
        updateButtons();
    }

    private void addComponentToPane(Container pane) {
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);

        backButton.addActionListener(_ -> {
            previousCard();
            updateButtons();
        });

        nextButton.addActionListener(_ -> {
            var question = hist.get(current);
            answerQuestion(question, answers.get(question));
            nextCard();
            updateButtons();
        });

        pane.add(cards);
        pane.add(buttonPanel, BorderLayout.SOUTH);
        updateButtons();
    }

    private void answerQuestion(String question, Object answer) {
        var nextQuestion = onQuestionAnswered.apply(question, answer);
        if (nextQuestion == null) {
            frame.setVisible(false);
        } else if (!hist.contains(nextQuestion)) {
            hist.add(nextQuestion);
        }
    }

    private void nextCard() {
        if (!inLastCard()) {
            current += 1;
            showCurrentCard();
        }
    }

    private void previousCard() {
        if (notInFirstCard()) {
            current -= 1;
            showCurrentCard();
        }
    }

    private void showCurrentCard() {
        ((CardLayout)cards.getLayout()).show(cards, hist.get(current));
    }

    private boolean inLastCard() {
        return current == hist.size() - 1;
    }

    private boolean notInFirstCard() {
        return current > 0;
    }

    private void updateButtons() {
        backButton.setEnabled(notInFirstCard());
        nextButton.setEnabled(answers.get(hist.get(current)) != null);
    }

    public void onQuestionAsnwered(BiFunction<String, Object, String> function) {
        this.onQuestionAnswered = function;
    }

    class NumericQuestion extends Question {

        private final JFormattedTextField field;

        public NumericQuestion(String id, String text) {
            super(id, text);
            var numPanel = new JPanel();
            numPanel.setLayout(new BoxLayout(numPanel, BoxLayout.Y_AXIS));

            var format = NumberFormat.getIntegerInstance();
            format.setGroupingUsed(false);

            var formatter = new NumberFormatter(format);
            formatter.setValueClass(Integer.class);
            formatter.setAllowsInvalid(false);
            formatter.setMinimum(0);
            formatter.setMaximum(Integer.MAX_VALUE);

            field = new JFormattedTextField(formatter);
            field.setColumns(10);
            field.setMaximumSize(field.getPreferredSize());
            field.addPropertyChangeListener("value", this::onValueChanged);
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK),
                    BorderFactory.createEmptyBorder(2, 4, 2, 4)
            ));

            numPanel.add(field);
            add(numPanel, BorderLayout.LINE_START);
        }

        @Override
        void reset() {
            field.setValue(null);
        }

        public void onValueChanged(PropertyChangeEvent evt) {
            var fieldValue = Optional.ofNullable((Number) field.getValue())
                    .map(Number::intValue)
                    .orElse(null);

            answers.put(this.id, fieldValue);
            updateButtons();
        }
    }

    class OptionQuestion extends Question implements ItemListener {

        private final List<JCheckBox> checkBoxes = new ArrayList<>();

        OptionQuestion(String id, String text, Set<String> options) {
            super(id, text);

            var checkBoxPanel = new JPanel();
            checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));

            options.forEach(opt -> {
                var cb = new JCheckBox(opt);
                checkBoxes.add(cb);
                checkBoxPanel.add(cb);
                cb.addItemListener(this);
            });

            add(checkBoxPanel, BorderLayout.CENTER);
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                answers.put(this.id, null);
            } else {
                for (var cb: checkBoxes) {
                    var selectedCb = e.getItemSelectable();
                    if (cb != selectedCb && cb.isSelected()) {
                        cb.setSelected(false);
                    }
                }

                answers.put(this.id, ((JCheckBox) e.getItem()).getText());
            }

            updateButtons();
        }

        @Override
        void reset() {
            checkBoxes.forEach(cb -> cb.setSelected(false));
        }
    }

    abstract static class Question extends JPanel {

        final String id;

        Question(String id, String text) {
            super(new BorderLayout(20, 20));
            this.id = id;
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            var questionLabel = new JLabel(text);
            add(questionLabel, BorderLayout.NORTH);
        }


        abstract void reset();
    }
}
