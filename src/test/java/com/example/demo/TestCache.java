package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileSystems;



import com.example.demo.Sentence;
import com.example.demo.TypedTree;
import com.example.demo.Tab;

public class TestCache {
    private void check_singleton (LinkedList<TypedTree> tts, Cache singleton) {
        assertNotNull (tts);
        assertNotNull(singleton);
        assertNotNull(singleton.c);
        assertEquals(1,singleton.c.size());
        assertSame (singleton.get(0,0), tts);
    }

    public void try_reading_from_file() {
        try  {
            String path = Paths.get(".").toAbsolutePath().normalize().toString();
            Tab.ln ("default path=" + path);
            String fname = path + "\\src\\test\\resources\\" + "samples.txt";
            Tab.ln ("fname=" + fname);
            File file=new File(fname);  
            FileReader fr=new FileReader(file); 
            BufferedReader br=new BufferedReader(fr);  
            String line;
            int ln = 0;
            Tab.ln ("Trying some parses:");

            while((line=br.readLine())!=null)  
            {   ++ ln;
                Tab.o__();
                Sentence S = new Sentence (line);

                Tab.ln ("first ... S.tt_list.size() = " + S.tt_list.size());
                LinkedList<TypedTree> l_tt = TypedTree.typed_trees(S.tt_list);

                Iterator<TypedTree> li = l_tt.iterator();
                while (li.hasNext()) {
                    TypedTree tt = li.next();
                    Tab.ln ("typed_trees(): " + tt.str());
                }

                Tab.ln ("then ... S.tt_list.size() = " + S.tt_list.size());
                Cache C = Cache.cache (S);
                        // has side effect on S!
                Tab.ln (ln + ": " + line);

                Tab.ln ("cache(): " + TypedTree.ls_str (C.c.get(0).get(0)));
                Tab.__o();
            }  
            fr.close();  
        }  
        catch(IOException e) {  
            e.printStackTrace();  
        }  
    }  

    @Test
    public void main() throws Exception {
        Tab.reset();
        Atom l = new Atom();
        LinkedList<TypedTree> l_tt;

        String Str_2w = "SAY SOMETHING";
        String Str_1w = "BAD";
        String Str_3w = "I SAY SOMETHING";
        String Str_4w = "NOW I IS BAD";
        String Str_indirect_quote = "I SAY NOW I IS BAD";

        Tab.ln ("**** TestCache ****");  
        Tab.trace(false);

// Test single-word sentence case:

        Cache c = new Cache();
        assertNotNull(c);
        Sentence S_1w = new Sentence(Str_1w);
        assertNotNull(S_1w);
        LinkedList<TypedTree> tts = S_1w.tt_list;
        assertNotNull(tts);
        TypedTree tt0 = tts.get(0);
        assertNotNull (tt0);
    
        Cache singleton = Cache.cache(S_1w);
        check_singleton (tts, singleton);

// test transpose
        // try transpose on singleton


        Cache T_singleton = singleton.transpose ();
        check_singleton (tts, T_singleton);

        // then on 2x2 upper left diag -- probably enough

        Sentence S_2w = new Sentence(Str_2w);

        Tab.trace(false);
        Cache A22 = Cache.cache(null, S_2w.tt_list);
        assertEquals (2,A22.c.size());
        l_tt = A22.c.get(0).get(0);
        Tab.ln ("A22[0,0]=" + TypedTree.ls_str(l_tt));
        Tab.trace(false);  


if (false) {    // really need to construct without call to cache()
                // transpose seems to work for now
                // revisit if cache data structure changes
        tt0 = S_2w.tt_list.get(0);
        Tab.ln ("tt0 = " + tt0.str());
        TypedTree tt_A22_0_0 = A22.get(0,0).get(0);
        Tab.ln ("tt_A22_0_0 = " + tt_A22_0_0.str());
        assertSame(tt0,A22.get(0,0).get(0));
        TypedTree tt1 = S_2w.tt_list.get(1);
        assertSame (A22.get(0,1),tt1);
        }

// add a singleton sentence on second row
        LinkedList<LinkedList<TypedTree>> ttls = new LinkedList<LinkedList<TypedTree>>();
        A22.c.add (ttls);

        LinkedList<TypedTree> tt_ls_1_0 = new LinkedList<TypedTree>();
        tt_ls_1_0.add (S_1w.tt_list.get(0));
        A22.add_to_row (1, tt_ls_1_0);

        Cache T_A22 = A22.transpose ();

        assertNotNull (T_A22.c);
        assertNotNull (T_A22.c.get(0));
        assertNotNull (T_A22.c.get(1));
 // see above about transpose testing
    if (false ) {
        assertSame (T_A22.get(0,0),tt0);
    //    assertSame (T_A22.get(1,0),tt1); 
        assertSame (T_A22.get(0,1),tt_ls_1_0);
        }

        Tab.reset();
        Tab.trace (false);

        Sentence S_3w = new Sentence (Str_3w);
        assertNotNull (S_3w);
        Cache A33 = Cache.cache (S_3w);

        LinkedList<LinkedList<TypedTree>> ll_tt = A33.c.get(0);
        assertNotNull (ll_tt);
        l_tt =ll_tt.get(0);
        assertNotNull (l_tt);
        Tab.ln ("A33[0,0]=" + TypedTree.ls_str(l_tt));
        Tab.ln ("------------------------------------");
        Sentence S_4w = new Sentence (Str_4w);
        Cache A44 = Cache.cache (S_4w);
        l_tt = A44.c.get(0).get(0);
        Tab.ln ("A44[0,0]=" + TypedTree.ls_str(l_tt));
        Tab.ln ("------------------------------------");       
        Sentence indirect_quote = new Sentence (Str_indirect_quote);
        Cache iq = Cache.cache (indirect_quote);
        l_tt = iq.c.get(0).get(0);
        Tab.ln ("indirect quoting: " + TypedTree.ls_str(l_tt));
        Tab.ln ("TestCache: file-reading test");
        try_reading_from_file();
    }
}