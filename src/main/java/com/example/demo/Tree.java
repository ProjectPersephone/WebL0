package com.example.demo;

import org.junit.jupiter.api.Assertions;

import java.util.*;

import com.example.demo.Order;
import com.example.demo.AUGType;
import com.example.demo.Type;
import com.example.demo.TypedTree;

public class Tree {
    Order order;
    String atom; // type==NEITHER
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
        if (order == Order.NEITHER)
            return "\"" + atom + "\"";
        else
            return s + " " + "(" + before.str() + "," + after.str() + ")";
    }
}