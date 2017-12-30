package de.lambeck.pned.elements.gui;

import de.lambeck.pned.elements.ITransition;
import de.lambeck.pned.models.gui.IGuiModel;

/**
 * Interface for transitions in a {@link IGuiModel}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IGuiTransition extends ITransition, IGuiNode {

    /**
     * Sets the enabled attribute of this transition.<BR>
     * <BR>
     * Note: The transition in the GUI model has a Setter for this state, while
     * the transition in the Data model determines its state by itself.
     * 
     * @param newState
     *            The new enabled state
     */
    void setEnabled(boolean newState);

}
