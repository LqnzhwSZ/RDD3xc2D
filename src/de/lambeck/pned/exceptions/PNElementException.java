package de.lambeck.pned.exceptions;

/**
 * Exceptions for elements of the Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
@SuppressWarnings("serial")
public class PNElementException extends Exception {

    /**
     * @param message
     *            as in Exception
     */
    public PNElementException(String message) {
        super(message);
    }

    /**
     * @param message
     *            as in Exception
     * @param cause
     *            as in Exception
     */
    public PNElementException(String message, Throwable cause) {
        super(message, cause);
    }

}
