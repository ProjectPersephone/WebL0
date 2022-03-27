package com.example.demo;

import org.junit.jupiter.api.Assertions;

import ch.qos.logback.core.joran.conditional.ElseAction;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.junit.jupiter.api.Assertions.assertEquals;

// import java.security.cert.X509CRLEntry; // How'd this get here????????????
import java.util.*;

import com.example.demo.Order;
import com.example.demo.AUGType;
import com.example.demo.Type;
import com.example.demo.Tree;
import com.example.demo.Splits;
import com.example.demo.Lexicon;

import com.example.demo.Tab;

public class TypedTree {

    Tree tree; // these are actually 1-for-1 with TypedTree nodes
    LinkedList<Type> types;
/*
    I LIVE : S
        I : SOMEONE
        LIVE : O(SOMEONE->S)
    => s(live(i))

    I IS GOOD : S
        I : O(IS->S)
        IS GOOD : IS
            IS : O(GOOD->IS)
            GOOD : GOOD
    => s(is(i,good))

    I SAY I LIVE : S
        I : O(SAY->S)
        SAY I LIVE : SAY
            SAY : O(S->SAY)
            I LIVE : S
                I : SOMEONE
                LIVE : O(SOMEONE->S)
    => s(say(i,s(live(i))))
    */

    public String prolog () {
        TypedTree f,x;
        if (tree.order == Order.NEITHER)
            return (tree.atom);
        if (tree.order == Order.BEFORE) {
            x = tree.before;
            f = tree.after;
        } else {
            x = tree.after;
            f = tree.before;
        }
        return "f:" + f.prolog() + "(x:" + x.prolog() + ")";
    }
/*
    public String prolog() {
        String s = "";
//        for (Type t : types) {
//            s += "\nprolog: type=" + t + '\n';
//            s += "   " + tree.str();
            if (tree.order == Order.NEITHER) {
                s += tree.atom.toLowerCase();
            // should be a predicate-based in-order traversal
            // not a replication of the original order
            } else if (tree.order == Order.BEFORE) 
                    s = // tree.order.toString() + ": " + 
                        tree.before.prolog() + "(" + tree.after.prolog()  + ")";
                else 
                    s = tree.after.prolog()  + "(" + tree.before.prolog() + ")";
//        }

    //    s += ".";

        return s;
    }
*/
    public String str() {
        String s = tree.str() + "[";
        assertNotNull (types);
        Iterator<Type> ti = types.iterator();
        while (ti.hasNext()) {
            Type t = ti.next();
            s += t.toString();
            if (ti.hasNext()) s += " ";
        }
        return s + "]";
    }

    // some ghastly printers that should go away with a transition
    // to the TrellisCache.

    public static String ls_str(LinkedList<TypedTree> tt_ls) {
        Iterator<TypedTree> tti = tt_ls.iterator();
        String s = "[";
        while (tti.hasNext()) {
            s += tti.next().str();
            if (tti.hasNext())
                s += " ";
        }
        return s + "]";
    }

    public static String ls_ls_str(LinkedList<LinkedList<TypedTree>> tt_ll) {
        String s = "[";
        Iterator<LinkedList<TypedTree>> ll_it = tt_ll.iterator();
        while (ll_it.hasNext()) {
            s += ls_str(ll_it.next());
            if (ll_it.hasNext())
                s += " ";
        }
        return s + "]";
    }

    public static String ls_ls_ls_str(LinkedList<LinkedList<LinkedList<TypedTree>>> tt_lll) {
        String s = "[";
        Iterator<LinkedList<LinkedList<TypedTree>>> lll_it = tt_lll.iterator();
        while (lll_it.hasNext()) {
            s += ls_ls_str(lll_it.next());
            if (lll_it.hasNext())
                s += " ";
        }
        return s + "]";
    }

    public TypedTree (Tree p_tree, LinkedList<Type> p_types) {
        assertNotNull (p_tree);
        assertNotNull (p_types);
        tree = p_tree;
        types = p_types;
    }

    public TypedTree (Tree p_tree, Type t) {
        assertNotNull (p_tree);
        tree = p_tree;
        types = new LinkedList<Type>();
        types.add (t);
    }

