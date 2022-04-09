package com.example.demo;

import org.junit.jupiter.api.Assertions;

import ch.qos.logback.core.joran.conditional.ElseAction;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.junit.jupiter.api.Assertions.assertEquals;

// import java.security.cert.X509CRLEntry; // How'd this get here????????????
import java.util.*;
import java.util.Set;

import javax.swing.text.DefaultStyledDocument.ElementSpec;

import com.example.demo.Order;
import com.example.demo.AUGType;
import com.example.demo.Type;
import com.example.demo.Tree;
import com.example.demo.Splits;
import com.example.demo.Lexicon;

import com.example.demo.Tab;

public class TypedTree implements Comparable<TypedTree> {

    Tree tree; // these are actually 1-for-1 with TypedTree nodes    
    Set<Type> types;

    public int compareTo(TypedTree tt) {
  
            if (tt == this)
                return 0;

            if (tt.types.size() != types.size())
                return 1;

boolean save = Tab.trace(false);
            if (types.containsAll(tt.types)) {
            //    if (tree == tt.tree)        // iffy
Tab.trace (save);
                    return 0;
            }
Tab.trace (save);
            return 1;
    }

    static void lookit (String internal_name, Type t, TypedTree tt, TypedTree arg) {
        Tab.ln ("Type " + internal_name +": " + t);
        Tab.ln ("tt.types=" + Type.ls_str(tt.types));

        if (arg != null) Tab.ln ("arg = " + arg.str());
        else Tab.ln ("arg = null");

        Tab.ln ("tt.tree.atom="+tt.tree.atom);

        if (tt.tree.before != null) Tab.ln ("tt.tree.before = " + tt.tree.before.str());
        else Tab.ln ("tt.tree.before = null");

        if (tt.tree.after != null) Tab.ln ("tt.tree.after = " + tt.tree.after.str());
        else Tab.ln ("tt.tree.after = null");
    }

    static String sentence1 (TypedTree before, TypedTree after, TypedTree arg) {
        Tab.ln ("sentence1:");
        String s ="";
        if (before.types.contains(Lexicon.CondS)) {
            Tab.ln ("before: Conds");
            s += pl1 (after,arg);
            s += " :- ";
            s += pl1 (before.tree.after, arg);
        } else { 
            Tab.ln ("before: not CondS");
            s += pl1 (after, before);
        }
        return s;
    }

    private static Boolean substantive (TypedTree tt) {
        Type t = get_type(tt);
        if (tt != null)
            Tab.ln ("substantive: tt=" + tt.str());
        Boolean r = (t.x == Lexicon.Someone || t.x == Lexicon.Something);
        Tab.ln ("returning " + r);
        return r;
    }

    private static Type get_type(TypedTree tt) {
        assertEquals(1,tt.types.size());

        Type type = null; // shut up "may not be initialized" warning
        for (Type ttx : tt.types) type = ttx; // only way to pull out the singleton?
        return type;
    }

    private static String pl1(TypedTree tt, TypedTree arg) {
        Tab.ln("pl1:");

        String s = "";
        assertNotNull(tt);
        assertNotNull(tt.tree);

        Type type = get_type(tt); // to shut up "may not be initialized" warning
  
        lookit("*** ", type,tt, arg);
        String atom = tt.tree.atom;
        TypedTree before = tt.tree.before;
        TypedTree after = tt.tree.after;

        if (tt.types.contains(Lexicon.S) || tt.types.contains(Lexicon.PredOp)) { lookit("PredOp/S", Lexicon.PredOp,tt, arg);
            s += sentence1 (before, after, arg);                                  Tab.ln ("s = " + s); 
        } else                
        if (tt.types.contains(Lexicon.Cond)) {                           lookit("Cond", Lexicon.Cond,tt, arg);
            s += sentence1 (before,after,arg);                                Tab.ln ("s = " + s);             
        } else
        if (tt.types.contains(Lexicon.Conseq)) {                       lookit("Conseq", Lexicon.Cond,tt, arg);
            s += sentence1 (before,after,arg);                                 Tab.ln ("s = " + s);
        } else
        if (tt.types.contains(Lexicon.PredPred)) {                     lookit("PredPred", Lexicon.PredPred,tt, arg);
            if (tt.tree.atom != null)
                s += tt.tree.atom.toLowerCase();
            if (before != null)
                s += pl1 (before, null);
            if (after != null)
                s += "(" + pl1 (after,arg) + ")";
                                                                              Tab.ln ("s = " + s);
        }  else
        if (type.x == Lexicon.Pred) {
            if (atom == null) {
                Tab.ln ("Pred op null -> complex");
                s += pl1 (before,null) + "(";                                    Tab.ln ("s = " + s);
            } else {
                Tab.ln ("Pred op = " + atom);
                s += atom + "(";                                   Tab.ln ("s = " + s);
            }
            if (arg != null) {
                s += pl1 (arg, null);                                    Tab.ln ("s = " + s);
            }
            if (after != null) {
                if (arg != null) s += ",";                                     Tab.ln ("s = " + s);
                s += pl1 (after, null);                                     Tab.ln ("s = " + s);
            }
            s += ")";                                     Tab.ln ("s = " + s);
        } else
        if (atom == null && type.y == Lexicon.Someone) {
            Tab.ln ("atom == null && type.y == Lexicon.Someone");
            s += pl1 (after, before);                                   Tab.ln ("s = " + s);
        } else
        if (type.y == Lexicon.Pred && after != null) {
            Tab.ln("type.y == Lexicon.Pred && after != null");
            if (atom == null) {
                if (substantive(after)) {
                    s += pl1 (before,after);                                    Tab.ln ("s = " + s);
                } else {
                    s += pl1 (after,before);                                    Tab.ln ("s = " + s);
                }
            } else
                s += pl1 (after,before);                                    Tab.ln ("s = " + s);
        } else {
            Tab.ln("Default");
            Tab.ln ("atom = " + atom);
            if (atom != null)
                s += atom;                                    Tab.ln ("s = " + s);
            if (arg != null)
                s += "(" + pl1 (arg,null) + ")";
        }
        return s;
    }

