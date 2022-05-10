package com.example.demo;

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

    String label;

    // not crazy about this, because it's O(n), n=# of nodes in tree
    // but only in some terribly bad case

    public int compareTo(Valence t) {
        int c = 0;
        assertNotNull(t);

        __.push_trace(false);

        if (this != t) {
            __.ln ("comparing " + t  + " to " + this);
            if (n == Nucleus.O_ && t.n == Nucleus.O_) {
                __.ln ("Both t and this are O-type");
                assertNotNull(x);
                assertNotNull(y);
                assertNotNull(t.x);
                assertNotNull(t.y);

                c = x.compareTo (t.x);
                if (c == 0)
                    c = y.compareTo (t.y);
            }
            else {
                __.ln ("Comparing " + this.n + " to " + t.n);
                c = this.n.compareTo (t.n);
            }
        }

        __.ln (this.toString() + " compared to " + t.toString() + " = " + c);
        __.pop_trace();

        return c;
    }

    public Valence fxy(Valence q) {
        assertNotNull(q);

        if (n == Nucleus.O_) {
            if (x.compareTo(q) == 0) {
                __.ln (this.toString() + "." + "fxy(" + q.toString() + ") = y =" + y.toString());
                return y;
             }
             else {

                __.ln ("x:" + x.toString() + " =/= q:" + q.toString() + " -- FAILS");

                __.ln ("x="+x.hashCode()+" q="+q.hashCode());

                return null;
             }
        }

        __.ln (this.toString() + "." + "fxy(" + q.toString() + ") = null");
        return null;
    }

    private Valence(Nucleus n, Valence one, Valence the_other) {
        __.ln ("******** Valence constructor called:" + n + "," + one + "," + the_other);
        this.n = n;
        x = one;
        y = the_other;
        label = null;
    }

    public Valence(Nucleus n, Valence one, Valence the_other, String label) {
        __.ln ("******** Valence constructor called:" + n + "," + one + "," + the_other);
        this.n = n;
        x = one;
        y = the_other;
        this.label = label;
    }

    public String toString() {

        if (label != null) {
            return label;
        }

        String s = this.n.name();

        if (n == Nucleus.O_) {
            assertNotNull (x);
            assertNotNull (y);
            return s + "(+" + x.toString() + "⥅" + y.toString() + ")";
        }
        else
            return s;
    }

    static public Valence of (Nucleus n, Valence t_x, Valence t_y) {
        return Valence.of (n, t_x, t_y, null);
    }

    static public Valence of (Nucleus n, Valence t_x, Valence t_y, String label) {
        __.ln ("Trying insert of " + n + "," + t_x + ", " + t_y);

        Valence tr = new Valence (n, t_x, t_y, label);
// boolean tmp = __.trace(false);     // avoid a million compare traces
        if (n == Nucleus.O_) { 
            for (Valence t : uniques) {        // list may get huge, but not during initialization at least
                if (tr.compareTo(t) == 0)
                    return t;
                }
        }
// __.trace (tmp);

        Valence t = index.get(n);

//        if (t != null && t.x == t_x && t.y == t_y)
        if (t != null && t.compareTo(tr) == 0)
            return t;

        index.put (n, tr);
        uniques.add (tr);

        __.ln ("**************** Valence.of: created " + tr + "***********");

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