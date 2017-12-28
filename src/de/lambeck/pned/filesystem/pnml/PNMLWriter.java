package de.lambeck.pned.filesystem.pnml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Diese Klasse implementiert eine einfache XML Ausgabe für PNML Dateien.
 * 
 * Erweiterung durch
 * 
 * Neue Methoden: newLine() und insertSpacers()
 */
public final class PNMLWriter {

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
            String[] example = { "G:\\Testdateien\\Test1.pnml" };
            main(example);
        }
    }

    /**
     * Dies ist eine Referenz zum Java Datei Objekt.
     */
    private File pnmlDatei;

    /**
     * Dies ist eine Referenz zum XML Writer. Diese Referenz wird durch die
     * Methode startXMLDocument() initialisiert.
     */
    private XMLStreamWriter writer = null;
    private FileOutputStream fos = null;

    /**
     * Dieser Konstruktor erstellt einen neuen Writer für PNML Dateien, dem die
     * PNML Datei als Java {@link File} übergeben wird.
     * 
     * @param pnml
     *            Java {@link File} Objekt der PNML Datei
     */
    public PNMLWriter(final File pnml) {
        super();

        pnmlDatei = pnml;
    }

    /**
     * Diese Methode beginnt ein neues XML Dokument und initialisiert den XML
     * Writer für diese Datei.
     * 
     * @return Exit code 0 if completed without errors; 1 on IO errors; 2 on XML
     *         errors
     */
    public int startXMLDocument() {
        int result = -1;

        try {
        	this.fos = (FileOutputStream) saveOutputStreamClose(this.fos);
            this.fos = new FileOutputStream(pnmlDatei);
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            writer = factory.createXMLStreamWriter(fos, "UTF-8");

            // XML Dokument mit Version 1.0 und Kodierung UTF-8 beginnen
            writer.writeStartDocument("UTF-8", "1.0");
            newLine();

            writer.writeStartElement("pnml");
            newLine();

            insertSpacers(1);
            writer.writeStartElement("net");
            newLine();

            result = 0;

        } catch (FileNotFoundException e) {
            System.err.println(
                    "Die Datei " + pnmlDatei.getAbsolutePath() + " kann nicht geschrieben werden! " + e.getMessage());
            e.printStackTrace();
            result = 1;

        } catch (XMLStreamException e) {
            System.err.println("XML Fehler: " + e.getMessage());
            e.printStackTrace();
            result = 2;

        }

        return result;
    }

    /**
     * Diese Methode beendet das Schreiben eines Petrinetzes als XML Datei.
     * 
     * @return Exit code 0 if completed without errors; 2 on XML errors
     */
    public int finishXMLDocument() {
        int result = -1;

        if (writer != null) {
            try {
                insertSpacers(1);
                writer.writeEndElement();
                newLine();

                writer.writeEndElement();
                newLine();
                writer.writeEndDocument();

                writer.close();

                this.fos = (FileOutputStream) saveOutputStreamClose(this.fos);
                
                result = 0;

            } catch (XMLStreamException e) {
                System.err.println("XML Fehler: " + e.getMessage());
                e.printStackTrace();
                result = 2;

            }
        } else {
            System.err.println("Das Dokument wurde noch nicht gestartet!");
        }

        return result;
    }

    /**
     * Diese Methode fügt eine neue Stelle zum XML Dokument hinzu. Vor dieser
     * Methode muss startXMLDocument() aufgerufen worden sein.
     * 
     * @param id
     *            Indentifikationstext der Stelle
     * @param label
     *            Beschriftung der Stelle
     * @param xPosition
     *            x Position der Stelle
     * @param yPosition
     *            y Position der Stelle
     * @param initialMarking
     *            Anfangsmarkierung der Stelle
     * @return Exit code 0 if completed without errors; 2 on XML errors
     */
    public int addPlace(final String id, final String label, final String xPosition, final String yPosition,
            final String initialMarking) {
        int result = -1;

        if (writer != null) {
            try {
                insertSpacers(2);
                writer.writeStartElement("", "place", "");
                writer.writeAttribute("id", id);
                newLine();

                insertSpacers(3);
                writer.writeStartElement("", "name", "");
                newLine();

                insertSpacers(4);
                writer.writeStartElement("", "value", "");
                writer.writeCharacters(label);
                writer.writeEndElement();
                newLine();

                insertSpacers(3);
                writer.writeEndElement();
                newLine();

                insertSpacers(3);
                writer.writeStartElement("", "initialMarking", "");
                newLine();

                insertSpacers(4);
                writer.writeStartElement("", "token", "");
                newLine();

                insertSpacers(5);
                writer.writeStartElement("", "value", "");
                writer.writeCharacters(initialMarking);
                writer.writeEndElement();
                newLine();

                insertSpacers(4);
                writer.writeEndElement();
                newLine();

                insertSpacers(3);
                writer.writeEndElement();
                newLine();

                insertSpacers(3);
                writer.writeStartElement("", "graphics", "");
                newLine();

                insertSpacers(4);
                writer.writeStartElement("", "position", "");
                writer.writeAttribute("x", xPosition);
                writer.writeAttribute("y", yPosition);
                writer.writeEndElement();
                newLine();

                insertSpacers(3);
                writer.writeEndElement();
                newLine();

                insertSpacers(2);
                writer.writeEndElement();
                newLine();

                result = 0;

            } catch (XMLStreamException e) {
                System.err.println("Stelle " + id + " konnte nicht geschrieben werden! " + e.getMessage());
                e.printStackTrace();
                result = 2;

            }

        } else {
            System.err.println("Das Dokument muss zuerst gestartet werden!");
        }

        return result;
    }

    /**
     * Diese Methode fügt eine neue Transition zum XML Dokument hinzu. Vor
     * dieser Methode muss startXMLDocument() aufgerufen worden sein.
     * 
     * @param id
     *            Indentifikationstext der Transition
     * @param label
     *            Beschriftung der Transition
     * @param xPosition
     *            x Position der Transition
     * @param yPosition
     *            y Position der Transition
     * @return Exit code 0 if completed without errors; 2 on XML errors
     */
    public int addTransition(final String id, final String label, final String xPosition, final String yPosition) {
        int result = -1;

        if (writer != null) {
            try {
                insertSpacers(2);
                writer.writeStartElement("", "transition", "");
                writer.writeAttribute("id", id);
                newLine();

                insertSpacers(3);
                writer.writeStartElement("", "name", "");
                newLine();

                insertSpacers(4);
                writer.writeStartElement("", "value", "");
                writer.writeCharacters(label);
                writer.writeEndElement();
                newLine();

                insertSpacers(3);
                writer.writeEndElement();
                newLine();

                insertSpacers(3);
                writer.writeStartElement("", "graphics", "");
                newLine();

                insertSpacers(4);
                writer.writeStartElement("", "position", "");
                writer.writeAttribute("x", xPosition);
                writer.writeAttribute("y", yPosition);
                writer.writeEndElement();
                newLine();

                insertSpacers(3);
                writer.writeEndElement();
                newLine();

                insertSpacers(2);
                writer.writeEndElement();
                newLine();

                result = 0;

            } catch (XMLStreamException e) {
                System.err.println("Transition " + id + " konnte nicht geschrieben werden! " + e.getMessage());
                e.printStackTrace();
                result = 2;

            }

        } else {
            System.err.println("Das Dokument muss zuerst gestartet werden!");
        }

        return result;
    }

    /**
     * Diese Methode fügt eine neue Kante zum XML Dokument hinzu. Vor dieser
     * Methode muss startXMLDocument() aufgerufen worden sein.
     * 
     * @param id
     *            Indentifikationstext der Stelle
     * @param source
     *            Indentifikationstext des Startelements der Kante
     * @param target
     *            Indentifikationstext der Endelements der Kante
     * @return Exit code 0 if completed without errors; 2 on XML errors
     */
    public int addArc(final String id, final String source, final String target) {
        int result = -1;

        if (writer != null) {
            try {
                insertSpacers(2);
                writer.writeStartElement("", "arc", "");
                writer.writeAttribute("id", id);
                writer.writeAttribute("source", source);
                writer.writeAttribute("target", target);
                writer.writeEndElement();
                newLine();

                result = 0;

            } catch (XMLStreamException e) {
                System.err.println("Kante " + id + " konnte nicht geschrieben werden! " + e.getMessage());
                e.printStackTrace();
                result = 2;

            }

        } else {
            System.err.println("Das Dokument muss zuerst gestartet werden!");
        }

        return result;
    }

    /**
     * Inserts a new line for better readability for humans.
     */
    private void newLine() {
        try {
            writer.writeCharacters(System.getProperty("line.separator"));
        } catch (XMLStreamException e) {
            System.err.println("Unable to write a line separator: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inserts the specified number of spacers. This can be used to improve the
     * readability for humans.
     * 
     * @param number
     *            The number of tabs to insert
     */
    private void insertSpacers(int number) {
        String spacer = "\t";
        // String spacer = " ";

        try {
            for (int i = 0; i < number; i++) {
                writer.writeCharacters(spacer);
            }
        } catch (XMLStreamException e) {
            System.err.println("Unable to write spacers: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private OutputStream saveOutputStreamClose(OutputStream os) {
    	if (os == null) {
    		return null;
    	}
    	try {
			os.flush();
			os.close();
		} catch (IOException e) {
			// NOP
		}
    	os = null;
    	return os;
    }

    private InputStream saveInputStreamClose(InputStream os) {
    	if (os == null) {
    		return null;
    	}
    	try {
			os.close();
		} catch (IOException e) {
			// NOP
		}
    	os = null;
    	return os;
    }
}
