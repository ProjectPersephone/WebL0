package com.example.demo;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileSystems;

import com.example.demo.Sentence;
import com.example.demo.TypedTree;
import com.example.demo.Line;
import com.example.demo.Cache;
import com.example.demo.__;

public class TestLine  {
    @Test
    public void main () {
        __.reset();
        Atom l = new Atom();
        __.ln ("----- TestRPline ------");
        String Str_3 = "I IS BAD";
        Sentence S = new Sentence (Str_3);
        Cache C = Cache.cache (S);   // possible side-effects on S
        LinkedList<TypedTree> ttl = C.c.get(0).get(0);
        TypedTree tt = ttl.get(0);
        Line ln = new Line (0, tt);
        __.ln ("ln = " + ln.level + "/" + ln.line.str());
    }
}