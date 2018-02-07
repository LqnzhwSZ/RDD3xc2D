package de.lambeck.pned.elements.gui;

import de.lambeck.pned.elements.IArc;
import de.lambeck.pned.exceptions.PNNoSuchElementException;
import de.lambeck.pned.models.gui.IGuiModel;

/**
 * Sub type of IArc for arcs in a {@link IGuiModel}.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public interface IGuiArc extends IGuiElement, IArc {

    /* Getter and Setter */

    /*
     * Keine Knoten von Kanten entfernen! Kanten immer gleich löschen!
     */

    /**
     * This method returns the predecessor (the "start") of the arc.<BR>
     * <BR>
     * Note: Throws a {@link PNNoSuchElementException} if predecessor is null
     * because per definition a {@link IGuiArc} has a predecessor at any time.
     * 
     * @return The predecessor
     * @throws PNNoSuchElementException
     *             if predecessor is null
     */
    IGuiNode getPredElem() throws PNNoSuchElementException;

    /**
     * This method returns the successor (the "end") of the arc.<BR>
     * <BR>
     * Note: Throws a {@link PNNoSuchElementException} if successor is null
     * because per definition a {@link IGuiArc} has a successor at any time.
     * 
     * @return The successor
     * @throws PNNoSuchElementException
     *             if successor is null
     */
    IGuiNode getSuccElem() throws PNNoSuchElementException;

}
