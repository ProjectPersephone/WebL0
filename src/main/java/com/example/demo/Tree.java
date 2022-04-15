package com.example.demo;

import org.junit.jupiter.api.Assertions;

import java.util.*;

import com.example.demo.Order;
import com.example.demo.Nucleus;
import com.example.demo.Valence;
import com.example.demo.TypedTree;

// These are actually 1-for-1 with TypedTree nodes, haven't seen a case against merging
// except that atom and before/after are mutually exclusive, suggesting that an
// abstract class would make more sense: one for leaves, the other for order+before/after

public class Tree {
    Order order;  // type==NEITHER
    String atom;
    TypedTree before; // forward application
    TypedTree after;  // backward application 

    public Tree (String p_atom) {
        order = Order.NEITHER;
        atom = p_atom;
        before = null;
        after = null;
    }

    public Tree (Order p_order, TypedTree p_before, TypedTree p_after) {
        order = p_order;
        before = p_before;
        after = p_after;
    }

    public String str () {
        String s = order.name();
        if (s == "BEFORE") s = "<:";
        else s = ">:";
        if (order == Order.NEITHER)
            return "~:\"" + atom + "\"";
        else
            return s + "(" + before.str() + "," + after.str() + ")";
    }
}