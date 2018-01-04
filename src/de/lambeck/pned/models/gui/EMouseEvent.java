package de.lambeck.pned.models.gui;

// TODO This is not in use anymore, remove if we are sure.

/**
 * Events that will be recognized by {@link MyMouseAdapter}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public enum EMouseEvent {
    /**
     * No event
     */
    NONE("No event"),
    /**
     * Only left mouse button pressed
     */
    MOUSE_PRESSED("Only left mouse button pressed"),
    /**
     * Left mouse button pressed + CTRL
     */
    MOUSE_PRESSED_CTRL("Left mouse button pressed + CTRL"),
    /**
     * Left mouse button pressed + ALT
     */
    MOUSE_PRESSED_ALT("Left mouse button pressed + ALT");

    /** The {@link String} value of this enum element */
    private String value;

    /**
     * Constructs this enum element with a specified String value.
     * 
     * @param initValue
     *            The {@link String} value for this enum element
     */
    private EMouseEvent(final String initValue) {
        this.value = initValue;
    }

    /**
     * @return the value of the enum element
     */
    public String getValue() {
        return this.value;
    }

}
