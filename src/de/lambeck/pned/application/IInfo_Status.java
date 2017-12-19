package de.lambeck.pned.application;

/**
 * Interface for components that can show status messages (or can pass them to a
 * parent component).
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IInfo_Status {

    /**
     * Sets the new status.
     * 
     * @param s
     *            The new status
     * @param level
     *            The level of this message (info, warning or error)
     */
    void setInfo_Status(String s, EStatusMessageLevel level);

}
