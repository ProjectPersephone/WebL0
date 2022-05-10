package com.example.demo;

import java.util.*;

import com.example.demo.Order;
import com.example.demo.Nucleus;
import com.example.demo.Valence;
import com.example.demo.TypedTree;
import com.example.demo.Sentence;

public class Split {
    public LinkedList<TypedTree> before, after;

    Split (LinkedList<TypedTree> S, int i) {
        before = new LinkedList<TypedTree>(S.subList(0, i));
        after = new LinkedList<TypedTree>(S.subList(i, S.size()));
    }

    public String str() {
        String s = "{_";

        s += TypedTree.ls_str(before);
        s += ", ";
        s += TypedTree.ls_str(after); 

        s += "_}";
        return s;
    }
}