package de.lambeck.pned.models.gui;

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

    private String value;

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
