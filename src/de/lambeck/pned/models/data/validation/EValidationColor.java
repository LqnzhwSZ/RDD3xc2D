package de.lambeck.pned.models.data.validation;

import java.awt.Color;

import de.lambeck.pned.gui.ECustomColor;

/**
 * Color values for the validation text area
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public enum EValidationColor {

    /**
     * White smoke (very light grey) for an empty Petri net
     */
    EMPTY(ECustomColor.WHITE_SMOKE.getColor()),
    /**
     * Khaki1 for pending validation
     */
    PENDING(ECustomColor.KHAKI1.getColor()),
    /**
     * Pale green for a valid Petri net
     */
    VALID(ECustomColor.PALE_GREEN.getColor()),
    /**
     * Light coral for an invalid Petri net
     */
    INVALID(ECustomColor.LIGHT_CORAL.getColor());

    /** The used {@link Color} */
    private final Color color;

    /**
     * Constructor with a {@link Color} parameter.
     * 
     * @param c
     *            The {@link Color} to use for this enum element
     */
    private EValidationColor(Color c) {
        this.color = c;
    }

    /**
     * @return the color of this enum element
     */
    public Color getColor() {
        return this.color;
    }

}
