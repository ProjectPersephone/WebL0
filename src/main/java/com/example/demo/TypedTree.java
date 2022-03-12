package com.example.demo;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import com.example.demo.Order;
import com.example.demo.AUGType;
import com.example.demo.Type;
import com.example.demo.Tree;
import com.example.demo.Splits;

import com.example.demo.Tab;

public class TypedTree {
    Tree tree; 
    LinkedList<Type> types;

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

    public static String ls_str(LinkedList<TypedTree> tt_ls) {
        Iterator<TypedTree> tti = tt_ls.iterator();
        String s = "[";
        while (tti.hasNext()) {
            s += tti.next().toString();
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

// This will get ungainly if we add more types than just T, S, and O.

    public static LinkedList<TypedTree>
    app (Order order, TypedTree this_ttree, TypedTree other_ttree) {
        assertNotNull(this_ttree);
        assertNotNull(other_ttree); //- Tab.ln ("app (" + order + ", " + this_ttree.str() + ", " + other_ttree.str() + ")");
        LinkedList<TypedTree> result = new LinkedList<TypedTree>();
        Iterator<Type> ti_this = this_ttree.types.iterator(); //- Tab.ln ("-loop on " + ls_str(result)); Tab.o__();
        while (ti_this.hasNext()) {
            Type t_this = ti_this.next();
            assertNotNull(t_this);  //-Tab.ln ("-Looking at t_this = " + t_this.str());
            if (t_this.type == AUGType.O) { // for all Oxy in this ttree
                Iterator<Type> ti_other = other_ttree.types.iterator();
                                    //- Tab.ln ("-loop on " + Type.ls_str (other_ttree.types)); Tab.o__();
                while (ti_other.hasNext()) {
                    Type t_other = ti_other.next(); //-Tab.ln ("-Looking at t_other = " + t_other.str());
                    Type r = t_this.fxy (t_other);
                    if (r != null) {
                        TypedTree new_before = new TypedTree (
                            this_ttree.tree,
                            Type.of (AUGType.O, t_this.x, r) );
                                    //- Tab.ln ("new_before=" + new_before.str());
                        TypedTree new_after = new TypedTree (
                            other_ttree.tree,
                            t_this.x );
                                    //- Tab.ln ("new_after=" + new_after.str());
                        LinkedList<Type> ls_type2 = new LinkedList<Type>();
                        ls_type2.add(r);
                        TypedTree new_tt =new TypedTree (
                                            new Tree (order, new_before, new_after),
                                            ls_type2
                                        );
                        //- Tab.ln ("-Adding new_tt = " + new_tt.str());
                        result.add (new_tt);
                    }
                }
                //- Tab.__o();
            }
        } //- Tab.__o();
        //- Tab.ln ("app()"); Tab.ln (" =" + ls_str(result));
        return result;
    }

    public static LinkedList<TypedTree> combine (TypedTree one, TypedTree the_other) {
                //- Tab.ln ("combine(" + one.str() + ", " + the_other.str() + ")");
        LinkedList<TypedTree> tl = new LinkedList<TypedTree>();
        LinkedList<TypedTree> tb = app (Order.BEFORE, one,       the_other );
        LinkedList<TypedTree> ta = app (Order.AFTER,  the_other, one       );
        tl.addAll(tb);
        tl.addAll(ta);
                //- Tab.ln ("combine():"); Tab.ln (" =" + ls_str(tl));
        return tl;
    }

// This should probably go into tests--does the result match the cached result?

    public static LinkedList<TypedTree>
    typed_trees(LinkedList<TypedTree> S) {
      assertNotNull(S);         //- Tab.ln ("typed_trees (" + ls_str(S) + "):");
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

        Iterator<Split> li = splits.all_splits.iterator(); //- Tab.ln ("-loop on " + splits.str());
        //- Tab.o__();
        while (li.hasNext()) {
            Split split = li.next();
            assertNotNull (split.before);
            assertNotNull (split.after);
            LinkedList<TypedTree> before_tts = typed_trees (split.before);
            LinkedList<TypedTree> after_tts  = typed_trees (split.after);

            Iterator<TypedTree> bli = before_tts.iterator(); //- Tab.ln (" -loop on" + ls_str(before_tts));
            //- Tab.o__();
            while (bli.hasNext()) {
                TypedTree before = bli.next(); //- Tab.ln ("-before = " + before.str());

                Iterator<TypedTree> ali = after_tts.iterator(); //- Tab.ln ("-loop on " + ls_str(after_tts));
                //- Tab.o__();
                while (ali.hasNext()) {
                    TypedTree after = ali.next();   //- Tab.ln ("-after =" + after.str());
                    LinkedList<TypedTree> x = combine (before, after); //- Tab.ln ("-adding " + ls_str(x));
                    r.addAll (x);
                } //- Tab.__o();
            } //- Tab.__o();
        }
        //- Tab.__o();
      }                 //- Tab.ln ("typed_trees():"); Tab.ln (" =" + ls_str(r));
      return r;
    }
}