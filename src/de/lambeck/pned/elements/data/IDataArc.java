package de.lambeck.pned.elements.data;

import de.lambeck.pned.elements.IArc;
import de.lambeck.pned.exceptions.PNElementException;
import de.lambeck.pned.models.data.IDataModel;

/**
 * Sub type of IArc for arcs in a {@link IDataModel}.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public interface IDataArc extends IDataElement, IArc {

    /*
     * Getter and setter
     */

    /*
     * Keine Knoten von Kanten entfernen! Kanten immer gleich löschen!
     */

    /**
     * This method returns the predecessor of the arc. If the arc has no
     * predecessor (no "start"), the method returns null.
     * 
     * @return The predecessor
     * @throws PNElementException
     *             if the value is null
     */
    IDataNode getPredElem() throws PNElementException;

    /**
     * This method returns the successor of the arc. If the arc has no successor
     * (no "end"), the method returns null.
     * 
     * @return The successor
     * @throws PNElementException
     *             if the value is null
     */
    IDataNode getSuccElem() throws PNElementException;

}
