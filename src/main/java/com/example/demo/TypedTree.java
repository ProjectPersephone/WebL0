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
import com.example.demo.Nucleus;
import com.example.demo.Valence;
import com.example.demo.Tree;
import com.example.demo.Splits;
import com.example.demo.Atom;
import com.example.demo.Compound;

import com.example.demo.Tab;

public class TypedTree implements Comparable<TypedTree> {

    Tree tree; // these are actually 1-for-1 with TypedTree nodes    
    Set<Valence> types;

    public int compareTo(TypedTree tt) {
  
            if (tt == this)
                return 0;

            if (tt.types.size() != types.size())
                return 1;

    Tab.push_trace(false);
            if (types.containsAll(tt.types)) {
            //    if (tree == tt.tree)        // iffy
    Tab.pop_trace();
                    return 0;
            }
            return 1;
    }

    static void lookit (String internal_name, Valence t, TypedTree tt, TypedTree arg) {
        Tab.ln ("Type " + internal_name +": " + t);
        Tab.ln ("tt.types=" + Valence.ls_str(tt.types));

        if (arg != null) Tab.ln ("arg = " + arg.str());
        else Tab.ln ("arg = null");

        Tab.ln ("tt.tree.lexeme="+tt.tree.lexeme);

        if (tt.tree.before != null) Tab.ln ("tt.tree.before = " + tt.tree.before.str());
        else Tab.ln ("tt.tree.before = null");

        if (tt.tree.after != null) Tab.ln ("tt.tree.after = " + tt.tree.after.str());
        else Tab.ln ("tt.tree.after = null");
    }

    private static Valence get_type(TypedTree tt) {
        assertEquals(1,tt.types.size());

        Valence type = null; // shut up "may not be initialized" warning
        for (Valence ttx : tt.types) type = ttx; // only way to pull out the singleton?
        return type;
    }

    private static Boolean substantive (TypedTree tt) {
        Valence v = get_type(tt);
        if (tt != null)
            Tab.ln ("substantive: tt=" + tt.str());
        Boolean r = (v.x == Atom.Someone || v.x == Atom.Something);
        Tab.ln ("returning " + r);
        return r;
    }


    static LinkedList<Compound> start_list(LinkedList<Compound> t)       { return new LinkedList<Compound>(); }
    static LinkedList<Compound> end_list(LinkedList<Compound> npl)         { return npl; }
    static LinkedList<Compound>
    add_ls_arg(LinkedList<Compound> npl, LinkedList<Compound> new_one) {
        npl.addAll(new_one);
        return npl;
    }
    static LinkedList<Compound>
    add_ls_arg(LinkedList<Compound> npl, String lexeme) {
        Nucleus tl = Nucleus.valueOf(lexeme);
        Compound np = new Compound(tl);
        npl.add(np);
        return npl;
    }
    static LinkedList<Compound> listify(LinkedList<Compound> npl, LinkedList<Compound> arg) {
         LinkedList<Compound> new_npl = start_list(npl); // arg ignored
         add_ls_arg (new_npl,arg);
         return end_list(new_npl);
    }

    static LinkedList<Compound>
    sentence (TypedTree before, TypedTree after, TypedTree arg) {
        Tab.ln ("sentence:");
        LinkedList<Compound> s = new LinkedList<Compound>();
        if (before.types.contains(Atom.CondS)) {
            Tab.ln ("before: Conds");
            LinkedList<Compound> if_constr = new LinkedList<Compound>();
            Compound an_if = new Compound(Nucleus.IF);
            if_constr.add(an_if);
            an_if.args = add_ls_arg (an_if.args, pl (before.tree.after, arg));
            an_if.args = add_ls_arg (an_if.args, pl (after,arg));
            s = if_constr;
        } else { 
            Tab.ln ("before: not CondS");
            s = add_ls_arg (s, pl (after, before));
        }
        return s;
    }

