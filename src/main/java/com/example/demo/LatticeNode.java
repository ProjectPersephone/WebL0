package com.example.demo;

import java.util.*;

import com.example.demo.TypedTree;
import com.example.demo.TrellisCache;

// Instead of a typical vector-of-vectors array, use a lattice of
// nodes and a custom memory manager for them, to save on GC overhead.
//
// Consider initializing with a "sentinel" node for the before/after pointers.
// There could be a kind of "null application" function associated with
// them, like "space between words", so that it's not just a safe way
// to terminate.
//
// Modify algorithm so it goes word-by-word, starting from the beginning?
// There might even be efficiency improvements this way.

public class LatticeNode {
    private LatticeNode lower[];
    public int number;
    public LinkedList<TypedTree> ltt;

    public LatticeNode(TrellisCache c, int n) {
        number = n;
        lower = new LatticeNode[2];
        lower[0] = null;
        lower[1] = null;
        ltt = new LinkedList<TypedTree>();
    }

    private LatticeNode     lower(TrellisCache c, int i) {
        return lower[i^c.transposed];
        }
    private LatticeNode set_lower(TrellisCache c, int i, LatticeNode ln) {
        lower[i^c.transposed] = ln;
        return ln;
    }

    public LatticeNode     lower_left(TrellisCache c) { return lower(c, 0); }
    public LatticeNode set_lower_left(TrellisCache c, LatticeNode ln) {
        return set_lower(c, 0, ln);
    }

    public LatticeNode     lower_right(TrellisCache c) { return lower(c, 1); }
    public LatticeNode set_lower_right(TrellisCache c, LatticeNode ln) {
        return set_lower(c, 1, ln);
    }
}