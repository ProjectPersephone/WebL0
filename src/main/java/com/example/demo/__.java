package com.example.demo;

// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Stack;

public class __ {
    public __() { super(); }  // shuts the compiler up
    private   static int base_level = 0;
    private   static Boolean initialized = false;
    protected static String one_tab = "| ";
    // private   static Boolean on = true;
    private   static Stack<Boolean> flag_stack = new Stack<Boolean>();

    public static void push_trace(Boolean flag) {
        flag_stack.push(flag);
    }

    public static void pop_trace() {
        flag_stack.pop();
    }

    public static Boolean trace (Boolean flag) {
        boolean tmp = flag_stack.peek();
        flag_stack.setElementAt(flag, flag_stack.size()-1);
        return tmp;
    }

    private static int stackdepth() {
        // assertTrue(initialized);
        StackTraceElement a[] = Thread.currentThread().getStackTrace();
        return a.length;
    }

    public static void init() {
        // assertFalse(initialized);
        initialized = true;
        base_level = stackdepth() - 1; // -1 to account for this call to init
        flag_stack.clear();
        flag_stack.push(false); // prevent exception from peeking at empty stack
    }

    public static void reset() {
        base_level = 0;
        initialized = false;
        init();
        --base_level;   // account for this call to reset
    }

    public static String out() {
        // assertTrue(initialized);
        if (!flag_stack.peek()) return "";

        String tabs = new String("");
        int i = stackdepth() - base_level;

        while (--i > 0) {
            tabs += one_tab;
        }
        return tabs;
    }

    public static void ln(String S) {
        if (!flag_stack.peek()) return;
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