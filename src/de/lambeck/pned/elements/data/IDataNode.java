package de.lambeck.pned.elements.data;

import java.util.List;
import java.util.NoSuchElementException;

import de.lambeck.pned.elements.INode;
import de.lambeck.pned.exceptions.PNDuplicateAddedException;
import de.lambeck.pned.models.data.IDataModel;

/**
 * Sub type of INode for nodes in a {@link IDataModel}.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public interface IDataNode extends IDataElement, INode {

    /*
     * Adding and removal of elements
     */

    /**
     * Adds an {@link IDataArc} to the list of predecessors (incoming).
     * 
     * @param newArc
     *            The specified {@link IDataArc}
     * @throws PNDuplicateAddedException
     *             If this arc already exists
     */
    void addPred(IDataArc newArc) throws PNDuplicateAddedException;

    /**
     * Adds an arc to the list of successors (outgoing).
     * 
     * @param newArc
     *            The specified {@link IDataArc}
     * @throws PNDuplicateAddedException
     *             If this arc already exists
     */
    void addSucc(IDataArc newArc) throws PNDuplicateAddedException;

    /**
     * Removes the specified arc from the list of predecessors.
     * 
     * @param arc
     *            The specified {@link IDataArc}
     * @throws NoSuchElementException
     *             If this arc does not exist
     */
    void removePred(IDataArc arc) throws NoSuchElementException;

    /**
     * Removes the specified arc from the list of successors.
     * 
     * @param arc
     *            The specified {@link IDataArc}
     * @throws NoSuchElementException
     *             If this arc does not exist
     */
    void removeSucc(IDataArc arc) throws NoSuchElementException;

    /**
     * This method returns a list of all predecessors of the node. If the node
     * has no predecessors, the method returns null.
     * 
     * @return A {@link List} of all predecessors
     */
    List<IDataArc> getPredElems();

    /**
     * This method returns a list of all successors of the node. If the node has
     * no successors, the method returns null.
     * 
     * @return A {@link List} of all successors
     */
    List<IDataArc> getSuccElems();

    /**
     * Returns true if this nodes list of predecessors contains the specified
     * arc.
     * 
     * @param arc
     *            The specified {@link IDataArc}
     * @return True if arc is in the predecessor list; otherwise false
     */
    boolean predListContains(IDataArc arc);

    /**
     * Returns true if this nodes list of successors contains the specified arc.
     * 
     * @param arc
     *            The specified {@link IDataArc}
     * @return True if arc is in the successor list; otherwise false
     */
    boolean succListContains(IDataArc arc);

}
