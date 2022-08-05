package com.example.demo;

import java.util.LinkedList;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Text;
import org.commonmark.node.BulletList;

// Need to generate a list of words without linebreaks or indentation
// Best to track line numbers and word positions, however
// This runs against limits in writing systems without word spacing

class WordVisitor extends AbstractVisitor {
    public static LinkedList<String> word_list;

    static void init() {
        if (word_list == null)
            word_list = new LinkedList<String>();
        word_list.clear();
    }

    @Override
    public void visit (BulletList bl) {             __.ln ("Hitting a BulletList: " + bl);
                                                    __.ln ("Calling visitChildren");
        visitChildren(bl);
    }

    @Override
    public void visit(Text text) {             __.ln ("Hitting a Text=: " + text);

        String [] list = text.getLiteral().split("\\W+");
        if (list != null)
            for (String s : list) word_list.add (s);
    } 
}
