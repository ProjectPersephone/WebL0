package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.*;

import com.example.demo.Type;
import com.example.demo.TypedTree;
import com.example.demo.Lexicon;

public class TestLexicon {

    public static void print_word (String w) {
        LinkedList<Type> types = Lexicon.types_for (w);
        Iterator<Type> ti = types.iterator();
        while (ti.hasNext()) {
            Type t = ti.next();
            System.out.println ("TestLexicon on '" + w + "': t=" + t);
        }
    }

// only works if word w has only one type associated
    public static void readback (String w, Type t) {
        LinkedList<Type> w_types = Lexicon.types_for (w);
        assertNotNull(w_types);
        assertEquals(w_types.size(),1);
        Type trb = w_types.get(0);
        assertEquals (trb,t);
    }

    @Test
    public void main() throws Exception {
        Lexicon d = new Lexicon();
        /*
        Type T = Type.term();
        AUGType O = AUGType.O;
        Type OTT = Type.of (O,T,T); // OTT is like an adjective

        // readback (new String ("I"), T   );  // commented out after adding more types
        readback (new String ("BAD"),   OTT );
        */
    }
}