package de.lambeck.pned.elements.gui;

/**
 * General arc directions for the squares because the calculation is different
 * for each corner and side of the square. (The simple "Pythagoras calculation"
 * as in the circles does not work for squares!)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ArcDirection {

    @SuppressWarnings("javadoc")
    public static final int UNDEFINED = -1;

    @SuppressWarnings("javadoc")
    public static final int LEFT_SIDE = 0;

    @SuppressWarnings("javadoc")
    public static final int TOP_LEFT_CORNER = 1;

    @SuppressWarnings("javadoc")
    public static final int TOP = 2;

    @SuppressWarnings("javadoc")
    public static final int TOP_RIGHT_CORNER = 3;

    @SuppressWarnings("javadoc")
    public static final int RIGHT_SIDE = 4;

    @SuppressWarnings("javadoc")
    public static final int BOTTOM_RIGHT_CORNER = 5;

    @SuppressWarnings("javadoc")
    public static final int BOTTOM = 6;

    @SuppressWarnings("javadoc")
    public static final int BOTTOM_LEFT_CORNER = 7;

}
