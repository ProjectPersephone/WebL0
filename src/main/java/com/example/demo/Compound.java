package com.example.demo;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.*;

import com.example.demo.Valence;
import com.example.demo.TypedTree;

// Actually more like "element of a compound" -- is there a way to say that in chemistry
public class Compound {
    private static Stack<Compound> stack = new Stack<Compound>();
    private static LinkedList<Compound> compounds = new LinkedList<Compound>();

    Nucleus n;       // maybe an initial "if" is ":-"???
    LinkedList<Compound> args; // if null, not a predicate, just an atom
    
    public Compound(Nucleus n) {
        this.n = n;
        args = new LinkedList<Compound>();
    }

    // add to end of node list at top-of-stack
    public static void add(Nucleus n) {
        Compound npl = new Compound(n);
        stack.peek().args.add(npl);
    }

    public static void sublist_start(Nucleus n) {
        Compound ex_tos = stack.peek();
        Compound npl = stack.push(new Compound(n));
        ex_tos.args.add(npl);
    }

    public String toString() {
        return n.toString() + args.toString();
    }

    public String pp1() {
        String s = n.toString();
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
