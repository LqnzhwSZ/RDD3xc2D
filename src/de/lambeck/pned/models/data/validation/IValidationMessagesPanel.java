package de.lambeck.pned.models.data.validation;

/**
 * Interface for {@link ValidationMessagesPanel}. Shows the validation messages
 * that belong to the current Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IValidationMessagesPanel {

    /*
     * Setter and Getter
     */

    /**
     * Returns the full name of this {@link ValidationMessagesPanel}. This should
     * be the canonical (unique) path name of the file.
     * 
     * @return The full name
     */
    String getModelName();

    /**
     * Sets the name of this {@link ValidationMessagesPanel}. (Use this method to
     * rename this ValidationMessagesArea in case the user has saved the model
     * under a new file name.)
     * 
     * @param s
     *            The new full name
     */
    void setModelName(String s);

    /**
     * Returns the display name of this {@link ValidationMessagesPanel}.
     * 
     * Intended use: name of the tab
     * 
     * @return The display name
     */
    String getDisplayName();

    /**
     * Sets the display name of this {@link ValidationMessagesPanel}. (Use this
     * method to rename this ValidationMessagesArea in case the user has saved
     * the model under a new file name.)
     * 
     * Intended use: name of the tab
     * 
     * @param s
     *            The new display name
     */
    void setDisplayName(String s);

    /*
     * Methods for the content
     */

    /**
     * Removes all validation messages and resets colors.
     */
    void reset();

    /**
     * Adds a new line.
     * 
     * @param s
     *            The new status
     */
    void addMessage(String s);

    /**
     * Sets the background color of the {@link ValidationMessagesPanel}.
     * 
     * @param c
     *            The new color
     */
    void setBgColor(ValidationColor c);

}
