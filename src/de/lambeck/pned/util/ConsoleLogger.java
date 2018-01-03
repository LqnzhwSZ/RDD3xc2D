package de.lambeck.pned.util;

/**
 * Helper for logging of method calls etc.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ConsoleLogger {

    /**
     * Prints the message to the standard output (stdout).
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
     *            boolean: true = message will be printed; false = message will
     *            not be printed
     * @param message
     *            The message
     */
    public static void logIfDebug(boolean debug, String message) {
        if (!debug) { return; }

        logAlways(message);
    }

    /**
     * Prints the specified method call to the standard output (stdout) in a
     * more human readable form.<BR>
     * <BR>
     * All parameters will be printed using their own .toString() method.
     * 
     * @param methodName
     *            The name of the method that was called
     * @param params
     *            Variable parameter list (varargs) for the parameters of the
     *            method call
     */
    public static void consoleLogMethodCall(String methodName, Object... params) {
        String parameterlist = "";

        if (params == null) {
            parameterlist = "null";
        } else {
            for (int i = 0; i < params.length; i++) {
                if (parameterlist != "")
                    parameterlist = parameterlist + ", ";

                if (params[i] == null) {
                    parameterlist = parameterlist + "null";
                } else {
                    parameterlist = parameterlist + params[i].toString();
                }
            }
        }

        String message = methodName + "(" + parameterlist + ")";

        System.out.println(message);

    }

}
