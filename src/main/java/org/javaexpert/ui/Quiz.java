package org.javaexpert.ui;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import static javax.swing.SwingConstants.SOUTH;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Quiz {

    private final static int WIDTH = 720;
    private final static int HEIGHT = 400;
    private final static int TEXT_WIDTH = Quiz.WIDTH - 20*5 - 70;

    private JFrame frame;
    private final String title;

    private final JPanel cards = new JPanel(new CardLayout());
    private final List<Question> questions = new ArrayList<>();
    private final JButton backButton = new JButton("Anterior");
    private final JButton nextButton = new JButton("Próximo");
    private boolean inHome = false;

    private String firstQuestion;
    private final Map<String, Object> answers = new HashMap<>();
    private final List<String> hist = new ArrayList<>();
    private int current = 0;

    private BiFunction<String, Object, String> onQuestionAnswered;

    public Quiz(String title) {
        this.title = title;
    }

    public void setFirstQuestion(String question) {
        firstQuestion = question;
    }

    public void newNumericQuestion(String id, String text) {
        var question = new NumericQuestion(id, text);
        questions.add(question);
        cards.add(question, id);
    }

    public void newOptionQuestion(String id, String text, List<String> options) {
        var question = new OptionQuestion(id, text, options);
        questions.add(question);
        cards.add(question, id);
    }

    public void newMultiOptionQuestion(String id, String text, List<String> options) {
        var question = new MultiOptionQuestion(id, text, options);
        questions.add(question);
        cards.add(question, id);
    }

    public void newMultiOptionQuestion(String id, String text, List<String> options, String exclusiveOption) {
        var question = new MultiOptionQuestion(id, text, options, exclusiveOption);
        questions.add(question);
        cards.add(question, id);
    }

    public void withHome(String title, String text) {
        var main = new JPanel(new BorderLayout(20, 20));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 0));
        var questionLabel = new JLabel("<html><body style='width: %spx'>%s".formatted(TEXT_WIDTH, title));
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        var textPanel = new JTextPane();
        textPanel.setText(text);

        var scrollPane = new JScrollPane(textPanel);
        configureScrollable(scrollPane);