    public String prolog() {

        Tab.ln ("********************* pl1 traces ****************");
        String s1 = pl1 (this,null);
        Tab.ln ("====== pl1 output: " + s1);
        Tab.ln ("-------------------------------------------------");
        return s1;
    }

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

    static String ls_set(Set<TypedTree> tt_set) {
        Iterator<TypedTree> tti = tt_set.iterator();
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

    public TypedTree (Tree p_tree, Set<Type> p_types) {
        assertNotNull (p_tree);
        assertNotNull (p_types);
        tree = p_tree;
        types = p_types;
    }

    public TypedTree (Tree p_tree, Type t) {
        assertNotNull (p_tree);
        tree = p_tree;
        types = new TreeSet<>();
        types.add (t);
    }

    public static Set<TypedTree>
    app (Order order, TypedTree this_ttree, TypedTree other_ttree) {
        assertNotNull(this_ttree);
        assertNotNull(other_ttree);  Tab.ln ("app: apply " + this_ttree.str() + " to " + other_ttree.str());
        Set<TypedTree> result = new TreeSet<TypedTree>();

        Tab.ln ("Over " + this_ttree.types); Tab.o__();
        
        for (Type t_this : this_ttree.types) {

            TreeSet<Type> lx;

            Tab.ln (t_this.toString() + Type.ls_str (Lexicon.types_for(t_this.toString())) + "...applying...");
            if (t_this.type == AUGType.O) {
                lx = new TreeSet<Type>();                           Tab.ln ("...to  = " + t_this + " with just " + Type.ls_str(lx));
                lx.add (t_this);
            } else {
                lx = Lexicon.types_for(t_this.toString());          Tab.ln ("...to  = " + t_this + " including " + Type.ls_str(lx));
            }

            for (Type t_this_x : lx) 
//          Type t_this_x = t_this;
                { // for all Oxy in this ttree

                Tab.ln ("...to these: " + Type.ls_str (other_ttree.types)); Tab.o__();
                for (Type t_other : other_ttree.types) {

                    // Type t_other_y = t_other;
/*
                    TreeSet<Type> ly;
                    
                    if (t_other.type == AUGType.O) {
                        ly = new TreeSet<Type>();
                        boolean ok = ly.add (t_other);
                        assertTrue(ok);
                        assertTrue(ly.contains(t_other));
                        Tab.ln ("Listing out ly:");
                        for (Type xxx : ly) {
                            Tab.ln ("...in ly: " + xxx);
                        }                           
                                                                    Tab.ln ("...to  = " + t_other + " with just " + Type.ls_str(ly));
                    } else {
                        ly = Lexicon.types_for(t_other.toString()); Tab.ln ("...to  = " + t_other + " including " + Type.ls_str(ly));
                    }
                    
                                                                    Tab.o__();
                    for (Type t_other_y : ly)
*/                  Type t_other_y = t_other;
                                            {
                                                                    Tab.ln ("t_other_y loop");
                        Type r = t_this_x.fxy (t_other_y);
                        if (r == null) {                            Tab.ln ("...exiting");
                            continue;
                        }
                        Tab.o__();
/*
                        Type x = t_this_x.x;
                        if (x == null) {
                            Tab.ln ("But " + t_this_x + " x = null, t_other =" + t_other);
                            x = t_other_y.x; // Try it --------------------------------------------
                        }
*/                      
                        Type x = t_this_x;                                                                      Tab.ln ("x = " + x.toString());
                        
                        TypedTree new_before = new TypedTree (this_ttree.tree,  Type.of (AUGType.O, x, r) );    Tab.ln ("new_before=" + new_before.str());
                        TypedTree new_after =  new TypedTree (other_ttree.tree, x );                            Tab.ln ("new_after=" + new_after.str());

                        Set<Type> ls_type2 = new TreeSet<>();
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
         Tab.ln ("app()"); Tab.ln (" =" + TypedTree.ls_set(result));
        return result;
    }

    public static Set<TypedTree> combine (TypedTree one, TypedTree the_other) {
                 Tab.ln ("combine(" + one.str() + ", " + the_other.str() + ")");
        Set<TypedTree> tl = new TreeSet<TypedTree>();
        Set<TypedTree> tb = app (Order.BEFORE, one,       the_other );
        Set<TypedTree> ta = app (Order.AFTER,  the_other, one       );
        tl.addAll(tb);
        tl.addAll(ta);
                 Tab.ln ("combine():"); Tab.ln (" =" + ls_set(tl));
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