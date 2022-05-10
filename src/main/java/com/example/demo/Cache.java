package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demo.Sentence;
import com.example.demo.TypedTree;
import com.example.demo.__;

import java.util.*;

public class Cache {
    LinkedList<LinkedList<LinkedList<TypedTree>>> c;

    public Cache() {
        super();
        c = new LinkedList<LinkedList<LinkedList<TypedTree>>>();
    }

    public LinkedList<TypedTree> get(int i,int j) {
        return (c.get(i).get(j));
    }

    public void add_to_row (int i, LinkedList<TypedTree> tts) {
        c.get(i).add (tts);
    }

    public String str() {
        String s = "[";
        Iterator<LinkedList<LinkedList<TypedTree>>> row_it = c.iterator();
        while (row_it.hasNext()) {
            s += TypedTree.ls_ls_str (row_it.next());
            if (row_it.hasNext())
                s += " ";
        }
        return s + "]";
    }

    public Cache (TypedTree tt) {
        LinkedList<LinkedList<TypedTree>> tt1 = new LinkedList<LinkedList<TypedTree>>();
        LinkedList<TypedTree> one_elt = new LinkedList<TypedTree>();
        one_elt.add (tt);
        tt1.add (one_elt);
        c = new LinkedList<LinkedList<LinkedList<TypedTree>>>();
        c.add (tt1);
    }

// -- guessing that "is" meant something like "intermediates"
// build a b | trace ("build " ++ show a ++ " " ++ show b) False = undefined
// build typed_tree (row:rest_of_rows)
//  = g (reverse intermediates) row : intermediates
//    where intermediates       = build typed_tree rest_of_rows
//          g is row = [ r | (i,t) <- zip is row,
//                        ti   <- i,
//                        tt   <- t,
//                        r    <- combination_of ti tt ]

    private static LinkedList<LinkedList<TypedTree>>
    build (TypedTree parent, TypedTree tt, LinkedList<LinkedList<LinkedList<TypedTree>>> C) {
        assertNotNull (tt);
        assertNotNull (C);       __.ln ("build: tt=" + tt.str() + " C=" + TypedTree.ls_ls_ls_str (C));
        LinkedList<LinkedList<TypedTree>> rv_row = new LinkedList<LinkedList<TypedTree>>();

        if (C.size() == 0) {         __.ln ("in build C.size()==0...");
    // could call new Cache (tt) here, probably
            LinkedList<TypedTree> one_elt = new LinkedList<TypedTree>();
            one_elt.add (tt);
            rv_row.add (one_elt);    __.ln ("size = 0 case: rv_row=" + TypedTree.ls_ls_str(rv_row));
            return rv_row;
        }
        // From Main.bak version, which I think worked OK:

        // g :: HasCallStack => Row -> Row -> [Typed_Tree]
        // g is row = do { let x = [ r | (i,t) <- zip is row,
        //                            ti   <- i,
        //                            tt   <- t,
        //                            r    <- combination_of ti tt ]
        //               ; x
        //               }
        // build :: HasCallStack => Typed_Tree -> Cache -> Row 
        // build typed_tree (row:rest_of_rows)
        //  =  do { let interms = build typed_tree rest_of_rows
        //        ; let x = g (reverse interms) row : interms
        //        ; x
        //        }

        LinkedList<TypedTree> rv_row_elt1 = new LinkedList<TypedTree>();

        LinkedList<LinkedList<TypedTree>> row = C.removeFirst();  __.ln ("After removeFirst, calling build on remainder, C.size()=" + C.size());
        LinkedList<LinkedList<TypedTree>> interms = build (parent, tt, C); __.ln ("row.size()=" + row.size() + " interms.size()=" + interms.size()); __.ln ("After build with tt=" + tt.str() + ", getting interms desc iter:");
        Iterator<LinkedList<TypedTree>> is_it = interms.descendingIterator();  __.ln ("Got is_it, getting row_it:");
        Iterator<LinkedList<TypedTree>> row_it = row.iterator();  __.ln ("Starting is_it loop:");
        while (is_it.hasNext()) {
            LinkedList<TypedTree> i = is_it.next();    __.ln ("i=" + TypedTree.ls_str(i));

            LinkedList<TypedTree> t = row_it.next();   __.ln ("t=" + TypedTree.ls_str(t));
                                                       __.ln ("Starting inner loop on i_it"); __.o__();
            Iterator<TypedTree> i_it = i.iterator();
            while (i_it.hasNext()) {
                TypedTree ti = i_it.next();  __.ln ("At ti=" + ti.str()); __.ln ("Starting inner loop on t_it");
                                             __.o__();
                Iterator<TypedTree> t_it = t.iterator();
                while (t_it.hasNext()) {
                    tt = t_it.next();        __.ln ("tt=" + tt.str());
                    rv_row_elt1.addAll (TypedTree.combine (ti, tt)); // parent null may be wrong
                }
                                             __.__o();
            }  __.__o();
        }
        LinkedList<LinkedList<TypedTree>> x = new LinkedList<LinkedList<TypedTree>>();
        x.add (rv_row_elt1);
        x.addAll (interms);     __.ln ("End of is_it loop, returning x=" + TypedTree.ls_ls_str(x));
        return x;
    }