//        main.setBackground(bg);
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

        main.add(questionLabel, BorderLayout.NORTH);
        main.add(textPanel, BorderLayout.CENTER);
        cards.add(main, "home");
    }

    public void runGui() {
        runGui(false);
    }

    public void runGui(boolean fromHome) {
        inHome = fromHome;
        resetState();
        if (frame == null) {
            frame = createFrame();
        }

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JFrame createFrame() {
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setResizable(false);
        addComponentToPane(frame.getContentPane());
        frame.setSize(WIDTH, HEIGHT);
        return frame;
    }

    private void resetState() {
        answers.clear();
        hist.clear();
        if (firstQuestion == null) {
            firstQuestion = questions.getFirst().id;
        }

        hist.add(firstQuestion);
        current = 0;
        showCurrentCard();
        questions.forEach(Question::reset);
        updateButtons();
    }

    private void addComponentToPane(Container pane) {
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));
        buttonPanel.add(backButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(5, 1)));
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
        if (inHome) {
            inHome = false;
            current = 0;
        } else if (!inLastCard()) {
            current += 1;
            showCurrentCard();
        }
    }

    private void previousCard() {
        if (inHome) {
            backButton.setVisible(false);
        } else if (notInFirstCard()) {
            current -= 1;
            showCurrentCard();
        }
    }

    private void showCurrentCard() {
        if (inHome) {
            ((CardLayout)cards.getLayout()).show(cards, "home");
        } else {
            ((CardLayout)cards.getLayout()).show(cards, hist.get(current));
        }
    }

    private boolean inLastCard() {
        return current == hist.size() - 1;
    }

    private boolean notInFirstCard() {
        return current > 0;
    }

    private void updateButtons() {
        backButton.setEnabled(notInFirstCard());
        if (current < hist.size()) {
            nextButton.setEnabled(answers.get(hist.get(current)) != null);
        }
    }

    private Map<JCheckBox, String> createCheckBoxes(JScrollPane scrollPane, ItemListener listener, List<String> options) {
        var bg = new Color(245, 245, 245);
        var main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        var checkBoxes = new HashMap<JCheckBox, String>();

        options.forEach(opt -> {
            var cb = new JCheckBox(opt);
            cb.setText("<html><body style='width: %spx'>%s".formatted(TEXT_WIDTH - 50, opt));
            cb.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 0));
            cb.setBackground(bg);
            cb.setBorderPainted(true);
            checkBoxes.put(cb, opt);
            main.add(cb);
            cb.addItemListener(listener);
        });

        scrollPane.setViewportView(main);
        configureScrollable(scrollPane);
        main.setBackground(bg);
        main.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

        return checkBoxes;
    }

    private static void configureScrollable(JScrollPane scrollPane) {
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 20),
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true)
        ));
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
            field.setValue(0);
        }

        public void onValueChanged(PropertyChangeEvent evt) {
            var fieldValue = Optional.ofNullable((Number) field.getValue())
                    .map(Number::intValue)
                    .orElse(null);

            answers.put(this.id, fieldValue);
            updateButtons();
        }
    }

    class MultiOptionQuestion extends Question implements ItemListener {

        private final Set<String> selectedOptions = new HashSet<>();
        private final Map<JCheckBox, String> checkBoxes;
        private final String exclusiveOption;

        MultiOptionQuestion(String id, String text, List<String> options) {
            this(id, text, options, null);
        }

        MultiOptionQuestion(String id, String text, List<String> options, String exclusiveOption) {
            super(id, text);
            assert options.contains(exclusiveOption);
            this.exclusiveOption = exclusiveOption;
            var checkBoxPanel = new JScrollPane();
            checkBoxes = createCheckBoxes(checkBoxPanel, this, options);
            add(checkBoxPanel, BorderLayout.CENTER);
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            var cb = (JCheckBox) e.getItem();
            var select = checkBoxes.get(cb);
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                selectedOptions.remove(select);
                if (Objects.equals(select, exclusiveOption)) {
                    checkBoxes.keySet().forEach(otherCb -> otherCb.setEnabled(true));
                }
            } else {
                if (exclusiveOption != null && Objects.equals(select, exclusiveOption)) {
                    checkBoxes.keySet().forEach(otherCb -> {
                        var otherCbText = checkBoxes.get(otherCb);
                        if (!otherCbText.equals(select)) {
                            otherCb.setSelected(false);
                            otherCb.setEnabled(false);
                        }
                    });
                }

                selectedOptions.add(select);
            }

            answers.put(id, selectedOptions.isEmpty() ? null : selectedOptions);
            updateButtons();
        }

        @Override
        void reset() {
            checkBoxes.keySet().forEach(cb -> cb.setSelected(false));
        }
    }

    class OptionQuestion extends Question implements ItemListener {

        private final Map<JCheckBox, String> checkBoxes;

        OptionQuestion(String id, String text, List<String> options) {
            super(id, text);
            var scrollPane = new JScrollPane();
            checkBoxes = createCheckBoxes(scrollPane, this, options);
            add(scrollPane, BorderLayout.CENTER);
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                answers.put(this.id, null);
            } else {
                for (var cb: checkBoxes.keySet()) {
                    var selectedCb = e.getItemSelectable();
                    if (cb != selectedCb && cb.isSelected()) {
                        cb.setSelected(false);
                    }
                }

                var cb =  ((JCheckBox) e.getItem());
                var text = checkBoxes.get(cb);
                answers.put(this.id, text);
            }

            updateButtons();
        }

        @Override
        void reset() {
            checkBoxes.keySet().forEach(cb -> cb.setSelected(false));
        }
    }

    abstract static class Question extends JPanel {

        final String id;

        Question(String id, String text) {
            super(new BorderLayout(20, 20));
            this.id = id;
            setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 0));

            var questionLabel = new JLabel(
                    "<html><body style='width: %spx'>%s".formatted(TEXT_WIDTH, text)
            );

            questionLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            add(questionLabel, BorderLayout.NORTH);
        }

        abstract void reset();
    }
}
