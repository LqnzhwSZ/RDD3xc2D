package de.lambeck.pned.exceptions;

/**
 * Exception for errors during creation of elements in a Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class PNElementCreationException extends Exception {

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
    public PNElementCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     *            as in {@link Exception}
     */
    public PNElementCreationException(String message) {
        super(message);
    }

}
