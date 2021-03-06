package de.lambeck.pned.gui;

import java.awt.Color;

/**
 * More color values with a name (https://web.njit.edu/~kevin/rgb.txt.html)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public enum ECustomColor {

    /**
     * Dark green (e.g. for selection area)
     */
    DARK_GREEN(0, 128, 0),
    /**
     * Pale green (e.g. for enabled state)
     */
    PALE_GREEN(152, 251, 152),
    /**
     * Light coral
     */
    LIGHT_CORAL(240, 128, 128),
    /**
     * Indian red 1
     */
    INDIAN_RED1(255, 106, 106),
    /**
     * Fire brick
     */
    FIREBRICK(178, 34, 34),
    /**
     * Dark red
     */
    DARK_RED(139, 0, 0),
    /**
     * Silver
     */
    SILVER(192, 192, 192),
    /**
     * Ivory (e.g. for background of nodes)
     */
    IVORY(255, 255, 240),
    /**
     * AliceBlue (e.g. for grid lines; but very light!)
     */
    ALICE_BLUE(240, 248, 255),
    /**
     * honeydew2 (e.g. for grid lines)
     */
    HONEY_DEW2(224, 238, 224),
    /**
     * Khaki
     */
    KHAKI(240, 230, 140),
    /**
     * Khaki1
     */
    KHAKI1(255, 246, 143),
    /**
     * Gainsboro
     */
    GAINSBORO(220, 220, 220),
    /**
     * White smoke (e.g. for grid lines; but very light!)
     */
    WHITE_SMOKE(245, 245, 245),
    /**
     * Linen (e.g. for grid lines; but very light!)
     */
    LINEN(250, 240, 230),
    /**
     * Seashell2 (e.g. for grid lines)
     */
    SEASHELL2(238, 229, 222),
    /**
     * Snow2 (e.g. for grid lines)
     */
    SNOW2(238, 233, 233),
    /**
     * Hex: E5E5E5 ("grey90")
     */
    E5E5E5(229, 229, 229),
    /**
     * Hex: F2F2F2 ("grey95")
     */
    F2F2F2(242, 242, 242);

    /** The red component */
    private final int red;
    /** The green component */
    private final int green;
    /** The blue component */
    private final int blue;

    /**
     * Constructor with parameters for the 3 color components.
     * 
     * @param r
     *            the red component
     * @param g
     *            the green component
     * @param b
     *            the blue component
     */
    private ECustomColor(final int r, final int g, final int b) {
        this.red = r;
        this.green = g;
        this.blue = b;
    }

    /**
     * @return the color of this enum element
     */
    public Color getColor() {
        return new Color(red, green, blue);
    }

}
