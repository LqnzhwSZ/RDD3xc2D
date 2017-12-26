package de.lambeck.pned.models.data.validation;

/**
 * Interface for {@link ValidationMsgPanel}. Shows the validation messages that
 * belong to the current Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IValidationMsgPanel {

    /*
     * Setter and Getter
     */

    /**
     * Returns the full name of this {@link ValidationMsgPanel}. This should be
     * the canonical (unique) path name of the file.
     * 
     * @return The full name
     */
    String getModelName();

    /**
     * Sets the name of this {@link ValidationMsgPanel}. (Use this method to
     * rename this ValidationMessagesArea in case the user has saved the model
     * under a new file name.)
     * 
     * @param s
     *            The new full name
     */
    void setModelName(String s);

    /*
     * Methods for the content
     */

    /**
     * Removes all validation messages and resets the background color.
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
     * Sets the background color of the {@link ValidationMsgPanel}.
     * 
     * @param c
     *            The new color
     */
    void setBgColor(EValidationColor c);

}
