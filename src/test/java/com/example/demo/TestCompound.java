package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import com.example.demo.Sentence;
import com.example.demo.TypedTree;
import com.example.demo.Compound;
import com.example.demo.Tab;

public class TestCompound {
    @Test
    public void main() throws Exception {
//        String st = "THIS BE BIG";
//        String st = "I BE ILL";
//        String st = "I BE BECAUSE I THINK";
//        String st = "BECAUSE I THINK I BE";
//        String st = "I BE";
//        String st = "I THINK";
//        String st = "IF I THINK I BE";
        String st = "I SAY SOMETHING";

        Tab.reset();
        Tab.trace(true);
        Tab.ln ("**** TestCompound ****");

        Sentence S = new Sentence (st);

        Tab.ln (Valence.all_valences());

        Tab.reset();

        Tab.ln ("Sentence \"" + st + "\" is TypeTree list of length="
             + S.tt_list.size ());

    Tab.trace(true);
        
        LinkedList<TypedTree> tl = TypedTree.typed_trees(S.tt_list);
    
    Tab.trace(true);

        Tab.ln ("TestTypedTree: Length of typed_trees = " + tl.size());

        Iterator<TypedTree> li = tl.iterator();
        while (li.hasNext()) {
            TypedTree tt = li.next();
            Tab.ln ("typed tree: " + tt.str());
        }

        Tab.ln ("TestTypedTree: exiting");
    }
}
