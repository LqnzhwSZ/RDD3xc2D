package de.lambeck.pned.exceptions;

/**
 * General exception for elements of the Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class PNElementException extends Exception {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 8006448432604300388L;

    /**
     * @param message
     *            as in {@link Exception}
     * @param cause
     *            as in {@link Exception}
     */
    public PNElementException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     *            as in {@link Exception}
     */
    public PNElementException(String message) {
        super(message);
    }

}
