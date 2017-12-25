package de.lambeck.pned.util;

/**
 * Helper for logging of method calls etc.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ConsoleLogger {

    /**
     * Prints the message to stdout.
     * 
     * @param message
     *            The message
     */
    public static void logAlways(String message) {
        System.out.println(message);
    }

    /**
     * Invokes logAlways(String message) if debug == true.
     * 
     * @param debug
     *            If true: the message gets printed; otherwise not
     * @param message
     *            The message
     */
    public static void logIfDebug(boolean debug, String message) {
        if (!debug) { return; }

        logAlways(message);
    }

    /**
     * Prints the specified method call to stdout in a more human readable form.
     * 
     * @param methodname
     *            The specified method
     * @param params
     *            Variable parameter list (varargs) for the parameters of the
     *            method call; all printed with their own .toString() method
     */
    public static void consoleLogMethodCall(String methodname, Object... params) {
        String parameterlist = "";

        for (int i = 0; i < params.length; i++) {
            if (parameterlist != "")
                parameterlist = parameterlist + ", ";
            parameterlist = parameterlist + params[i].toString();
        }

        String message = methodname + "(" + parameterlist + ")";

        System.out.println(message);

    }

}
