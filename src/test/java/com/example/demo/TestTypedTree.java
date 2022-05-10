package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import com.example.demo.Sentence;
import com.example.demo.TypedTree;
import com.example.demo.__;

public class TestTypedTree {

    static void check_parents (TypedTree tt) {
        __.ln ("check_parents(" + tt.str() + ")");
        if (tt.before != null) {
            assertNotNull (tt.before.parent);
            if (tt.before.parent != tt) {
                __.ln ("check_parent: tt = " + tt.str() + " but tt.before.parent = " + tt.before.parent.str());
                assertEquals (tt.before.parent, tt);
            }
            check_parents (tt.before);
            assertEquals (tt.before.twin(), tt.after);
        }
        if (tt.after != null) {
            assertNotNull (tt.after.parent);
            if (tt.after.parent != tt) {
                __.ln ("check_parent: tt = " + tt.str() + " but tt.after.parent = " + tt.after.parent.str());
                assertEquals (tt.after.parent, tt);
            }
            check_parents (tt.after);
            assertEquals (tt.after.twin(), tt.before);
        }


    }

    @Test
    public void main() throws Exception {
//        String st = "THIS BE BIG";
//        String st = "I BE ILL";
//        String st = "I BE BECAUSE I THINK";
//        String st = "BECAUSE I THINK I BE";
//        String st = "I BE";
//        String st = "I THINK";
//        String st = "IF I THINK I BE";
        String st[] = { "I LIVE",
                        "I SAY SOMETHING",
                        "I BE GOOD",
                    };

        __.reset();
        __.trace(true);
        __.ln ("**** TestTypedTree ****");

        __.push_trace(true);

        for (String sample : st) {
            Sentence S = new Sentence (sample);

            __.ln (Valence.all_valences());

            __.ln ("Sentence \"" + st + "\" is TypeTree list of length="
                + S.tt_list.size ());
            
            LinkedList<TypedTree> tl = TypedTree.typed_trees(S.tt_list);

            __.ln ("TestTypedTree: Length of typed_trees = " + tl.size());

            __.reset();
            __.push_trace(true);

            Iterator<TypedTree> li = tl.iterator();
            while (li.hasNext()) {
                TypedTree tt = li.next();
                __.ln ("typed tree: " + tt.str());
                check_parents (tt);
            }
        }

        __.pop_trace();

        __.ln ("TestTypedTree: exiting");
    }
}