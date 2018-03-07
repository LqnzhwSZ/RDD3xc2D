package de.lambeck.pned.elements.data;

import de.lambeck.pned.elements.util.NodeCheck;
import de.lambeck.pned.exceptions.PNElementCreationException;
import de.lambeck.pned.exceptions.PNNoSuchElementException;

/**
 * Implements the arcs (arrows) of the Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class DataArc extends DataElement implements IDataArc {

    /** The predecessor - the node that is the source of this arc */
    private IDataNode pred;
    /** The successor - the node that is the target of this arc */
    private IDataNode succ;

    /**
     * Constructor with parameters for target and source. Makes sure that the
     * arc always connects a place and a transition.
     * 
     * @param id
     *            The id of this Arc
     * @param source
     *            The source id
     * @param target
     *            The target id
     * @throws PNElementCreationException
     *             For an invalid combination of source and target
     */
    @SuppressWarnings("hiding")
    public DataArc(String id, IDataNode source, IDataNode target) throws PNElementCreationException {
        super(id);

        /* Check for different types of elements */
        if (!NodeCheck.isValidConnection(source, target))
            throw new PNElementCreationException("Invalid combination of source and target for Arc");

        /*
         * predElements/succElements must have only 1 entry and they are lists
         * but empty when creating this instance.
         */
        this.pred = source;
        this.succ = target;
    }

    @Override
    public int getAllPredCount() {
        if (this.pred == null)
            return 0;
        return 1; // 0 or 1
    }

    @Override
    public int getAllSuccCount() {
        if (this.succ == null)
            return 0;
        return 1; // 0 or 1
    }

    /* Getter and Setter */

    @Override
    public String getSourceId() {
        return pred.getId();
    }

    @Override
    public String getTargetId() {
        return succ.getId();
    }

    @Override
    public IDataNode getPredElem() throws PNNoSuchElementException {
        if (this.pred == null)
            throw new PNNoSuchElementException(this.toString() + " has no predecessor.");
        return this.pred;
    }

    @Override
    public IDataNode getSuccElem() throws PNNoSuchElementException {
        if (this.succ == null)
            throw new PNNoSuchElementException(this.toString() + " has no successor.");
        return this.succ;
    }

    @Override
    public String toString() {
        String returnString = "DataArc [" + super.toString() + ", source=" + pred.getId() + ", target=" + succ.getId()
                + "]";
        return returnString;
    }

}
