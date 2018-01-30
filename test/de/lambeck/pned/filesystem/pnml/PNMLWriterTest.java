package de.lambeck.pned.filesystem.pnml;

import java.io.File;

/**
 * Test of {@link PNMLWriter}
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class PNMLWriterTest {

    /**
     * Mit dieser Main Methode kann der PNMLWriter zum Testen aufgerufen werden.
     * Als erster und einziger Paramter muss dazu der Pfad der zu erstellenden
     * PNML Datei angegeben werden.
     * 
     * @param args
     *            Die Konsolen Parameter, mit denen das Programm aufgerufen
     *            wird.
     */
    public static void main(final String[] args) {
        if (args.length > 0) {
            File pnmlDatei = new File(args[0]);
            PNMLWriter pnmlWriter = new PNMLWriter(pnmlDatei);
            pnmlWriter.startXMLDocument();

            pnmlWriter.addPlace("place1", "Stelle 1", "100", "300", "1");
            pnmlWriter.addPlace("place2", "Stelle 2", "300", "300", "0");

            pnmlWriter.addTransition("transition1", "Transition A", "200", "200");
            pnmlWriter.addTransition("transition2", "Transition B", "200", "400");

            pnmlWriter.addArc("arc1", "transition1", "place1");
            pnmlWriter.addArc("arc2", "place1", "transition2");
            pnmlWriter.addArc("arc3", "transition2", "place2");
            pnmlWriter.addArc("arc4", "place2", "transition1");

            pnmlWriter.finishXMLDocument();
        } else {
            // System.out.println("Bitte eine Datei als Parameter angeben!");
            String[] example = { "G:\\Aufgabenstellung\\Eigene Testdateien\\Test1.pnml" };
            main(example);
        }
    }

}
