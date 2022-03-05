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
    public
        static // should allow multiple lexicons but for now, just one for English NSM
           HashMap<String,LinkedList<Type>> map; 

    public static void insert(String w, LinkedList<Type> tl) {
        map.put (w, tl);
    }

    public static LinkedList<Type> lookup(String w) {
        return map.get (w);
    }

    public static void add_one_word_and_type (String w, Type t) {
        LinkedList<Type> tl = new LinkedList<Type>();
        tl.add(t);
        Lexicon.insert (w,tl);
    }

    public Lexicon() {
        map = new HashMap<String,LinkedList<Type>>();
        Type T = Type.term();
        Type S = Type.sentence();
        AUGType O = AUGType.O;
        Type a = Type.of (O,T,T); // OTT is like an adjective
        Type c = Type.of (O,S,S);
        Type p1  = Type.of (O,T,S); // e.g., is
        Type cop = Type.of (O,a,p1);
        Type osc = Type.of (O,S,c);
//        Type something = Type.of (O,p1,p1);  //  Main.hs "today"
        Type sometime = Type.of (O,S,S);  //  Main.hs "tomorrow", should be same as c

        // better to read from file or have per-language init modules in Java


        Lexicon.add_one_word_and_type ("BAD", a);
        Lexicon.add_one_word_and_type ("GOOD", a);

        LinkedList<Type> for_is = new LinkedList<Type>();
        for_is.add (cop);
        for_is.add (p1);
        Lexicon.insert ("IS", for_is);

        Lexicon.add_one_word_and_type ("THINK", p1);
        Lexicon.add_one_word_and_type ("BECAUSE", osc);
        Lexicon.add_one_word_and_type ("IF", osc);
        Lexicon.add_one_word_and_type ("NOW", sometime);

        LinkedList<Type> how_to_something = new LinkedList<Type>();
        Type something = Type.of (AUGType.SOMETHING,null,null);
        assertNotNull (something);
        how_to_something.add (something);
        how_to_something.add (T); // until other cases covered
        Lexicon.insert ("SOMETHING", how_to_something);

        LinkedList<Type> how_to_say = new LinkedList<Type>();
        Type say = Type.of (AUGType.SAY, null, null);
        assertNotNull (say);
        how_to_say.add (Type.of (O, something, say));
        how_to_say.add (Type.of (O, S, say));
        Lexicon.insert ("SAY", how_to_say);

        LinkedList<Type> I_can_do = new LinkedList<Type>();
        I_can_do.add (Type.of (O, say, S));
        I_can_do.add (T);   // until other cases covered
        Lexicon.insert ("I", I_can_do);
    }
}