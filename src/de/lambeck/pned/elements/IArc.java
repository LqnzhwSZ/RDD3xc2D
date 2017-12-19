package de.lambeck.pned.elements;

import de.lambeck.pned.elements.data.IDataElement;

/**
 * Interface for all arcs (arrows) in the Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public interface IArc extends IDataElement {

    /*
     * Getter and setter
     */

    /*
     * No Setter for source and target! (Always create new arcs via
     * constructor!)
     */

    /**
     * Returns the source id of this Arc.
     * 
     * Note: This method is used by the parser.
     * 
     * @return the source id
     */
    String getSourceId();

    /**
     * Returns the target id of this Arc.
     * 
     * Note: This method is used by the parser.
     * 
     * @return the target id
     */
    String getTargetId();

    /*
     * Keine Knoten von Kanten entfernen! Kanten immer gleich löschen!
     */

}