    public Cache transpose () {
                                     __.ln ("transpose just entered"); __.ln ("this=" + this.str());
        Cache T_A = new Cache();     __.ln ("new T_A cache created, empty");
        Iterator<LinkedList<LinkedList<TypedTree>>> A_i;
        LinkedList<LinkedList<TypedTree>> A_i_ls;
        Iterator<LinkedList<LinkedList<TypedTree>>> T_A_i;
        LinkedList<LinkedList<TypedTree>> T_A_i_ls;
        Iterator<LinkedList<TypedTree>> A_j;

        A_i = c.iterator();          __.ln ("Initializing list headers of T_A");
        while (A_i.hasNext()) {
            T_A.c.add (new LinkedList<LinkedList<TypedTree>>());
            A_i.next();
        }

        A_i = c.iterator();
        while (A_i.hasNext()) {
            A_i_ls = A_i.next();
            A_j = A_i_ls.iterator();
            T_A_i = T_A.c.iterator();

            while (A_j.hasNext()) {
                LinkedList<TypedTree> A_tt = A_j.next();
                T_A_i_ls = T_A_i.next();
                T_A_i_ls.add (A_tt);
            }
        }            __.ln ("T_A=" + T_A.str()); __.ln ("Exiting transpose()");
        return T_A;
    }

    public static Cache
    cache(TypedTree parent, LinkedList<TypedTree> tts) {
        assertNotNull(tts);      __.ln ("cache: tts = " + TypedTree.ls_str(tts));
        if (tts.size() == 1) {
            LinkedList<LinkedList<TypedTree>> x = new LinkedList<LinkedList<TypedTree>>();
            x.add (tts);
            Cache C = new Cache();
            C.c.add (x);
            return C;
        }
        // cache (this_typed_tree:the_rest)
        // = [build this_typed_tree (transpose rs)] ++ rs
        //    where rs = cache the_rest
        TypedTree t = tts.removeFirst();     __.ln ("t = " + t.str() + " tts now = " + TypedTree.ls_str(tts));
        Cache rs = cache(parent, tts);
        Cache trs = rs.transpose ();         __.ln ("About to enter build...");
        LinkedList<LinkedList<TypedTree>> btt = build (parent, t, trs.c); // __.ln ("Finished that build, now allocating new cache");
        Cache tc = new Cache();              __.ln ("After tc = new Cache()");
        tc.c.add (btt);                      __.ln ("After adding btt to tc");
        tc.c.addAll (rs.c);                 __.ln ("After addAll of rs.c to tc, returning");
        return tc;
    }

    public static Cache cache (Sentence S) {
        assertNotNull (S);           __.ln ("cache(S): S = " + S.str());
        assertNotNull (S.tt_list);   __.ln ("About to start really caching...");
        Cache c = cache (null, S.tt_list);  // null here may be wrong for sentences in blocks
        __.ln ("result c = " + c.str());
        return c;
    }
}