    // probably redundant code in here, needs analysis and trimming
    private static LinkedList<Compound> pl(TypedTree tt, TypedTree arg) {
        Tab.ln("pl:");

        LinkedList<Compound> s = new LinkedList<Compound>();
        assertNotNull(tt);
        assertNotNull(tt.tree);

        Valence type = get_type(tt);
  
        lookit("*** ", type,tt, arg);
        String lexeme = tt.tree.lexeme;
        TypedTree before = tt.tree.before;
        TypedTree after = tt.tree.after;

        if (tt.types.contains(Atom.S)
         || tt.types.contains(Atom.PredOp)
         || tt.types.contains(Atom.Cond)
         || tt.types.contains(Atom.Conseq)) {                       lookit("<*>", Atom.PredOp,tt, arg);
            s = add_ls_arg (s, sentence (before, after, arg));             Tab.ln ("s = " + s); 
        }  else
        if (tt.types.contains(Atom.PredPred)) {                     lookit("PredPred", Atom.PredPred,tt, arg);
            if (lexeme != null)   { s = add_ls_arg(s, tt.tree.lexeme);          Tab.ln ("(1)s = " + s);
            }
            if (before != null) { s = add_ls_arg(s, pl (before, null));     Tab.ln ("(2)s = " + s);
            }
            if (after != null)  { s = listify(s, pl (after,arg));     Tab.ln ("(3)s = " + s);
            }
        }  else
        if (type.x == Atom.Pred) {
            if (lexeme == null) {
                Tab.ln ("Pred op null -> complex");
                s = add_ls_arg(s, pl (before,null));
            } else {
                Tab.ln ("Pred op = " + lexeme);
                s = add_ls_arg(s, lexeme);
            }
            LinkedList<Compound> s1 = start_list(s);                            Tab.ln ("(4)s1 = " + s);
            if (arg != null) {
                s1 = add_ls_arg(s1, pl (arg, null));                                    Tab.ln ("(6)s1 = " + s);
            }
            if (after != null) {
                if (arg != null) { // s += ",";                             Tab.ln ("(7)s = " + s);
                }
                s1= add_ls_arg(s1, pl (after, null));                                  Tab.ln ("(8)s1 = " + s);
            }
            s.get(0).args.addAll(s1);
            s = end_list(s);                                                   Tab.ln ("(9)s = " + s);
        }  else
        if (tt.types.contains(Atom.Subst)) {
            Tab.ln ("Subst: <stub>");
            if (lexeme != null) {
                s = add_ls_arg(s, lexeme);                                              Tab.ln ("s = " + s);
            } else {
                if (substantive(after)) { s = add_ls_arg(s, pl (before,after));      Tab.ln ("s = " + s);
                } else {                  s = add_ls_arg(s, pl (after,before));      Tab.ln ("s = " + s);
                }
            }
        } else
        if (lexeme == null && type.y == Atom.Someone) {    // SEE IF THIS IS ACTUALLY USED
            Tab.ln ("lexeme == null && type.y == Lexicon.Someone");
            s = add_ls_arg(s, pl (after, before));                                   Tab.ln ("s = " + s);
        } else
        if (type.y == Atom.Pred && after != null) {      // was for x + good/bad / good/bad + x
            Tab.ln("type.y == Lexicon.Pred && after != null");
            if (lexeme == null) {
                if (substantive(after)) {
                    s = add_ls_arg(s, pl (before,after));                                    Tab.ln ("s = " + s);
                } else {
                    s = add_ls_arg(s, pl (after,before));                                    Tab.ln ("s = " + s);
                }
            } else
                s = add_ls_arg(s, pl (after,before));                                    Tab.ln ("s = " + s);
        } else {
            Tab.ln("Default:");  Tab.ln ("lexeme = " + lexeme);
            if (lexeme != null) {
                s = add_ls_arg(s, lexeme);                                    Tab.ln ("(d1)s = " + s);
            }
            if (arg != null) {
                LinkedList<Compound> s1 = start_list(s);
                s1 = pl (arg,null);
                s1 = end_list(s1);                          
                s.get(0).args.addAll(s1);                               Tab.ln ("(d2)s = " + s);
            }
        }
        return s;
    }


