package com.example.demo;

import com.example.demo.TypedTree;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

// Actually more like "element of a compound" -- is there a way to say that in chemistry?

public class Compound {

    static private int call_depth = 0;

    Nucleus n;       // maybe an initial "if" is ":-"???
    LinkedList<Compound> args; // if null, not a predicate, just an atom
    Compound next = null;  // in a sequence of compounds, the next one
    Compound block;     // actually a linked list; this is the head

            public Compound(Nucleus n) {
                this.n = n;
                args = new LinkedList<Compound>();
                block = null;
            }

            public String toString() {
                String s = n.toString() + ":" + args.toString();
                return s;
            }

            @Override
            public boolean equals(Object o) {
                if (o == this)
                    return true;
                if (!(o instanceof Compound))
                    return false;
                Compound other = (Compound) o;
                if (this.n != other.n)
                    return false;
                return (this.args.equals(other.args));
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
    // Maybe this should be in Context, because some Compounds will be assertions
    // during execution that may need to be retracted. All this argues for each
    // Context to have its own preds list.



    // bindings - keep bindings stacked  ===============================================

    private static Stack<Context> bindings = new Stack<Context>();

                    // Shouldn't really be a member function of Context, just hacked for now
                    private static Compound value_of(Compound sym, Context this_context) {
                        __.ln ("value_of: this_context="+this_context);
                        for (Bond bi : this_context.bonds) {
                            if (bi.nom.equals(sym)) {
                                __.ln ("value_of: found "+ bi + "returning " + bi.val);
                                return bi.val;
                            }
                            else
                                __.ln ("failed: "+bi+".nom.equals("+sym+")");
                        }
                        if (this_context.outer != null) {                    __.ln ("recursive call of value_of");
                            return value_of(sym, this_context.outer);
                        }
                        return null;
                    }
    
                    public static Compound value_of (Compound sym) {
                        return value_of(sym, bindings.peek());
                    }

            private class Context {

                LinkedList<Bond> bonds;
                LinkedList<Compound> free;
                private HashMap<Nucleus,LinkedList<Compound>> preds;    // rename "assumptions"?
                private Context outer = null;

                // Maybe also push and inherit from lower in stack?
                public Context() {
                    bonds = new LinkedList<Bond>();
                    free = new LinkedList<Compound>();
                    preds = new HashMap<Nucleus,LinkedList<Compound>>();
                    if (bindings == null) {
                        bindings = new Stack<Context>();
                        outer = null;
                    } else {
                        if (!bindings.isEmpty())
                            outer = bindings.peek();
                        else
                            outer = null;
                    }
                    bindings.push(this);
                }

                public Context pop() {
                    Context tos = bindings.pop();

                    return tos;
                }

                // if adding an IF-cond that's met, add it to the context for the block that gets opened
                // in the consequent.
                // if adding an assertion about a THIS <t> in a block, it should also be in that block's context.
                // Search for a goal should be a search down through context stack.
                // With cascaded gets, could modify in outer scope

                public Boolean add_pred(Compound c) {
                    __.ln ("add_pred: this=" + this + "c="+c);
                    LinkedList<Compound> l = preds.get(c.n);
                    if (l == null) {
                        l = new LinkedList<Compound>();
                        preds.put(c.n, l);
                    }
                    if (!l.contains(c))     // Gricean problem here, unless c is a cond
                        l.add (c);
                    return true;
                }

                // deep binding?
                public LinkedList<Compound> get(Nucleus n) {
                    __.ln ("Context.get("+n+") with bindings="+bindings);
                    LinkedList<Compound> r = preds.get(n);
                    if (outer != null && outer.preds != null) { // should make sure preds ptr non-null
                        if (r != null) {
                            LinkedList<Compound> gotten = outer.preds.get(n);
                            if (gotten != null)
                                r.addAll(outer.preds.get(n));
                        }
                        else r = outer.preds.get(n);
                    }
                    __.ln("Context.get returning r="+r);
                    return r;
                }

                public Bond reset(Compound n, Compound v) {
                    for (Bond b : bonds) {
                        if (b.nom == n) {
                            b.val = v;
                            return b;
                        }
                    }
                    return null;
                }

                public Bond put (Compound n, Compound v) {
                    __.ln ("new binding being added, n=" + n + " v=" + v);
                    Bond b;
                    if (value_of (n) == null) {
                        b = new Bond (n,v);
                        bonds.add (b);
                    } else
                        b = reset (n,v);
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
                    s += " preds" + preds;
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

                private Bond (Compound n, Compound v) {
                    __.ln ("making bond from n=" + n.toString() + " and v=" + v.toString());
                    nom = n;
                    val = v;
                }

                public String toString() {
                    String s = "Bond:<";
                    s += nom.toString();
                    s += ",";
                    s += val.toString();
                    s += ">";
                    return s;
                }
            };

// main interpreter routines --------------------------------------------------------------

// problems here: want to use SOMEONE, etc. as variable, referred to as THIS SOMEONE later
    // I LIVE -- no unbound args, just assert in preds

    // need to accommodate modifiers on substantives?

      // maybe have a type "MAYBE I [am] NOT KNOW[ing] THIS" for free variables?

      // tricky here -- bound vs. unbound, and modified variables like (THIS) GOOD SOMEONE
      // not covered

    public Boolean has_unbound_var () {
        switch (n) {
            case SOMEONE: case SOMETHING: case SOMEWHERE:
                        return true;
            // TIME_WHEN, others???? Maybe IF can be a "whether", THIS can be a "which", etc.
            /*
            case THIS:  __.ln ("Checking for are THIS in has_unbound_var");
                        return (just_atom());
            */
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
            if (c.has_unbound_var()) { // defer deep binding as issue to explore
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

    private Boolean satisfy (Compound to_do) {
        Boolean r = false;
        LinkedList<Compound> ps;                                    __.ln ("satisfy: this="+this+" to_do="+to_do);

        ps = bindings.peek().get(n);                               __.ln(""); __.ln ("satisfy: ps="+ps);

        if (ps == null)
            r = false;
        else
            for (Compound cp : ps) {                                __.ln ("satisfy: trying cp="+cp);
                if (unify (this, cp, to_do)) {                      __.ln ("satisfy: unified");
                    r = true;
                }
            }
    
        return r;
    }

    // deep copy, could be trouble if conseq of a cond or some other block
    // has sub-blocks; is our language single-assignment?
    private Compound subst_this_copy() {
        Compound x = new Compound(n);

        for (Compound a : args) {
            if (a.n == Nucleus.THIS) {
                if (a.just_atom()) {
                    __.ln ("subst_this_copy: unhandled case");
                    return null;  // sure, take the null pointer exception later
                }
                assertEquals(1, a.args.size());
                Compound nom = a.args.get(0);
                __.ln ("About to call value_of on "+nom+" with bindings=" + bindings);
                Compound v = value_of(nom);     // drag it up from whatever context
                assertNotNull(v);               // bad if un-init var
                bindings.peek().put(nom,v);     // assert the value in this context
                x.args.add(v);                  // and make sure the generated substitution has it as a literal
            }
            else {
                x.args.add (a.subst_this_copy());
            }
        }
        return x;
    }

    private static Context react (Compound cl) {
        if (cl == null)
            return bindings.peek(); // unchanged
        // to return a different context, must traverse the entire sentence list
        // and succeed to the end at least once. Simply adding a fact doesn't
        // contribute. These may actually need to be retracted if
        // there's failure.
                                                                __.push_trace(true);
                                                                if (++call_depth > 30) { __.ln ("call_depth>30, bailing"); return null; }
                                                                __.ln("");
                                                                __.ln("In react(cl="+cl+",bindings="+bindings);
        for (Compound c = cl; c != null; c = c.next)  {         __.ln ("react: c="+c);
            if (c.n == Nucleus.IF) {
                Compound cond = c.args.get(0);
                Compound to_do = c.args.get(1);
                __.ln ("Hit IF, trying cond " + cond);

                // new_Context();
                /* Boolean r = */ cond.satisfy(to_do); // ideally, no dependency on side-effects
                // bindings.pop();
                /*
                if (r) {
                    Compound consequent = c.args.get(1);
                    __.ln ("IF cond="+cond+" was met, now reacting consequent="+ consequent);
                    react (consequent);
                } else {
                    __.ln ("IF cond="+cond+" failed, bindings now "+bindings);
                }
                */
                continue;
            }

        // react() will see something like THIS SOMEONE BE GOOD as a consequence.
        // There may be several candidates for SOMEONE in the bindings.
        // And there may be THIS SOMETHING, etc.
        // So go through the bindings to see values can match "THIS <t>".
        // This makes it something like unify.
        // One way:
        //   substitute <t> for THIS <t> in a copy,
        //   grind recursively through unify/satisfy to exhaustion.
        // Right now, unify just grabs value_of.
        // Another way:
        //   substitute values for THIS <t> after getting
        //      a list of satisfying values back from react(cond)
        // Unify already does THIS-substitions on the fly, so try to use that.
        // maybe a do_on_satisfy(cond,consequent)?
        // This seems to imply a unify (x, consequent), however.

            LinkedList<Compound> ps;
            ps = bindings.peek().get(c.n);                                __.ln ("react: ps="+ps);
            LinkedList<Compound> ua = c.unbound_vars();         __.ln ("react: ua="+ua);
            if (ps == null || ua.isEmpty()) {
                //
                // need to resolve THIS <t> bound vars. How? Straight-up substitution in a copy?
                // Context C = new_Context();      // just to make react return a different context

                /* go through all preds in scope (down through scopes?) */ {
                    Compound x = c.subst_this_copy();   __.ln ("react: after subst_this_copy, x="+x); // add the bound THIS <t> values literally
                    // react (x);                          // cause any bindings needed for this context
                    // adds only first one found (I, when there's also YOU)
                    // so maybe subst_this_copy has to do the bindings?, recursively? calling react again
                    // but not ending up in an infinite recursion? Can it work just by calling add_pred?
                    bindings.peek().add_pred(x);                          __.ln("just added " + c +" in bindings "+bindings);
                    // recursion step here, or ....?
                }
                continue;
            }
            if (!ua.isEmpty()) {                        __.ln ("react: ua="+ua +" so going through ps to bind vars");
              for (Compound cp : ps) {                  __.ln ("react: trying cp="+cp+"with bindings="+bindings);
                if (unify (c, cp, null)) {        __.ln ("react: unified");
                    Context r = react (c.next);         __.ln ("react r=react (c.next, C)="+r);
                }
              }
            }
                else __.ln ("react: ua had no vars to bind");
        }
        Context C = bindings.peek();
                                                                __.ln("react: returning "+C);
                                                                --call_depth;
                                                                __.pop_trace();
        return C;
    }

    private Boolean just_atom() { return args.isEmpty();  }
    private Boolean complex()   { return !args.isEmpty(); }
    private int arity()         { return args.size();     }

    // maybe stop dragging Context C around and refer to top of Context stack?

    private static Boolean unify (Compound T1, Compound T2, Compound to_do) {
                                                    __.push_trace(true);
                                                    if (++call_depth > 30) { __.ln ("call_depth>30, bailing"); return false; }
        Boolean r = false;                          __.ln(""); __.ln ("unify (" + T1 + ", " + T2 + "):");

        if (T1.n == Nucleus.THIS) {
            __.ln ("THIS T1="+T1);
            LinkedList<Compound> sym1 = T1.args;
            assertEquals(1,sym1.size());
            Compound sym = sym1.get(0);

            bindings.peek().put (sym,T2);
            r = true;
        } else
        if (T2.n == Nucleus.THIS) {
            __.ln ("THIS T2="+T2);
            LinkedList<Compound> sym2 = T2.args;
            assertEquals(1,sym2.size());
            Compound sym = sym2.get(0);
            //
            // a = what sym is bound to; sym is a SOMEONE, SOMETHING, etc
            //
            __.ln ("Getting value_of for sym="+sym);
            __.ln ("   ...from bindings="+bindings);
            Compound a = value_of(sym);
            __.ln (" ...and value_of = "+a);
            bindings.peek().put (sym,T1);
            r = true;
        } else
        if (T1.has_unbound_var()) {                         __.ln ("var T1=" + T1);
            bindings.peek().put (T1,T2);
            r = true;
        } else
        if (T2.has_unbound_var()) {                         __.ln ("var T2=" + T2);
            bindings.peek().put (T2,T1);
            r = true;
        } else
        if (T1.just_atom() && T2.just_atom()) {             __.ln ("trying to unify two atoms: " + T1.n + " == " + T2.n);
            r = (T1.n == T2.n);
        } else
        if (T1.complex() && T2.complex()
         && T1.arity() == T2.arity()
         && T1.n == T2.n) {                                 __.ln ("Try unifying " + T1 + " with " + T2);
                Iterator<Compound> T1i = T1.args.iterator();
                Iterator<Compound> T2i = T2.args.iterator();
                r = true;
                while (T1i.hasNext())  {
                    if (!unify (T1i.next(), T2i.next(), to_do)) {  __.ln ("Failed to unify two args to unify");
                        r = false;
                        break;
                    }
                if (r) {
                    __.ln ("all args bound for " + T1 +" and "+ T2);
                }
            }
        }
        // use the bindings created so far:
        if (to_do != null)
            react(to_do);
                                             __.ln ("unify=" + r);
                                            --call_depth;
                                            __.pop_trace();
        return r;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //
    //      for building up predicates and running queries
    //

    private static Compound build(LinkedList<Line> L) {
  
        Compound run_list = null;
        Compound run_list_end_ptr = null;

        for (Line Li : L) {
            if (Li.line.types.size() > 1) {                 __.ln ("build: Looks like unreduced type for lexeme " + Li.line.lexeme);
                break;
            }
            // Just calling TypedTree on an Li.line thats just IF <cond>
            // will apparently throw out the IF.
            __.push_trace(false);
             LinkedList<Compound> npl = TypedTree.pl(Li.line,null);
            __.pop_trace();

  
                                                        __.ln("build: npl=" + npl.toString());
                                                        __.ln("Adding to compound...");
            for (Compound c : npl) {
                if (run_list == null)
                    run_list = c;
                else
                    run_list_end_ptr.next = c;
                run_list_end_ptr = c;
            }

            if (Li.block != null && Li.block.size() > 0) {
                run_list_end_ptr.block = build (Li.block);
            }
        }
        return run_list;
    }


    public static LinkedList<Compound> load_and_run(NestedLines nlp) {

        __.reset();
        __.trace(true);

        __.ln ("----------- load_and_run: clear bindings & run list & preds ---------");
        bindings.clear();

        new_Context();      // prevent empty stack exception

        __.ln ("initial bindings =" + bindings);

        call_depth = 0;

        __.ln ("------------------- load_and_run: get query ---------------------------");

        Line last = nlp.lines.removeLast(); // last line will be query, for now
        __.push_trace(false);
        LinkedList<Compound> npl = TypedTree.pl(last.line,null);
        __.pop_trace();
        __.ln ("npl=" + npl);
        Compound query = npl.get(0);
        __.ln ("query=" + query);

        __.ln ("------------------- load_and_run: build: -----------------------------");

        // process all lines into NSM prolog, adding them to run_list
        // Needs handling of block structure; maybe make it recursive routine
        Compound run_list = build (nlp.lines);
        
        __.ln ("----------- load_and_run: run_list & initial bindings ----------------");

        for (Compound ci = run_list; ci != null; ci = ci.next) {
            __.ln (ci.toString());
        }

        __.ln ("bindings = " + bindings);

        __.ln ("------------ load_and_run: running react on run_list -------------------");

        Context C = react (run_list);
        if (C == null) {    // need to prep free vars first????
            __.ln ("FAILED!!!!!!!!!!!!!");
        } else {
            __.ln ("SUCCESS ! ! ! !");
        }
        __.ln ("Context C=" + C);
        __.ln ("bindings = " + bindings);

        __.ln ("------------------- load_and_run: try running query ------------------");

        __.ln ("Bindings before running query:" + bindings);
        __.ln ("query=" + query);

        Context r = react (query);

        __.ln ("react returned " + r + " possibly for stupid reasons");
        __.ln ("query now=" + query);
        __.ln ("Bindings now: " + bindings);
        __.ln ("~~~~~~~~~~~~~~~~ TO DO ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        __.ln ("I get to do these things:");
        __.ln ("Implement");

        __.ln ("  CAN - done!");
        __.ln ("  NOT - seems OK");
        __.ln ("  multiline rules -- chained IFs inserted or ...");
        __.ln ("  call -- may require meta, using WORDS as a handle on Compounds");

        __.ln ("Figure out where my Prolog comb rule fits into the flow");
        __.ln ("    (Maybe as first-level check on app()/combine() success, intrasentially?");

        __.ln ("Red Queen arithmetic");

        __.ln ("Cleaner handling of unrecognized words");
        __.ln ("Change Title to Query");
        __.ln ("A Run button, to get query output on web page");

        Set<Nucleus> keys = bindings.peek().preds.keySet();
        LinkedList<Compound> whats_true = new LinkedList<Compound>();
        for (Nucleus n : keys) {
            LinkedList<Compound> cl = bindings.peek().preds.get(n);
            whats_true.addAll (cl);
        }
        __.ln(""); __.ln("");
        __.ln ("whats_true="+whats_true);

        return whats_true;
    }
}

