package de.lambeck.pned.application;

/**
 * Possible levels of status messages for the status bar.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public enum EStatusMessageLevel {
    /**
     * Info message
     */
    INFO(0),
    /**
     * Warning message
     */
    WARNING(1),
    /**
     * Error message
     */
    ERROR(2);

    /** The int value of this enum element for comparisons */
    private int value;

    /**
     * Constructs this enum element with a specified int value.
     * 
     * @param initValue
     *            The int value for this enum element
     */
    private EStatusMessageLevel(final int initValue) {
        this.value = initValue;
    }

    /**
     * @return the value of the enum element
     */
    public int getValue() {
        return this.value;
    }
}
