package de.lambeck.pned.elements;

/**
 * Possible tokens count for places. (0 or 1 for a Workflow net)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public enum EPlaceToken {
    /**
     * Place has no token.
     */
    ZERO(0),
    /**
     * Place has 1 token. (Maximum for a Workflow net)
     */
    ONE(1);

    /** The int value of this enum element for comparisons */
    private int value;

    /**
     * Constructs this enum element with a specified int value.
     * 
     * @param initValue
     *            The int value for this enum element
     */
    private EPlaceToken(final int initValue) {
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
        return Integer.toString(this.value);
    }

    /**
     * @return the value of the enum element as int
     */
    public int toInt() {
        return this.value;
    }

}
