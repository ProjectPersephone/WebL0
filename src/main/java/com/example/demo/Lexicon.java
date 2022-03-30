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

    private static TreeSet<Type> insert(String w) {
        TreeSet<Type> l = new TreeSet<Type>();
        map.put (w, l);
        return l;
    }

    public static TreeSet<Type> types_for(String w) {
        return map.get (w);
    }

    public Lexicon() {

        map = new HashMap<String,TreeSet<Type>>();

        // English-specific:

//        Type T = Type.term();
//        Type S = Type.sentence();
        AUGType O = AUGType.O;
        /*
        Type a = Type.of (O,T,T); // OTT is like an adjective
        Type c = Type.of (O,S,S);
        Type p1  = Type.of (O,T,S); // e.g., is
        Type cop = Type.of (O,a,p1);
        Type osc = Type.of (O,S,c);
        */

//  somewhether IF
//  somemuch(?) MANY -- quantifier
//  -- evaluator/goodness -- "somewhat"
//  -- intensifier - very

        Tab.reset();
        for (AUGType t : AUGType.values()) {
            String sym = t.toString();
            if (t == AUGType.O)
                continue;

            insert (sym);

            types_for(sym).add(Type.of (t,null,null));
        }

        Type something = Type.of (AUGType.SOMETHING,null,null);
//        Type sometime = Type.of (O,S,S);  //  Main.hs "tomorrow", should be same as c
        Type somewhere = Type.of (AUGType.SOMEWHERE,null,null);
        Type someone = Type.of(AUGType.SOMEONE,null,null);
        Type some_cause = Type.of(AUGType.BECAUSE,null,null);
        Type somehow = Type.of(AUGType.LIKE,null,null);
        Type some = Type.of(AUGType.THIS,null,null);  // when not modifying, so maybe not needed
        Type good = Type.of(AUGType.GOOD,null,null);
        Type say = Type.of (AUGType.SAY, null, null);
        Type is = Type.of(AUGType.IS,null,null);
        Type live = Type.of (AUGType.LIVE,null,null);

/* I */
        Set<Type> I = types_for("I");
////        I.add (Type.of (O, say, S));
        I.add (someone);
////        I.add (Type.of (O, p1, S)); // too general, but anyway
/* YOU,
   SOMEONE, */
        types_for("SOMEONE").add(Type.of(O,Type.of(O,good,is),is));
        types_for("SOMEONE").add(Type.of(O,say,say));
        types_for("SOMEONE").add(Type.of(O,live,live));
/*
   SOMETHING, */
        TreeSet<Type> st = types_for ("SOMETHING");
        st.add (Type.of(O,say,say));   // can say something, though a something can't say things
////        st.add (T);
/* BODY,
   PEOPLE,
   KIND,
   PART,
   WORDS,

   THIS,
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

   WANT,
   DONT_WANT,
   FEEL,
   DO,
   SAY, */
        TreeSet<Type> how_to_say = types_for ("SAY");
        how_to_say.add (Type.of (O, something, say));
        how_to_say.add (Type.of (O, is, say));
        how_to_say.add (Type.of (O, live, say));
////        how_to_say.add (Type.of (O, someone, S));
/* KNOW,
   SEE,
   HEAR,
   THINK, */ 
////        types_for ("THINK").add (p1);
/* HAPPEN,
   IS,    // copula */
        TreeSet<Type> is_ = types_for("IS");
////        is.add (Type.of(O,someone,p1));
////        is.add (Type.of(O,something,p1));
////        is.add (cop);                   // ???????????
        is_.add(Type.of(O,good,is));
        is_.add(Type.of(O,someone,is));  // Makes "someone is"->S, but it will be discarded as nonsensical unless alone
        is_.add(Type.of(O,say,is)); // e.g. say (someone is good) -> someone is saying an "is"
/* LIVE, */
        types_for("LIVE").add(Type.of(O,someone,live));
/*
   DIE,
   THERE_IS,
   BE,    // ... somewhere */
////        TreeSet<Type> be = types_for("BE");
////        be.add (Type.of(O,something,somewhere)); // was p1
////        be.add (Type.of(O,someone,somewhere));
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

   NOT,
   CAN,
   BECAUSE, */
////        types_for("BECAUSE").add(osc);
/* IF, */
////        types_for ("IF").add(osc);
/* MAYBE,
   LIKE,
   VERY,
   MORE,

   SMALL,
   BIG,
   BAD, */
////        types_for ("BAD").add(a);
                types_for("BAD").add(Type.of(O,is,is));
/* GOOD, */
////        types_for("GOOD").add (a);
                types_for("GOOD").add(Type.of(O,is,is));
/* TRUE
*/

        for (AUGType t : AUGType.values()) {
                String sym = t.toString();
                if (t == AUGType.O)
                        continue;
                Tab.ln ("t=" + t + " string=" + sym + Type.ls_str(types_for(sym)));            
        }
    }
}