package de.lambeck.pned.elements.gui;

import de.lambeck.pned.elements.IPlace;
import de.lambeck.pned.models.gui.IGuiModel;

/**
 * Sub type of IGuiNode for places in a {@link IGuiModel} (places with a "token"
 * circle).
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IGuiPlace extends IGuiNode, IPlace {

    /**
     * Sets this places status as the real (unambiguous) start place.
     * 
     * @param b
     *            True = this is the real (unambiguous) start place; false =
     *            this is not a start place or there are multiple candidates
     */
    void setGuiStartPlace(boolean b);

    /**
     * Use this method to set this places status as a start place candidate if
     * there is more than 1 place without input arcs. Start place candidates
     * will be highlighted slightly different to (unambiguous) start places.
     * 
     * @param b
     *            Set to true if this is the start place; otherwise false.
     */
    void setGuiStartPlaceCandidate(boolean b);

    /**
     * Sets this places status as the real (unambiguous) end place.
     * 
     * @param b
     *            True = this is the real (unambiguous) end place; false = this
     *            is not an end place or there are multiple candidates
     */
    void setGuiEndPlace(boolean b);

    /**
     * Use this method to set this places status as an end place candidate if
     * there is more than 1 place without output arcs. End place candidates will
     * be highlighted slightly different to (unambiguous) end places.
     * 
     * @param b
     *            Set to true if this is the end place; otherwise false.
     */
    void setGuiEndPlaceCandidate(boolean b);

}
