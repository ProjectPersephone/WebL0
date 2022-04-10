// Lexicon.java -- should probably be folded in Type, since primes
// and the basic grammar types initialized here are really types.

package com.example.demo;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.*;

import com.example.demo.Type;
import com.example.demo.TypedTree;

public class Lexicon {
    private
        static // should allow multiple lexicons but for now, just one for English NSM
           HashMap<String,TreeSet<Type>> map;
    public static Type S;
    public static Type Pred;
    public static Type PredOp; // really means takes sentence as operand, e.g. SAY, THINK, etc.
    public static Type Subst; // SAY, THINK ....
    // public static Type Arg; // SAY, THINK ....
    public static Type Cond; // IF, BECAUSE
    public static Type Conseq;
    public static Type CondS;
    public static Type PredPred;
    public static Type Good;
    public static Type Bad;
    public static Type Someone;
    public static Type Something;


    private static TreeSet<Type> insert(String w) {
        TreeSet<Type> l = new TreeSet<Type>();
        map.put (w, l);
        return l;
    }

    public static TreeSet<Type> types_for(String w) {
        return map.get (w);
    }

    public static Type O_(Type x, Type y) {
            return Type.of(AUGType.O, x, y);
    }

    public Lexicon() {

        map = new HashMap<String,TreeSet<Type>>();

        // English-specific:

//        Type T = Type.term();
//        Type S = Type.sentence();
//        AUGType O = AUGType.O;
        /*
        Type a = O_(T,T); // OTT is like an adjective
        Type c = O_(S,S);
        Type p1  = O_(T,S); // e.g., is
        Type cop = O_(a,p1);
        Type osc = O_(S,c);
        */

//  somewhether IF
//  somemuch(?) MANY -- quantifier
//  -- evaluator/goodness -- "somewhat"
//  -- intensifier - very

        Tab.reset();
        Tab.trace(false);
        for (AUGType t : AUGType.values()) {
            String sym = t.toString();
            if (t == AUGType.O)
                continue;

            insert (sym);

            types_for(sym).add(Type.of (t,null,null));
        }
        Tab.trace(false);

        Type something = Type.of (AUGType.SOMETHING,null,null);
//        Type sometime = O_(S,S);  //  Main.hs "tomorrow", should be same as c
//        Type somewhere = Type.of (AUGType.SOMEWHERE,null,null);
        Type someone = Type.of (AUGType.SOMEONE,null,null);
//        Type some_cause = Type.of (AUGType.BECAUSE,null,null);
//        Type somehow = Type.of (AUGType.LIKE,null,null);
//        Type some = Type.of (AUGType.THIS,null,null);  // when not modifying, so maybe not needed
        Type good = Type.of (AUGType.GOOD,null,null);
        Type bad = Type.of (AUGType.BAD,null,null);
        Type say = Type.of (AUGType.SAY, null, null);
        Type is = Type.of (AUGType.IS,null,null);
        Type live = Type.of (AUGType.LIVE,null,null);
        Type true_ = Type.of (AUGType.TRUE,null,null);
        Type maybe = Type.of (AUGType.MAYBE,null,null);
        Type if_ = Type.of (AUGType.IF,null,null);
        Type this_ = Type.of(AUGType.THIS,null,null);

        S = true_;
        // S = O_(say,this_);

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
        TreeSet<Type> I = types_for("I");
////        I.add (O_(say, S));
        I.add (someone);
////        I.add (O_(p1, S)); // too general, but anyway
/* YOU,
   SOMEONE, */
        TreeSet<Type> so = types_for("SOMEONE");
        so.add(O_(is,Pred));
        so.add(O_(Pred, S)); // someone can be good, bad; do thing, etc.
        so.add(O_(bad,someone));
        so.add(O_(good,someone));
//        types_for("SOMEONE").add(O_(O_(say,true_),say)); // someone can say something maybe true
//        types_for("SOMEONE").add(O_(live,true_));
/*
   SOMETHING, */
        TreeSet<Type> st = types_for ("SOMETHING");
//        st.add (O_(say,O_(say,true_)));   // can say something, though a something can't say things
        st.add(O_(is,Pred));
        st.add(O_(Pred,S));
        st.add(O_(bad,something)); // something that's bad is still something
        st.add(O_(good,something)); // same for good thing
/* BODY,
   PEOPLE,
   KIND,
   PART,
   WORDS,
*/
//   THIS,
        TreeSet<Type> a_this = types_for("THIS");
        a_this.add(O_(something,something));
/*
   THE_SAME,
   OTHER,
   ONE,
   TWO,
   MUCH_many,
   ALL,
   SOME,
   LITTLE,

   TIME_when,
   NOW, */
////        types_for ("NOW").add (sometime);
/* MOMENT,
   FOR_SOME_TIME,
   A_LONG_TIME,
   A_SHORT_TIME,
   BEFORE,
   AFTER,
*/
//   WANT,
        TreeSet<Type> how_to_want = types_for ("WANT");
        how_to_want.add(O_(something, Pred));
        how_to_want.add(PredPred);      // predicated predicate like "WANT [to] KNOW <something>"
        how_to_want.add(O_(this_,Pred));

//   DONT_WANT,
        TreeSet<Type> how_to_not_want = types_for ("DONT_WANT");
        how_to_not_want.add(O_(something, Pred));       // ???
        how_to_not_want.add(PredPred);      // ???
        how_to_not_want.add(O_(this_,Pred));

/*
   FEEL,
*/
//   DO,
        TreeSet<Type> how_to_do = types_for ("DO");
        how_to_do.add (O_(something,Pred));
//   SAY,
        TreeSet<Type> how_to_say = types_for ("SAY");
//        how_to_say.add (O_(something, say)); // just raw SOMETHING, but need SOMETHING LIKE WORD(S)
        how_to_say.add (O_(S, Pred));       // for actual statement, maybe need a "MAYBE TRUE"
        how_to_say.add (O_(something,Pred));    // "SAY SOMETHING", but dangerous: could mean you could say an object
//        how_to_say.add (O_(live, say));
////        how_to_say.add (O_(someone, S));
/* KNOW, */
        TreeSet<Type> how_to_know = types_for ("KNOW");
        how_to_know.add (O_(something, Pred));
        how_to_know.add(Pred); // e.g., "I [NOT/don't] KNOW"/"I KNOW" ???
/*
   SEE,
   HEAR,
   THINK, */ 
////        types_for ("THINK").add (p1);
// HAPPEN,
        TreeSet<Type> how_to_happen = types_for("HAPPEN");
        how_to_happen.add(O_(someone,Pred));   // happen TO someone
        how_to_happen.add(Pred);        // too broad -- a person can't happen to you, for example
//   IS,    // copula not BE (SOMEWHERE) */
        TreeSet<Type> is_ = types_for("IS");
////        is.add (O_(someone,p1));
        is_.add (O_(something,Pred));
        is_.add (O_(someone,Pred));             // may need to distinguish person IS from thing IS
////        is.add (cop);                   // ???????????
        is_.add(O_(good,Pred));
        is_.add(O_(bad,Pred));
//        is_.add(O_(someone,is));  // Makes "someone is"->S, but it will be discarded as nonsensical unless alone
//        is_.add(O_(say,is)); // e.g. say (someone is good) -> someone is saying an "is"
/* LIVE, */
 //       types_for("LIVE").add(O_(someone,live));
        types_for("LIVE").add(Pred);
/*
   DIE, */
        types_for("DIE").add(Pred);
/*
   THERE_IS,
   BE,    // ... somewhere */
////        TreeSet<Type> be = types_for("BE");
////        be.add (O_(something,somewhere)); // was p1
////        be.add (O_(someone,somewhere));
/* IS_MINE,
   MOVE,
   TOUCH,
   INSIDE,
   SOMEWHERE,
   HERE, */
////        types_for("HERE").add(somewhere);
/* ABOVE,
   BELOW,
   ON_ONE_SIDE,
   NEAR,
   FAR,
*/
//   NOT,
        types_for ("NOT").add(PredPred);

//   CAN,
        types_for("CAN").add(PredPred);    // too broad, just a start
/*
   BECAUSE, */
////        types_for("BECAUSE").add(osc);
// IF,
        types_for ("IF").add(O_(S,Cond)); // guessing from Haskell code
/* MAYBE,
   LIKE,
   VERY,
   MORE,

   SMALL,
   BIG,
   BAD, */
        TreeSet<Type> how_to_be_bad = types_for ("BAD");
        how_to_be_bad.add(O_(something,something));
        how_to_be_bad.add(O_(someone,someone));

////                types_for("BAD").add(O_(is,O_(is, bad)));
/* GOOD, */
        TreeSet<Type> how_to_be_good = types_for ("GOOD");
        how_to_be_good.add(O_(something,something));
        how_to_be_good.add(O_(someone,someone));
/* TRUE
*/

        for (AUGType t : AUGType.values()) {
                String sym = t.toString();
                if (t == AUGType.O)
                        continue;
                Tab.ln ("t=" + t + " string=" + sym + Type.ls_str(types_for(sym)));            
        }
        Tab.ln ("<<<<<<<<<<<<<<<<<<<<< ALL TYPES >>>>>>>>>>>>>>>>>>>>>");
        Tab.ln (Type.all_types());
    }
}