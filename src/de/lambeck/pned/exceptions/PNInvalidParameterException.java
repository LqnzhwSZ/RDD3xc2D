package de.lambeck.pned.exceptions;

import java.security.InvalidParameterException;

/**
 * Checked Exception to be used instead of Javas unchecked
 * {@link InvalidParameterException}.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class PNInvalidParameterException extends Exception {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -368961620909769649L;

    /**
     * Exception without parameters
     */
    public PNInvalidParameterException() {
        super();
    }

    /**
     * @param message
     *            as in {@link Exception}
     * @param cause
     *            as in {@link Exception}
     */
    public PNInvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     *            as in {@link Exception}
     */
    public PNInvalidParameterException(String message) {
        super(message);
    }

}
