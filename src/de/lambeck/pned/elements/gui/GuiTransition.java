package de.lambeck.pned.elements.gui;

import java.awt.*;

import de.lambeck.pned.gui.CustomColor;

/**
 * Implements the transitions for the GUI model of the Petri net.
 * 
 * This means that they have properties which play a role in the GUI only. (e.g.
 * size of nodes, colors)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class GuiTransition extends GuiNode {

    private boolean activated = false;

    /*
     * Constructor etc.
     */

    /**
     * Invokes GuiTransition(id, name, p, zOrder, shapeSize) using the static
     * attribute shapeSizeStandard.
     * 
     * @param id
     *            The id
     * @param name
     *            The name of this transition
     * @param p
     *            The center point
     * @param zOrder
     *            The height level
     */
    @SuppressWarnings("hiding")
    public GuiTransition(String id, String name, Point p, int zOrder) {
        // super(id, name, p, zOrder);
        this(id, name, p, zOrder, shapeSizeStandard);
    }

    /**
     * Constructs a Transition at a given location and in the specified z order
     * (height level) and an additional size value.
     * 
     * @param id
     *            The id
     * @param name
     *            The name of this transition
     * @param p
     *            The center point
     * @param zOrder
     *            The height level
     * @param shapeSize
     *            The size of the shape
     */
    @SuppressWarnings("hiding")
    public GuiTransition(String id, String name, Point p, int zOrder, int shapeSize) {
        super(id, name, p, zOrder, shapeSize);
    }

    /*
     * Getter and setter
     */

    /**
     * @return True if this transition is activated.
     */
    public boolean isActivated() {
        return this.activated;
    }

    /**
     * Sets the activated attribute of this transition.
     * 
     * @param newState
     *            The new activated state
     */
    public void setActivated(boolean newState) {
        this.activated = newState;
    }

    /*
     * Methods for interface IGuiElement
     */

    @Override
    public void paintElement(Graphics g) {
        super.paintElement(g);
    }

    @Override
    void drawInterior(Graphics2D g2) {
        drawActivated(g2);
        g2.fillRect(shapeLeftX, shapeTopY, shapeSize, shapeSize);
    }

    /**
     * Paints the shape of this transition in green if activated to show the
     * activated status.
     * 
     * @param g2
     *            The Graphics2D object
     */
    private void drawActivated(Graphics2D g2) {

        // TODO Test
        setActivated(true);

        if (isActivated()) {
            g2.setColor(CustomColor.PALE_GREEN.getColor());
            g2.fillRect(shapeLeftX, shapeTopY, shapeSize, shapeSize);
        } else {
            // TODO Reset to white or just nothing?
            return;
        }
    }

    @Override
    void drawShape(Graphics2D g2) {
        g2.drawRect(shapeLeftX, shapeTopY, shapeSize, shapeSize);
    }

    @Override
    public boolean contains(Point p) {
        return (new Rectangle(shapeLeftX, shapeTopY, shapeSize, shapeSize).contains(p));
    }

    /*
     * Methods for interface IGuiNode
     */

    @Override
    public Point getArcAnchor(Point target) {
        int generalArcDirection = ArcDirection.UNDEFINED;

        double target_x = target.getX();
        double target_y = target.getY();
        double shape_center_x = this.shapeCenter.getX();
        double shape_center_y = this.shapeCenter.getY();

        double x_dist = target_x - shape_center_x;
        double y_dist = target_y - shape_center_y;

        double abs_x_dist = Math.abs(target_x - shape_center_x);
        double abs_y_dist = Math.abs(target_y - shape_center_y);

        /*
         * Determine the general direction
         */
        if (Math.abs(x_dist) == Math.abs(y_dist)) {
            // System.out.println("Der Pfeil verläuft genau diagonal...");
            if (target_x < shape_center_x && target_y < shape_center_y) {
                generalArcDirection = ArcDirection.TOP_LEFT_CORNER;
            }
            if (target_x > shape_center_x && target_y < shape_center_y) {
                generalArcDirection = ArcDirection.TOP_RIGHT_CORNER;
            }
            if (target_x > shape_center_x && target_y > shape_center_y) {
                generalArcDirection = ArcDirection.BOTTOM_RIGHT_CORNER;
            }
            if (target_x < shape_center_x && target_y > shape_center_y) {
                generalArcDirection = ArcDirection.BOTTOM_LEFT_CORNER;
            }

        } else {
            // System.out.println("Der Pfeil kommt von einer der Seiten...");
            if (target_x < shape_center_x && abs_x_dist > abs_y_dist) {
                generalArcDirection = ArcDirection.LEFT_SIDE;
            }
            if (target_y < shape_center_y && abs_y_dist > abs_x_dist) {
                generalArcDirection = ArcDirection.TOP;
            }
            if (target_x > shape_center_x && abs_x_dist > abs_y_dist) {
                generalArcDirection = ArcDirection.RIGHT_SIDE;
            }
            if (target_y > shape_center_y && abs_y_dist > abs_x_dist) {
                generalArcDirection = ArcDirection.BOTTOM;
            }
        }

        if (generalArcDirection == ArcDirection.UNDEFINED) {
            System.err.println("Couldn't determine the general arc direction for id: " + this.getId());
            return null;
        }

        /*
         * Calculate the anchor point
         */
        double doubleX = 0, doubleY = 0, ratio = 0;

        switch (generalArcDirection) {
        case ArcDirection.TOP_LEFT_CORNER:
            doubleX = shapeLeftX;
            doubleY = shapeTopY;
            break;
        case ArcDirection.TOP_RIGHT_CORNER:
            doubleX = shapeLeftX + shapeSize;
            doubleY = shapeTopY;
            break;
        case ArcDirection.BOTTOM_RIGHT_CORNER:
            doubleX = shapeLeftX + shapeSize;
            doubleY = shapeTopY + shapeSize;
            break;
        case ArcDirection.BOTTOM_LEFT_CORNER:
            doubleX = shapeLeftX;
            doubleY = shapeTopY + shapeSize;
            break;
        case ArcDirection.LEFT_SIDE:
            doubleX = shapeLeftX; // The squares left border
            ratio = Math.abs((shapeSize / 2) / (target_x - shape_center_x));
            doubleY = shape_center_y + (target_y - shape_center_y) * ratio;
            break;
        case ArcDirection.TOP:
            doubleY = shapeTopY; // The squares top border
            ratio = Math.abs((shapeSize / 2) / (target_y - shape_center_y));
            doubleX = shape_center_x + (target_x - shape_center_x) * ratio;
            break;
        case ArcDirection.RIGHT_SIDE:
            doubleX = shapeLeftX + shapeSize; // The squares right border
            ratio = Math.abs((shapeSize / 2) / (target_x - shape_center_x));
            doubleY = shape_center_y + (target_y - shape_center_y) * ratio;
            break;
        case ArcDirection.BOTTOM:
            doubleY = shapeTopY + shapeSize; // The squares bottom border
            ratio = Math.abs((shapeSize / 2) / (target_y - shape_center_y));
            doubleX = shape_center_x + (target_x - shape_center_x) * ratio;
            break;
        default:
            break;
        }

        // Create and return the Point
        int intX = (int) Math.round(doubleX);
        int intY = (int) Math.round(doubleY);
        Point anchor = new Point(intX, intY);
        return anchor;
    }

    @Override
    public String toString() {
        // String returnString = super.toString() + ", GuiTransition [id=" + id
        // + ", name=" + name + ", position=" + shapeCenter.getX() + "," +
        // shapeCenter.getY() + "]";
        String returnString = "GuiTransition [" + super.toString() + "]";
        return returnString;
    }

    /*
     * Private helper methods
     */

}
