package com.example.demo;

import org.junit.jupiter.api.Assertions;

import java.util.*;

import com.example.demo.Order;
import com.example.demo.Nucleus;
import com.example.demo.Valence;
import com.example.demo.TypedTree;
import com.example.demo.Sentence;
import com.example.demo.Split;

public class Splits {
    LinkedList<Split> all_splits;

    public Splits (LinkedList<TypedTree> S) {
        int size = S.size();

        all_splits = new LinkedList<Split>();

        for (int i = 1; i < size; ++i) {
            Split sp = new Split (S, i);
            all_splits.add(sp);
        }
    }

    public String str() {
        String s = "[";
        Iterator<Split> ls = all_splits.iterator();
        while (ls.hasNext()) {
            s += ls.next().str();
            if (ls.hasNext())
                s += " ";
        }
        return s + "]";
    }
}