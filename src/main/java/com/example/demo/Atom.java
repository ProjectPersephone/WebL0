package com.example.demo;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.*;

import com.example.demo.Valence;
import com.example.demo.TypedTree;

// "Element" probably better, with "Atom" being a single instance of an Element,
//    "Compound" combinations of Atoms,
//    and "Molecule" being a stable definition of a Compound
public class Atom {
    private static HashMap<String,TreeSet<Valence>> map;
    public static Valence S;
    public static Valence Pred;
    public static Valence PredOp; // really means takes sentence as operand, e.g. SAY, THINK, etc.
    public static Valence Subst; // SAY, THINK ....
    public static Valence Cond; // IF, BECAUSE
    public static Valence Conseq;
    public static Valence CondS;
    public static Valence PredPred;

    private static Valence Good;
    private static Valence Bad;
    public static Valence Someone;
    public static Valence Something;

    private static TreeSet<Valence> insert(String w) {
        TreeSet<Valence> l = new TreeSet<Valence>();
        map.put (w, l);
        return l;
    }

    public static TreeSet<Valence> valences_for(String w) {
        return map.get (w);
    }

    public static Valence O_(Valence x, Valence y) {
            return Valence.of(Nucleus.O_, x, y);
    }

    public Atom() {

        map = new HashMap<String,TreeSet<Valence>>();

//  somewhether IF
//  somemuch(?) MANY -- quantifier
//  -- evaluator/goodness -- "somewhat"
//  -- intensifier - very

        Tab.reset();
        Tab.trace(false);

        for (Nucleus n : Nucleus.values()) {
            String sym = n.toString();
            if (n == Nucleus.O_)
                continue;
            insert (sym);
            valences_for(sym).add(Valence.of (n,null,null));
        }

        Tab.trace(false);

        Valence something = Valence.of (Nucleus.SOMETHING,null,null);
//        Type sometime = O_(S,S);  //  Main.hs "tomorrow", should be same as c
//        Type somewhere = Type.of (AUGType.SOMEWHERE,null,null);
        Valence someone = Valence.of (Nucleus.SOMEONE,null,null);
//        Type some_cause = Type.of (AUGType.BECAUSE,null,null);
//        Type somehow = Type.of (AUGType.LIKE,null,null);
//        Type some = Type.of (AUGType.THIS,null,null);  // when not modifying, so maybe not needed
        Valence good = Valence.of (Nucleus.GOOD,null,null);
        Valence bad = Valence.of (Nucleus.BAD,null,null);
        Valence say = Valence.of (Nucleus.SAY, null, null);
        Valence is = Valence.of (Nucleus.BE,null,null);
        Valence live = Valence.of (Nucleus.LIVE,null,null);
        Valence true_ = Valence.of (Nucleus.TRUE,null,null);
        Valence maybe = Valence.of (Nucleus.MAYBE,null,null);
        Valence if_ = Valence.of (Nucleus.IF,null,null);
        Valence this_ = Valence.of(Nucleus.THIS,null,null);

        S = true_;
        // S = O_(say,this_);   // or someone can think this?

        Pred = maybe;
        // Pred = O_(O_(maybe,true_),this_); // Predicate something that may be true about something/someone

        PredOp = O_(S,Pred);
        Subst = O_(O_(Pred,S),S);    // iffy
        Cond = O_(S,S);
        Conseq = O_(S,Cond);
        CondS = O_(Cond,S);
        PredPred = O_(Pred,Pred);
        Good = good;
        Bad = bad;
        Someone = someone;
        Something = something;

/* I */
        TreeSet<Valence> I = valences_for("I");
////        I.add (O_(say, S));
        I.add (someone);
////        I.add (O_(p1, S)); // too general, but anyway

/* YOU, */
        TreeSet<Valence> you = valences_for("YOU");
        you.add (someone);

 /*  SOMEONE, */
        TreeSet<Valence> so = valences_for("SOMEONE");
        so.add(O_(is,Pred));
        so.add(O_(Pred, S)); // someone can be good, bad; do thing, etc.
        so.add(O_(bad,someone));
        so.add(O_(good,someone));
        so.add(O_(this_,someone));
//        valences_for("SOMEONE").add(O_(O_(say,true_),say)); // someone can say something maybe true
//        valences_for("SOMEONE").add(O_(live,true_));
/*
   SOMETHING, */
        TreeSet<Valence> st = valences_for ("SOMETHING");
//        st.add (O_(say,O_(say,true_)));   // can say something, though a something can't say things
        st.add(O_(is,Pred));
        st.add(O_(Pred,S));
        st.add(O_(bad,something)); // something that's bad is still something
        st.add(O_(good,something)); // same for good thing
        so.add(O_(this_,something));
/* BODY,
   PEOPLE,
   KIND,
   PART,
   WORDS,
*/
//   THIS,
        TreeSet<Valence> a_this = valences_for("THIS");
        a_this.add(O_(something,something));
        a_this.add(O_(someone,someone));
        a_this.add(something); // bare THIS
/*
   THE_SAME,
   OTHER,
   ONE,
   TWO,
   MUCH_many,
   ALL,
*/

//   SOME,
/*
   LITTLE,

   TIME_when,
   NOW, */
////        valences_for ("NOW").add (sometime);
/* MOMENT,
   FOR_SOME_TIME,
   A_LONG_TIME,
   A_SHORT_TIME,
   BEFORE,
   AFTER,
*/
//   WANT,
        TreeSet<Valence> how_to_want = valences_for ("WANT");
        how_to_want.add(O_(something, Pred));
        how_to_want.add(PredPred);      // predicated predicate like "WANT [to] KNOW <something>"
        how_to_want.add(O_(this_,Pred));

//   DONT_WANT,
        TreeSet<Valence> how_to_not_want = valences_for ("DONT_WANT");
        how_to_not_want.add(O_(something, Pred));       // ???
        how_to_not_want.add(PredPred);      // ???
        how_to_not_want.add(O_(this_,Pred));

/*
   FEEL,
*/
//   DO,
        TreeSet<Valence> how_to_do = valences_for ("DO");
        how_to_do.add (O_(something,Pred));
//   SAY,
        TreeSet<Valence> how_to_say = valences_for ("SAY");
//        how_to_say.add (O_(something, say)); // just raw SOMETHING, but need SOMETHING LIKE WORD(S)
        how_to_say.add (O_(S, Pred));       // for actual statement, maybe need a "MAYBE TRUE"
        how_to_say.add (O_(something,Pred));    // "SAY SOMETHING", but dangerous: could mean you could say an object
//        how_to_say.add (O_(live, say));
////        how_to_say.add (O_(someone, S));
/* KNOW, */
        TreeSet<Valence> how_to_know = valences_for ("KNOW");
        how_to_know.add (O_(something, Pred));
        how_to_know.add(Pred); // e.g., "I [NOT/don't] KNOW"/"I KNOW" ???
/*
   SEE,
   HEAR,
   THINK, */ 
////        valences_for ("THINK").add (p1);
// HAPPEN,
        TreeSet<Valence> how_to_happen = valences_for("HAPPEN");
        how_to_happen.add(O_(someone,Pred));   // happen TO someone
        how_to_happen.add(Pred);        // too broad -- a person can't happen to you, for example
//   IS,    // copula not BE (SOMEWHERE) */
        TreeSet<Valence> is_ = valences_for("BE");
////        is.add (O_(someone,p1));
        is_.add (O_(something,Pred));
        is_.add (O_(someone,Pred));             // may need to distinguish person IS from thing IS
////        is.add (cop);                   // ???????????
        is_.add(O_(good,Pred));
        is_.add(O_(bad,Pred));
//        is_.add(O_(someone,is));  // Makes "someone is"->S, but it will be discarded as nonsensical unless alone
//        is_.add(O_(say,is)); // e.g. say (someone is good) -> someone is saying an "is"
/* LIVE, */
 //       valences_for("LIVE").add(O_(someone,live));
        valences_for("LIVE").add(Pred);
/*
   DIE, */
        valences_for("DIE").add(Pred);
/*
   THERE_IS,
   BE,    // ... somewhere */
////        TreeSet<Type> be = valences_for("BE");
////        be.add (O_(something,somewhere)); // was p1
////        be.add (O_(someone,somewhere));
/* IS_MINE,
   MOVE,
   TOUCH,
   INSIDE,
   SOMEWHERE,
   HERE, */
////        valences_for("HERE").add(somewhere);
/* ABOVE,
   BELOW,
   ON_ONE_SIDE,
   NEAR,
   FAR,
*/
//   NOT,
        valences_for ("NOT").add(PredPred);

//   CAN,
        valences_for("CAN").add(PredPred);    // too broad, just a start
/*
   BECAUSE, */
////        valences_for("BECAUSE").add(osc);
// IF,
        valences_for ("IF").add(O_(S,Cond)); // guessing from Haskell code
/* MAYBE,
   LIKE,
   VERY,
   MORE,

   SMALL,
   BIG,
   BAD, */
        TreeSet<Valence> how_to_be_bad = valences_for ("BAD");
        how_to_be_bad.add(O_(something,something));
        how_to_be_bad.add(O_(someone,someone));

////                valences_for("BAD").add(O_(is,O_(is, bad)));
/* GOOD, */
        TreeSet<Valence> how_to_be_good = valences_for ("GOOD");
        how_to_be_good.add(O_(something,something));
        how_to_be_good.add(O_(someone,someone));
/* TRUE
*/

        for (Nucleus t : Nucleus.values()) {
                String sym = t.toString();
                if (t == Nucleus.O_)
                        continue;
                Tab.ln ("t=" + t + " string=" + sym + Valence.ls_str(valences_for(sym)));            
        }
        Tab.ln ("<<<<<<<<<<<<<<<<<<<<< ALL TYPES >>>>>>>>>>>>>>>>>>>>>");
        Tab.ln (Valence.all_valences());
    }
}