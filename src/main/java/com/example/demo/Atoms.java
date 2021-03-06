package com.example.demo;

import java.util.*;

import com.example.demo.Valence;
import com.example.demo.TypedTree;

// "Element" probably better, with "Atom" being a single instance of an Element,
//    "Compound" combinations of Atoms,
//    and "Molecule" being a stable definition of a Compound
public class Atoms {
    private static HashMap<String,TreeSet<Valence>> map
        // = new HashMap<String,TreeSet<Valence>>()
        ;

    public static Valence S;
    public static Valence Pred;
    public static Valence Subst; // SAY, THINK ....
    public static Valence Cond; // IF, BECAUSE
    public static Valence Conseq;
    public static Valence CondS;
    public static Valence ModPred;
    public static Valence PredS;

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

    public static Valence map (Valence x, Valence y, String label) {
            return Valence.of (Nucleus.O_, x, y, label);
    }

    public static Valence O_(Valence x, Valence y, String label) {
            return Valence.of (Nucleus.O_, x, y, label);
    }

    public Atoms() {
            map = new HashMap<String,TreeSet<Valence>>();

//  somewhether IF
//  somemuch(?) SOME -- quantifier
//  -- evaluator/goodness -- "somewhat"
//  -- intensifier - very

                                                __.reset();
                                                __.trace(false);

        for (Nucleus n : Nucleus.values()) {
            String sym = n.toString();
            if (n == Nucleus.O_)
                continue;
            insert (sym);
            valences_for(sym).add(Valence.of (n,null,null));
        }

                                                __.trace(false);

        Valence something = Valence.of (Nucleus.SOMETHING,null,null);
//        Type sometime = O_(S,S);  //  Main.hs "tomorrow", should be same as c
//        Type somewhere = Type.of (AUGType.SOMEWHERE,null,null);
        Valence someone = Valence.of (Nucleus.SOMEONE,null,null);
//        Type some_cause = Type.of (AUGType.BECAUSE,null,null);
//        Type somehow = Type.of (AUGType.LIKE,null,null);
//        Type some = Type.of (AUGType.THIS,null,null);  // when not modifying, so maybe not needed
        Valence good = Valence.of (Nucleus.GOOD,null,null);
        Valence bad = Valence.of (Nucleus.BAD,null,null);
        // Valence say = Valence.of (Nucleus.SAY, null, null);
        Valence is = Valence.of (Nucleus.BE,null,null);
        // Valence live = Valence.of (Nucleus.LIVE,null,null);
        Valence true_ = Valence.of (Nucleus.TRUE,null,null);

        // Valence if_ = Valence.of (Nucleus.IF,null,null);
        Valence this_ = Valence.of(Nucleus.THIS,null,null);
        Valence maybe = Valence.of(Nucleus.MAYBE, null, null);

        S = map (true_,true_, "S"); // for now
        
        Pred = map (maybe,maybe, "Pred");  // for now
        // Pred = O_(O_(maybe,true_),this_); // Predicate something that may be true about something/someone

        PredS = map (Pred, S, "PredS");

        Subst = map (PredS, S, "Subst");    // iffy

        Cond = map (S,S, "Cond");
        Conseq = map (S,Cond, "Conseq");
        CondS = map (Cond,S, "CondS");
        ModPred = map (Pred,Pred, "ModPred");        // really needed?????????????????????????????

        Someone = someone;
        Something = something;
        //
        // still no O_(Pred,S)
        // this is why it goes into default
        //

// I
        TreeSet<Valence> I = valences_for("I");
        I.add (someone);

// YOU
        TreeSet<Valence> you = valences_for("YOU");
        you.add (someone);

 // SOMEONE
        TreeSet<Valence> so = valences_for("SOMEONE");
        so.add(O_(is,Pred));
        so.add(O_(Pred, S)); // someone can be good, bad; do thing, etc.
        so.add(O_(ModPred, S));
        so.add(O_(bad,someone));
        so.add(O_(good,someone));
        so.add(O_(this_,someone));

// SOMETHING
        TreeSet<Valence> st = valences_for ("SOMETHING");
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
// THIS
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

/* SOME,
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
// WANT
        TreeSet<Valence> how_to_want = valences_for ("WANT");
        how_to_want.add(O_(something, Pred));
        how_to_want.add(O_(this_,Pred));

// DONT_WANT
        TreeSet<Valence> how_to_not_want = valences_for ("DONT_WANT");
        how_to_not_want.add(O_(something, Pred));       // ???
        how_to_not_want.add(O_(this_,Pred));

/*
   FEEL,
*/
// DO
        TreeSet<Valence> how_to_do = valences_for ("DO");
        how_to_do.add (O_(something,Pred));

// SAY
        TreeSet<Valence> how_to_say = valences_for ("SAY");
//        how_to_say.add (O_(something, say)); // just raw SOMETHING, but need SOMETHING LIKE WORD(S)
        how_to_say.add (O_(S, Pred));       // for actual statement, maybe need a "MAYBE TRUE"
        how_to_say.add (O_(something,Pred));    // "SAY SOMETHING", but dangerous: could mean you could say an object

// KNOW
        TreeSet<Valence> how_to_know = valences_for ("KNOW");
        how_to_know.add (O_(something, Pred));
        how_to_know.add(Pred); // e.g., "I [NOT/don't] KNOW"/"I KNOW" ???
/*
   SEE,
   HEAR,
   THINK,
*/ 

////        valences_for ("THINK").add (p1);

// HAPPEN,
        TreeSet<Valence> how_to_happen = valences_for("HAPPEN");
        how_to_happen.add(O_(someone,Pred));   // happen TO someone
        how_to_happen.add(Pred);        // too broad -- a person can't happen to you, for example

// BE    // copula not BE (SOMEWHERE) */
        TreeSet<Valence> be = valences_for("BE");
////        is.add (O_(someone,p1));
        be.add (O_(something,Pred));
        be.add (O_(someone,Pred));             // may need to distinguish person IS from thing IS
////        is.add (cop);                   // ???????????
        be.add(O_(good,Pred));
        be.add(O_(bad,Pred));
//        be.add(O_(someone,is));  // Makes "someone is"->S, but it will be discarded as nonsensical unless alone
//        be.add(O_(say,is)); // e.g. say (someone is good) -> someone is saying an "is"

// LIVE
 //       valences_for("LIVE").add(O_(someone,live));
        valences_for("LIVE").add(Pred);

// DIE
        valences_for("DIE").add(Pred);
/*
   THERE_IS,
   BE_SOMEWHERE,
////        TreeSet<Type> be_sw = valences_for("BE");
////        be_sw.add (O_(something,somewhere)); // was p1
////        be_sw.add (O_(someone,somewhere));

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
// NOT
        valences_for ("NOT").add(ModPred);

// CAN
        valences_for("CAN").add(ModPred);    // too broad, just a start

//   BECAUSE, */
////        valences_for("BECAUSE").add(osc);

// IF
        valences_for ("IF").add(O_(S,Cond)); // guessing from Haskell code

/* MAYBE,
   LIKE,
   VERY,
   MORE,

   SMALL,
   BIG,
*/

// BAD
        TreeSet<Valence> how_to_be_bad = valences_for ("BAD");
        how_to_be_bad.add(O_(something,something));
        how_to_be_bad.add(O_(someone,someone));

// GOOD
        TreeSet<Valence> how_to_be_good = valences_for ("GOOD");
        how_to_be_good.add(O_(something,something));
        how_to_be_good.add(O_(someone,someone));

/* TRUE
*/

        for (Nucleus t : Nucleus.values()) {
                String sym = t.toString();
                if (t == Nucleus.O_)
                        continue;
                                        __.ln ("t=" + t + " string=" + sym + Valence.ls_str(valences_for(sym)));            
        }
                                        __.ln ("<<<<<<<<<<<<<<<<<<<<< ALL TYPES >>>>>>>>>>>>>>>>>>>>>");
                                        __.ln (Valence.all_valences());
    }
}