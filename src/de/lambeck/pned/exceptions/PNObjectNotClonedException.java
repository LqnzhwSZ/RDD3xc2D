package de.lambeck.pned.exceptions;

import de.lambeck.pned.models.IModel;

/**
 * Exception for errors when cloning objects. (Deep copy of {@link IModel} for
 * Undo/Redo)
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class PNObjectNotClonedException extends Exception {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 3992419118904691577L;

    /**
     * @param message
     *            as in {@link Exception}
     * @param cause
     *            as in {@link Exception}
     */
    public PNObjectNotClonedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     *            as in {@link Exception}
     */
    public PNObjectNotClonedException(String message) {
        super(message);
    }

}
