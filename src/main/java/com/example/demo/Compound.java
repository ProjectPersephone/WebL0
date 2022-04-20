package com.example.demo;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
// import static org.mockito.Mockito.clearAllCaches;  // don't know how this got here

import java.util.*;

import com.example.demo.Valence;
import com.example.demo.TypedTree;

// Actually more like "element of a compound" -- is there a way to say that in chemistry?

public class Compound {
    private static Stack<Compound> stack = new Stack<Compound>();
    private static LinkedList<Compound> compound = new LinkedList<Compound>();

    Nucleus n;       // maybe an initial "if" is ":-"???
    LinkedList<Compound> args; // if null, not a predicate, just an atom
    Stack<Compound> bindings = new Stack<Compound>(); // outer stack: scope; inner set: bonds
                                            // maybe applies only to THIS (alone or as qualifier)
    static private int call_depth = 0;
    
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
        String s = n.toString() + ":" + args.toString();
        if (!bindings.isEmpty())
            s += "[=" + bindings + "]";
        return s;
    }

    public String pp1() {
        String s = "<" + n.toString() + ">";
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

    private static Compound build(LinkedList<Line> L) {
        for (Line Li : L) {
            if (Li.line.types.size() > 1) {                 Tab.ln ("build: Looks like unreduced type for lexeme " + Li.line.tree.lexeme);
                return null;
            }
            Tab.push_trace(false);
             LinkedList<Compound> npl = TypedTree.pl(Li.line,null);
            Tab.pop_trace();
                                                        Tab.ln("build: npl=" + npl.toString());
                                                        Tab.ln("Adding to compound...");
            compound.addAll(npl);
            if (Li.block != null && Li.block.size() > 0) {
                Compound c = build (Li.block);
            }
        }
        return null;
    }

    private Compound get_1_arg() {
        assertNotNull(this.args);
        LinkedList<Compound> args = this.args;
        assert(0 != args.size());
        Compound r = args.get(0);                Tab.ln ("get_1_arg returning " + r);
        return r;
    }

    private Compound free_var() {
        Tab.ln ("free_var: this=" + this);
        if (n == Nucleus.THIS) {
            return this.get_1_arg();
        }
        return null;    
    }

            // OR of all preds
        //    each pred: an OR of each rule, stopping with the first true
        //    each rule: an AND of each functor(args)

                // How to infer that "I" matches "THIS[SOMEONE[]]"?
                // LIVE(I) is recorded
                // IS(I,SOMEONE) - axiomatic
                // THIS is seen
                // SOMEONE is the argument of THIS
                // so ask IS(I,SOMEONE)
                // if this is true (it is) bind this THIS to I.
                // which means we need to construct IS(I,SOMEONE)
                // then ask if that's true.

    // Because I don't have just the prolog functor(arg) for "facts"
    // I need to look at where IS(x,y) has been asserted.
    // Better to generalize this to all two-place predicates, and n-place predicates
    // Need to make it recursive 
    private static Boolean two_args(HashMap<Nucleus,LinkedList<Compound>> preds, Compound x, Compound y) {
                                                        Tab.ln ("is_ (" + x + "," + y + "):");
        Boolean bind_x1 = false, bind_y1 = false;
        Compound x1, y1;
                                                        Tab.ln ("Trying x,y=" + x + ", " + y);
        if (x.free_var() != null) {
            x1 = x.free_var();
            bind_x1 = true;
        } else x1 = x;
        if (y.free_var() != null) {
            y1 = y.free_var();
            bind_y1 = true;
        } else y1 = y;
        Boolean r = false;
        LinkedList<Compound> isness = preds.get(Nucleus.BE);
        for (Compound c : isness) {
            LinkedList<Compound> two_args = c.args;
            Compound cx1 = two_args.get(0);
            Compound cy1 = two_args.get(1);
            
            // Naive match ==================================
            if (x1.n == cx1.n && y1.n == cy1.n) {       Tab.ln ("matched " + x1 + " with " + cx1 + " and " + y1 + " with " + cy1);
                if (bind_x1) { // x has modifier THIS; bind
                    x.bindings.push(cy1);
                }
                if (bind_y1) {
                    y.bindings.push(cx1);
                }
                r = true;
                break; // perhaps prematurely
            } else {                                    Tab.ln ("no match of " + x1 + " with " + cx1 + " and " + y1 + " with " + cy1);
            }
        }
        Tab.ln ("..." + r);
        return r;
    }

    private Boolean satisfy(HashMap<Nucleus,LinkedList<Compound>> preds) {
        if (++call_depth > 10) {                            Tab.ln ("call_depth>10, bailing"); return false; } 
                                                            Tab.ln ("satisfy" + this);
        if (this.args.isEmpty()) {                          Tab.ln ("no arguments to " + this + "--returning true");
            return true;
        }
        int n_args = args.size();
        Compound this_arg = this.args.get(0);  // sticking with single-arg case, BE (is_) special case
        LinkedList<Compound> p_ls = preds.get(n);
        if (p_ls == null) {                                 Tab.ln (n.toString() + " is no predicate, returning true");
            return true;
        }
        Boolean r = false;
        for (Compound c : p_ls) {                          assertNotNull(c.args);
            if (c.args.size() != n_args)
                continue;
            Compound arg = c.args.get(0);            Tab.ln ("c=" + c + " arg=" + arg);
            if (arg == null) {
                                                            Tab.ln ("c arg = null");
            } else {                                        Tab.ln ("Calling is_ on " + arg + " and " + this_arg + "...");
                r = two_args(preds,arg,this_arg);           Tab.ln ("Calling satisfy on " + arg);
                arg.satisfy(preds);
            }
        }
        --call_depth;
        return r;
    }

    public static void load_and_run(NestedLines nlp) {
        Line last = nlp.lines.removeLast(); // last line will be query, for now

        Tab.ln ("----------- load_and_run: Compound.run: calling build: ---------------");
        Compound c = build (nlp.lines);
        Tab.ln ("----------- load_and_run: Dump of compound ---------------------------");
        for (Compound ci : compound) {
            Tab.ln (ci.toString());
        }

        Tab.ln ("----------- load_and_run: establish predicates -----------------------");
        // This should be done in build
        HashMap<Nucleus,LinkedList<Compound>> preds = new HashMap<Nucleus,LinkedList<Compound>>();

        // functor(args): hash table with functor as key

        for (Compound ci : compound) {
            LinkedList<Compound> ne = preds.get(ci.n);
            if (ne == null) {
                ne = new LinkedList<Compound>();
            }
            ne.add(ci);
            preds.put (ci.n, ne);
        }

        Tab.ln ("------------------- load_and_run: predicates --------------------------");
        Tab.ln ("preds=" + preds);

        Tab.ln ("------------------- load_and_run: get query ---------------------------");
        Tab.push_trace(false);
        LinkedList<Compound> npl = TypedTree.pl(last.line,null);
        Tab.pop_trace();
        Tab.ln ("npl=" + npl);
        Compound query = npl.get(0);
        Tab.ln ("query=" + query);

        Tab.ln ("------------------- run: try running ------------------------");

        call_depth = 0;
        Tab.ln ("query=" + query);
        Boolean r = query.satisfy (preds);
        Tab.ln ("satisfy returned " + r + " possibly for stupid reasons");
        Tab.ln ("query now=" + query);
    }
}
