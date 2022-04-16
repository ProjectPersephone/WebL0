package com.example.demo;

import org.junit.jupiter.api.Assertions;

import java.util.*;

import com.example.demo.Order;
import com.example.demo.Nucleus;
import com.example.demo.Valence;
import com.example.demo.TypedTree;

// These are actually 1-for-1 with TypedTree nodes, haven't seen a case against merging
// except that lexeme and before/after are mutually exclusive, suggesting that an
// abstract class would make more sense: one for leaves, the other for order+before/after

public class Tree {
    Order order; // order of application that yielded this tree node

    String lexeme; // presumptuous, but is it morpheme, morph, ...? Ultimately, phone? ???
                    // https://en.wikipedia.org/wiki/Emic_unit

    TypedTree before; // forward application
    TypedTree after;  // backward application 

    public Tree (String lexeme) {
        order = Order.NEITHER;
        this.lexeme = lexeme;
        before = null;
        after = null;
    }

    public Tree (Order order, TypedTree before, TypedTree after) {
        this.order = order;
        this.before = before;
        this.after = after;
    }

    public String str () {
        String s = order.name();
        if (s == "BEFORE") s = "<:";
        else s = ">:";
        if (order == Order.NEITHER)
            return "~:\"" + lexeme + "\"";
        else
            return s + "(" + before.str() + "," + after.str() + ")";
    }
}