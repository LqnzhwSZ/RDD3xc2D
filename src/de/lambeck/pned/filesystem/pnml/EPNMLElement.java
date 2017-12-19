package de.lambeck.pned.filesystem.pnml;

/**
 * Allowed types of elements for read-in.
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

    private String value;

    private EPNMLElement(final String initValue) {
        this.value = initValue;
    }

    /**
     * @return the value of the enum elemment
     */
    public String getValue() {
        return this.value;
    }

}
