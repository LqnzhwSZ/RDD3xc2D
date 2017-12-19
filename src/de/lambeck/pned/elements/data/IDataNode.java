package de.lambeck.pned.elements.data;

import java.util.List;
import java.util.NoSuchElementException;

import de.lambeck.pned.elements.INode;
import de.lambeck.pned.exceptions.PNDuplicateAddedException;

/**
 * Sub type of INode for data models.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public interface IDataNode extends IDataElement, INode {

    /*
     * Adding and removal of elements
     */

    /**
     * Adds an arc to the list of predecessors (incoming).
     * 
     * @param newArc
     * @throws PNDuplicateAddedException
     */
    void addPred(IDataArc newArc) throws PNDuplicateAddedException;

    /**
     * Adds an arc to the list of successors (outgoing).
     * 
     * @param newArc
     * @throws PNDuplicateAddedException
     */
    void addSucc(IDataArc newArc) throws PNDuplicateAddedException;

    /**
     * Removes the specified arc from the list of predecessors.
     * 
     * @param arc
     * @throws NoSuchElementException
     */
    void removePred(IDataArc arc) throws NoSuchElementException;

    /**
     * Removes the specified arc from the list of successors.
     * 
     * @param arc
     * @throws NoSuchElementException
     */
    void removeSucc(IDataArc arc) throws NoSuchElementException;

    /**
     * This method returns a list of all predecessors of the node. If the node
     * has no predecessors, the method returns null.
     * 
     * @return List of all predecessors
     */
    List<IDataArc> getPredElems();

    /**
     * This method returns a list of all successors of the node. If the node has
     * no successors, the method returns null.
     * 
     * @return List of all successors
     */
    List<IDataArc> getSuccElems();

}