package de.lambeck.pned.models.data.validation;

/**
 * Enum for the severity of problems in a workflow net.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public enum EValidationResultSeverity {
    /**
     * Info message (e.g. intermediate results).
     */
    INFO(0),
    /**
     * Minor problem or debugging information.
     */
    DEBUG(1),
    /**
     * Warning: The workflow net is technically valid, but might be not optimal.
     */
    WARNING(2),
    /**
     * Critical error: The workflow net is not valid!
     */
    CRITICAL(3);

    private int value;

    private EValidationResultSeverity(final int initValue) {
        this.value = initValue;
    }

    /**
     * @return the value of the enum element
     */
    public int getValue() {
        return this.value;
    }

    /**
     * @return the value of the enum element as String
     */
    public String toPnedString() {
        String returnValue = Integer.toString(this.value) + " (" + this + ")";
        return returnValue;
    }

}
