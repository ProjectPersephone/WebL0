package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.*;

import com.example.demo.Valence;
import com.example.demo.TypedTree;
import com.example.demo.Lexicon;

public class TestLexicon {

    public static void print_word (String w) {
        Set<Valence> types = Lexicon.valences_for (w);
        Iterator<Valence> ti = types.iterator();
        while (ti.hasNext()) {
            Valence t = ti.next();
            System.out.println ("TestLexicon on '" + w + "': t=" + t);
        }
    }

// only works if word w has only one type associated
    public static void readback (String w, Valence t) {
        Set<Valence> w_types = Lexicon.valences_for (w);
        assertNotNull(w_types);
        assertEquals(w_types.size(),1);
/* change to "set" kills this:
        Type trb = w_types.get(0);
        assertEquals (trb,t);
*/
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