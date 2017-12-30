package de.lambeck.pned.elements;

/**
 * Interface for transitions in the Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface ITransition extends INode {

    /**
     * A transition is enabled if:<BR>
     * - It has at least one input place,<BR>
     * - All input places have a token, <BR>
     * - It has at least one output place,<BR>
     * - Only output places that are input places as well have a token.
     * 
     * @return The "enabled" state of this transition.
     */
    boolean isEnabled();

    /*
     * No general Setter for all transitions because the transition in the Data
     * model determines its state by itself, while the transition in the GUI
     * model has a Setter for this state.
     */

}
