package de.lambeck.pned.elements.gui;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import de.lambeck.pned.elements.data.EPlaceToken;
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

    private static boolean debug = false;

    /** The size of the token circle relative to the shape size */
    private final static int tokensSizePercentage = 33;

    /** The tokens of this place */
    private EPlaceToken tokens;

    /** Stores if this place is the start place in his workflow net */
    private boolean isStartPlace = false;

    /** Stores if this place is the end place in his workflow net */
    private boolean isEndPlace = false;

    /*
     * Constructor etc.
     */

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

    /*
     * Getter and Setter
     */

    /*
     * Methods for interface IGuiPlace
     */

    @Override
    public EPlaceToken getTokensCount() {
        return this.tokens;
    }

    @Override
    public void setTokens(EPlaceToken newTokens) {
        this.tokens = newTokens;
    }

    @Override
    public void setStartPlace(boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiPlace(" + this.getId() + ").setStartPlace", b);
        }

        this.isStartPlace = b;
    }

    @Override
    public void setEndPlace(boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiPlace(" + this.getId() + ").setEndPlace", b);
        }

        this.isEndPlace = b;
    }

    /*
     * Methods for interface IGuiElement
     */

    @Override
    public void paintElement(Graphics g) {
        super.paintElement(g);
        Graphics2D g2 = (Graphics2D) g;

        /*
         * Draw the token in the center of the place.
         */
        drawTokens(g2);

        /*
         * Highlight this place if it is start or end place of the workflow net.
         */
        if (this.isStartPlace) {
            drawStartPlaceCircle(g2);
        }
        if (this.isEndPlace) {
            drawEndPlaceCircle(g2);
        }
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
        int size = shapeSize * (tokensSizePercentage / 100); // shapeSize =
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
     * Highlights the start place with a (thicker) green circle.
     * 
     * @param g2
     *            The Graphics2D object
     */
    private void drawStartPlaceCircle(Graphics2D g2) {
        /*
         * Different line width and color.
         */
        int borderWidth = 5;
        g2.setStroke(new BasicStroke(borderWidth));
        g2.setColor(ECustomColor.DARK_GREEN.getColor());

        /*
         * Draw the circle at the inside of the normal shape.
         */
        g2.drawOval(shapeLeftX + 3, shapeTopY + 3, shapeSize - 6, shapeSize - 6);

        /*
         * Reset line width and color.
         */
        g2.setColor(stdLineColor);
        g2.setStroke(new BasicStroke(stdLineWidth));
    }

    /**
     * Highlights the end place with a (thicker) red circle.
     * 
     * @param g2
     *            The Graphics2D object
     */
    private void drawEndPlaceCircle(Graphics2D g2) {
        /*
         * Different line width and color.
         */
        int borderWidth = 5;
        g2.setStroke(new BasicStroke(borderWidth));
        g2.setColor(ECustomColor.FIREBRICK.getColor());

        /*
         * Draw the circle at the inside of the normal shape.
         */
        g2.drawOval(shapeLeftX + 3, shapeTopY + 3, shapeSize - 6, shapeSize - 6);

        /*
         * Reset line width and color.
         */
        g2.setColor(stdLineColor);
        g2.setStroke(new BasicStroke(stdLineWidth));
    }

    @Override
    public boolean contains(Point p) {
        double x = (double) shapeLeftX;
        double y = (double) shapeTopY;
        double w = (double) shapeSize;
        double h = (double) shapeSize;
        return (new Ellipse2D.Double(x, y, w, h).contains(p));
    }

    /*
     * Methods for interface IGuiNode
     */

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

    /*
     * Private helper methods
     */

}
