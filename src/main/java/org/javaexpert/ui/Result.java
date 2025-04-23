package org.javaexpert.ui;

import org.javaexpert.expert.Expert;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.Map;
import java.util.regex.Pattern;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Result {

    private final JFrame frame;
    private final String hist;
    private final String system;
    private final Map<String, Object> allFacts; // attrName -> attrValue
    private final Map<String, Object> objectives; // attrName -> attrValue

    static {
        StyleConstants.setForeground(addStyle("red"), Color.RED);
        StyleConstants.setForeground(addStyle("blue"), Color.BLUE);
    }

    private static final Map<Pattern, Style> keywords = Map.of(
            Pattern.compile("\\bATRIBUTO\\b"), getStyle("blue"),
            Pattern.compile("\\bOBJETIVOS\\b"), getStyle("blue"),
            Pattern.compile("\\bREGRA\\b"), getStyle("blue"),
            Pattern.compile("\\bSE\\b"), getStyle("red"),
            Pattern.compile("\\bOU\\b"), getStyle("red"),
            Pattern.compile("\\bE\\b"), getStyle("red"),
            Pattern.compile("\\bENTAO\\b"), getStyle("red")
    );

    private Runnable onNewFn;

    public Result(Expert expert) {
        this(expert.getFacts(), expert.getObjectivesConclusions(), expert.print(), expert.getSystem());
    }

    private Result(Map<String, Object> allFacts, Map<String, Object> objectives, String hist, String system) {
        this.allFacts = allFacts;
        this.objectives = objectives;
        this.hist = hist;
        this.system = system;
        this.frame = new JFrame("Resumo");
    }

    public void onNew(Runnable fn) {
        this.onNewFn = fn;
    }

    private void addComponentToPane(Container pane) {
        var tabbedPane = new JTabbedPane(SwingConstants.TOP);
        tabbedPane.addTab("Conclusões", makeMapPanel(parseMap(objectives)));
        tabbedPane.addTab("Resultados", makeMapPanel(parseMap(allFacts)));
        tabbedPane.addTab("Histórico", makeTextPanel(hist));
        tabbedPane.addTab("Sistema", makeSystemPanel(system));

        pane.add(makeMenuBar(), BorderLayout.NORTH);
        pane.add(tabbedPane, BorderLayout.CENTER);
    }

    private JMenuBar makeMenuBar() {
        var menuBar = new JMenuBar();
        var menu = new JMenu("Nova");
        menu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                frame.dispose();
                onNewFn.run();
            }

            @Override
            public void menuDeselected(MenuEvent e) { /* do nothing */ }

            @Override
            public void menuCanceled(MenuEvent e) { /* do nothing */ }
        });

        menuBar.add(menu);
        menuBar.setBorder(new MatteBorder(0, 0, 10, 0, menuBar.getBackground()));
        return menuBar;
    }

    protected JComponent makeMapPanel(Object[][] data) {
        var panel = new JPanel();
        panel.setLayout(new BorderLayout());

        var columns = new String[]{"Atributo", "Valor"};
        var table = new JTable(data, columns);
        var header = table.getTableHeader();

        header.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        table.setFillsViewportHeight(true);
        table.setBorder(new MatteBorder(0, 1, 1, 1, Color.GRAY));

        var scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private static Object[][] parseMap(Map<String, Object> map) {
        var data = new Object[map.size()][2];
        int i = 0;
        for (var entry : map.entrySet()) {
            data[i][0] = entry.getKey();
            data[i][1] = entry.getValue();
            i += 1;
        }
        return data;
    }

    private JComponent makeSystemPanel(String system) {
        var panel = new JPanel();
        panel.setLayout(new BorderLayout());

        var textPanel = new JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                // disable wrap lines
                return getParent() == null || getUI().getPreferredSize(this).width <= getParent().getSize().width;
            }
        };

        appendToPane(textPanel, system);
        configureTextComponent(textPanel, panel);

        textPanel.replaceSelection(system);
        keywords.forEach((pattern, style) -> highlight(textPanel, pattern, style));
        return panel;
    }

    private void appendToPane(JTextPane tp, String msg) {
        if (msg == null || msg.isBlank()) return;
        var sc = StyleContext.getDefaultStyleContext();
        var aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }

    public void highlight(JTextPane textComp, Pattern pattern, Style style) {
        var doc = textComp.getStyledDocument();
        var text = textComp.getText();

        var matcher = pattern.matcher(text);
        while (matcher.find()) {
            doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), style, false);
        }

    }

    private JComponent makeTextPanel(String text) {
        var panel = new JPanel();
        panel.setLayout(new BorderLayout());

        var textArea = new JTextArea(text);
        configureTextComponent(textArea, panel);

        return panel;
    }

    private void configureTextComponent(JTextComponent textComponent, JPanel mainPainel) {
        textComponent.setSize(300, 300);
        textComponent.setEditable(false);
        textComponent.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        var scrollPane = new JScrollPane(textComponent);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        mainPainel.add(scrollPane, BorderLayout.CENTER);
    }

    private static Style getStyle(String n) {
        return StyleContext.getDefaultStyleContext().getStyle(n);
    }

    private static Style addStyle(String n) {
        return StyleContext.getDefaultStyleContext().addStyle(n, null);
    }

    public void createAndShowGUI() {
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        addComponentToPane(frame.getContentPane());

        frame.setSize(500, 500);
        frame.setVisible(true);
    }

}
