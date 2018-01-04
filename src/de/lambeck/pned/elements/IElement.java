package de.lambeck.pned.elements;

/**
 * Interface for all types (nodes and arcs) in the Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public interface IElement {

    /**
     * Returns the id of the Petri net element.<BR>
     * <BR>
     * Note: This application creates UUIDs but the PNML-Format allows simple
     * Strings.
     * 
     * @return the id
     */
    String getId();

    /* No Setter for id because it should never change after creation. */

}
