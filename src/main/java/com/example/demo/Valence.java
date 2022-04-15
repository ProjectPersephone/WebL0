package com.example.demo;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.*;

import com.example.demo.Nucleus;

public class Valence implements Comparable<Valence> {
    static HashMap<Nucleus,Valence> index = new HashMap<Nucleus,Valence>();
    static Set<Valence> uniques = new TreeSet<>();

    Nucleus n;

// if n = O:
    Valence x;
    Valence y;

    // not crazy about this, because it's O(n), n=# of nodes in tree
    // but only in some terribly bad case

    public int compareTo(Valence t) {
        int c = 0;
        assertNotNull(t);

        Tab.push_trace(false);

        if (this != t) {
            Tab.ln ("comparing " + t  + " to " + this);
            if (n == Nucleus.O && t.n == Nucleus.O) {
                Tab.ln ("Both t and this are O-type");
                assertNotNull(x);
                assertNotNull(y);
                assertNotNull(t.x);
                assertNotNull(t.y);

                c = x.compareTo (t.x);
                if (c == 0)
                    c = y.compareTo (t.y);
            }
            else {
                Tab.ln ("Comparing " + this.n + " to " + t.n);
                c = this.n.compareTo (t.n);
            }
        }

        Tab.ln (this.toString() + " compared to " + t.toString() + " = " + c);
        Tab.pop_trace();

        return c;
    }

    public Valence fxy(Valence q) {
        assertNotNull(q);

        if (n == Nucleus.O) {
            if (x.compareTo(q) == 0) {
                Tab.ln (this.toString() + "." + "fxy(" + q.toString() + ") = y =" + y.toString());
                return y;
             }
             else {

                Tab.ln ("x:" + x.toString() + " =/= q:" + q.toString() + " -- FAILS");

                Tab.ln ("x="+x.hashCode()+" q="+q.hashCode());

                return null;
             }
        }

        Tab.ln (this.toString() + "." + "fxy(" + q.toString() + ") = null");
        return null;
    }

    private Valence(Nucleus n, Valence one, Valence the_other) {
        Tab.ln ("******** Valence constructor called:" + n + "," + one + "," + the_other);
        this.n = n;
        x = one;
        y = the_other;
    }

    public String toString() {
        String s = this.n.name();

        if (n == Nucleus.O) {
            assertNotNull (x);
            assertNotNull (y);
            return s + "(+" + x.toString() + "=" + y.toString() + ")";
        }
        else
            return s;
    }

    static public Valence of (Nucleus n, Valence t_x, Valence t_y) {
        Tab.ln ("Trying insert of " + n + "," + t_x + ", " + t_y);

        Valence tr = new Valence (n, t_x, t_y);
// boolean tmp = Tab.trace(false);     // avoid a million compare traces
        if (n == Nucleus.O) { 
            for (Valence t : uniques) {        // list may get huge, but not during initialization at least
                if (tr.compareTo(t) == 0)
                    return t;
                }
        }
// Tab.trace (tmp);

        Valence t = index.get(n);

//        if (t != null && t.x == t_x && t.y == t_y)
        if (t != null && t.compareTo(tr) == 0)
            return t;

        index.put (n, tr);
        uniques.add (tr);

        Tab.ln ("**************** Valence.of: created " + tr + "***********");

        if (tr.x != null && tr.x.n == Nucleus.BE && tr.y != null && tr.y.n == Nucleus.GOOD) {
            Tab.ln ("==== in Valence of creation, tr = " + tr.hashCode() + "==========================");
        }

        return tr;
    }

    public static String ls_str (Set<Valence> l) {
        String s = "{";
        if (l != null) {
            String delim = "";
            for (Valence t : l) {
                s += delim + t.toString();
                delim = ",";
            }
        }

        s += "}";

        return s;
    }

    public static String all_valences() {
        String s = "all valences=";

        for (Valence t : uniques) {
            s += t.toString() + " ";
        }
        s += "\n";

        return s;
    }
}