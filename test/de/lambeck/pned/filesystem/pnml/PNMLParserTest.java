package de.lambeck.pned.filesystem.pnml;

import java.io.File;

/**
 * Test of {@link PNMLParser}
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class PNMLParserTest {

    /**
     * Mit dieser Main Methode kann der Parser zum Testen aufgerufen werden. Als
     * erster und einziger Paramter muss dazu der Pfad zur PNML Datei angegeben
     * werden.
     *
     * @param args
     *            Die Konsolen-Parameter, mit denen das Programm aufgerufen
     *            wird.
     */
    public static void main(final String[] args) {
        if (args.length > 0) {
            File pnmlDatei = new File(args[0]);
            if (pnmlDatei.exists()) {
                PNMLParser pnmlParser = new PNMLParser(pnmlDatei, null);
                pnmlParser.initParser();
                pnmlParser.parse();
            } else {
                System.err.println("Die Datei " + pnmlDatei.getAbsolutePath() + " wurde nicht gefunden!");
            }
        } else {
            // System.out.println("Bitte eine Datei als Parameter angeben!");
            String[] example = { "G:\\Aufgabenstellung\\Beispiele\\Beispiel-03.pnml" };
            // String[] example = { "G:\\Testdateien\\Fehlertests\\Test -
            // falsche Werte.pnml" };
            // String[] example = { "G:\\Testdateien\\Test1.pnml" };
            main(example);
        }
    }

}
