package de.lambeck.pned.exceptions;

/**
 * Checked Exception to be used instead of Javas unchecked
 * {@link IllegalStateException}.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class PNIllegalStateException extends Exception {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 6058526276623755063L;

    /**
     * Exception without parameters
     */
    public PNIllegalStateException() {
        super();
    }

    /**
     * @param message
     *            as in {@link Exception}
     * @param cause
     *            as in {@link Exception}
     */
    public PNIllegalStateException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     *            as in {@link Exception}
     */
    public PNIllegalStateException(String message) {
        super(message);
    }

}
