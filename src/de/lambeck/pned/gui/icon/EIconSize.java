package de.lambeck.pned.gui.icon;

/**
 * Enum for possible icon sizes in Pixel.<BR>
 * <BR>
 * For best results:<BR>
 * Store icons in a size that is a multiple of 96 (least common multiple of all
 * values in this enum).
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public enum EIconSize {
    /**
     * No icon
     */
    NONE(0),
    /**
     * Size 1
     */
    SMALL(16),
    /**
     * Size 2
     */
    MEDIUM(24),
    /**
     * Size 3
     */
    LARGE(32),
    /**
     * Size 4
     */
    VERY_LARGE(48);

    /** The int value of this enum element */
    private int size;

    /**
     * Constructs this enum element with a specified int value.
     * 
     * @param initValue
     *            The int value for this enum element
     */
    EIconSize(final int initValue) {
        this.size = initValue;
    }

    /**
     * @return the value of the enum element
     */
    public int getValue() {
        return this.size;
    }

}
