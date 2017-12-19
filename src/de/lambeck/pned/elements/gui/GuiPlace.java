package de.lambeck.pned.elements.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

import de.lambeck.pned.elements.data.EPlaceMarking;

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

    private EPlaceMarking marking;
    private static int markingSizePercentage = 33;

    /*
     * Constructor etc.
     */

    /**
     * Invokes GuiPlace(id, name, p, zOrder, initialMarking, shapeSize) using
     * the static attribute shapeSizeStandard.
     * 
     * @param id
     *            The id
     * @param name
     *            The name of this place
     * @param p
     *            The center point
     * @param zOrder
     *            The height level
     * @param initialMarking
     *            The initial marking of this place
     */
    @SuppressWarnings("hiding")
    public GuiPlace(String id, String name, Point p, int zOrder, EPlaceMarking initialMarking) {
        this(id, name, p, zOrder, initialMarking, shapeSizeStandard);
    }

    /**
     * Constructs a Place at a given location and in the specified z order
     * (height level) and an additional size value.
     * 
     * @param id
     *            The id
     * @param name
     *            The name of this place
     * @param p
     *            The center point
     * @param zOrder
     *            The height level
     * @param initialMarking
     *            The initial marking of this place
     * @param shapeSize
     *            The size of the shape
     */
    @SuppressWarnings("hiding")
    public GuiPlace(String id, String name, Point p, int zOrder, EPlaceMarking initialMarking, int shapeSize) {
        super(id, name, p, zOrder, shapeSize);
        this.marking = initialMarking;
    }

    /*
     * Getter and Setter
     */

    @Override
    public EPlaceMarking getMarking() {
        return this.marking;
    }

    @Override
    public void setMarking(EPlaceMarking newMarking) {
        this.marking = newMarking;
    }

    /*
     * Methods for interface IGuiElement
     */

    @Override
    public void paintElement(Graphics g) {
        super.paintElement(g);
        Graphics2D g2 = (Graphics2D) g;

        drawMarking(g2);

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
     * Draws the "marking" circle in the center of the shape. Uses the private
     * attribute markingSizePercentage to determine the marking circle size.
     * 
     * @param g2
     *            The Graphics2D object
     */
    private void drawMarking(Graphics2D g2) {
        int size = shapeSize * markingSizePercentage / 100; // shapeSize =
                                                            // shape diameter
        int offset = size / 2;
        int left_x = this.shapeCenter.x - offset;
        int top_y = this.shapeCenter.y - offset;

        g2.setColor(Color.BLACK);

        if (this.marking == EPlaceMarking.ZERO) {
            g2.drawOval(left_x, top_y, size, size);
        } else {
            g2.fillOval(left_x, top_y, size, size);
        }
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
        double ratio = (this.shapeSize / 2) / dist;
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

    /*
     * Method for interface IMarkingCircle
     */

    @Override
    public boolean markingCircleContains(Point p) {
        double max_distance = (double) (shapeSize * markingSizePercentage / 200);

        double x_dist = Math.abs(shapeCenter.getX() - p.getX());
        double y_dist = Math.abs(shapeCenter.getY() - p.getY());
        double distance = Math.sqrt(x_dist * x_dist + y_dist * y_dist);

        if (distance <= max_distance)
            return true;
        return false;
    }

    @Override
    public String toString() {
        // String returnString = super.toString() + ", GuiPlace [id=" + id + ",
        // name=" + name + ", marking=" + marking + ", position=" +
        // shapeCenter.getX() + "," + shapeCenter.getY() + "]";
        String returnString = "GuiPlace [" + super.toString() + ", marking=" + marking + "]";
        return returnString;
    }

    /*
     * Private helper methods
     */

}
