package de.lambeck.pned.elements.gui;

/**
 * Possible directions for arcs to be used to calculate the arcAnchor in
 * {@link IGuiNode}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public enum EArcDirection {
    /**
     * Arc direction is undefined.
     */
    UNDEFINED(-1),
    /**
     * Arc comes from the left side (somewhere between top left corner and
     * bottom left corner).
     */
    LEFT_SIDE(0),
    /**
     * Arc comes exactly from the top left corner.
     */
    TOP_LEFT_CORNER(1),
    /**
     * Arc comes from the top (somewhere between top left corner and top right
     * corner).
     */
    TOP(2),
    /**
     * Arc comes exactly from the top right corner.
     */
    TOP_RIGHT_CORNER(3),
    /**
     * Arc comes from the right side (somewhere between top right corner and
     * bottom right corner).
     */
    RIGHT_SIDE(4),
    /**
     * Arc comes exactly from the bottom right corner.
     */
    BOTTOM_RIGHT_CORNER(5),
    /**
     * Arc comes from the bottom (somewhere between bottom right corner and
     * bottom left corner).
     */
    BOTTOM(6),
    /**
     * Arc comes exactly from the bottom left corner.
     */
    BOTTOM_LEFT_CORNER(7);

    private int value;

    private EArcDirection(final int initValue) {
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
    public String toPnedString() {
        return Integer.toString(this.value);
    }
}
