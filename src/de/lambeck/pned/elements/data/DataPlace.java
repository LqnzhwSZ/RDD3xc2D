package de.lambeck.pned.elements.data;

import java.awt.Point;

import de.lambeck.pned.elements.EPlaceToken;

/**
 * Implements the places (circles) of the Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class DataPlace extends DataNode implements IDataPlace {

    /** The tokens of this place */
    private EPlaceToken tokens = EPlaceToken.ZERO;

    /**
     * Constructor with parameters
     * 
     * @param id
     *            The id of this place
     * @param name
     *            The name of this place
     * @param initialPosition
     *            (center) of this place
     * @param initialTokens
     *            Specifies the initial tokens count of this place.
     */
    @SuppressWarnings("hiding")
    public DataPlace(String id, String name, Point initialPosition, EPlaceToken initialTokens) {
        super(id, name);

        this.position = initialPosition;
        this.tokens = initialTokens;
    }

    /* Getter and Setter */

    @Override
    public EPlaceToken getTokensCount() {
        return this.tokens;
    }

    @Override
    public void setTokens(EPlaceToken newTokens) {
        this.tokens = newTokens;
    }

    @Override
    public String toString() {
        String returnString = "DataPlace [" + super.toString() + ", tokens=" + tokens + "]";
        return returnString;
    }

}
