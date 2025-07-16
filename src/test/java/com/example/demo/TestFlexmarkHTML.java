package com.example.demo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// mport org.jsoup.parseBodyFragment;

import org.junit.jupiter.api.Test;

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.*;

import com.example.demo.FlexmarkHTML;
import com.example.demo.__;

public class TestFlexmarkHTML {

// crappy translator of the HTML, won't work for multiply-nested lists:
        // From some sample code, figure it out:
        // 
        // PrettyPrinter prettyPrinter = new PrettyPrinter();
        // NodeTraversor nodeTraversor = new NodeTraversor(prettyPrinter);
        // nodeTraversor.traverse(document);

    private void test_HTML_to_NSM(Document doc) {


        Elements elements = doc.getAllElements();

        String C1 = null;
        String C2 = null;
        String td1 = null;
        
        for (Element element : elements) {

            switch (element.tagName()) {
                case "p":   __.ln(element.text());
                            break;
                case "th":  if (C1 != null) {
                                C2 = element.text();
                            }
                            else {
                                C1 = element.text();
                            }
                            break;
                case "td":  if (td1 != null) {
                                __.ln(C1 + " " + td1 + " " + C2 + " " + element.text());
                                td1 = null;
                            }
                            else {
                                td1 = element.text();
                            }
                            break;
                case "li":  __.ln ("\t" + element.text());
                            break;
                default:    // __.ln (element.tagName());
                            break;
            }
        }
    }

    private void some_tests(String some_markdown, Parser parser, HtmlRenderer renderer) {
        Node document = parser.parse(some_markdown);

        String html = renderer.render(document);

        __.trace(false);
        __.ln ("\nhtml renderer output:");
        __.ln(html);

        __.ln ("\nGetting just paragraphs:");
        Document doc = Jsoup.parseBodyFragment(html);
        Elements paras = doc.getElementsByTag("p");
        for (Element para : paras) {
            String paraText = para.text();
            __.ln ("paragraph: " + paraText);
        }

        __.ln ("\nGetting just data tags:");
        Elements tds = doc.getElementsByTag("td");
        for (Element td : tds) {
            String tdText = td.text();
            __.ln ("td: " + tdText);
        }

        __.trace(false);
        __.ln ("\ndoc.toString output:");
        String prettyHtml = doc.toString();
        __.ln(prettyHtml);

    }

    @Test
    public void main() throws Exception {

        __.reset();
        __.ln ("\n----------- Flexmark/Jsoup tryout ------------------\n");
        
        Atoms l = new Atoms();

        MutableDataSet options = new MutableDataSet();

        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));

        // uncomment to convert soft-breaks to hard breaks
        //options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        String NSM_test =
              "I IS BAD"
            + "\n"
            +"\n| WHEN I THINK | I CAN SAY |"
            +"\n| ---  |  --- |"
            +"\n| 'THIS'   | 'this' |"
            +"\n| 'THINK'   | 'think' |"
            +"\n"
            +"\nI WANT TO SAY THIS:"
            +"\n- SOMETHING"
            +"\n- SOMETHING ELSE"
            ;

        some_tests (NSM_test, parser, renderer);
    
        Node document = parser.parse(NSM_test);

        String html = renderer.render(document);

        __.ln ("\nGetting just paragraphs:");
        Document doc = Jsoup.parseBodyFragment(html);

        __.ln ("\nAll elements + text content:");
        test_HTML_to_NSM(doc);
    
        __.ln ("\n--------------------------------------------------------");

   // need this or the next test fails????? wow
        __.trace(false);
    }
}
