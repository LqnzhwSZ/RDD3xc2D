package de.lambeck.pned.application;

/**
 * Exit codes for method calls.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ExitCode {

    /**
     * An unexpected error has happened.
     */
    public static final int UNEXPECTED_ERROR = -1;

    /**
     * The operation was successful.
     */
    public static final int OPERATION_SUCCESSFUL = 0;

    /**
     * The operation failed.
     */
    public static final int OPERATION_FAILED = 1;

    /**
     * The user has canceled the operation.
     */
    public static final int OPERATION_CANCELED = 2;

}
