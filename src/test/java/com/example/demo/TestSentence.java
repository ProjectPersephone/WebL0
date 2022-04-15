package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import com.example.demo.Nucleus;
import com.example.demo.Valence;
import com.example.demo.TypedTree;
import com.example.demo.Sentence;

// What Hudak et al. call a "Sentence" isn't really one

public class TestSentence {
    @Test
    public void main() throws Exception {
        String[] a = {"BAD","IS"};
        Sentence s = new Sentence(new String(a[0] + " " + a[1]));
        assertNotNull(s);
        LinkedList<TypedTree> l = s.tt_list;
        assertEquals(l.size(),2);
        Iterator<TypedTree> li = l.iterator();
        int i = 0;
        while (li.hasNext()) {
            TypedTree tt = li.next();
            assertNotNull(tt);
            Tree t = tt.tree;
            assertEquals(t.order, Order.NEITHER);
            String as = t.atom;
            assertNotNull (as);
            assertEquals(a[i],as);
            ++i;
        }
    }
}