package de.lambeck.pned.elements.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.exceptions.PNElementException;

/**
 * Implements the transitions (squares) of the Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class DataTransition extends DataNode implements IDataTransition {

    /** The "enabled" state of this transition */
    private boolean enabled = false;

    /** The "safe" state of this transition */
    private boolean safe = true; // Assume "safe" from the start

    /*
     * Constructor
     */

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

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isSafe() {
        return this.safe;
    }

    @Override
    public void resetEnabled() {
        this.enabled = false;
        this.safe = true; // Assume "safe" after reset
    }

    @Override
    public boolean checkEnabled() throws IllegalStateException {
        this.enabled = false;

        List<DataPlace> inputPlaces = getPredPlaces();
        if (inputPlaces == null) {
            /* Failed condition 1: At least one input place */
            return false;
        }

        for (DataPlace inputPlace : inputPlaces) {
            if (inputPlace.getTokensCount() == EPlaceToken.ZERO) {
                /* Failed condition 2: All input places have a token. */
                return false;
            }
        }

        List<DataPlace> outputPlaces = getSuccPlaces();
        if (outputPlaces == null) {
            /* Failed condition 3: At least one output place */
            return false;
        }

        for (DataPlace outputPlace : outputPlaces) {
            if (outputPlace.getTokensCount() == EPlaceToken.ONE) {
                if (!inputPlaces.contains(outputPlace)) {
                    /*
                     * Failed condition 4: Only output places that are input
                     * places as well have a token. (We throw an Exception
                     * because a simple "false" would not be clear enough!)
                     */
                    String message = "Output place with a token";
                    throw new IllegalStateException(message);
                }
            }
        }

        this.enabled = true;
        return true;
    }

    @Override
    public String toString() {
        String returnString = "DataTransition [" + super.toString() + ", isEnabled=" + this.isEnabled() + "]";
        return returnString;
    }

    @Override
    public List<DataPlace> getPredPlaces() {
        ArrayList<DataPlace> predPlaces = new ArrayList<DataPlace>();
        IDataNode currentNode;
        DataPlace currentPlace;

        for (IDataArc prevArc : this.predElems) {
            try {
                currentNode = prevArc.getPredElem();
                if (currentNode instanceof DataPlace) {
                    currentPlace = (DataPlace) currentNode;
                    predPlaces.add(currentPlace);
                }
            } catch (PNElementException e) {
                // NOP
            }
        }

        return predPlaces;
    }

    @Override
    public List<DataPlace> getSuccPlaces() {
        ArrayList<DataPlace> succPlaces = new ArrayList<DataPlace>();
        IDataNode currentNode;
        DataPlace currentPlace;

        for (IDataArc succArc : this.succElems) {
            try {
                currentNode = succArc.getSuccElem();
                if (currentNode instanceof DataPlace) {
                    currentPlace = (DataPlace) currentNode;
                    succPlaces.add(currentPlace);
                }
            } catch (PNElementException e) {
                // NOP
            }
        }

        return succPlaces;
    }

}
