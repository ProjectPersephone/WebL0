package com.example.demo;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.*;

import com.example.demo.Type;
import com.example.demo.TypedTree;

public class Compound {
    private static Stack<Compound> stack = new Stack<Compound>();
    private static LinkedList<Compound> compounds = new LinkedList<Compound>();

    AUGType type;       // maybe an initial "if" is ":-"???
    LinkedList<Compound> args; // if null, not a predicate, just an atom
    
    public Compound(AUGType t) {
        type = t;
        args = new LinkedList<Compound>();
    }

    // add to end of node list at top-of-stack
    public static void add(AUGType t) {
        Compound npl = new Compound(t);
        stack.peek().args.add(npl);
    }

    public static void sublist_start(AUGType t) {
        Compound ex_tos = stack.peek();
        Compound npl = stack.push(new Compound(t));
        ex_tos.args.add(npl);
    }

    public String toString() {
        return type.toString() + args.toString();
    }

    public String pp1() {
        String s = type.toString();
        if (!args.isEmpty()) {
            String sep = "(";
            for (Compound np : args) {
                s += sep;
                s += np.pp1();
                sep = ","; 
            }
            s += ")";
        }
        return s;
    }
    public static String pp(LinkedList<Compound> npl) {
        String s = "[";
        String sep = "";
        for (Compound np : npl) {
            s += sep + np.pp1();
            sep = ",";
        }
        return s + "]";
    }
}
