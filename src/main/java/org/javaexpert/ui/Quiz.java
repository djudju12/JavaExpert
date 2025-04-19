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
import java.util.function.Consumer;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Quiz {

    private final JPanel cards = new JPanel(new CardLayout());
    private final List<Question> questions = new ArrayList<>();

    private final JButton backButton = new JButton("Anterior");
    private final JButton nextButton = new JButton("Próximo");

    protected Map<Integer, Object> answers = new HashMap<>();

    private JFrame frame;

    private int current = 0;
    private int counter = 0;

    private Consumer<Map<Integer, Object>> whenFinished;

    public Quiz() { }

    public void reset() {
        questions.forEach(Question::reset);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public int newQuestion(String text) {
        var id = counter++;
        var question = new NumericQuestion(id, text);
        questions.add(question);
        cards.add(question);
        return id;
    }

    public int newQuestion(String text, Set<String> options) {
        var id = counter++;
        var question = new OptionQuestion(id, text, options);
        questions.add(question);
        cards.add(question);
        return id;
    }

    public void onFinished(Consumer<Map<Integer, Object>> whenFinished) {
        this.whenFinished = whenFinished;
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
            nextCard();
            updateButtons();
        });

        pane.add(cards);
        pane.add(buttonPanel, BorderLayout.SOUTH);
        updateButtons();
    }

    private void nextCard() {
        if (!inLastCard()) {
            current++;
            ((CardLayout)cards.getLayout()).next(cards);
        } else {
            whenFinished.accept(answers);
            frame.setVisible(false);
        }
    }

    private void previousCard() {
        if (notInFirstCard()) {
            current--;
            ((CardLayout)cards.getLayout()).previous(cards);
        }
    }

    private boolean inLastCard() {
        return current == (cards.getComponentCount() - 1);
    }

    private boolean notInFirstCard() {
        return current != 0;
    }

    private void updateButtons() {
        backButton.setEnabled(notInFirstCard());

        if (inLastCard()) {
            nextButton.setText("Finalizar");
        } else {
            nextButton.setText("Próximo");
        }
    }

    class NumericQuestion extends Question {

        private final JFormattedTextField field;

        public NumericQuestion(int id, String text) {
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
        }
    }

    class OptionQuestion extends Question implements ItemListener {

        private final List<JCheckBox> checkBoxes = new ArrayList<>();

        OptionQuestion(int id, String text, Set<String> options) {
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
                return;
            }

            for (var cb: checkBoxes) {
                var selectedCb = e.getItemSelectable();
                if (cb != selectedCb && cb.isSelected()) {
                    cb.setSelected(false);
                }
            }

            answers.put(this.id, ((JCheckBox) e.getItem()).getText());
        }

        @Override
        void reset() {
            checkBoxes.forEach(cb -> cb.setSelected(false));
        }
    }

    abstract static class Question extends JPanel {

        protected final int id;

        Question(int id, String text) {
            super(new BorderLayout(20, 20));
            this.id = id;
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            var questionLabel = new JLabel(text);
            add(questionLabel, BorderLayout.NORTH);
        }

        abstract void reset();
    }

    public void createAndRunGUI() {
        frame = new JFrame("Quiz");
        answers.clear();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setResizable(false);
        addComponentToPane(frame.getContentPane());
        frame.pack();
        frame.setVisible(true);
    }
}
