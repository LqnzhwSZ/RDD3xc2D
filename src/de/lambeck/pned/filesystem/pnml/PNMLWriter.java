package de.lambeck.pned.filesystem.pnml;

import java.io.*;

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
     * Dies ist eine Referenz zum Java Datei Objekt.
     */
    private File pnmlDatei;

    /**
     * Dies ist eine Referenz zum XML Writer. Diese Referenz wird durch die
     * Methode startXMLDocument() initialisiert.
     */
    private XMLStreamWriter xmlWriter = null;

    /**
     * Reference to the {@link FileOutputStream}. (Defined as class attribute
     * because the xmlWriter (that is using it) is used in different methods.)
     */
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
            /* Make sure that previous FileOutputStream is closed and null. */
            this.fos = (FileOutputStream) safeOutputStreamClose(this.fos);

            /* Create a new FileOutputStream. */
            this.fos = new FileOutputStream(pnmlDatei);

            /* Create a new instance of the XMLStreamWriter. */
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            xmlWriter = factory.createXMLStreamWriter(fos, "UTF-8");

            // XML Dokument mit Version 1.0 und Kodierung UTF-8 beginnen
            xmlWriter.writeStartDocument("UTF-8", "1.0");
            newLine();

            xmlWriter.writeStartElement("pnml");
            newLine();

            insertSpacers(1);
            xmlWriter.writeStartElement("net");
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

        if (xmlWriter != null) {
            try {
                insertSpacers(1);
                xmlWriter.writeEndElement();
                newLine();

                xmlWriter.writeEndElement();
                newLine();
                xmlWriter.writeEndDocument();

                xmlWriter.close();

                /* Close and set the FileOutputStream to null. */
                this.fos = (FileOutputStream) safeOutputStreamClose(this.fos);

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

        if (xmlWriter != null) {
            try {
                insertSpacers(2);
                xmlWriter.writeStartElement("", "place", "");
                xmlWriter.writeAttribute("id", id);
                newLine();

                insertSpacers(3);
                xmlWriter.writeStartElement("", "name", "");
                newLine();

                insertSpacers(4);
                xmlWriter.writeStartElement("", "value", "");
                xmlWriter.writeCharacters(label);
                xmlWriter.writeEndElement();
                newLine();

                insertSpacers(3);
                xmlWriter.writeEndElement();
                newLine();

                insertSpacers(3);
                xmlWriter.writeStartElement("", "initialMarking", "");
                newLine();

                insertSpacers(4);
                xmlWriter.writeStartElement("", "token", "");
                newLine();

                insertSpacers(5);
                xmlWriter.writeStartElement("", "value", "");
                xmlWriter.writeCharacters(initialMarking);
                xmlWriter.writeEndElement();
                newLine();

                insertSpacers(4);
                xmlWriter.writeEndElement();
                newLine();

                insertSpacers(3);
                xmlWriter.writeEndElement();
                newLine();

                insertSpacers(3);
                xmlWriter.writeStartElement("", "graphics", "");
                newLine();

                insertSpacers(4);
                xmlWriter.writeStartElement("", "position", "");
                xmlWriter.writeAttribute("x", xPosition);
                xmlWriter.writeAttribute("y", yPosition);
                xmlWriter.writeEndElement();
                newLine();

                insertSpacers(3);
                xmlWriter.writeEndElement();
                newLine();

                insertSpacers(2);
                xmlWriter.writeEndElement();
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

        if (xmlWriter != null) {
            try {
                insertSpacers(2);
                xmlWriter.writeStartElement("", "transition", "");
                xmlWriter.writeAttribute("id", id);
                newLine();

                insertSpacers(3);
                xmlWriter.writeStartElement("", "name", "");
                newLine();

                insertSpacers(4);
                xmlWriter.writeStartElement("", "value", "");
                xmlWriter.writeCharacters(label);
                xmlWriter.writeEndElement();
                newLine();

                insertSpacers(3);
                xmlWriter.writeEndElement();
                newLine();

                insertSpacers(3);
                xmlWriter.writeStartElement("", "graphics", "");
                newLine();

                insertSpacers(4);
                xmlWriter.writeStartElement("", "position", "");
                xmlWriter.writeAttribute("x", xPosition);
                xmlWriter.writeAttribute("y", yPosition);
                xmlWriter.writeEndElement();
                newLine();

                insertSpacers(3);
                xmlWriter.writeEndElement();
                newLine();

                insertSpacers(2);
                xmlWriter.writeEndElement();
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

        if (xmlWriter != null) {
            try {
                insertSpacers(2);
                xmlWriter.writeStartElement("", "arc", "");
                xmlWriter.writeAttribute("id", id);
                xmlWriter.writeAttribute("source", source);
                xmlWriter.writeAttribute("target", target);
                xmlWriter.writeEndElement();
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

    /** Inserts a new line for better readability for humans. */
    private void newLine() {
        try {
            xmlWriter.writeCharacters(System.getProperty("line.separator"));
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
                xmlWriter.writeCharacters(spacer);
            }
        } catch (XMLStreamException e) {
            System.err.println("Unable to write spacers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ensures that the previous instance of the specified {@link OutputStream}
     * is closed and null.
     * 
     * @param os
     *            The {@link OutputStream}
     * @return The reset {@link OutputStream}
     */
    private OutputStream safeOutputStreamClose(OutputStream os) {
        if (os == null) { return null; }

        try {
            os.flush();
            os.close();
        } catch (IOException e) {
            // NOP
        }
        os = null;
        return os;
    }

}
