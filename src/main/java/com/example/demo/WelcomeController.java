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
import com.example.demo.Tab;
import com.example.demo.Compound;


@Controller
@RequestMapping("/")
public class WelcomeController {

    private static Atom L = new Atom();
    private static FileWriter myFileWriter;
    String prolog_output_filename = "output.pl";

    // inject via application.properties
    @Value("${welcome.message}")
    private String message;

    private List<String> tasks = Arrays.asList(
        "Figure out why ServletInitializer stuff needed before",
        "Try to fully parameterized for the name Deliberatorium",
        "Why does addAttribute take a couple of different types?",
        "inject via application.properties -- how does this work?",
        "Back to getting junit tests working again",
        "look into multilingualism",
        "make argument tree active",
        "icons for argument tree for question, comment, etc.");

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

    private void markdownToParses (String content) {
        Node doc = markdownToDocument (content);

        String  s = doc.toString();

        Tab.ln ("markdownToParses: doc.toString() = " + s);
    }

    @PostMapping("/edit")
    public String save(Post post, Model model) {
        Compound npl = new Compound(Nucleus.IF);  // ************ just to make sure something works

        Tab.reset();

        if (post == null) {
            Tab.ln ("save: post is null, strangely....");
        }

        String content = post.getContent();

        Tab.ln ("In /edit:save, content = " + content);

        if (content == null) {
            model.addAttribute("post", null);
            return "saved";
        }

        String h = markdownToHTML(content); // linebreaks lost even tho editor keeps
        String[] lines = content.split("\\R");
        // String s = ""; // this really needs to be an array of strings
                        // and processed as such on the template side
        post.setHtml(h);

//        markdownToParses (content);
        try {
            myFileWriter = new FileWriter(prolog_output_filename);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        Visitor v = new Visitor();  
        Node doc = markdownToDocument(content);
        doc.accept(v);
        v.indented.show();
        v.indented.postprocess();  // cross fingers ...
        Tab.reset();
//        Tab.trace(true);
        v.indented.show();
//        Tab.trace(false);

        Tab.trace(true);
        TypedTree.nested_pp(v.indented);
        Tab.trace(false);

        JSONArray ja_new = v.toJSON();
        Tab.ln ("Array of JSON parses with blocks=" + ja_new);

        String jas = ja_new.toString();

        Tab.ln ("Array of JSON parses=" + jas);

        model.addAttribute("message", jas);
        model.addAttribute("post", post);

        try {
            myFileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return "saved";
    }

    private String markdownToHTML(String markdown) {
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        Node document = markdownToDocument (markdown);

        return renderer.render(document);
    }

    class Visitor extends AbstractVisitor {
        public NestedLines indented = new NestedLines();

        @Override
        public void visit (BulletList bl) {
            Tab.ln ("Hitting a BulletList: " + bl);
            
            Tab.ln ("Calling visitChildren");
            indented.indent();
            visitChildren(bl);
            indented.dedent();
        }

        @Override
        public void visit(Text text) {

            int wordCount = text.getLiteral().split("\\W+").length; // ???
            String Str_nw = text.getLiteral();
            Sentence S = new Sentence (Str_nw);
            Tab.reset();
            Tab.trace(false);
            Cache C = Cache.cache (S);
            LinkedList<TypedTree> tt_ls = C.get(0,0);
            if (tt_ls.isEmpty()) {
                Tab.ln ("visit: empty tt_ls");
                return;
            }
            TypedTree tt = tt_ls.get(0);
            Tab.ln (wordCount + " words in " + text.getLiteral() + ": " + tt.str());
            Tab.reset();
            Tab.trace(true);
            try {
                String pl = tt.prolog();
                pl += " ,,, ";
                 myFileWriter.write(pl);
                 Tab.ln("*** Wrote " + pl + " to " + prolog_output_filename + " ***");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            Tab.trace(false);
            indented.add (tt);
        }

        private JSONArray toJSONArray_helper (LinkedList<Line> ll) {
            JSONArray ja = new JSONArray();

            Tab.ln ("In toJSONArray_helper");

            for (Line ln : ll) {
                Tab.ln ("tab level = " + ln.level);
                JSONObject json = TypedTreeToJSON (ln.line);
                if (ln.block != null && ln.block.size() != 0) {
                    JSONArray sub = toJSONArray_helper(ln.block);
                    json.put ("block", sub);
                    Tab.ln ("adding to block: " + sub.toString());
                }
                ja.add (json);
                JSONTypedTreeShow (json);
            }

            Tab.ln ("Exiting toJSONArray_helper with ja = " + ja.toString());
            return ja;
        }
        // assume postprocessed already
        public JSONArray toJSON() {
            return toJSONArray_helper (indented.lines);
        }
    }

    private Node markdownToDocument (String markdown) {
        Parser parser = Parser.builder().build();
        Node node = parser.parse(markdown);

        return node;
    }

    private JSONObject documentToJSON (Document d) {
        JSONObject json = new JSONObject();

        return json;
    }

// should really look at whether Jackson can do most of this JSON construction

    public static String OrderToJSON(Order order) {
    //    return "\"" + order + "\"";
        return order.toString();
    }

    public static JSONObject TypeToJSON(Valence ty) {
        JSONObject o = new JSONObject();
        o.put ("type", ty.toString());
        return o;
    }

    public static JSONObject TreeToJSON(Tree t) {
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
        o.put ("tree", TreeToJSON (tt.tree));
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

    public static void JSONTypedTreeShow (JSONObject o) {
//-        Tab.ln ("JSONTypedTreeShow entered");
        assertNotNull (o);
//-                Tab.ln ("getting types JSONTypedTreeShow entered");
        JSONArray ty_ls = (JSONArray) o.get("types");
        assertNotNull (ty_ls);
//-                Tab.ln ("getting first elt of type list ");
        JSONObject ot = (JSONObject) ty_ls.get(0);
        assertNotNull (ot);
//-                Tab.ln ("getting type of tree node");
        String ty = (String) ot.get("type");
        assertNotNull (ty);
//-                Tab.ln ("getting tree stuff");
        JSONObject tr = (JSONObject) o.get ("tree");

        assertNotNull (tr);
//-                Tab.ln ("getting app order");
        String ord  = (String) tr.get ("order");
        assertNotNull (ord);
//-                Tab.ln ("ord=" + ord);
        if (ord != "NEITHER") {
            Tab.ln ("[" + ty + "]");
//-                    Tab.ln ("getting before");
            JSONObject b = (JSONObject) tr.get ("before");
            assertNotNull (b);
//-                    Tab.ln ("getting after");
            JSONObject a = (JSONObject) tr.get ("after");
            assertNotNull (a);
            JSONTypedTreeShow (b);
            JSONTypedTreeShow (a);
        }
        if (ord == "NEITHER") {
//-                    Tab.ln ("getting atom");
            String atom = (String) tr.get ("atom");
            assertNotNull (atom);
            Tab.ln ("\"" + atom + "\" [" + ty + "]");
        }

    }

    // maybe broken now ---
    @GetMapping("/sandbox")
    public String sandbox(Model model) {

        Tab.reset();

        // String Str_nw = "IF I THINK I IS BAD";
        String Str_nw = "I SAY I IS BAD";
        Sentence S = new Sentence (Str_nw);
        Cache C = Cache.cache (S);
        LinkedList<TypedTree> tt_ls = C.get(0,0);

        // TypedTree tt = tt_ls.get(0);
        String s = "";
        for (TypedTree tt : tt_ls) {
            JSONObject json = TypedTreeToJSON (tt);
            s += json.toString();
            System.out.println ("s=" + s);
            Tab.reset();
            JSONTypedTreeShow (json);
        }

        model.addAttribute("message", s);

//        model.addAttribute("message", message);
//        model.addAttribute("tasks", tasks);

        return "sandbox"; //view
    }

}
