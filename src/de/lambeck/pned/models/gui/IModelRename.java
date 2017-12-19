package de.lambeck.pned.models.gui;

/**
 * Non-public interface for methods that should be visible to the model
 * controller only.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
interface IModelRename {

    /**
     * Sets the name of this model. (Use this method to rename this model in
     * case the user has saved the model under a new file name.)
     * 
     * Note: This is in a non-public interface because the model controller has
     * to keep his list of model up to date!
     *
     * @param s
     *            The new full name
     */
    void setModelName(String s);

    /**
     * Sets the display name of this model. (Use this method to rename this
     * model in case the user has saved the model under a new file name.)
     * 
     * Intended use: name of the tab
     * 
     * @param s
     *            The new display name
     */
    void setDisplayName(String s);

}
