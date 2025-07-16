package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Text;
import org.commonmark.node.BulletList;
import org.commonmark.node.Document;

import com.example.demo.Post;

import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Set;

import com.example.demo.Cache;
import com.example.demo.__;
import com.example.demo.Compound;
import com.example.demo.Visitor;
import com.example.demo.WordVisitor;

@Controller
@RequestMapping("/")
public class WelcomeController {

    Atoms ___ = new Atoms();

    // inject via application.properties
    @Value("${welcome.message}")
    private String message;

    @GetMapping
    public String main(Model model) {
//        model.addAttribute("message", message);
//        model.addAttribute("tasks", tasks);

        return "jstree"; //view
    }

    // /deliberatorium?name=Eliza
    @GetMapping("/deliberatorium")
    public String mainWithParam(
            @RequestParam(name = "name", required = false, defaultValue = "Eliza") 
			String name, Model model) {

        model.addAttribute("message", name);

        return "argue"; //view
    }

    @GetMapping("/edit")
    public String mainNothingSpecial(Model model) {
        System.out.println ("Hit nsm editor ...");
        model.addAttribute("post", new Post());

        return "edit";
    }

    @PostMapping("/edit")
    public String save(Post post, Model model) {
                                                                __.reset();
                                                                __.push_trace(true);
        if (post == null) {                                     __.ln ("save: post is null, strangely....");
        }
        String content = post.getContent();                     __.ln ("In /edit:save, content = " + content);

        if (content == null) {
            model.addAttribute("post", null);                   __.pop_trace();
            return "saved";
        }

                                                __.push_trace(true);
        Node doc = markdownToDocument(content);
        WordVisitor wv = new WordVisitor();
        WordVisitor.init();
        doc.accept(wv);

                                                __.ln ("Word list");
        for (String s : WordVisitor.word_list) {
            __.ln ("-> " + s);
        }
        LinkedList<TypedTree> glommed = Sentence.Glom(WordVisitor.word_list);
                                                __.pop_trace();

                                                __.push_trace(true);
        Cache Cwv = Cache.cache (null, glommed);

        __.ln ("-------------- Cwv -----------");
        __.ln ("Cwv.c = " + Cwv.c);
        __.ln ("");
        __.ln (TypedTree.ls_ls_ls_str (Cwv.c));
        __.ln ("------------------------------");
                                                __.pop_trace();       
        Visitor v = new Visitor();  
//        Node doc = markdownToDocument(content);
        doc.accept(v);
        v.indented.show();
        v.indented.postprocess();  // cross fingers ...
                                                                __.reset();
        v.indented.show();
                                                                __.trace(true);
        TypedTree.nested_pp(v.indented);                        __.trace(false);

                                                                __.trace(true);
        LinkedList<Compound> results = Compound.load_and_run(v.indented);
                    __.ln ("");
                    __.ln ("---------------------------------------------------------------");
                    __.ln ("Results: " + results);
                    __.ln ("---------------------------------------------------------------");
        
        String h = markdownToHTML(content); // linebreaks lost even tho editor keeps
        String[] lines = content.split("\\R");
        // String s = ""; // really need string array, processed on HTML template side
        
        h += "<i>Answer:</i><p>";
        for (Compound c : results) {
            h += c.toString() + "<br>";
        }
        h += "</p>";
        post.setHtml(h);

        JSONArray ja_new = v.toJSON();              __.ln ("Array of JSON parses with blocks=" + ja_new);

        String jas = ja_new.toString();             __.ln ("Array of JSON parses=" + jas);

        model.addAttribute("message", jas);
        model.addAttribute("post", post);
                                                        __.pop_trace();
        return "saved";
    }

    private String markdownToHTML(String markdown) {
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        Node document = markdownToDocument (markdown);

        return renderer.render(document);
    }

    private Node markdownToDocument (String markdown) {
        Parser parser = Parser.builder().build();
        Node node = parser.parse(markdown);

        return node;
    }

