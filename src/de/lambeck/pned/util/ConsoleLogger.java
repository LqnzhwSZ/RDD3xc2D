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
     * more human readable form.
     * 
     * @param methodName
     *            The specified method
     * @param params
     *            Variable parameter list (varargs) for the parameters of the
     *            method call; all printed with their own .toString() method
     */
    public static void consoleLogMethodCall(String methodName, Object... params) {
        String parameterlist = "";

        for (int i = 0; i < params.length; i++) {
            if (parameterlist != "")
                parameterlist = parameterlist + ", ";
            parameterlist = parameterlist + params[i].toString();
        }

        String message = methodName + "(" + parameterlist + ")";

        System.out.println(message);

    }

}
