package de.lambeck.pned.models.data.validation;

import java.awt.Color;

import de.lambeck.pned.gui.CustomColor;

/**
 * Color values for the validation text area
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public enum ValidationColor {

    /**
     * White smoke (very light grey) for an empty Petri net
     */
    EMPTY(CustomColor.WHITE_SMOKE.getColor()),
    /**
     * Khaki1 for pending validation
     */
    PENDING(CustomColor.KHAKI1.getColor()),
    /**
     * Pale green for a valid Petri net
     */
    VALID(CustomColor.PALE_GREEN.getColor()),
    /**
     * Light coral for an invalid Petri net
     */
    INVALID(CustomColor.LIGHT_CORAL.getColor());

    private final Color color;

    private ValidationColor(Color c) {
        this.color = c;
    }

    /**
     * @return the color of this enum elemment
     */
    public Color getColor() {
        return this.color;
    }

}