    public String prolog() {
        Tab.push_trace(true);
        if (this.types.size() > 1) {
            Tab.ln ("Looks like unreduced (maybe single) lexeme " + this.tree.lexeme);
            return this.tree.lexeme;
        }
        Tab.pop_trace();

        Tab.push_trace(false);
        Tab.ln ("-------------------------------------------------");
        LinkedList<Compound> npl = pl(this,null);
        String pps = Compound.pp(npl);
        Tab.pop_trace();

        Tab.ln ("%%%%%%%%%%% PP output %%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        Tab.ln (pps);
        return pps;
    }

    private static void nested_pp_helper(LinkedList<Line> L) {
        for (Line Li : L) {
            Tab.ln ("nested_pp_helper: Li.line = " + Li.line.str());
            if (Li.line.types.size() > 1) {
                Tab.ln ("Looks like unreduced type for lexeme " + Li.line.tree.lexeme);
                return;
            }
            Tab.push_trace(false);
            LinkedList<Compound> npl = pl(Li.line,null);
            Tab.pop_trace();
            Tab.ln(npl.toString());
            if (Li.block != null && Li.block.size() > 0) {
                nested_pp_helper (Li.block);
            }
        }
    }
    public static void nested_pp(NestedLines nlp) {
        Tab.ln ("nested_pp:");
        nested_pp_helper (nlp.lines);
    }
    

    public String str() {
        String s = tree.str() + "[";
        assertNotNull (types);
        Iterator<Valence> ti = types.iterator();
        while (ti.hasNext()) {
            Valence t = ti.next();
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

    public TypedTree (Tree tree, Set<Valence> types) {
        assertNotNull (tree);
        assertNotNull (types);
        this.tree = tree;
        this.types = types;
    }

    public TypedTree (Tree tree, Valence t) {
        assertNotNull (tree);
        this.tree = tree;
        types = new TreeSet<>();
        types.add (t);
    }

    public static Set<TypedTree>
    app (Order order, TypedTree this_ttree, TypedTree other_ttree) {
        assertNotNull(this_ttree);
        assertNotNull(other_ttree);  Tab.ln ("app: apply " + this_ttree.str() + " to " + other_ttree.str());
        Set<TypedTree> result = new TreeSet<TypedTree>();

        Tab.ln ("Over " + this_ttree.types); Tab.o__();
        
        for (Valence t_this : this_ttree.types) {

            TreeSet<Valence> lx;

            Tab.ln (t_this.toString() + Valence.ls_str (Atom.valences_for(t_this.toString())) + "...applying...");
            if (t_this.n == Nucleus.O_) {
                lx = new TreeSet<Valence>();                           Tab.ln ("...to  = " + t_this + " with just " + Valence.ls_str(lx));
                lx.add (t_this);
            } else {
                lx = Atom.valences_for(t_this.toString());          Tab.ln ("...to  = " + t_this + " including " + Valence.ls_str(lx));
            }

            for (Valence t_this_x : lx) 
//          Valence t_this_x = t_this;
                { // for all Oxy in this ttree

                Tab.ln ("...to these: " + Valence.ls_str (other_ttree.types)); Tab.o__();
                for (Valence t_other : other_ttree.types) {
                  Valence t_other_y = t_other;
                        {
                                                                    Tab.ln ("t_other_y loop");
                        Valence r = t_this_x.fxy (t_other_y);
                        if (r == null) {                            Tab.ln ("...exiting");
                            continue;
                        }
                        Tab.o__();
                
                        Valence x = t_this_x;                                                                      Tab.ln ("x = " + x.toString());
                        
                        TypedTree new_before = new TypedTree (this_ttree.tree,  Valence.of (Nucleus.O_, x, r) );    Tab.ln ("new_before=" + new_before.str());
                        TypedTree new_after =  new TypedTree (other_ttree.tree, x );                            Tab.ln ("new_after=" + new_after.str());

                        Set<Valence> ls_type2 = new TreeSet<>();
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