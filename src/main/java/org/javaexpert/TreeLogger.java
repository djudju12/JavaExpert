package org.javaexpert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class TreeLogger {

    private static final TreeLogger INSTANCE = new TreeLogger();

    private final List<Node> roots = new ArrayList<>();
    private final List<String> lines = new ArrayList<>();

    protected TreeLogger() { }

    public static TreeLogger instance() {
        return INSTANCE;
    }

    public Node appendf(Node parent, String fmt, Object ...objects) {
        return append(parent, format(fmt, objects));
    }

    public Node append(Node parent, String content) {
        var node = new Node(content, parent);
        if (parent == null) {
            roots.add(node);
        } else {
            parent.children().add(node);
        }

        return node;
    }

    public void print(Node node, int depth, boolean lastChild, int maxSize) {
        var padIndex = node.content().lastIndexOf("~>");
        var line = new StringBuilder();
        if (depth != 0) {
            line.append("|");
            line.repeat(' ', depth * 2);
        }

        line.append(lastChild ? '`' : '|');
        line.append("--> ");

        if (padIndex > 0) {
            line.append(node.content(), 0, padIndex);
            line.repeat(' ', (maxSize - lengthOfContent(node, depth)));
            line.append(node.content(), padIndex + 2, node.content().length());
        } else {
            line.append(node.content());
        }

        lines.add(line.toString());

        for (int i = 0; i < node.children().size(); i++) {
            print(node.children().get(i), depth + 1, i == node.children().size() - 1, maxSize);
        }

    }

    public void print() {
        for (var root: roots) {
            lines.add(format("\n> %s", root.content()));
            var max = maxSize(root, 0, lengthOfContent(root, 0));
            for (int i = 0; i < root.children().size(); i++) {
                var isLast = i == root.children.size() - 1;
                print(root.children().get(i), 0, isLast, max);
                if (isLast) {
                    var lastLine = lines.getLast();
                    if (lastLine.startsWith("|")) {
                        lines.set(lines.size() - 1, lastLine.replaceFirst("\\|", ""));
                    }
                }
            }
        }

        System.out.println(String.join("\n", lines));
    }

    private int maxSize(Node node, int depth, int current) {
        current = Math.max(lengthOfContent(node, depth), current);
        for (var child: node.children()) {
            current = Math.max(maxSize(child, depth + 1, current), current);
        }

        return current;
    }

    private static int lengthOfContent(Node node, int depth) {
        var padIndex = node.content().lastIndexOf("~>");
        var implicitChars = depth == 0 ? 2 : depth*4;
        if (padIndex > 0) {
            implicitChars -= (node.content().length() - padIndex);
        }

        return node.content().length() + implicitChars;
    }

    public record Node(String content, Node parent, List<Node> children) {
        public Node(String content, Node parent) {
            this(content, parent, new ArrayList<>());
        }

        @Override
        public String toString() {
            return "Node: " + content;
        }
    }

}
