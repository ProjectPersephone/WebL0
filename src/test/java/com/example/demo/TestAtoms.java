package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.*;

import com.example.demo.Valence;
import com.example.demo.TypedTree;
import com.example.demo.Atoms;

public class TestAtoms {

    public static void print_word (String w) {
        Set<Valence> types = Atoms.valences_for (w);
        Iterator<Valence> ti = types.iterator();
        while (ti.hasNext()) {
            Valence t = ti.next();
            System.out.println ("TestLexicon on '" + w + "': t=" + t);
        }
    }

// only works if word w has only one type associated
    public static void readback (String w, Valence t) {
        Set<Valence> w_types = Atoms.valences_for (w);
        assertNotNull(w_types);
        assertEquals(w_types.size(),1);
    }

    @Test
    public void main() throws Exception {
        Atoms d = new Atoms();
    }
}