    public static LinkedList<TypedTree>
    app (Order order, TypedTree this_ttree, TypedTree other_ttree) {
        assertNotNull(this_ttree);
        assertNotNull(other_ttree);  Tab.ln ("app (" + order + ", " + this_ttree.str() + ", " + other_ttree.str() + ")");
        LinkedList<TypedTree> result = new LinkedList<TypedTree>();

        Tab.ln ("Over " + this_ttree.types); Tab.o__();
        
        for (Type t_this : this_ttree.types) {

            Tab.ln (t_this.toString() + Type.ls_str (Lexicon.types_for(t_this.toString())) + "...applied...");
            // if (t_this.type == AUGType.O) // comment out because primes are now types
                
                { // for all Oxy in this ttree

                Tab.ln ("...to these: " + Type.ls_str (other_ttree.types)); Tab.o__();
                for (Type t_other : other_ttree.types) {

                    // Type t_other_y = t_other;

                    LinkedList<Type> ly;
                    
                    if (t_other.type == AUGType.O) {
                        ly = new LinkedList<Type>();
                        ly.add (t_other);                           Tab.ln ("...to  = " + t_other + " with just " + Type.ls_str(ly));
                    } else {
                        ly = Lexicon.types_for(t_other.toString()); Tab.ln ("...to  = " + t_other + " including " + Type.ls_str(ly));
                    }
                    
                                                                    Tab.o__();
                    for (Type t_other_y : ly) {
                        Type r = t_this.fxy (t_other_y);
                        if (r == null)
                            continue;
                        Tab.o__();
 // with primes as types, t_this.x can be null! BAD!!!  ********************************************
                        Type x = t_this.x;
                        if (x == null) {
                            Tab.ln ("But " + t_this + " x = null, t_other =" + t_other);
                            x = t_other_y.x; // Try it --------------------------------------------
                        }
                                                                                                                Tab.ln ("x = " + x.toString());
                        TypedTree new_before = new TypedTree (this_ttree.tree,  Type.of (AUGType.O, x, r) );    Tab.ln ("new_before=" + new_before.str());
                        TypedTree new_after =  new TypedTree (other_ttree.tree, x );                            Tab.ln ("new_after=" + new_after.str());

                        LinkedList<Type> ls_type2 = new LinkedList<Type>();
                        ls_type2.add(r);
                        TypedTree new_tt =new TypedTree (
                                            new Tree (order, new_before, new_after),
                                            ls_type2
                                        );
                                                                                                                Tab.ln ("-Adding new_tt = " + new_tt.str());
                        result.add (new_tt);
                        Tab.__o();
                    }
                    Tab.__o();
                }
                 Tab.__o();
            }
        }  Tab.__o();
         Tab.ln ("app()"); Tab.ln (" =" + ls_str(result));
        return result;
    }

    public static LinkedList<TypedTree> combine (TypedTree one, TypedTree the_other) {
                 Tab.ln ("combine(" + one.str() + ", " + the_other.str() + ")");
        LinkedList<TypedTree> tl = new LinkedList<TypedTree>();
        LinkedList<TypedTree> tb = app (Order.BEFORE, one,       the_other );
        LinkedList<TypedTree> ta = app (Order.AFTER,  the_other, one       );
        tl.addAll(tb);
        tl.addAll(ta);
                 Tab.ln ("combine():"); Tab.ln (" =" + ls_str(tl));
        return tl;
    }

// This should probably go into tests--does the result match the cached result?

    public static LinkedList<TypedTree>
    typed_trees(LinkedList<TypedTree> S) {
      assertNotNull(S);          Tab.ln ("typed_trees (" + ls_str(S) + "):");
      LinkedList<TypedTree> r;
      if (S.size() == 1) {
            r = S;       // or a copy of S? any difference?
        }
      else
        {
        Splits splits = new Splits (S);
        assertNotNull(splits);
        assertNotNull(splits.all_splits);

        r = new LinkedList<TypedTree>();

         Tab.ln ("-loop on " + splits.str()); Tab.o__();
        for (Split split : splits.all_splits) {

            LinkedList<TypedTree> before_tts = typed_trees (split.before);
            LinkedList<TypedTree> after_tts  = typed_trees (split.after);

             Tab.ln (" -loop on" + ls_str(before_tts)); Tab.o__();
            for (TypedTree before : before_tts) {
                 Tab.ln ("-loop on " + ls_str(after_tts)); Tab.o__();
                for (TypedTree after : after_tts) {
                    r.addAll (combine (before, after));
                }  Tab.__o();
            }  Tab.__o();
        }
         Tab.__o();
      }                  Tab.ln ("typed_trees():"); Tab.ln (" =" + ls_str(r));
      return r;
    }
}