package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.demo.LatticeNode;
import com.example.demo.TrellisCache;

public class TestTrellisCache {

    private TrellisCache try3(TrellisCache c) {
        LatticeNode lln,lrn;

        assertNotNull(c);
        assertNotNull(c.root);
        lln = c.root.lower_left(c);
        lrn = c.root.lower_right(c);
        assertNotNull(lln);
        assertNotNull(lrn);
        assertNotNull(lln.lower_right(c));
        assertNotNull(lrn.lower_left(c));

        assertEquals(lln.lower_right(c),lrn.lower_left(c));

        return c;
    }

    @Test
    public void main() throws Exception {
        LatticeNode ln0,ln1,lln,lrn;
        TrellisCache c = new TrellisCache(0);

        ln0 = new LatticeNode(c,-1);
        assertNotNull(ln0);
        ln1 = new LatticeNode(c,666);
        assertNotNull(ln1);
        assertEquals(ln1.number,666);

        TrellisCache l0,l1;
        l0 = new TrellisCache(0);

        assertEquals(l0.root,null);

        l1 = new TrellisCache(1);

        assertNotNull(l1);

        TrellisCache l_dumb;

        l_dumb = new TrellisCache(1);

        TrellisCache l2;

        l2 = new TrellisCache(2);

        assertNotNull(l2);

        TrellisCache l_fun = l2;

        assertNotNull(l_fun.root);
        LatticeNode l_fun_root = l_fun.root;
        assertNotNull(l_fun_root.lower_left(l_fun));


        assertNotNull(l_fun_root.lower_right(l_fun));

        l_fun = new TrellisCache(3);
        try3 (l_fun);

        // test of transpose:

        l0 = new TrellisCache(3);
        try3 (l0);
        l_fun = l0.transpose();
        try3 (l_fun);

        l1 = l_fun.transpose();
        assertEquals(l1.transposed,0);
        assertEquals(l_fun.transposed,1);   // no side effect here?
        assertEquals(l0.transposed,0);      // ... or here?
    }
}