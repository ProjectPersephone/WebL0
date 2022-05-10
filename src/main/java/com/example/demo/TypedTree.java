package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

// import java.security.cert.X509CRLEntry; // How'd this get here????????????
import java.util.*;
import java.util.Set;

import com.example.demo.Order;
import com.example.demo.Nucleus;
import com.example.demo.Valence;
import com.example.demo.Splits;
import com.example.demo.Atom;
import com.example.demo.Compound;

import com.example.demo.__;

public class TypedTree implements Comparable<TypedTree> {

    TypedTree parent;

    void set_parent (TypedTree new_par) {
        assert (this != new_par);
        parent = new_par;
    }

    //-- Tree tree; // these are actually 1-for-1 with TypedTree nodes    
    Set<Valence> types;

    Order order; // order of application that yielded this tree node

    String lexeme; // presumptuous, but is it morpheme, morph, ...? Ultimately, phone? ???
                    // https://en.wikipedia.org/wiki/Emic_unit

    TypedTree before; // forward application
    TypedTree after;  // backward application 

    public TypedTree twin() {
        if (parent == null) return null;
        if (this == parent.before) return parent.after;
        if (this == parent.after) return parent.before;
        return null;        // should actually raise exception
    }

    public int compareTo(TypedTree tt) {
  
            if (tt == this)
                return 0;

            if (tt.types.size() != types.size())
                return 1;

    __.push_trace(false);
            if (types.containsAll(tt.types)) {
            //    if (tree == tt.tree)        // iffy
    __.pop_trace();
                    return 0;
            }
            return 1;
    }

