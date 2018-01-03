package de.lambeck.pned.elements.gui;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.gui.ECustomColor;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * Implements the places for the GUI model of the Petri net.
 * 
 * This means that they have properties which play a role in the GUI only. (e.g.
 * size of nodes, colors)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class GuiPlace extends GuiNode implements IGuiPlace {

    /** Show debug messages? */
    private static boolean debug = false;

    /** The size of the token circle relative to the shape size */
    private final static int tokensSizePercentage = 33;

    /** Border color of the start place (or start place candidates) */
    private final static Color startPlaceCircleColor = ECustomColor.DARK_GREEN.getColor();

    /** Border color of the end place (or end place candidates) */
    private final static Color endPlaceCircleColor = ECustomColor.FIREBRICK.getColor();

    /** The tokens of this place */
    private EPlaceToken tokens;

    /**
     * Stores whether this place is the real (unambiguous) start place in this
     * workflow net.
     */
    private boolean isStartPlace = false;

    /**
     * Stores whether this place is a start place candidate (if there is more
     * than 1 place without input arcs in this workflow net).
     */
    private boolean isStartPlaceCandidate = false;

    /**
     * Stores whether this place is the real (unambiguous) end place in this
     * workflow net.
     */
    private boolean isEndPlace = false;

    /**
     * Stores whether this place is an end place candidate (if there is more
     * than 1 place without output arcs in this workflow net).
     */
    private boolean isEndPlaceCandidate = false;

    /* Constructor etc. */

    /**
     * Constructs a Place at a given location and in the specified z order
     * (height level).
     * 
     * @param id
     *            The id
     * @param name
     *            The name of this place
     * @param p
     *            The center point
     * @param zOrder
     *            The height level
     * @param initialTokens
     *            Specifies the initial tokens count of this place.
     */
    @SuppressWarnings("hiding")
    public GuiPlace(String id, String name, Point p, int zOrder, EPlaceToken initialTokens) {
        super(id, name, p, zOrder);
        this.tokens = initialTokens;
    }

    /* Getter and Setter */

    /* Methods for interface IGuiPlace */

    @Override
    public EPlaceToken getTokensCount() {
        return this.tokens;
    }

    @Override
    public void setTokens(EPlaceToken newTokens) {
        this.tokens = newTokens;
    }

    @Override
    public void setGuiStartPlace(boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiPlace(" + this.getId() + ").setGuiStartPlace", b);
        }

        this.isStartPlace = b;
    }

    @Override
    public void setGuiStartPlaceCandidate(boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiPlace(" + this.getId() + ").setGuiStartPlaceCandidate", b);
        }

        this.isStartPlaceCandidate = b;
    }

    @Override
    public void setGuiEndPlace(boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiPlace(" + this.getId() + ").setGuiEndPlace", b);
        }

        this.isEndPlace = b;
    }

    @Override
    public void setGuiEndPlaceCandidate(boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiPlace(" + this.getId() + ").setGuiEndPlaceCandidate", b);
        }

        this.isEndPlaceCandidate = b;
    }

    /* Methods for interface IGuiElement */

    @Override
    public void paintElement(Graphics g) {
        super.paintElement(g);
        Graphics2D g2 = (Graphics2D) g;

        /*
         * Draw the token in the center of the place.
         */
        drawTokens(g2);

        /* Highlight if start or end place (candidate). */
        drawStartOrEndPlaceCircles(g2);
    }

    @Override
    void drawInterior(Graphics2D g2) {
        g2.fillOval(shapeLeftX, shapeTopY, shapeSize, shapeSize);
    }

    @Override
    void drawShape(Graphics2D g2) {
        g2.drawOval(shapeLeftX, shapeTopY, shapeSize, shapeSize);
    }

    /**
     * Draws the "tokens" circle in the center of the shape. Uses the private
     * attribute tokensSizePercentage to determine the tokens circle size.
     * 
     * @param g2
     *            The Graphics2D object
     */
    private void drawTokens(Graphics2D g2) {
        int size = (shapeSize * tokensSizePercentage) / 100; // shapeSize =
                                                             // shape diameter
        int offset = size / 2;
        int left_x = this.shapeCenter.x - offset;
        int top_y = this.shapeCenter.y - offset;

        g2.setColor(Color.BLACK);

        if (this.tokens == EPlaceToken.ZERO) {
            // g2.drawOval(left_x, top_y, size, size);
        } else {
            g2.fillOval(left_x, top_y, size, size);
        }
    }

    /**
     * Highlights start/end place (candidates) with thicker colored (and dashed)
     * circles.
     * 
     * @param g2
     *            The Graphics2D object
     */
    private void drawStartOrEndPlaceCircles(Graphics2D g2) {
        if (!isStartOrEndPlaceOrCandidate())
            return;

        /* Create a copy of the Graphics instance. */
        Graphics2D g2copy = (Graphics2D) g2.create();

        /* Line width, (dashed stroke) and color */
        Stroke stroke = getStartOrEndPlaceStroke();
        g2copy.setStroke(stroke);

        Color color = getStartOrEndPlaceColor();
        g2copy.setColor(color);

        /* Draw the circle at the inside of the normal shape. */
        g2copy.drawOval(shapeLeftX + 3, shapeTopY + 3, shapeSize - 6, shapeSize - 6);
    }

    @Override
    public boolean contains(Point p) {
        double x = (double) shapeLeftX;
        double y = (double) shapeTopY;
        double w = (double) shapeSize;
        double h = (double) shapeSize;
        return (new Ellipse2D.Double(x, y, w, h).contains(p));
    }

    /* Methods for interface IGuiNode */

    @Override
    public Point getArcAnchor(Point target) {
        /*
         * Determine the offsets in x and y direction. Use Pythagoras + consider
         * the size of the node.
         */
        double x_dist = target.getX() - this.shapeCenter.getX();
        double y_dist = target.getY() - this.shapeCenter.getY();

        double dist = Math.sqrt((x_dist * x_dist) + (y_dist * y_dist));
        double ratio = (GuiNode.shapeSize / 2) / dist;
        double x_offset = x_dist * ratio;
        double y_offset = y_dist * ratio;

        // Convert to coordinates
        double x_coord = this.shapeCenter.getX() + x_offset;
        double y_coord = this.shapeCenter.getY() + y_offset;

        // Create and return the Point
        int intX = (int) Math.round(x_coord);
        int intY = (int) Math.round(y_coord);
        Point anchor = new Point(intX, intY);
        return anchor;
    }

    @Override
    public String toString() {
        String returnString = "GuiPlace [" + super.toString() + ", tokens=" + tokens + "]";
        return returnString;
    }

    /* Private helpers */

    /**
     * @return True = This is a start or end place (candidate).
     */
    private boolean isStartOrEndPlaceOrCandidate() {
        if (this.isStartPlace || this.isStartPlaceCandidate || this.isEndPlace || this.isEndPlaceCandidate)
            return true;

        return false;
    }

    /**
     * Returns the stroke for the place depending on whether this is a start/end
     * place or a start/end place candidate.
     * 
     * @return A {@link Stroke}
     */
    private Stroke getStartOrEndPlaceStroke() {
        int borderWidth = 5;

        Stroke stroke = new BasicStroke();

        if (this.isStartPlace || this.isEndPlace) {
            /* thicker */
            stroke = new BasicStroke(borderWidth);
        }

        if (this.isStartPlaceCandidate || this.isEndPlaceCandidate) {
            /* thicker and dashed */
            stroke = new BasicStroke(borderWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 5 },
                    0);
        }

        return stroke;
    }

    /**
     * Returns the color for the place depending on whether this is a start/end
     * place or a start/end place candidate.
     * 
     * @return A {@link Color}
     */
    private Color getStartOrEndPlaceColor() {
        Color circleColor = Color.BLACK;

        if (this.isStartPlace || this.isStartPlaceCandidate) {
            circleColor = GuiPlace.startPlaceCircleColor;
        }

        if (this.isEndPlace || this.isEndPlaceCandidate) {
            circleColor = GuiPlace.endPlaceCircleColor;
        }

        return circleColor;
    }

}
