package de.lambeck.pned.filesystem.pnml;

import java.awt.Point;
import java.io.*;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import de.lambeck.pned.elements.data.EPlaceToken;
import de.lambeck.pned.models.data.IDataModelController;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * Diese Klasse implementiert die Grundlage für einen einfachen PNML Parser.
 */
public class PNMLParser {

    private static boolean debug = false;

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
            // String[] example = { "G:\\Testdateien\\Fehlertests\\Test - falsche Werte.pnml" };
            // String[] example = { "G:\\Testdateien\\Test1.pnml" };
            main(example);
        }
    }

    /**
     * Dies ist eine Referenz zum Java Datei Objekt.
     */
    private File pnmlDatei;

    /**
     * Dies ist eine Referenz zum XML Parser. Diese Referenz wird durch die
     * Methode parse() initialisiert.
     */
    private XMLEventReader xmlParser = null;

    /**
     * Reference to the {@link FileInputStream}. (Defined as class attribute
     * because the xmlParser (that is using it) is used in different methods.)
     */
    private FileInputStream fis = null;

    /**
     * Diese Variable dient als Zwischenspeicher für die ID des zuletzt
     * gefundenen Elements.
     */
    private String lastId = null;

    /**
     * Dieses Flag zeigt an, ob der Parser gerade innerhalb eines Token Elements
     * liest.
     */
    private boolean isToken = false;

    /**
     * Dieses Flag zeigt an, ob der Parser gerade innerhalb eines Name Elements
     * liest.
     */
    private boolean isName = false;

    /**
     * Dieses Flag zeigt an, ob der Parser gerade innerhalb eines Value Elements
     * liest.
     */
    private boolean isValue = false;

    /**
     * Reference to the data model controller to transmit the elements after
     * read-in (and to forward status messages).
     */
    private IDataModelController dataModelController;

    /*
     * Attributes for the next element which will be passed to the data model
     * controller after reading all information from the PNML file.
     */

    /**
     * Stores the type ({@link EPNMLElement}) of the element that is parsed at
     * the moment.
     */
    private EPNMLElement nextElementType = null;

    /** Stores the id of the current element. */
    private String nextId = null;

    /** Stores the name of the current element. */
    private String nextName = null;

    /**
     * Stores the number of tokens ({@link EPlaceToken}) of the current element.
     */
    private EPlaceToken nextMarking = null;

    /** Stores the position ({@link Point}) of the current element. */
    private Point nextPosition = null;

    /** Stores the id of the source for the current arc. */
    private String nextSourceId = null;

    /** Stores the id of the target for the current arc. */
    private String nextTargetId = null;

    /** Stores if the PNML file had invalid values for the current element. */
    private boolean invalidValues = false;

    /**
     * Return values for the data model controller
     */
    private int exitCode = EPNMLParserExitCode.ZERO.getValue();

    // TODO Add I18NManager i18n for error messages?

    /**
     * Dieser Konstruktor erstellt einen neuen Parser für PNML Dateien, dem die
     * PNML Datei als Java {@link File} übergeben wird.
     * 
     * @param pnml
     *            Java {@link File} Objekt der PNML Datei
     * @param controller
     *            The data model controller
     */
    public PNMLParser(final File pnml, IDataModelController controller) {
        super();

        this.pnmlDatei = pnml;
        this.dataModelController = controller;
    }

    /**
     * Diese Methode öffnet die PNML Datei als Eingabestrom und initialisiert
     * den XML Parser.
     */
    public final void initParser() {
        try {
            /* Make sure that previous FileInputStream is closed and null. */
            this.fis = (FileInputStream) safeInputStreamClose(this.fis);

            /* Create a new FileInputStream. */
            this.fis = new FileInputStream(pnmlDatei);

            /* Create a new instance of the XMLEventReader. */
            XMLInputFactory factory = XMLInputFactory.newInstance();
            try {
                xmlParser = factory.createXMLEventReader(fis);
            } catch (XMLStreamException e) {
                System.err.println("XML Verarbeitungsfehler: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            System.err.println("Die Datei wurde nicht gefunden! " + e.getMessage());
        }
    }

    /**
     * Diese Methode liest die XML Datei und delegiert die gefundenen XML
     * Elemente an die entsprechenden Methoden.
     * 
     * @return The exit code
     */
    public final int parse() {
        while (xmlParser.hasNext()) {
            try {
                XMLEvent event = xmlParser.nextEvent();
                switch (event.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    handleStartEvent(event);
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    String name = event.asEndElement().getName().toString().toLowerCase();
                    if (name.equals("token")) {
                        isToken = false;
                    } else if (name.equals("name")) {
                        isName = false;
                    } else if (name.equals("value")) {
                        isValue = false;
                    } else if (name.equals("place") || name.equals("transition")) {
                        /*
                         * The last line for this place or transition was read.
                         * Transmit this place to the data model controller.
                         */
                        sendElementToController();
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (isValue && lastId != null) {
                        Characters ch = event.asCharacters();
                        if (!ch.isWhiteSpace()) {
                            handleValue(ch.getData());
                        }
                    }
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    // schließe den Parser
                    xmlParser.close();

                    /* Close and set the FileInputStream to null. */
                    this.fis = (FileInputStream) safeInputStreamClose(this.fis);

                    break;
                default:
                }
            } catch (XMLStreamException e) {
                System.err.println("Fehler beim Parsen des PNML Dokuments. " + e.getMessage());
                e.printStackTrace();
                this.exitCode = this.exitCode | EPNMLParserExitCode.FLAG_ERROR_READING_FILE.getValue();
            }
        }
        return this.exitCode;
    }

    /**
     * Diese Methode behandelt den Start neuer XML Elemente, in dem der Name des
     * Elements überprüft wird und dann die Behandlung an spezielle Methoden
     * delegiert wird.
     * 
     * @param event
     *            {@link XMLEvent}
     */
    private void handleStartEvent(final XMLEvent event) {
        StartElement element = event.asStartElement();
        if (element.getName().toString().toLowerCase().equals("place")) {
            handlePlace(element);
        } else if (element.getName().toString().toLowerCase().equals("transition")) {
            handleTransition(element);
        } else if (element.getName().toString().toLowerCase().equals("arc")) {
            handleArc(element);
        } else if (element.getName().toString().toLowerCase().equals("name")) {
            isName = true;
        } else if (element.getName().toString().toLowerCase().equals("position")) {
            handlePosition(element);
        } else if (element.getName().toString().toLowerCase().equals("token")) {
            isToken = true;
        } else if (element.getName().toString().toLowerCase().equals("value")) {
            isValue = true;
        }
    }

    /**
     * Diese Methode wird aufgerufen, wenn Text innerhalb eines Value Elements
     * gelesen wird.
     * 
     * @param value
     *            Der gelesene Text als String
     */
    private void handleValue(final String value) {
        if (isName) {
            setName(lastId, value);
        } else if (isToken) {
            setMarking(lastId, value);
        }
    }

    /**
     * Diese Methode wird aufgerufen, wenn ein Positionselement gelesen wird.
     * 
     * @param element
     *            das Positionselement
     */
    private void handlePosition(final StartElement element) {
        String x = null;
        String y = null;
        Iterator<?> attributes = element.getAttributes();
        while (attributes.hasNext()) {
            Attribute attr = (Attribute) attributes.next();
            if (attr.getName().toString().toLowerCase().equals("x")) {
                x = attr.getValue();
            } else if (attr.getName().toString().toLowerCase().equals("y")) {
                y = attr.getValue();
            }
        }
        if (x != null && y != null && lastId != null) {
            setPosition(lastId, x, y);
        } else {
            System.err.println("Unvollständige Position wurde verworfen!");
            this.exitCode = this.exitCode | EPNMLParserExitCode.FLAG_INVALID_VALUES.getValue();
        }
    }

    /**
     * Diese Methode wird aufgerufen, wenn ein Stellenelement gelesen wird.
     * 
     * @param element
     *            das Stellenelement
     */
    private void handlePlace(final StartElement element) {
        String placeId = null;
        Iterator<?> attributes = element.getAttributes();
        while (attributes.hasNext()) {
            Attribute attr = (Attribute) attributes.next();
            if (attr.getName().toString().toLowerCase().equals("id")) {
                placeId = attr.getValue();
                break;
            }
        }
        if (placeId != null) {
            newPlace(placeId);
            lastId = placeId;
        } else {
            System.err.println("Stelle ohne id wurde verworfen!");
            lastId = null;
            this.exitCode = this.exitCode | EPNMLParserExitCode.FLAG_INVALID_VALUES.getValue();
        }
    }

    /**
     * Diese Methode wird aufgerufen, wenn ein Transitionselement gelesen wird.
     * 
     * @param element
     *            das Transitionselement
     */
    private void handleTransition(final StartElement element) {
        String transitionId = null;
        Iterator<?> attributes = element.getAttributes();
        while (attributes.hasNext()) {
            Attribute attr = (Attribute) attributes.next();
            if (attr.getName().toString().toLowerCase().equals("id")) {
                transitionId = attr.getValue();
                break;
            }
        }
        if (transitionId != null) {
            newTransition(transitionId);
            lastId = transitionId;
        } else {
            System.err.println("Transition ohne id wurde verworfen!");
            lastId = null;
            this.exitCode = this.exitCode | EPNMLParserExitCode.FLAG_INVALID_VALUES.getValue();
        }
    }

    /**
     * Diese Methode wird aufgerufen, wenn ein Kantenelement gelesen wird.
     * 
     * @param element
     *            das Kantenelement
     */
    private void handleArc(final StartElement element) {
        String arcId = null;
        String source = null;
        String target = null;
        Iterator<?> attributes = element.getAttributes();
        while (attributes.hasNext()) {
            Attribute attr = (Attribute) attributes.next();
            if (attr.getName().toString().toLowerCase().equals("id")) {
                arcId = attr.getValue();
            } else if (attr.getName().toString().toLowerCase().equals("source")) {
                source = attr.getValue();
            } else if (attr.getName().toString().toLowerCase().equals("target")) {
                target = attr.getValue();
            }
        }
        if (arcId != null && source != null && target != null) {
            newArc(arcId, source, target);
        } else {
            System.err.println("Unvollständige Kante wurde verworfen!");
            this.exitCode = this.exitCode | EPNMLParserExitCode.FLAG_INVALID_VALUES.getValue();
        }
        // Die id von Kanten wird nicht gebraucht
        lastId = null;
    }

    /**
     * Diese Methode kann überschrieben werden, um geladene Stellen zu
     * erstellen.
     * 
     * @param id
     *            Identifikationstext der Stelle
     */
    public void newPlace(final String id) {
        if (debug) {
            System.out.println("Stelle mit id " + id + " wurde gefunden.");
        }

        /*
         * Add data for the next element.
         */
        this.nextElementType = EPNMLElement.PLACE;
        this.nextId = id;

        /*
         * Note: The element is not complete! (We have to wait for the parser to
         * find the end tag because places and transitions are multi-line values
         * in the PNML file.)
         */
    }

    /**
     * Diese Methode kann überschrieben werden, um geladene Transitionen zu
     * erstellen.
     * 
     * @param id
     *            Identifikationstext der Transition
     */
    public void newTransition(final String id) {
        if (debug) {
            System.out.println("Transition mit id " + id + " wurde gefunden.");
        }

        /*
         * Store data for the next element.
         */
        this.nextElementType = EPNMLElement.TRANSITION;
        this.nextId = id;

        /*
         * Note: The element is not complete! (We have to wait for the parser to
         * find the end tag because places and transitions are multi-line values
         * in the PNML file.)
         */
    }

    /**
     * Diese Methode kann überschrieben werden, um geladene Kanten zu erstellen.
     * 
     * @param id
     *            Identifikationstext der Kante
     * @param source
     *            Identifikationstext des Startelements der Kante
     * @param target
     *            Identifikationstext des Endelements der Kante
     */
    public void newArc(final String id, final String source, final String target) {
        if (debug) {
            System.out.println("Kante mit id " + id + " von " + source + " nach " + target + " wurde gefunden.");
        }

        /*
         * Store data for the next element.
         */
        this.nextElementType = EPNMLElement.ARC;
        this.nextId = id;
        this.nextSourceId = source;
        this.nextTargetId = target;

        /*
         * Note: This arc is complete because arcs have only 1 line in the pnml
         * file.
         */
        sendElementToController();
    }

    /**
     * Diese Methode kann überschrieben werden, um den Beschriftungstext der
     * geladenen Elemente zu aktualisieren.
     * 
     * @param id
     *            Identifikationstext des Elements
     * @param name
     *            Beschriftungstext des Elements
     */
    public void setName(final String id, final String name) {
        if (debug) {
            System.out.println("Setze den Namen des Elements " + id + " auf " + name);
        }

        /*
         * Store data for the next element.
         */
        this.nextName = name;
    }

    /**
     * Diese Methode kann überschrieben werden, um die Markierung der geladenen
     * Elemente zu aktualisieren.
     * 
     * @param id
     *            Identifikationstext des Elements
     * @param marking
     *            Markierung des Elements
     */
    public void setMarking(final String id, final String marking) {
        if (debug) {
            System.out.println("Setze die Markierung des Elements " + id + " auf " + marking);
        }

        /*
         * Store data for the next element.
         */
        switch (marking) {
        case "0":
            this.nextMarking = EPlaceToken.ZERO;
            break;
        case "1":
            this.nextMarking = EPlaceToken.ONE;
            break;
        default:
            System.err.println("Invalid marking for element " + id + "! Ignoring this element...");
            this.exitCode = this.exitCode | EPNMLParserExitCode.FLAG_INVALID_VALUES.getValue();
            invalidValues = true;
        }
    }

    /**
     * Diese Methode kann überschrieben werden, um die Positionen der geladenen
     * Elemente zu aktualisieren.
     * 
     * @param id
     *            Identifikationstext des Elements
     * @param x
     *            x Position des Elements
     * @param y
     *            y Position des Elements
     */
    public void setPosition(final String id, final String x, final String y) {
        if (debug) {
            System.out.println("Setze die Position des Elements " + id + " auf (" + x + ", " + y + ")");
        }

        /*
         * Store data for the next element.
         */
        int xPos = 0;
        int yPos = 0;
        try {
            xPos = Integer.parseInt(x);
            yPos = Integer.parseInt(y);
            this.nextPosition = new Point(xPos, yPos);
        } catch (NumberFormatException e) {
            System.err.println("Invalid position for element " + id + "! Ignoring this element...");
            this.exitCode = this.exitCode | EPNMLParserExitCode.FLAG_INVALID_VALUES.getValue();
            invalidValues = true;
        }
    }

    /**
     * Sends the last element to the controller if all values were read from the
     * PNML file.
     */
    private void sendElementToController() {
        if (invalidValues) {
            this.invalidValues = false;
            resetNextValues();
            System.out.println("Element ignored.");
            return;
        }

        /*
         * Assemble the necessary data
         */
        String errorMessage = "";
        switch (nextElementType) {
        case PLACE:
            if (!isCompletePlace()) {
                this.exitCode = this.exitCode | EPNMLParserExitCode.FLAG_MISSING_VALUES.getValue();
                break;
            }

            if (dataModelController == null) {
                // Nothing (parser test via main method?)
                if (debug) {
                    System.out.println("Parser would send a new place to the data model controller.");
                }
                break;
            }

            dataModelController.addPlaceToCurrentDataModel(nextId, nextName, nextMarking, nextPosition);
            break;

        case TRANSITION:
            if (!isCompleteTransition()) {
                this.exitCode = this.exitCode | EPNMLParserExitCode.FLAG_MISSING_VALUES.getValue();
                break;
            }

            if (dataModelController == null) {
                // Nothing (parser test via main method?)
                if (debug) {
                    System.out.println("Parser would send a new transition to the data model controller.");
                }
                break;
            }

            dataModelController.addTransitionToCurrentDataModel(nextId, nextName, nextPosition);
            break;

        case ARC:
            if (!isCompleteArc()) {
                this.exitCode = this.exitCode | EPNMLParserExitCode.FLAG_MISSING_VALUES.getValue();
                break;
            }

            if (dataModelController == null) {
                // Nothing (parser test via main method?)
                if (debug) {
                    System.out.println("Parser would send a new arc to the data model controller.");
                }
                break;
            }

            dataModelController.addArcToCurrentDataModel(nextId, nextSourceId, nextTargetId);
            break;

        default:
            this.exitCode = this.exitCode | EPNMLParserExitCode.FLAG_UNKNOWN_ELEMENT.getValue();
            errorMessage = "Unknown element type: " + nextElementType;
            if (debug) {
                System.out.println(errorMessage);
            }
            JOptionPane.showMessageDialog(null, errorMessage, "PNMLParser", JOptionPane.INFORMATION_MESSAGE);
        }

        /*
         * Reset the "next" values for the next element in the PNML file!
         */
        resetNextValues();
    }

    /**
     * Checks if we have read all necessary values for a place.
     * 
     * @return True if all necessary values are there; otherwise false
     */
    private boolean isCompletePlace() {
        /*
         * <Name> == null is OK!
         */
        if (nextId != null && nextMarking != null && nextPosition != null)
            return true;
        return false;
    }

    /**
     * Checks if we have read all necessary values for a transition.
     * 
     * @return True if all necessary values are there; otherwise false
     */
    private boolean isCompleteTransition() {
        /*
         * <Name> == null is OK!
         */
        if (nextId != null && nextPosition != null)
            return true;
        return false;
    }

    /**
     * Checks if we have read all necessary values for an arc.
     * 
     * @return True if all necessary values are there; otherwise false
     */
    private boolean isCompleteArc() {
        if (nextId != null && nextSourceId != null && nextTargetId != null)
            return true;
        return false;
    }

    /**
     * Resets all values for the next element in the PNML file.
     */
    private void resetNextValues() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("resetNextValues");
        }
        this.nextElementType = null;
        this.nextId = null;
        this.nextName = null;
        this.nextMarking = null;
        this.nextPosition = null;
        this.nextSourceId = null;
        this.nextTargetId = null;
    }

    /**
     * Ensures that the previous instance of the specified {@link InputStream}
     * is closed and null.
     * 
     * @param is
     *            The {@link InputStream}
     * @return The reset {@link InputStream}
     */
    private InputStream safeInputStreamClose(InputStream is) {
        if (is == null) { return null; }

        try {
            is.close();
        } catch (IOException e) {
            // NOP
        }
        is = null;
        return is;
    }

}
