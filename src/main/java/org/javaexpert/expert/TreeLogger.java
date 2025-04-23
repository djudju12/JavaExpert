package org.javaexpert.expert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

public class TreeLogger {

    private final List<Node> roots = new ArrayList<>();
    private final List<String> lines = new ArrayList<>();
    private int counter = 1;

    public Node appendf(Node parent, String fmt, Object ...objects) {
        return append(parent, format(fmt, objects));
    }

    public void clear() {
        roots.clear();
        lines.clear();
    }

    public Node append(Node parent, String content) {
        var node = new Node(content, parent, counter++);
        if (parent == null) {
            roots.add(node);
        } else {
            parent.children().add(node);
        }

        return node;
    }

    public String print() {
        var maxSize = roots
                .stream()
                .mapToInt(root -> maxSize(root, 0, lengthOfContent(root, 0)))
                .max()
                .orElse(0);

        for (var root: roots) {
            lines.add(format("> %s", root.content()));
            for (int i = 0; i < root.children().size(); i++) {
                var isLast = i == root.children.size() - 1;
                print(root.children().get(i), 0, isLast, maxSize);
                if (isLast) {
                    var lastLine = lines.getLast();
                    if (lastLine.startsWith("|")) {
                        lines.set(lines.size() - 1, lastLine.replaceFirst("\\|", ""));
                    }
                }
            }

            lines.add("");
        }

        return String.join("\n", lines);
    }
    
    private void print(Node node, int depth, boolean lastChild, int maxSize) {
        var padIndex = node.content().lastIndexOf("~>");
        var line = new StringBuilder();

        if (depth > 0) {
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

        var parent = node.parent();
        if (parent.children().size() > 1) {
            var i = parent.children().indexOf(node);
            if (i > 0 && depth > 0) {
                var siblingsChildCnt = parent.children().get(i - 1).children().size();
                for (int j = 1; j <= siblingsChildCnt; j++) {
                    var lineI = lines.size() - 1 - j;
                    var siblingLine = lines.get(lineI);
                    var newLine =
                            siblingLine.substring(0, depth * 2 + 1) +
                            (siblingLine.charAt(depth * 2 + 1) == ' ' ? "|" : siblingLine.charAt(depth * 2 + 1)) +
                            siblingLine.substring(depth * 2 + 2);
                    lines.set(lineI, newLine);
                }
            }
        }

        for (int i = 0; i < node.children().size(); i++) {
            print(node.children().get(i), depth + 1, i == node.children().size() - 1, maxSize);
        }
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
        var implicitChars = depth == 0 ? 5 : 6 + depth*2;
        if (padIndex > 0) {
            implicitChars -= (node.content().length() - padIndex);
        }

        return node.content().length() + implicitChars;
    }

    public record Node(String content, Node parent, List<Node> children, int id) {
        public Node(String content, Node parent, int id) {
            this(content, parent, new ArrayList<>(), id);
        }

        @Override
        public String toString() {
            return "Node: " + content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return id == node.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }

}
