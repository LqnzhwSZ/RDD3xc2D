package de.lambeck.pned.exceptions;

import java.util.NoSuchElementException;

/**
 * Checked Exception to be used instead of Javas unchecked
 * {@link NoSuchElementException}.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class PNNoSuchElementException extends Exception {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -2626272961681414601L;

    /**
     * Exception without parameters
     */
    public PNNoSuchElementException() {
        super();
    }

    /**
     * @param message
     *            as in {@link Exception}
     * @param cause
     *            as in {@link Exception}
     */
    public PNNoSuchElementException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     *            as in {@link Exception}
     */
    public PNNoSuchElementException(String message) {
        super(message);
    }

}
