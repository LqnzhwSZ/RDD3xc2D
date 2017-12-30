package de.lambeck.pned.elements.data;

import de.lambeck.pned.elements.ITransition;
import de.lambeck.pned.models.data.IDataModel;

/**
 * Interface for transitions in a {@link IDataModel}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IDataTransition extends ITransition, IDataNode {

    /**
     * Resets the enabled state of this transition.<BR>
     * <BR>
     * Note: No Setter because "enabled" = true will be determined by method
     * checkEnabled().
     */
    void resetEnabled();

    /**
     * Checks whether this transition is enabled due to the state of all
     * (previous) input and (following) output places. Stores and returns the
     * result.<BR>
     * <BR>
     * A transition is enabled if:<BR>
     * - It has at least one input place,<BR>
     * - All input places have a token, <BR>
     * - It has at least one output place,<BR>
     * - Only output places that are input places as well have a token.<BR>
     * <BR>
     * Note: The transition in the Data model determines its state by itself,
     * while the transition in the GUI model has a Setter for this state.
     * 
     * @return True = transition enabled, false = transition disabled
     */
    boolean checkEnabled();

}