    static void lookit (String internal_name, Valence t, TypedTree tt, TypedTree arg) {
        __.ln ("Type " + internal_name +": " + t);
        __.ln ("tt.types=" + Valence.ls_str(tt.types));

        if (arg != null) __.ln ("arg = " + arg.str());
        else __.ln ("arg = null");

        __.ln ("tt.lexeme="+tt.lexeme);

        if (tt.before != null) __.ln ("tt.before = " + tt.before.str());
        else __.ln ("tt.before = null");

        if (tt.after != null) __.ln ("tt.after = " + tt.after.str());
        else __.ln ("tt.after = null");
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
            __.ln ("substantive: tt=" + tt.str());
        Boolean r = (v.x == Atom.Someone || v.x == Atom.Something);
        __.ln ("returning " + r);
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
    static LinkedList<Compound> funkify(String functor, LinkedList<Compound> arg) {
        LinkedList<Compound> s = start_list(null); // 

        __.ln ("funkify: functor = " + functor + " arg = " + arg);
        add_ls_arg (s, functor);
        if (arg != null)
            s.get(0).args.addAll(arg);

        __.ln ("funkify: returning " + s);
        return s;
    }

    static LinkedList<Compound> funkify2(String functor, LinkedList<Compound> a1, LinkedList<Compound> a2) {
        LinkedList<Compound> s = start_list(null); // 
        __.ln ("funkify: functor = " + functor + " a1 = " + a1 + " a2 = " + a2);
        add_ls_arg (s, functor);
        s.get(0).args.addAll(a1);
        s.get(0).args.addAll(a2);

        return s;
    }

    static LinkedList<Compound>
    sentence (TypedTree before, TypedTree after, TypedTree arg) {
        __.ln ("sentence:");
        LinkedList<Compound> s = new LinkedList<Compound>();
        if (before != null && before.types.contains(Atom.CondS)) {
            __.ln ("before: Conds");
            LinkedList<Compound> if_constr = new LinkedList<Compound>();
            Compound an_if = new Compound(Nucleus.IF);
            if_constr.add(an_if);
            an_if.args = add_ls_arg (an_if.args, pl (before.after, arg));
            an_if.args = add_ls_arg (an_if.args, pl (after,arg));
            s = if_constr;
        } else { 
            __.ln ("before: not CondS");
            if (after  != null) __.ln ("   after="+after.str()); else __.ln ("   after=null");
            if (before != null) __.ln ("   before="+before.str()); else __.ln ("   before=null");
            if (arg    != null) __.ln ("   arg="+arg.str()); else __.ln ("   arg=null");
            s = add_ls_arg (s, pl (after, before)); // 
        }
        return s;
    }

    // probably redundant code in here, needs analysis and trimming
    public static LinkedList<Compound>
    pl(TypedTree tt, TypedTree arg) {
        __.ln("pl:");

        LinkedList<Compound> s = new LinkedList<Compound>();
        assertNotNull(tt);


        Valence type = get_type(tt);
  
        lookit("*** ", type,tt, arg);
        String lexeme = tt.lexeme;
        TypedTree before = tt.before;
        TypedTree after = tt.after;

        if (tt.types.contains(Atom.S)
         || tt.types.contains(Atom.Cond)
         || tt.types.contains(Atom.Conseq)) {                       lookit("<*>", Atom.S,tt, arg);
            s = add_ls_arg (s, sentence (before, after, arg));
            __.ln ("...s = " + s); 
        } else
        if (tt.types.contains(Atom.Pred)
         || tt.types.contains(Atom.ModPred)) {                        lookit("Pred/ModPred", Atom.Pred,tt, arg);
            if (lexeme != null) {
                String functor = lexeme;
                LinkedList<Compound> a = null;
                if (arg != null)
                     a = pl (arg, null);
                s = funkify (functor, a);         __.ln ("s=funkify("+lexeme+","+arg+")="+s);
            } else {
                String functor = after.lexeme;
                if (arg != null)
                    s = funkify (functor, pl (arg, null));
                functor = before.lexeme;
                s = funkify (functor, s);
            }
            __.ln ("" + Atom.Pred + "/" + Atom.ModPred + ": s="+s);
        } else 
        if (tt.types.contains(Atom.PredS)) {
            String functor;
            if (after != null) {
                functor = after.lexeme;
                LinkedList<Compound> an_arg = pl(arg,null);
                if (functor != null)
                    s = funkify (functor, pl(arg,null));
                else
                    s = an_arg;
                functor = before.lexeme;
                s = funkify (functor, s);
            }
                else s = funkify (lexeme, pl(arg, null));
        } else
        if (tt.types.contains(Atom.Subst)) {
            __.ln ("Subst: ");
            if (lexeme != null) {
                s = add_ls_arg(s, lexeme);                                              __.ln ("s = " + s);
            } else {
                if (substantive(after)) { s = add_ls_arg(s, pl (before,after));      __.ln ("s = " + s);
                } else {                  s = add_ls_arg(s, pl (after,before));      __.ln ("s = " + s);
                }
            }
            __.ln ("Subst: s=" + s);
        } else
        if (lexeme == null && (type.y == Atom.Someone || type.y == Atom.Something)) {
            __.ln ("lexeme == null && type.y == Lexicon.Someone");
            s = add_ls_arg(s, pl (after, before));                                   __.ln ("s = " + s);
        } else
        if (type.y == Atom.Pred && after != null) {      // was for x + good/bad / good/bad + x
            __.ln("type.y == Lexicon.Pred && after != null");
            if (lexeme == null) {
                if (substantive(after)) {
                    s = add_ls_arg(s, pl (before,after));                                    __.ln ("s = " + s);
                } else {
                    s = add_ls_arg(s, pl (after,before));                                    __.ln ("s = " + s);
                }
            } else
                s = add_ls_arg(s, pl (after,before));                                    __.ln ("s = " + s);
        } else {
            __.ln("Default:");     lookit("<*>", Atom.S,tt, arg);

            // s = add_ls_arg (s, sentence (before, after, arg)) // could be infinite loop???

            if (arg != null) {
                if (before != null) {
                    String functor = before.lexeme;
                    LinkedList<Compound> a1 = pl(arg, null);
                    LinkedList<Compound> a2 = pl (after, null);   // this arg not making it through
                    s = funkify2 (functor, a1, a2);                   __.ln ("Default: arg and before not null");
                                                                __.ln ("arg=" + arg.str() + " before="
                                                                            + before.str()
                                                                            + " after =" + after.str());
                }
                else {
                    String functor = lexeme;
                    LinkedList<Compound> a = pl (arg, null);
                    s = funkify (functor, a);                   __.ln ("Default: arg not null before is null case");
                }
            }
            else {
                s = add_ls_arg (s, lexeme);                     __.ln ("Default: are null case");
            }
            __.ln ("Default: returning s=" + s);
        }
        return s;
    }

    // need distinction between one-place and two-place preds
    public LinkedList<Compound> pl2(TypedTree arg) {
        LinkedList<Compound> s = new LinkedList<Compound>();

        Valence type = get_type(this);
        Valence before_type = get_type(before);

        if (type == Atom.S || type == Atom.Conseq || type == Atom.Cond) {
            if (before_type == Atom.Subst) {
                s.addAll(after.pl2 (before));
            } else
            if (before_type == Atom.CondS) {
                s.addAll(before.pl2 (null));
                s.addAll(after.pl2 (null));
            }
        } else
        if (type == Atom.Subst) {
            if (lexeme == null) { // modified
                s.addAll(after.pl2 (before));
            } else {                  // raw
                s.add(new Compound(type.n));
            }
        } else
        if (type == Atom.PredS) {
            if (lexeme == null) { // complex
                s.addAll (before.pl2 (after));
            } else {
                s.add (new Compound(type.n));
            }
        } else {

        }

        return s;
    }

    public String prolog() {
        __.push_trace(true);
        if (this.types.size() > 1) {
            __.ln ("Looks like unreduced (maybe single) lexeme " + this.lexeme);
            return this.lexeme;
        }
        __.pop_trace();

        __.push_trace(true);
        __.ln ("-------------------------------------------------");
        LinkedList<Compound> npl = pl(this,null);
        String pps = Compound.pp(npl);
        __.pop_trace();

        __.ln ("%%%%%%%%%%% PP output %%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        __.ln (pps);
        return pps;
    }

    private static void nested_pp_helper(LinkedList<Line> L) {
        for (Line Li : L) {
            __.push_trace(false);
            __.ln ("nested_pp_helper: Li.line = " + Li.line.str());
            __.pop_trace();
            if (Li.line.types.size() > 1) {
                __.ln ("Looks like unreduced type for lexeme " + Li.line.lexeme);
                return;
            }
            __.push_trace(false);
            LinkedList<Compound> npl = pl(Li.line,null);
            __.pop_trace();
            __.ln(npl.toString());
            if (Li.block != null && Li.block.size() > 0) {
                nested_pp_helper (Li.block);
            }
        }
    }
    public static void nested_pp(NestedLines nlp) {
        __.ln ("nested_pp:");
        nested_pp_helper (nlp.lines);
    }

    public String tree_str () {
        String s = order.name();
        if (s == "BEFORE") s = "<:";
        else s = ">:";
        if (order == Order.NEITHER)
            return "~:\"" + lexeme + "\"";
        else
            return s + "(" + before.str() + "," + after.str() + ")";
    }

    public String str() {
        String s = tree_str() + "[";
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

    public TypedTree (Set<Valence> types, Order order, String lexeme, TypedTree before, TypedTree after) {
        assertNotNull (types);
        this.set_parent (parent);
        this.types = types;
        this.order = order;
        this.lexeme = lexeme;
        this.before = before;
        this.after  = after;
    }

    public TypedTree (Valence t, Order order, String lexeme, TypedTree before, TypedTree after) {
        this.set_parent (parent);
        types = new TreeSet<>();
        types.add (t);
        this.order = order;
        this.lexeme = lexeme;
        this.before = before;
        this.after  = after;
    }

    public static Set<TypedTree>
    app (Order order, TypedTree this_ttree, TypedTree other_ttree) {
        assertNotNull(this_ttree);
        assertNotNull(other_ttree);  __.ln ("app: apply " + this_ttree.str() + " to " + other_ttree.str());
        Set<TypedTree> result = new TreeSet<TypedTree>();

        __.push_trace(false);

        __.ln ("Over " + this_ttree.types); __.o__();
        
        for (Valence t_this : this_ttree.types) {

            TreeSet<Valence> lx;

            __.ln (t_this.toString() + Valence.ls_str (Atom.valences_for(t_this.toString())) + "...applying...");
            if (t_this.n == Nucleus.O_) {
                lx = new TreeSet<Valence>();                           __.ln ("...to  = " + t_this + " with just " + Valence.ls_str(lx));
                lx.add (t_this);
            } else {
                lx = Atom.valences_for(t_this.toString());          __.ln ("...to  = " + t_this + " including " + Valence.ls_str(lx));
            }

            for (Valence t_this_x : lx) 
//          Valence t_this_x = t_this;
                { // for all Oxy in this ttree

                __.ln ("...to these: " + Valence.ls_str (other_ttree.types)); __.o__();
                for (Valence t_other : other_ttree.types) {
                  Valence t_other_y = t_other;
                        {
                                                                    __.ln ("t_other_y loop");
                        Valence r = t_this_x.fxy (t_other_y);
                        if (r == null) {                            __.ln ("...exiting");
                            continue;
                        }
                        __.o__();
                
                        Valence x = t_this_x;                                                                      __.ln ("x = " + x.toString());
                        
                        TypedTree new_before = new TypedTree (
                                                    Valence.of (Nucleus.O_, x, r),
                                                    this_ttree.order,
                                                    this_ttree.lexeme,
                                                    this_ttree.before,
                                                    this_ttree.after );                     __.ln ("new_before=" + new_before.str());
                        
                        TypedTree new_after =  new TypedTree (
                                                    x,
                                                    other_ttree.order,
                                                    other_ttree.lexeme,
                                                    other_ttree.before,
                                                    other_ttree.after);                     __.ln ("new_after=" + new_after.str());

                        Set<Valence> ls_type2 = new TreeSet<>();
                        ls_type2.add(r);
                        TypedTree new_tt =new TypedTree (ls_type2, order, null, new_before, new_after);
                        new_before.set_parent (new_tt);
                        new_after.set_parent (new_tt);
                                                                                       __.ln ("-Adding new_tt = " + new_tt.str());
                        result.add (new_tt);
                        __.__o();
                    }
                    __.__o();
                }
                 __.__o();
            }
        }  __.__o();
         __.ln ("app()"); __.ln (" =" + TypedTree.ls_set(result));
         __.pop_trace();

        return result;
    }

    public static Set<TypedTree> combine (TypedTree one, TypedTree the_other) {
                 __.ln ("combine(" + one.str() + ", " + the_other.str() + ")");
        Set<TypedTree> tl = new TreeSet<TypedTree>();
        Set<TypedTree> tb = app (Order.BEFORE, one,       the_other );
        Set<TypedTree> ta = app (Order.AFTER,  the_other, one       );
        tl.addAll(tb);
        tl.addAll(ta);
                 __.ln ("combine():"); __.ln (" =" + ls_set(tl));
        return tl;
    }

// This should probably go into tests--does the result match the cached result?

// postprocess for parent links. More efficient to do it incrementally, but that's hard to figure out

    void set_parent_links (TypedTree par) {
        this.parent = par;
        if (this.before != null) this.before.set_parent_links (this);
        if (this.after  != null) this.after.set_parent_links (this);
    }

    public static LinkedList<TypedTree>
    typed_trees(LinkedList<TypedTree> S) {
      assertNotNull(S);          __.ln ("typed_trees (" + ls_str(S) + "):");
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

         __.ln ("-loop on " + splits.str()); __.o__();
        for (Split split : splits.all_splits) {

            LinkedList<TypedTree> before_tts = typed_trees (split.before);
            LinkedList<TypedTree> after_tts  = typed_trees (split.after);

             __.ln (" -loop on" + ls_str(before_tts)); __.o__();
            for (TypedTree before : before_tts) {
                 __.ln ("-loop on " + ls_str(after_tts)); __.o__();
                for (TypedTree after : after_tts) {
                    r.addAll (combine (before, after));
                }  __.__o();
            }  __.__o();
        }
         __.__o();
      }                  __.ln ("typed_trees():"); __.ln (" =" + ls_str(r));

      for (TypedTree tt : r) tt.set_parent_links (null);

      return r;
    }
}