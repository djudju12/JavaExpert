package org.javaexpert.parser;

import org.javaexpert.expert.predicate.LogicConnector;
import org.javaexpert.expert.predicate.LogicOperator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Character.isWhitespace;
import static java.lang.Integer.parseInt;
import static org.javaexpert.Asserts.assertNotNull;
import static org.javaexpert.Asserts.assertTrue;
import static org.javaexpert.expert.predicate.LogicConnector.AND;
import static org.javaexpert.expert.predicate.LogicConnector.OR;
import static org.javaexpert.expert.predicate.LogicOperator.EQ;
import static org.javaexpert.expert.predicate.LogicOperator.GT;
import static org.javaexpert.expert.predicate.LogicOperator.GTE;
import static org.javaexpert.expert.predicate.LogicOperator.LT;
import static org.javaexpert.expert.predicate.LogicOperator.LTE;
import static org.javaexpert.expert.predicate.LogicOperator.NEQ;

public class Lexer {
    private final String filePath;
    private final String content;
    private Token lastToken;

    private int row;
    private int col;
    private int cursor;

    public Lexer(String filePath) throws IOException {
        this.filePath = filePath;
        this.content = readFile(filePath);
        row = col = 1;
        cursor = 0;
    }

    public String getContent() {
        return content;
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

    private char consumeAndPeek() {
        consumeChar();
        return peekChar();
    }

    private void trimSpaceAndComments() {
        char c = peekChar();
        while ((isWhitespace(c) || c == '/') && cursor < content.length()) {
            if (c == '/') {
                assertTrue((c = consumeAndPeek()) == '/', "unexpected character '%c'".formatted(c), currentLoc(1));
                dropLine();
                c = peekChar();
            } else {
                c = consumeAndPeek();
            }
        }
    }

    public Token requireNextToken() {
        return nextToken().orElseThrow(() -> new RuntimeException("unexpected EOF"));
    }

    public Token requireNextToken(Token.TokenType type) {
        var token = nextToken().orElseThrow(() -> new RuntimeException("unexpected EOF"));
        assertTrue(token.type() == type, "expected '%s' found: '%s' ".formatted(type, token), token.location());
        return token;
    }

    public void assertLastToken(Token.TokenType type) {
        assertTrue(lastToken.type() == type, "expected '%s' found: '%s' ".formatted(type, lastToken), lastToken.location());
    }

    public Token getLastToken() {
        return lastToken;
    }

    public Optional<Token> nextToken() {
        lastToken = fetchToken();
        return Optional.ofNullable(lastToken);
    }

    private Token fetchToken() {
        trimSpaceAndComments();
        if (cursor >= content.length()) {
            return null;
        }

        var t = nextTokenValue();
        return switch (t) {
            case "(" -> newToken(t, Token.TokenType.OPEN_PAR);
            case ")" -> newToken(t, Token.TokenType.CLOSE_PAR);
            case "," -> newToken(t, Token.TokenType.COMMA);
            case "<" -> newTokenOperator(t, LT);
            case ">" -> newTokenOperator(t, GT);
            case "=" -> newTokenOperator(t, EQ);
            case "<=" -> newTokenOperator(t, LTE);
            case ">=" -> newTokenOperator(t, GTE);
            case "<>" ->  newTokenOperator(t, NEQ);
            case "E" -> newTokenConnector(t, AND);
            case "OU" -> newTokenConnector(t, OR);
            case "SE" -> newToken(t, Token.TokenType.SE);
            case "ENTAO" -> newToken(t, Token.TokenType.ENTAO);
            case "REGRA" -> newToken(t, Token.TokenType.RULE);
            case "ATRIBUTO" -> newToken(t, Token.TokenType.ATTRIBUTE);
            case "OBJETIVOS" -> newToken(t, Token.TokenType.OBJECTIVES);
            case "NUMERICO" -> newToken(t, Token.TokenType.ATTR_NUMERIC);
            case "TEXTO" -> newToken(t, Token.TokenType.ATTR_STRING);

            case String strToken when strToken.startsWith("\"") && strToken.endsWith("\"") -> newTokenString(t);
            case String numToken when numToken.matches("^-?\\d+(\\.\\d+)?$") -> newTokenNum(t);

            default -> throw new IllegalStateException("%s: Invalid token '%s'".formatted(currentLoc(t.length()), t));
        };
    }

    private void dropLine() {
        char c = peekChar();
        while (c != '\n' && cursor < content.length()) c = consumeAndPeek();
        consumeChar();
    }

    private String nextTokenValue() {
        var sb = new StringBuilder();
        var c = peekChar();
        sb.append(c);
        consumeChar();
        switch (c) {
            case ',', ')', '(', '=': break;

            case '<', '>':  {
                if ((c = peekChar()) == '=' || c == '>') {
                    sb.append(c);
                    consumeChar();
                }
            } break;

            case '"': {
                for (c = peekChar(); c != '"' && cursor < content.length(); c = consumeAndPeek()) {
                    assertTrue(c != '\n', "unexpected end of string literal", currentLoc(sb.length()));
                    sb.append(c);
                }
                sb.append(c);
                consumeChar();
            } break;

            default: {
                for (c = peekChar(); !isWhitespace(c) && cursor < content.length(); c = consumeAndPeek()) {
                    assertTrue(c != '\n', "unexpected end of string literal", currentLoc(sb.length()));
                    sb.append(c);
                }
            }
        }

        return sb.toString();
    }

    private Token newTokenNum(String token) {
        return new Token(currentLoc(token.length()), Token.TokenType.NUM, parseInt(token));
    }

    private Token newTokenString(String token) {
        return new Token(currentLoc(token.length()), Token.TokenType.STR, token.substring(1, token.length() - 1));
    }

    private Token newToken(String token, Token.TokenType type) {
        return new Token(currentLoc(token.length()), type, token);
    }

    private Token newTokenOperator(String token, LogicOperator op) {
        return new Token(currentLoc(token.length()), Token.TokenType.LOGIC_OPERATOR, op);
    }

    private Token newTokenConnector(String token, LogicConnector connector) {
        return new Token(currentLoc(token.length()), Token.TokenType.LOGIC_CONNECTOR, connector);
    }

    private Location currentLoc(int tokenLen) {
        return new Location(filePath, row, col - tokenLen);
    }

    private String readFile(String filePath) {
        var in = this.getClass().getClassLoader().getResourceAsStream(filePath);
        assertNotNull(in, "resource not found");
        return new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8)
        ).lines().parallel().collect(Collectors.joining("\n"));
    }
}
