package org.javaexpert;

import org.javaexpert.lexer.Location;

public class Asserts {

    private Asserts() { }

    public static void assertNull(Object o, String msg) {
        if (o != null) {
            throw new IllegalStateException(msg);
        }
    }

    public static void assertNotNull(Object o, String msg) {
        if (o == null) {
            throw new IllegalStateException(msg);
        }
    }

    public static void assertFalse(boolean b, String msg) {
        if (b) {
            throw new IllegalStateException(msg);
        }
    }

    public static void assertTrue(boolean b, String msg) {
        if (!b) {
            throw new IllegalStateException(msg);
        }
    }

    public static void assertTrue(boolean b, String msg, Location loc) {
        assertTrue(b, "%s: %s".formatted(loc, msg));
    }

    public static void assertNotNull(Object o, String msg, Location loc) {
        assertNotNull(o, "%s: %s".formatted(loc, msg));
    }

}
