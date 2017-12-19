package de.lambeck.pned.elements.data;

import de.lambeck.pned.exceptions.PNElementException;

/**
 * Implements the arcs (arrows) of the Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class DataArc extends DataElement implements IDataArc {

    private IDataNode pred;
    private IDataNode succ;

    /**
     * Constructor with parameters for target and source
     * 
     * Makes sure that the arc always connects a place and a transition.
     * 
     * @param id
     *            The id of this Arc
     * @param source
     *            The source id
     * @param target
     *            The target id
     * @throws PNElementException
     */
    @SuppressWarnings("hiding")
    public DataArc(String id, IDataNode source, IDataNode target) throws PNElementException {
        super(id);

        /*
         * Check for different types of elements
         */
        if (!isValidConnection(source, target))
            throw new PNElementException("Invalid combination of source and target for Arc");

        /*
         * predElements/succElements must have only 1 entry and they are lists
         * but empty when creating this instance.
         */
        this.pred = source;
        this.succ = target;
    }

    /**
     * Checks if source and target are a valid combination of a place and a
     * transition.
     * 
     * @param source
     * @param target
     * @return true if the combination is valid.
     */
    // TODO Checks in separate Klasse auslagern?
    private boolean isValidConnection(IDataNode source, IDataNode target) {
        if (source instanceof DataPlace)
            if (target instanceof DataTransition)
                return true;
        if (source instanceof DataTransition)
            if (target instanceof DataPlace)
                return true;
        return false;
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

    /*
     * Getter and setter
     */

    @Override
    public String getSourceId() {
        return pred.getId();
    }

    @Override
    public String getTargetId() {
        return succ.getId();
    }

    @Override
    public IDataNode getPredElem() throws PNElementException {
        if (this.pred == null)
            throw new PNElementException(this.toString() + " has no predecessor.");
        return this.pred;
    }

    @Override
    public IDataNode getSuccElem() throws PNElementException {
        if (this.succ == null)
            throw new PNElementException(this.toString() + " has no successor.");
        return this.succ;
    }

    @Override
    public String toString() {
        return "DataArc [id=" + id + ", source=" + pred.getId() + ", target=" + succ.getId() + "]";
    }

}
