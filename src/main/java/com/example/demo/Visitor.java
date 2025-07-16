package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.LinkedList;
import java.util.Set;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Text;
import org.commonmark.node.BulletList;

import com.example.demo.TypedTree;  // strangely not needed

class Visitor extends AbstractVisitor {
    public NestedLines indented = new NestedLines();

    @Override
    public void visit (BulletList bl) {             __.ln ("Hitting a BulletList: " + bl);
                                                    __.ln ("Calling visitChildren");
        indented.indent();
        visitChildren(bl);
        indented.dedent();
    }

    @Override
    public void visit(Text text) {

        int wordCount = text.getLiteral().split("\\W+").length; // ???
        String Str_nw = text.getLiteral();
        Sentence S = new Sentence (Str_nw);

        Cache C = Cache.cache (S);
        LinkedList<TypedTree> tt_ls = C.get(0,0);
        if (tt_ls.isEmpty()) {                          __.ln ("visit: empty tt_ls");
            return;
        }
        TypedTree tt = tt_ls.get(0);
                                                        __.ln (wordCount + " words in " + text.getLiteral() + ": " + tt.str());

        indented.add (tt);
    }

    private JSONArray
        toJSONArray_helper (LinkedList<Line> ll) {           __.ln ("In toJSONArray_helper");
            JSONArray ja = new JSONArray();

            for (Line ln : ll) {                            __.ln ("tab level = " + ln.level);
                JSONObject json = TypedTreeToJSON (ln.line);
                if (ln.block != null && ln.block.size() != 0) {
                    System.out.println("Just before helper call");
                    System.out.flush();
                    JSONArray sub = toJSONArray_helper(ln.block);
                    json.put ("block", sub);            __.ln ("adding to block: " + sub.toString());
                }
                ja.add (json);
                JSONTypedTreeShow (json);
            }
                                                            __.ln ("Exiting toJSONArray_helper with ja = " + ja.toString());
            return ja;
        }
        // assume postprocessed already
        public JSONArray toJSON() {
            return toJSONArray_helper (indented.lines);
        }

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
}