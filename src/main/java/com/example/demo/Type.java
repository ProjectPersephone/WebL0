package com.example.demo;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.*;

import com.example.demo.AUGType; // for now: S, T, Oxy

public class Type implements Comparable<Type> {
    static HashMap<AUGType,Type> index = new HashMap<AUGType,Type>();
    static Set<Type> uniques = new TreeSet<>();

    AUGType type;

// if type = O:
    Type x;
    Type y;

    // not crazy about this, because it's O(n), n=# of nodes in tree
    // but only in some terribly bad case


    public int compareTo(Type t) {
        int c = 0;
        assertNotNull(t);

        if (this != t) {
//            Tab.ln ("comparing " + t  + " to " + this);
            if (type == AUGType.O && t.type == AUGType.O) {
//                Tab.ln ("Both t and this are O-type");
                assertNotNull(x);
                assertNotNull(y);
                assertNotNull(t.x);
                assertNotNull(t.y);

                c = x.compareTo (t.x);
                if (c == 0)
                    c = y.compareTo (t.y);
            }
            else {
//                Tab.ln ("Comparing " + this.type + " to " + t.type);
                c = this.type.compareTo (t.type);
            }
        }

//        Tab.ln (this.toString() + " compared to " + t.toString() + " = " + c);
        return c;
    }

    public Type fxy(Type q) {
        assertNotNull(q);
/*
        if (type != AUGType.O) {    // prime
            if (q.type == AUGType.O)
                if (this == q.x) {
                    Tab.ln (this.toString() + "." + "fxy(" + q.toString() + ") = q.y =" + q.y.toString());
                    return q.y;
                }
        }
*/
        if (type == AUGType.O) {
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

    private Type(AUGType augt, Type one, Type the_other) {
        Tab.ln ("******** Type constructor called:" + augt + "," + one + "," + the_other);
        type = augt;
        x = one;
        y = the_other;
    }

    public String toString() {
        String s = this.type.name();

        if (type == AUGType.O) {
            assertNotNull (x);
            assertNotNull (y);
            return s + "(+" + x.toString() + "=" + y.toString() + ")";
        }
        else
            return s;
    }

    static public Type of (AUGType augt, Type t_x, Type t_y) {
        Tab.ln ("Trying insert of " + augt + "," + t_x + ", " + t_y);

        Type tr = new Type (augt, t_x, t_y);
// boolean tmp = Tab.trace(false);     // avoid a million compare traces
        if (augt == AUGType.O) { 
            for (Type t : uniques) {        // list may get huge, but not during initialization at least
                if (tr.compareTo(t) == 0)
                    return t;
                }
        }
// Tab.trace (tmp);

        Type t = index.get(augt);

//        if (t != null && t.x == t_x && t.y == t_y)
        if (t != null && t.compareTo(tr) == 0)
            return t;

        index.put (augt, tr);
        uniques.add (tr);

        Tab.ln ("**************** Type.of: created " + tr + "***********");

        if (tr.x != null && tr.x.type == AUGType.IS && tr.y != null && tr.y.type == AUGType.GOOD) {
            Tab.ln ("==== in Type of creation, tr = " + tr.hashCode() + "==========================");
        }


        return tr;
    }
/*
    static public Type term () {
        return Type.of (AUGType.T,null,null);
    }

    static public Type sentence () {
        return Type.of (AUGType.S,null,null);
    }
    */

    public static String ls_str (Set<Type> l) {
        String s = "{";
        if (l != null) {
            String delim = "";
            for (Type t : l) {
                s += delim + t.toString();
                delim = ",";
            }
        }

        s += "}";

        return s;
    }

    public static String all_types() {
        String s = "all types=";
/*
        for (AUGType k : index.keySet()) {
            s += index.get(k).toString();
        }
*/
        for (Type t : uniques) {
            s += t.toString() + " ";
        }
        s += "\n";

        return s;
    }
}