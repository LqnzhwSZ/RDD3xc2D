package de.lambeck.pned.elements;

/**
 * Interface for places in the Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IPlace extends INode {

    /**
     * Getter for the tokens of this place.
     * 
     * @return The tokens count of this place
     */
    EPlaceToken getTokensCount();

    /**
     * Sets the new tokens count ({@link EPlaceToken}) of this place.
     * 
     * @param newTokens
     *            The new tokens count
     */
    void setTokens(EPlaceToken newTokens);

}
