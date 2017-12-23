package de.lambeck.pned.elements.data;

/**
 * Possible tokens for places. (0 or 1 for a Workflow net)
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

    private int value;

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
    public String toPnmlString() {
        return Integer.toString(this.value);
    }
}
