package org.javaexpert.lexer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Character.isSpaceChar;
import static java.lang.Character.isWhitespace;
import static org.javaexpert.Asserts.assertFalse;
import static org.javaexpert.Asserts.assertNotNull;
import static org.javaexpert.Asserts.assertTrue;

public class Lexer {
    private final String filePath;
    private final String content;

    private int row;
    private int col;
    private int cursor;

    public Lexer(String filePath) throws IOException {
        this.filePath = filePath;
        this.content = readFile(filePath);
        row = col = 1;
        cursor = 0;
    }

    private Character peekChar() {
        if (cursor >= content.length()) {
            return content.charAt(cursor - 1);
        }

        return content.charAt(cursor);
    }

    private void consumeChar() {
        if (cursor < content.length()) {
            if (peekChar() == '\n') {
                row += 1;
                col = 1;
            } else {
                col += 1;
            }
            cursor += 1;
        }
    }

    private void trimLeft() {
        while (isWhitespace(peekChar())) consumeChar();
    }

    public Token requireNextToken() {
        return nextToken().orElseThrow(() -> new RuntimeException("unexpected EOF"));
    }

    public Token requireNextToken(Token.TokenType type) {
        var token = nextToken().orElseThrow(() -> new RuntimeException("unexpected EOF"));
        assertTrue(token.getType() == type, "%s: expected '%s' found: '%s' ".formatted(token.getLocation(), type, token.getType()));
        return token;
    }

    public Optional<Token> nextToken() {
        trimLeft();
        if (cursor >= content.length()) {
            return Optional.empty();
        }

        var c = peekChar();
        var sb = new StringBuilder();
        sb.append(c);
        if (!isOneCharToken(c)) {
            if (c == '"') {
                do {
                    consumeChar();
                    c = peekChar();
                    assertTrue(c != '\n', "unexpected end of string literal", currentLoc(sb.length()));
                    sb.append(c);
                } while (c != '"');
                consumeChar();
            } else {
                consumeChar();
                c = peekChar();
                while (!isWhitespace(c) && cursor < content.length()) {
                    sb.append(c);
                    consumeChar();
                    c = peekChar();
                }
            }
        } else {
            consumeChar();
        }


        var t = sb.toString();
        return switch (t) {
            case "REGRA" -> newToken(t, Token.TokenType.RULE);
            case "SE" -> newToken(t, Token.TokenType.SE);
            case "E" -> newToken(t, Token.TokenType.E);
            case "OU" -> newToken(t, Token.TokenType.OU);
            case "ENTAO" -> newToken(t, Token.TokenType.ENTAO);
            case "ATRIBUTO" -> newToken(t, Token.TokenType.ATTRIBUTE);
            case "OBJETIVOS" -> newToken(t, Token.TokenType.OBJECTIVES);
            case "(" -> newToken(t, Token.TokenType.OPEN_PAR);
            case ")" -> newToken(t, Token.TokenType.CLOSE_PAR);
            case "=" -> newToken(t, Token.TokenType.EQUAL);
            case "," -> newToken(t, Token.TokenType.COMMA);
            default -> {
                var loc = currentLoc(t.length());
                if (t.startsWith("\"") && t.endsWith("\"")) {
                    yield Optional.of(new TokenStr(loc, t.substring(1, t.length() - 1)));
                }

                throw new IllegalStateException("%s: Invalid token '%s'".formatted(loc, t));
            }
        };
    }

    private static boolean isOneCharToken(char c) {
        return ",)(".indexOf(c) >= 0;
    }

    private Optional<Token> newToken(String token, Token.TokenType type) {
        return Optional.of(new Token(currentLoc(token.length()), type));
    }

    private Token.Location currentLoc(int tokenLen) {
        return new Token.Location(filePath, row, col - tokenLen);
    }

    private String readFile(String filePath) throws IOException {
        var in = this.getClass().getClassLoader().getResource(filePath);
        assertNotNull(in, "resource not found");
        return new BufferedReader(
                new InputStreamReader(
                    (BufferedInputStream) in.getContent(),
                    StandardCharsets.UTF_8
                )
        ).lines().parallel().collect(Collectors.joining("\n"));
    }
}
