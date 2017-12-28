package de.lambeck.pned.gui.icon;

/**
 * Enum for possible icon sizes
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

    private int size;

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
