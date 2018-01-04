package de.lambeck.pned.exceptions;

/**
 * Exception for the case that an element which already is in the Petri net is
 * added again.
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
     *            as in Exception
     */
    public PNDuplicateAddedException(String message) {
        super(message);
    }

    /**
     * @param message
     *            as in Exception
     * @param cause
     *            as in Exception
     */
    public PNDuplicateAddedException(String message, Throwable cause) {
        super(message, cause);
    }

}
