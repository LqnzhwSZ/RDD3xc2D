package de.lambeck.pned.filesystem.parser;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

@SuppressWarnings("javadoc")
public class DOMPnml {
    public static void main(String[] args) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder
                .parse(new File("G:\\Downloads\\2017_2018_ws_aufgabenstellung\\Beispiele\\Test - noname.pnml"));
        // System.out.println(document.getFirstChild().getTextContent());

        Element pnml = document.getRootElement();
        Element b = pnml.getChild("place").getChild("name");
        String c = b.getText();
    }
}
