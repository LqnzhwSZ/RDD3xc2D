package de.lambeck.pned.application;

@SuppressWarnings("javadoc")
public class Parametertest {

    public static void main(String[] args) {
        Parametertest myTest = new Parametertest();
        myTest.doTest();
    }

    private void doTest() {
        String s = null;
        System.out.println(isParamNullOrEmpty(s, "doTest", "s"));
    }

    /**
     * Returns true if a parameter is null or empty.
     * 
     * @param param
     *            The parameter to check
     * @param methodName
     *            The name of the method
     * @param paramName
     *            The name of the parameter
     * @return True if parameter is null or empty; otherwise false
     */
    private boolean isParamNullOrEmpty(Object param, String methodName, String paramName) {
        if (param == null) {
            System.err.println("Undefined parameter " + paramName + " in method " + methodName + "!");
            return true;
        }
        if (param == "") {
            System.err.println("Empty parameter " + paramName + " in method " + methodName + "!");
            return true;
        }
        return false;
    }

}
