package de.lambeck.pned.exceptions;

/**
 * Exception for the case that an element which already is in the Petri net is
 * added again.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
@SuppressWarnings("serial")
public class PNDuplicateAddedException extends Exception {

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
