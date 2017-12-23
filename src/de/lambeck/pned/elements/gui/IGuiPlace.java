package de.lambeck.pned.elements.gui;

import de.lambeck.pned.elements.data.EPlaceToken;

/**
 * Interface for places with a "token" circle
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IGuiPlace extends IGuiNode {

    /**
     * Getter for the tokens of this place.
     * 
     * @return The tokens count of this place
     */
    EPlaceToken getTokensCount();

    /**
     * Setter for the tokens of this place.
     * 
     * @param newTokens
     *            Specifies the new tokens count of this place.
     */
    void setTokens(EPlaceToken newTokens);

    /**
     * Sets this places status as start place.
     * 
     * @param b
     *            Set to true if this is the start place; otherwise false.
     */
    void setStartPlace(boolean b);

    /**
     * Sets this places status as end place.
     * 
     * @param b
     *            Set to true if this is the end place; otherwise false.
     */
    void setEndPlace(boolean b);

}
