package de.lambeck.pned.models.gui;

/**
 * Possible names for {@link IOverlay} for the {@link IDrawPanel}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public enum EOverlayName {
    /**
     * Overlay for the "draw new arc" mode
     */
    DRAW_NEW_ARC_OVERLAY("draw new arc");

    /** The {@link String} value of this enum element */
    private String value;

    /**
     * Constructs this enum element with a specified String value.
     * 
     * @param initValue
     *            The {@link String} value for this enum element
     */
    private EOverlayName(final String initValue) {
        this.value = initValue;
    }

    /**
     * @return the value of the enum element
     */
    public String getValue() {
        return this.value;
    }

}
