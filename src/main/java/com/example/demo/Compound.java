package com.example.demo;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.nullable;
// import static org.mockito.Mockito.clearAllCaches;  // don't know how this got here
import static org.mockito.Mockito.reset;

import java.util.*;

import com.example.demo.Valence;
import com.example.demo.TypedTree;

// Actually more like "element of a compound" -- is there a way to say that in chemistry?

public class Compound {

    static private int call_depth = 0;

    Nucleus n;       // maybe an initial "if" is ":-"???
    LinkedList<Compound> args; // if null, not a predicate, just an atom
    Compound next = null;  // in a sequence of compounds, the next one

            public Compound(Nucleus n) {
                this.n = n;
                args = new LinkedList<Compound>();
            }

            public String toString() {
                String s = n.toString() + ":" + args.toString();
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

    // preds - keep list of all compounds ===============================================
    //
    // Maybe it should be in stack, since some assertions may need to be retracted

    private static HashMap<Nucleus,LinkedList<Compound>> preds = new HashMap<Nucleus,LinkedList<Compound>>();

            private void add_pred() {
                LinkedList<Compound> l = preds.get(n);
                if (l == null) {
                    l = new LinkedList<Compound>();
                    preds.put(n, l);
                }
                l.add (this);
            }

    // bindings - keep bindings stacked  ===============================================

    private static Stack<Context> bindings = new Stack<Context>();

            private class Context {
                LinkedList<Bond> bonds;
                LinkedList<Compound> free;

                public Context() {
                    bonds = new LinkedList<Bond>();
                    free = new LinkedList<Compound>();
                }

                public Bond put (Compound n, Compound v) {
                    Tab.ln ("new binding being added, n=" + n + " v=" + v);
                    Bond b = new Bond (n,v);
                    bonds.add (b);
                    return b;
                }

                public String toString() {
                    String s = "CTX:[bonds=";
                    String sep = "";
                    for (Bond b : bonds) {
                        s += (sep + b.toString());
                        sep = ",";
                    }
                    s +=" free=";
                    sep = "";
                    for (Compound c : free) {
                        s += (sep + c.toString());
                        sep = ",";
                    }
                    return s + "]";
                }
            };

            private static Context new_Context() {
                Compound __ = new Compound(Nucleus.DIE); // annoying workaround
                Context C = __.new Context();
                assert (C.bonds.isEmpty());
                __ = null;  // faster GC? Probably not, but anyway
                return C;
            }

            private class Bond {
                public Compound nom;    // like SOMEONE ....
                public Compound val;    // like I, YOU, ...

                private Bond (Compound n, Compound v) { nom = n; val = v; }

                public String toString() {
                    return "Bond:<" + nom.toString() + "," + val.toString() + ">";
                }
            };

// main interpreter routines --------------------------------------------------------------

// problems here: want to use SOMEONE, etc. as variable, referred to as THIS SOMEONE later
    // I LIVE -- no unbound args, just assert in preds
    // SOMEONE LIVE - 
    // 
    // GOOD SOMEONE - THIS GOOD SOMEONE
    // need to accommodate modifiers on substantives?

      // maybe have a type "MAYBE I [am] NOT KNOW[ing] THIS" for free variables?

      // tricky here -- bound vs. unbound, and modified variables like (THIS) GOOD SOMEONE
      // not covered
    public Boolean can_be_var () {
        switch (n) {
            case SOMEONE: case SOMETHING: case SOMEWHERE:
                        return true;
            // TIME_WHEN, others???? Maybe IF can be a "whether", THIS can be a "which", etc.
            case THIS:  Tab.ln ("Checking for are THIS in can_be_var");
                        return (just_atom());
            default:
                break;
        }
        return false;
    }

    private Boolean is_bound() { return (n == Nucleus.THIS); }

    // THIS on a (possibly-modified) wh-word: THIS <x> has type "I KNOW THIS"?
    // Need to catch cases of THIS x where there was no unbound var declared

    // Not sure I'll ever use this list; maybe it should just return Boolean

    public LinkedList<Compound> unbound_vars () {
        LinkedList<Compound> r = new LinkedList<Compound>();
        for (Compound c : args) {
            if (c.can_be_var()) { // defer deep binding as issue to explore
               r.add(c);
            } else
            if (c.is_bound())
                continue;
            else
                r.addAll (c.unbound_vars());
        }
        return r;
    }

    // react tries to find all satisfactory bindings for the Context C
    // in which it runs.
    // It needs to backtrack to produce all possible bindings.
    // Context C should already have all the unbound vars for the list
    // But somewhere (maybe a Context NewC = new_Context (cp)?)
    //                         LinkedList<Compound> ua = cp.unbound_vars();
    //                         Context newC = new_Context();   // new context per line???? No
    //                         newC.free.addAll(ua);

    // Not sure whether IF should be handled here or in unify

    private static Boolean react (Compound cl, Context C) {
        if (cl == null)
            return true;
                                                                if (++call_depth > 30) { Tab.ln ("call_depth>30, bailing"); return false; }
        Boolean succeeded_yet = false;                          Tab.ln("In react(cl="+cl+",C="+C);
        for (Compound c = cl; c != null; c = c.next)  {         Tab.ln ("react: c="+c);
            if (c.n == Nucleus.IF) {
                Tab.ln ("Hit IF, trying cond " + c.args.get(0));
                if (react (c.args.get(0), C))
                    react (c.args.get(1), C);
                continue;
            }
            LinkedList<Compound> ps = preds.get(c.n);           Tab.ln ("react: ps="+ps);
            LinkedList<Compound> ua = c.unbound_vars();         Tab.ln ("react: ua="+ua);
            if (ps == null || ua.isEmpty()) {
                c.add_pred();
                continue;
            }
            if (!ua.isEmpty())
              for (Compound cp : ps) {
                if (unify (c, cp, C)) {                 // does unify need a new context?
                        Boolean r = react (c.next, C);
                        if (r) {
                            succeeded_yet = true;               Tab.ln("succeeded_yet<=true");
                        }
                }
              }
        }
                                                                --call_depth;
        return succeeded_yet;
    }

    private Boolean just_atom() { return args.isEmpty();  }
    private Boolean complex()   { return !args.isEmpty(); }
    private int arity()         { return args.size();     }

    // maybe stop dragging Context C around and refer to tos of Context stack?

    private static Boolean unify (Compound T1, Compound T2, Context C) {
                                                    if (++call_depth > 30) { Tab.ln ("call_depth>30, bailing"); return false; }
        Boolean r = false;                                  Tab.ln ("unify (" + T1 + ", " + T2 + "):");

        if (T1.can_be_var()) {                         Tab.ln ("var T1=" + T1);
            C.put (T1,T2);
            r = true;
        } else
        if (T2.can_be_var()) {                         Tab.ln ("var T2=" + T2);
            C.put (T2,T1);
            r = true;
        } else
        if (T1.just_atom() && T2.just_atom()) {             Tab.ln ("trying to unify two atoms: " + T1.n + " == " + T2.n);
            r = (T1.n == T2.n);
        } else
        if (T1.complex() && T2.complex()
         && T1.arity() == T2.arity()
         && T1.n == T2.n) {                                 Tab.ln ("Try unifying " + T1 + " with " + T2);
                Iterator<Compound> T1i = T1.args.iterator();
                Iterator<Compound> T2i = T2.args.iterator();
                r = true;
                while (T1i.hasNext())  {
                    if (!unify (T1i.next(), T2i.next(), C)) {  Tab.ln ("Failed to unify two args to unify");
                        r = false;
                        break;
                    }
                if (r) {
                    Tab.ln ("all args bound for " + T1 +" and "+ T2);
                }
            }
        }                                     Tab.ln ("unify=" + r);
                                            --call_depth;
        return r;
    }

    public static void load_and_run(NestedLines nlp) {
        Line last = nlp.lines.removeLast(); // last line will be query, for now

        Compound run_list = null;
        Compound run_list_end_ptr = null;

        Tab.ln ("----------- load_and_run: clear bindings & compounds & preds ---------");
        bindings.clear();
        // compound.clear();  // should probably do this only on a 'clear' bool param to load_and_run
        preds.clear();

        Tab.ln ("----------- load_and_run: build: ---------------");

        // process all lines into NSM prolog, adding them to compound
        // Needs handling of block structure; maybe make it recursive routine
        LinkedList<Line> L = nlp.lines;

        for (Line Li : L) {
            if (Li.line.types.size() > 1) {                 Tab.ln ("build: Looks like unreduced type for lexeme " + Li.line.tree.lexeme);
                break;
            }
            Tab.push_trace(false);
             LinkedList<Compound> npl = TypedTree.pl(Li.line,null);
            Tab.pop_trace();
                                                        Tab.ln("build: npl=" + npl.toString());
                                                        Tab.ln("Adding to compound...");
            for (Compound c : npl) {
                if (run_list == null)
                    run_list = c;
                else
                    run_list_end_ptr.next = c;
                run_list_end_ptr = c;
            }

            if (Li.block != null && Li.block.size() > 0) {
                Tab.ln ("Compound c = build (Li.block); // unimplemented");
            }
        }

        Tab.ln ("----------- load_and_run: run_list ---------------------------");

        for (Compound ci = run_list; ci != null; ci = ci.next) {
            Tab.ln (ci.toString());
        }

        Tab.ln ("----------- load_and_run: start top-level scope ----------------------");

        Context C = new_Context();
        bindings.push(C);

        Tab.ln ("------------ load_and_run: initial bindings --------------------------");

        Tab.ln ("bindings = " + bindings);

        Tab.ln ("------------ load_and_run: running react -----------------------------");

        if (react (run_list, C)) {    // need to prep free vars first????
            Tab.ln ("FAILED!!!!!!!!!!!!!");
        } else {
            Tab.ln ("SUCCESS ! ! ! !");
        }
        Tab.ln ("Context C=" + C);
        Tab.ln ("bindings = " + bindings);

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
        Tab.ln ("Bindings before running query:" + bindings);
        Tab.ln ("query=" + query);

        Boolean r = react (query, C);

        Tab.ln ("satisfy returned " + r + " possibly for stupid reasons");
        Tab.ln ("query now=" + query);
        Tab.ln ("Bindings now: " + bindings);
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

        Tab.ln ("Cleaner handling of unrecognized words");
        Tab.ln ("Change Title to Query");
        Tab.ln ("A Run button, to get query output on web page");
    }
}

