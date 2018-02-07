package de.lambeck.pned.exceptions;

/**
 * Exception for the attempt to add an existing element to a Petri net again.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class PNDuplicateAddedException extends Exception {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -354979179841429255L;

    /**
     * Exception without parameters
     */
    public PNDuplicateAddedException() {
        super();
    }

    /**
     * @param message
     *            as in {@link Exception}
     * @param cause
     *            as in {@link Exception}
     */
    public PNDuplicateAddedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     *            as in {@link Exception}
     */
    public PNDuplicateAddedException(String message) {
        super(message);
    }

}
