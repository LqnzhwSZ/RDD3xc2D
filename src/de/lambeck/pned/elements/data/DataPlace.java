package de.lambeck.pned.elements.data;

import java.awt.Point;

/**
 * Implements the places (circles) of the Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class DataPlace extends DataNode {

    private EPlaceMarking marking = EPlaceMarking.ZERO;

    /**
     * Constructor with parameters
     * 
     * @param id
     *            The id of this place
     * @param name
     *            The name of this place
     * @param initialPosition
     *            (center) of the place
     * @param initialMarking
     *            specifies if the place gets a mark
     */
    @SuppressWarnings("hiding")
    public DataPlace(String id, String name, Point initialPosition, EPlaceMarking initialMarking) {
        super(id, name);

        this.position = initialPosition;
        this.marking = initialMarking;
    }

    /*
     * Getter and setter
     */

    /**
     * @return True if this place has a mark.
     */
    public EPlaceMarking getMarking() {
        return this.marking;
    }

    /**
     * @param newMarking
     *            specifies if the place gets a mark
     */
    public void setMarking(EPlaceMarking newMarking) {
        this.marking = newMarking;
    }

    @Override
    public String toString() {
        return "DataPlace [id=" + id + ", name=" + name + ", marking=" + marking + ", position=" + position.getX() + ","
                + position.getY() + "]";
    }

}
