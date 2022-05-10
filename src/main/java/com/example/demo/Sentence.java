package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.*;

import com.example.demo.Order;
import com.example.demo.Nucleus;
import com.example.demo.Valence;
import com.example.demo.TypedTree;
import com.example.demo.Atom;

public class Sentence {
    public LinkedList<TypedTree> tt_list;

// Raw split(" ") is naive. Blank/tab separation is naive in general.
// Needs handling of punctuation and eventually language orthography
// independence.

// One issue here: because the code of Hudak et al. defines Sentence
// similarly to my Row, what we may really have to deal with is a
// linked list running the either the left or right links of the
// TrellisNode lists.

// Need to look up types instead of adding this null_tl

// This blows up if a word isn't in the lexicon

    public Sentence (String s) {
        tt_list = new LinkedList<TypedTree>();

        assertNotNull(s);
        String[] sa = s.split(" ");
        assertNotNull (sa);
        for (String w : sa) {
            Set<Valence> tl = Atom.valences_for (w);
            if (tl == null) {
                __.ln ("Word w=" + w + " not in Lexicon");
            }
            else
            // it shouldn't be new Tree (w), I think
                tt_list.add (new TypedTree (tl, Order.NEITHER, w, null, null));
        }
    }

    public Sentence (LinkedList<TypedTree> tts) {
        tt_list = tts;
    }

    public String str() {
        String s = "[";
        Iterator<TypedTree> ti = tt_list.iterator();
        while (ti.hasNext()) {
            s += ti.next().str();
            if (ti.hasNext())
                s += " ";
        }
        s += "]";

        return s;
    }
}