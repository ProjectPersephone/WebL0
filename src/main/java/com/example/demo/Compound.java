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

    private Boolean just_atom() { return args.isEmpty();  }
    private Boolean complex()   { return !args.isEmpty(); }
    private int arity()         { return args.size();     }

    private static Boolean unify (HashMap<Nucleus,LinkedList<Compound>> ps, Compound T1, Compound T2) {
        Boolean r = false;                                  Tab.ln ("unify (" + T1 + ", " + T2 + "):");
        if (T1.just_atom() && T2.just_atom()) {             Tab.ln ("unified: " + T1.n + " == " + T2.n);
            r = (T1.n == T2.n);
        } else
        if (T1.n == Nucleus.THIS) {                         Tab.ln ("var THIS for T1=" + T1);
            r = (T2 == T1.bindings.push(T2));               // instantiate, and r <- true
        } else
        if (T2.n == Nucleus.THIS) {                         Tab.ln ("var THIS for T2=" + T2);
            r = (T1 == T2.bindings.push(T1));
        } else
        if (T1.complex() && T2.complex()
         && T1.arity() == T2.arity()
         && T1.n == T2.n) {                                 Tab.ln ("Try unifying " + T1 + " with " + T2);
                Iterator<Compound> T1i = T1.args.iterator();
                Iterator<Compound> T2i = T2.args.iterator();
                r = true;
                while (T1i.hasNext())  {
                    if (!unify (ps, T1i.next(), T2i.next())) {  Tab.ln ("Failed to unify two args to unify");
                        r = false;
                        break;
                    }
                if (r) {
                    Tab.ln ("all args bound for "+T1+" and "+ T2);
                }
            }
        }                                     Tab.ln ("unify=" + r);
        return r;
    }

    private Boolean satisfy(HashMap<Nucleus,LinkedList<Compound>> preds) {
                                                                if (++call_depth > 10) { Tab.ln ("call_depth>10, bailing"); return false; } 
        Boolean r = false;                                      Tab.ln ("satisfy (" + this + ")");
        int n_args = args.size();

        if (n_args == 0) r = true;
        else
        if (n == Nucleus.IF) {
            Tab.ln ("Hit IF, trying cond " + args.get(0));
            if (args.get(0).satisfy (preds)) {
                Tab.ln ("cond true, at least");
                Tab.ln ("assert consequent in this scope, at least");
                Tab.ln ("side effect on preds, so failure means unwinding somewhere");
                r = args.get(1).satisfy (preds);
            }
        } else {
            LinkedList<Compound> p_ls = preds.get(n);           Tab.ln("Looking for " + n);
            if (p_ls != null) {
            for (Compound c : p_ls) {                           assertNotNull(c.args);
                if (c.args.size() == n_args) {                  Tab.ln ("c=" + c +" -- calling unify on this=" + this);            
                    if (unify( preds, this, c )) {              Tab.ln ("c=" + c + " unified with " + this);
                        r = true;
                    }}}}}                                       Tab.ln ("...returning " + r);  --call_depth;   
        return r;
    }

    public static void load_and_run(NestedLines nlp) {
        Line last = nlp.lines.removeLast(); // last line will be query, for now

        compound.clear();  // should probably do this only on a 'clear' bool param to load_and_run

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
        Tab.ln ("~~~~~~~~~~~~~~~~ TO DO ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        Tab.ln ("I get to do these things:");
        Tab.ln ("Implement");
        Tab.ln ("  CAN - should be simple; looks almost done already!");
        Tab.ln ("  NOT - need to prove negation works in queries");
        Tab.ln ("  multiline rules -- chained IFs inserted or ...");
        Tab.ln ("  call -- may require meta, using WORDS as a handle on Compounds");
        Tab.ln ("Figure out where my Prolog comb rule fits into the flow");
        Tab.ln ("    (Maybe as first-level check on app()/combine() success, intrasentially?");
        Tab.ln ("Red Queen arithmetic");
        Tab.ln ("A Run button, to get query output on web page");
    }
}
