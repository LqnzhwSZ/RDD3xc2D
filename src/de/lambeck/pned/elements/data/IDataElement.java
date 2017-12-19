package de.lambeck.pned.elements.data;

import de.lambeck.pned.elements.IElement;

/**
 * Sub type of IElement for data models.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public interface IDataElement extends IElement {

    /**
     * This method returns the number of predecessors. A node is a root element,
     * if its number of predecessors is 0.
     * 
     * @return Number of predecessors
     */
    int getAllPredCount();

    /**
     * This method returns the number of successors. A node is an end element,
     * if its number of successors is 0.
     * 
     * @return Number of successors
     */
    int getAllSuccCount();

}
