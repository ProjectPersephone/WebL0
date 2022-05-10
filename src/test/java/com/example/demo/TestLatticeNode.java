package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.demo.Nucleus;
import com.example.demo.Valence;
import com.example.demo.LatticeNode;
import com.example.demo.TrellisCache;

// Wasted a half-hour because I didn't take 10 minutes to write this test

public class TestLatticeNode {
    @Test
    public void main() throws Exception {
        TrellisCache c = new TrellisCache(3);
        LatticeNode ln = new LatticeNode(c, 666);
        assertNotNull (ln);
        LatticeNode right  = new LatticeNode(c, 777);
        ln.set_lower_right (c, right);
        LatticeNode t = ln.lower_right(c);
        assertNotNull(t);
        assertEquals(t,right);
        LatticeNode left = new LatticeNode(c, 888);
        ln.set_lower_left(c, left);
        t = ln.lower_left(c);
        assertNotNull(t);
        assertEquals(t,left);
    }
}