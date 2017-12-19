package de.lambeck.pned.elements.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import de.lambeck.pned.exceptions.PNDuplicateAddedException;

/**
 * Superclass DataNode implements the common members for all nodes.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public abstract class DataNode extends DataElement implements IDataNode {

    /*
     * Attributes for interface IDataNode
     */
    protected String name = "";

    protected ArrayList<IDataArc> predElems = new ArrayList<IDataArc>();
    protected ArrayList<IDataArc> succElems = new ArrayList<IDataArc>();
    protected Point position = null; // The center of the node

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

    /*
     * Getter and setter
     */

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

    /*
     * Adding and removal of elements
     */

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
    public ArrayList<IDataArc> getPredElems() {
        return this.predElems;
    }

    @Override
    public ArrayList<IDataArc> getSuccElems() {
        return this.succElems;
    }

}
