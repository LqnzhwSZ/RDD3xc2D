package de.lambeck.pned.elements.gui;

import java.awt.*;

import de.lambeck.pned.gui.ECustomColor;

/**
 * Implements the transitions for the GUI model of the Petri net.<BR>
 * <BR>
 * This means that they have properties which play a role in the GUI only. (e.g.
 * size of nodes, colors)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class GuiTransition extends GuiNode implements IGuiTransition {

    /** The "enabled" state of this transition */
    private boolean enabled = false;

    /** The "safe" state of this transition */
    private boolean safe = true; // Assume "safe" from the start

    /* Constructor */

    /**
     * Constructs a Transition at a given location and in the specified z order
     * (height level).
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
        super(id, name, p, zOrder);
    }

    /* Getter and Setter */

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean newState) {
        this.enabled = newState;
    }

    @Override
    public boolean isSafe() {
        return this.safe;
    }

    @Override
    public void setSafe(boolean newState) {
        this.safe = newState;
    }

    /* Methods for interface IGuiElement */

    @Override
    public void paintElement(Graphics g) {
        super.paintElement(g);
    }

    @Override
    void drawInterior(Graphics2D g2) {
        drawEnabledState(g2);
    }

    /**
     * Paints the shape of this transition in green if enabled to show the
     * enabled state.
     * 
     * @param g2
     *            The Graphics2D object
     */
    private void drawEnabledState(Graphics2D g2) {

        /* Create a copy of the Graphics instance. */
        Graphics2D g2copy = (Graphics2D) g2.create();

        /* Draw most severe states first! */

        if (!this.isSafe()) {
            g2copy.setColor(Color.RED);
            g2copy.fillRect(shapeLeftX, shapeTopY, shapeSize, shapeSize);
            return;
        }

        if (this.unreachable) {
            /* -> Color set in GuiNode */
            g2copy.fillRect(shapeLeftX, shapeTopY, shapeSize, shapeSize);
            return;
        }

        if (this.isEnabled()) {
            g2copy.setColor(ECustomColor.PALE_GREEN.getColor());
            g2copy.fillRect(shapeLeftX, shapeTopY, shapeSize, shapeSize);
        } else {
            /* -> Color set in GuiNode */
            g2copy.fillRect(shapeLeftX, shapeTopY, shapeSize, shapeSize);
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

    /* Methods for interface IGuiNode */

    @Override
    public Point getArcAnchor(Point target) {
        EArcDirection generalArcDirection = EArcDirection.UNDEFINED;

        double target_x = target.getX();
        double target_y = target.getY();
        double shape_center_x = this.shapeCenter.getX();
        double shape_center_y = this.shapeCenter.getY();

        double x_dist = target_x - shape_center_x;
        double y_dist = target_y - shape_center_y;
        /*
         * Attention: distance == 0.0 if target location is == shapeCenter
         * (location of this node)!
         */
        if (x_dist == 0.0 && y_dist == 0.0)
            return null;

        double abs_x_dist = Math.abs(target_x - shape_center_x);
        double abs_y_dist = Math.abs(target_y - shape_center_y);

        /* Determine the general direction */
        if (Math.abs(x_dist) == Math.abs(y_dist)) {
            // System.out.println("Der Pfeil verläuft genau diagonal...");
            if (target_x < shape_center_x && target_y < shape_center_y) {
                generalArcDirection = EArcDirection.TOP_LEFT_CORNER;
            }
            if (target_x > shape_center_x && target_y < shape_center_y) {
                generalArcDirection = EArcDirection.TOP_RIGHT_CORNER;
            }
            if (target_x > shape_center_x && target_y > shape_center_y) {
                generalArcDirection = EArcDirection.BOTTOM_RIGHT_CORNER;
            }
            if (target_x < shape_center_x && target_y > shape_center_y) {
                generalArcDirection = EArcDirection.BOTTOM_LEFT_CORNER;
            }

        } else {
            // System.out.println("Der Pfeil kommt von einer der Seiten...");
            if (target_x < shape_center_x && abs_x_dist > abs_y_dist) {
                generalArcDirection = EArcDirection.LEFT_SIDE;
            }
            if (target_y < shape_center_y && abs_y_dist > abs_x_dist) {
                generalArcDirection = EArcDirection.TOP;
            }
            if (target_x > shape_center_x && abs_x_dist > abs_y_dist) {
                generalArcDirection = EArcDirection.RIGHT_SIDE;
            }
            if (target_y > shape_center_y && abs_y_dist > abs_x_dist) {
                generalArcDirection = EArcDirection.BOTTOM;
            }
        }

        if (generalArcDirection == EArcDirection.UNDEFINED) {
            System.err.println("Couldn't determine the general arc direction for id: " + this.getId());
            return null;
        }

        /* Calculate the anchor point. */
        double doubleX = 0, doubleY = 0, ratio = 0;

        switch (generalArcDirection) {
        case TOP_LEFT_CORNER:
            doubleX = shapeLeftX;
            doubleY = shapeTopY;
            break;
        case TOP_RIGHT_CORNER:
            doubleX = shapeLeftX + shapeSize;
            doubleY = shapeTopY;
            break;
        case BOTTOM_RIGHT_CORNER:
            doubleX = shapeLeftX + shapeSize;
            doubleY = shapeTopY + shapeSize;
            break;
        case BOTTOM_LEFT_CORNER:
            doubleX = shapeLeftX;
            doubleY = shapeTopY + shapeSize;
            break;
        case LEFT_SIDE:
            doubleX = shapeLeftX; // The squares left border
            ratio = Math.abs((shapeSize / 2) / (target_x - shape_center_x));
            doubleY = shape_center_y + (target_y - shape_center_y) * ratio;
            break;
        case TOP:
            doubleY = shapeTopY; // The squares top border
            ratio = Math.abs((shapeSize / 2) / (target_y - shape_center_y));
            doubleX = shape_center_x + (target_x - shape_center_x) * ratio;
            break;
        case RIGHT_SIDE:
            doubleX = shapeLeftX + shapeSize; // The squares right border
            ratio = Math.abs((shapeSize / 2) / (target_x - shape_center_x));
            doubleY = shape_center_y + (target_y - shape_center_y) * ratio;
            break;
        case BOTTOM:
            doubleY = shapeTopY + shapeSize; // The squares bottom border
            ratio = Math.abs((shapeSize / 2) / (target_y - shape_center_y));
            doubleX = shape_center_x + (target_x - shape_center_x) * ratio;
            break;
        default:
            break;
        }

        /* Create and return the Point */
        int intX = (int) Math.round(doubleX);
        int intY = (int) Math.round(doubleY);
        Point anchor = new Point(intX, intY);
        return anchor;
    }

    @Override
    public String toString() {
        String returnString = "GuiTransition [" + super.toString() + ", isEnabled=" + this.isEnabled() + ", isSafe="
                + this.isSafe() + "]";
        return returnString;
    }

}
