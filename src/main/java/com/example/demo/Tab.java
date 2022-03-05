package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Tab {
    private   static int base_level = 0;
    private   static Boolean initialized = false;
    protected static String one_tab = "| ";
    private   static Boolean on = true;

    public static void trace (Boolean flag) {
        on = flag;
    }

    private static int stackdepth() {
        assertTrue(initialized);
        StackTraceElement a[] = Thread.currentThread().getStackTrace();
        return a.length;
    }

    public static void init() {
        assertFalse(initialized);
        initialized = true;
        base_level = stackdepth() - 1; // -1 to account for this call to init
    }

    public static void reset() {
        base_level = 0;
        initialized = false;
        init();
        --base_level;   // account for this call to reset
    }

    public static String out() {
        assertTrue(initialized);
        if (!on) return "";

        String tabs = new String("");
        int i = stackdepth() - base_level;

        while (--i > 0) {
            tabs += one_tab;
        }
        return tabs;
    }

    public static void ln(String S) {
        assertTrue(initialized);
        if (!on) return;
        ++base_level;   // compensate for calling print
        System.out.println (out() + S);
        --base_level;
    }

    public static void o__() {
        --base_level;
    }
    public static void __o() {
        ++base_level;
    }
}