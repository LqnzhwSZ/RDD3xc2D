package de.lambeck.pned.elements.data;

import java.awt.Point;
import java.util.ArrayList;

import de.lambeck.pned.exceptions.PNElementException;

/**
 * Implements the transitions (squares) of the Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class DataTransition extends DataNode {

    private boolean activated = false;

    /**
     * Constructor with parameters
     * 
     * @param id
     *            The id of this transition
     * @param name
     *            The name of this transition
     * @param initialPosition
     *            (center) of the transition
     */
    @SuppressWarnings("hiding")
    public DataTransition(String id, String name, Point initialPosition) {
        super(id, name);

        this.position = initialPosition;
    }

    /*
     * Getter and setter
     */

    /**
     * @return True if this transition is activated.
     */
    public boolean isActivated() {
        return this.activated;
    }

    /**
     * Checks if this transition is activated and sets the member variable
     * "activated" accordingly.
     * 
     * Activated is evaluated to false if there are no previous nodes (places)
     * or if the first previous node without marking is found.
     */
    public void checkActivated() {
        ArrayList<IDataNode> predNodes = getPredNodes();
        if (predNodes == null) {
            // Condition 1: no previous nodes
            this.activated = false;
            return;
        }

        for (IDataNode currentNode : predNodes) {
            if (currentNode instanceof DataPlace) {
                DataPlace currentPlace = (DataPlace) currentNode;
                if (currentPlace.getMarking() == EPlaceMarking.ZERO) {
                    // Condition 2: first previous node without marking
                    this.activated = false;
                    return;
                }
            }
        }

        // All previous nodes have a marking.
        this.activated = true;
    }

    /**
     * Returns a list of nodes (places) before the previous arcs (arrows). A
     * transition can use this list to determine it's "activated" state.
     * 
     * If the previous arcs have no predecessors (no places), the method returns
     * null.
     * 
     * @return List of all nodes before the predecessors
     */
    @SuppressWarnings("null")
    private ArrayList<IDataNode> getPredNodes() {
        ArrayList<IDataNode> predNodes = null;
        IDataNode currentNode;

        for (IDataArc prevArc : this.predElems) {
            try {
                currentNode = prevArc.getPredElem();
                predNodes.add(currentNode);
            } catch (PNElementException e) {
                // Nothing
            }
        }

        return predNodes;
    }

    @Override
    public String toString() {
        return "DataTransition [id=" + id + ", name=" + name + ", position=" + position.getX() + "," + position.getY()
                + ", activated=" + activated + "]";
    }

}
