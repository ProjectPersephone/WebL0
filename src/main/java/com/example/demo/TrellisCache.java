package com.example.demo;

import com.example.demo.LatticeNode;

// Logically, the Lattice is private to a cache.
// But the code of Hudak et al creates copies of caches with transposition
// while basically staying with the same lattice nodes because of
// lazy evaluation.
// One approach is here: a 'transposed' flag here that
// is used to compute logical (rather than physical) left/right
// and pass the cache pointer with every left/right access.
// This means creating a lot of virtual caches that will end up
// garbage-collected; we have to count on Javac to be smart
// about the virtual reference counts on local variables that
// point to newly created stuff in contexts.
//
// Another approach is to make a LatticeNode index class that
// points to the native cache for the node, has a direction flag,
// and a pointer to the node. This could save us from the "plot hole"
// of a node being in one cache, the cache pointer passed to getters/setters
// in another.
//
// For now, just bite the bullet of adding cache pointers to the parameters
// of lattice node constructors and accessors.
//
// Because the Hudak et al. algorithm seems to require a list reversal,
// we might need a logical list reversal, made possible with upper left
// and upper right pointers, and an index record that includes its
// own transposition indicators.

public class TrellisCache {

    LatticeNode root;
    int transposed; // 0 or 1, index into LatticeNode 2-element pointer array

    static int count = 0;

// There are a number of different ways to traverse/create.
// This one attaches down the left diagonal and grows out
// right diagonals, attaching to parents.

    public TrellisCache(int n) {
            root = null;
            transposed = 0;

            if (n <= 0) return;

            LatticeNode last_diag = new LatticeNode(this, -1);       

    // pass 1: make LinkedLists right diagonally, prepending from bottom to top
            for (int i = 1; i < n; ++i) {
                 LatticeNode last_node = null;
                for (int j = 0; j < i+1; ++j) {
                     LatticeNode tn = new LatticeNode(this, count++);
                     tn.set_lower_right(this, last_node);
                     last_node = tn;
                }

               last_node.set_lower_left(this, last_diag);
                last_diag = last_node;
            }

            root = last_diag;

    // pass 2: set the lower-left-pointing links
            for (LatticeNode diag = root; diag != null; diag = diag.lower_left(this)) {
                LatticeNode next_diag = diag.lower_left(this);
                if (next_diag == null)
                    break;
                for (   LatticeNode nl = next_diag.lower_right(this), nu = diag.lower_right(this);
                        nl != null;
                        nl = nl.lower_right(this), nu = nu.lower_right(this))
                    nu.set_lower_left(this,nl);
            }
    }

    public TrellisCache transpose () {
            TrellisCache tc = new TrellisCache(0); // size doesn't matter (virtual)
            tc.root = root;
            tc.transposed = transposed ^ 1;
            return tc;
    }
}
