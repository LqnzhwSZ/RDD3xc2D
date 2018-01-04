package de.lambeck.pned.filesystem.pnml;

/**
 * Allowed types of elements for read-in with the {@link PNMLParser}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public enum EPNMLElement {
    /**
     * A new place
     */
    PLACE("Place"),
    /**
     * A new transition.
     */
    TRANSITION("Transition"),
    /**
     * A new arc.
     */
    ARC("Arc");

    /** The {@link String} value of this enum element */
    private String value;

    /**
     * Constructs this enum element with a specified String value.
     * 
     * @param initValue
     *            The {@link String} value for this enum element
     */
    private EPNMLElement(final String initValue) {
        this.value = initValue;
    }

    /**
     * @return the value of the enum element
     */
    public String getValue() {
        return this.value;
    }

}
