package de.lambeck.pned.elements.data;

import java.util.List;

import de.lambeck.pned.elements.ITransition;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;

/**
 * Interface for transitions in a {@link IDataModel}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IDataTransition extends ITransition, IDataNode {

    /**
     * Resets the "enabled" <B>and the "safe"</B> state of this transition.<BR>
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
     * @throws IllegalStateException
     *             If this transition is not safe
     */
    boolean checkEnabled() throws IllegalStateException;

    /**
     * Returns a {@link List} of places ({@link DataPlace}) before the previous
     * arrows ({@link IDataArc}). A transition can use this list to determine
     * its own "enabled" state.<BR>
     * <BR>
     * Note: If the previous arcs have no predecessors (no places), the method
     * returns null. But the {@link IDataModelController} should prevent this by
     * removing all adjacent arcs when removing nodes.
     * 
     * @return List of all places before the predecessors (arcs)
     */
    List<DataPlace> getPredPlaces();

    /**
     * Returns a {@link List} of places ({@link DataPlace}) behind the following
     * arrows ({@link IDataArc}). A transition can use this list to determine
     * its own "enabled" state.<BR>
     * <BR>
     * Note: If the following arcs have no successors (no places), the method
     * returns null. But the {@link IDataModelController} should prevent this by
     * removing all adjacent arcs when removing nodes.
     * 
     * @return List of all places behind the successors (arcs)
     */
    List<DataPlace> getSuccPlaces();

}
