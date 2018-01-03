package de.lambeck.pned.elements.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import de.lambeck.pned.exceptions.PNDuplicateAddedException;

/**
 * Superclass DataNode implements the common members for all nodes.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public abstract class DataNode extends DataElement implements IDataNode {

    /* Attributes for interface IDataNode */

    /** The name of this node (might be empty) */
    protected String name = "";

    /** The {@link List} of predecessors - the incoming {@link IDataArc} */
    protected List<IDataArc> predElems = new ArrayList<IDataArc>();
    /** The {@link List} of successors - the outgoing {@link IDataArc} */
    protected List<IDataArc> succElems = new ArrayList<IDataArc>();

    /** The center of this node */
    protected Point position = null;

    /**
     * Constructs a node with the specified id.
     * 
     * Note: The GUI will pass an id which is a UUID (as String) if the user has
     * created a node in the GUI. The pnml parser may pass any kind of String.
     * 
     * Note: The id has no Setter because it should never change after creation.
     * 
     * @param id
     *            The id of this Node
     * @param name
     *            The name of this place
     */
    @SuppressWarnings("hiding")
    public DataNode(String id, String name) {
        super(id);
        setName(name);
    }

    /* Getter and setter */

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String newName) {
        this.name = newName;
    }

    @Override
    public Point getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(Point newPosition) {
        this.position = newPosition;
    }

    @Override
    public int getAllPredCount() {
        if (this.predElems == null)
            return 0;
        return this.predElems.size(); // 0..n
    }

    @Override
    public int getAllSuccCount() {
        if (this.succElems == null)
            return 0;
        return this.succElems.size(); // 0..n
    }

    /* Adding and removal of elements */

    @Override
    public void addPred(IDataArc newArc) throws PNDuplicateAddedException {
        if (this.predElems.contains(newArc))
            throw new PNDuplicateAddedException();
        this.predElems.add(newArc);
    }

    @Override
    public void addSucc(IDataArc newArc) throws PNDuplicateAddedException {
        if (this.succElems.contains(newArc))
            throw new PNDuplicateAddedException();
        this.succElems.add(newArc);
    }

    @Override
    public void removePred(IDataArc arc) throws NoSuchElementException {
        if (!this.predElems.contains(arc))
            throw new NoSuchElementException();
        this.predElems.remove(arc);
    }

    @Override
    public void removeSucc(IDataArc arc) throws NoSuchElementException {
        if (!this.succElems.contains(arc))
            throw new NoSuchElementException();
        this.succElems.remove(arc);
    }

    @Override
    public List<IDataArc> getPredElems() {
        return this.predElems;
    }

    @Override
    public List<IDataArc> getSuccElems() {
        return this.succElems;
    }

    @Override
    public boolean predListContains(IDataArc arc) {
        return this.predElems.contains(arc);
    }

    @Override
    public boolean succListContains(IDataArc arc) {
        return this.succElems.contains(arc);
    }

    @Override
    public String toString() {
        String returnString = "DataNode [" + super.toString() + ", name=" + name + ", position=" + position.x + ","
                + position.y + "]";
        return returnString;
    }

}