    /*
// should really look at whether Jackson can do most of this JSON construction

    public static String OrderToJSON(Order order) {
        return order.toString();
    }

    public static JSONObject TypeToJSON(Valence ty) {
        JSONObject o = new JSONObject();
        o.put ("type", ty.toString());
        return o;
    }

    public static JSONObject TreeToJSON(TypedTree t) {
        JSONObject o = new JSONObject();
        o.put ("order", OrderToJSON (t.order));
        if (t.order == Order.NEITHER) {
            o.put ("atom", t.lexeme); 
        } else {
            JSONObject b = TypedTreeToJSON (t.before);
            JSONObject a = TypedTreeToJSON (t.after);
            o.put ("before", b);
            o.put ("after",  a);
        }
        return o;
    }

    public static JSONObject TypedTreeToJSON (TypedTree tt) {
        JSONObject o = new JSONObject();
        o.put ("tree", TreeToJSON (tt));
        Set<Valence> ls_ty = tt.types;
        JSONArray a = new JSONArray();
        Iterator<Valence> ty_it = ls_ty.iterator();
        while (ty_it.hasNext()) {
            JSONObject ot = TypeToJSON (ty_it.next());
            a.add (ot);
        }
        o.put ("types", a);
        return o;
    }

    public static void JSONTypedTreeShow (JSONObject o) {   //- __.ln ("JSONTypedTreeShow entered");
        assertNotNull (o);                                  //- __.ln ("getting types JSONTypedTreeShow entered");
        JSONArray ty_ls = (JSONArray) o.get("types");
        assertNotNull (ty_ls);                              //-  __.ln ("getting first elt of type list ");
        JSONObject ot = (JSONObject) ty_ls.get(0);
        assertNotNull (ot);                                 //-  __.ln ("getting type of tree node");
        String ty = (String) ot.get("type");
        assertNotNull (ty);                                 //-  __.ln ("getting tree stuff");
        JSONObject tr = (JSONObject) o.get ("tree");

        assertNotNull (tr);                                 //- __.ln ("getting app order");
        String ord  = (String) tr.get ("order");
        assertNotNull (ord);                                //- __.ln ("ord=" + ord);
        if (ord != "NEITHER") {
            __.ln ("[" + ty + "]");                         //- __.ln ("getting before");
            JSONObject b = (JSONObject) tr.get ("before");
            assertNotNull (b);                              //- __.ln ("getting after");
            JSONObject a = (JSONObject) tr.get ("after");
            assertNotNull (a);
            JSONTypedTreeShow (b);
            JSONTypedTreeShow (a);
        }
        if (ord == "NEITHER") {                             //- __.ln ("getting atom");
            String atom = (String) tr.get ("atom");
            assertNotNull (atom);                           //- __.ln ("\"" + atom + "\" [" + ty + "]");
        }

    }
    */

    // maybe broken now ---
    @GetMapping("/sandbox")
    public String sandbox(Model model) {
        System.out.println("Entered sandbox");
                                                                __.reset();
        // String Str_nw = "IF I THINK I IS BAD";
        // String Str_nw = "I SAY I BE BAD";

        String Str_nw = "I BE GOOD";
        System.out.println("About to make new sentence");
        Sentence S = new Sentence (Str_nw);
        Cache C = Cache.cache (S);
        String s = "";

        LinkedList<TypedTree> tt_ls = C.get(0,0);
        System.out.println("...entering tt loop...");
        for (TypedTree tt : tt_ls) {
            JSONObject json = Visitor.TypedTreeToJSON (tt);
            s += json.toString();
            System.out.println ("s=" + s);                      __.reset();
            Visitor.JSONTypedTreeShow (json);
        }

        model.addAttribute("message", s);
//        model.addAttribute("tasks", tasks);

        System.out.println("Returning from sandbox");
        return "sandbox"; //view
    }

}
