package de.lambeck.pned.elements.data;

import de.lambeck.pned.models.data.IDataModel;

/**
 * Interface for transitions in a {@link IDataModel}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IDataTransition extends IDataNode {

    /**
     * @return True if this transition is activated.
     */
    public boolean isActivated();

    /**
     * Checks if this transition is activated and sets the member variable
     * "activated" accordingly.
     * 
     * Activated is evaluated to false if there is no previous {@link DataPlace}
     * or if the first previous DataPlace without a token is found.
     */
    public void checkActivated();

}
