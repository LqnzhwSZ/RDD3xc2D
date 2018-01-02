package de.lambeck.pned.elements.gui;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import de.lambeck.pned.gui.ECustomColor;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * Superclass GuiNode implements the common members for all nodes.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public abstract class GuiNode extends GuiElement implements IGuiNode {

    private static boolean debug = false;

    /*
     * Attributes for interface IGuiNode (Bounds include the label for
     * repainting of former backgrounds.)
     */

    /** The name of this node */
    protected String name;

    /** List of predecessors (arcs) */
    protected ArrayList<IGuiArc> predElems = new ArrayList<IGuiArc>();
    /** List of successors (arcs) */
    protected ArrayList<IGuiArc> succElems = new ArrayList<IGuiArc>();
    /** The center of the shape (ignoring the size of the label) */
    protected Point shapeCenter = null; // The center of the shape

    /**
     * This nodes "unreachable" status: True = unreachable; False = can be
     * reached from the start place and can reach the end place
     */
    protected boolean unreachable = false;

    /* Info for label position */

    /**
     * Label offset in x direction (offset from shapeLeftX, "0" means start
     * below the left border of the node)
     */
    static int labelOffsetX = 0;
    /**
     * Label offset in y direction ("0" means start directly below the node)
     */
    static int labelOffsetY = 15;

    /* Attributes for optical appearance */

    /** Size of the node */
    static int shapeSize = 50;

    /** The font size for the label */
    protected final int fontSize = 12;
    /** The font for the label */
    protected final Font labelFont = new Font(null, Font.BOLD, fontSize);

    /* Bounds of only the shape (ignoring the size of the label) */

    /** x value for the leftmost point of the shape */
    protected int shapeLeftX;
    /** y value for the highest point of the shape */
    protected int shapeTopY;
    /** y value for the lowest point of the shape */
    protected int shapeBottomY;

    /* Bounds including the label (for the "selected marker") */

    /** x value for the leftmost point of the node (including the label) */
    protected int totalLeftX;
    /** x value for the highest point of the node (including the label) */
    protected int totalTopY;
    /** x value for the width of the node (including the label) */
    protected int totalWidth = 0;
    /** x value for the height of the node (including the label) */
    protected int totalHeight = 0;

    /**
     * The drawing area used during last invocation of paintElement.
     */
    Rectangle lastDrawingArea = null;

    /* Constructor */

    /**
     * Constructs a node at a given location and in the specified z level
     * (height level).
     * 
     * @param id
     *            The id
     * @param name
     *            The name of this node
     * @param p
     *            The center point
     * @param zValue
     *            The height level
     */
    @SuppressWarnings("hiding")
    public GuiNode(String id, String name, Point p, int zValue) {
        super(id, zValue);

        this.shapeCenter = p;
        this.name = name;
        calculateMyBounds(); // Depend on the other values.
    }

    /* Methods for interface IGuiNode */

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @SuppressWarnings("hiding")
    public void setName(String name) {
        this.name = name;
        calculateMyBounds(); // Because label size and position influences the
                             // boundaries!
    }

    @Override
    public Point getPosition() {
        return this.shapeCenter;
    }

    @Override
    public void setPosition(Point p) {
        this.shapeCenter = p;
        calculateMyBounds();
    }

    /* Method for interface IGuiElement */

    @Override
    public Rectangle getLastDrawingArea() {
        return this.lastDrawingArea;
    }

    /**
     * Returns the size of all shapes (static attribute).
     * 
     * Note: Used for placing new nodes inside of the visible area (left and top
     * border > 0).
     * 
     * @return The current shape size
     */
    public static int getShapeSize() {
        return GuiPlace.shapeSize;
    }

    /**
     * Changes the size of all shapes (static attribute).
     * 
     * @param size
     */
    public static void changeShapeSize(int size) {
        GuiPlace.shapeSize = size;
    }

    /*
     * No adding and removal of elements - the GUI nodes do not need to know
     * their predecessors/successors!
     */

    /* Methods for interface IGuiNode */

    @Override
    public int getTotalLeftX() {
        return this.totalLeftX;
    }

    @Override
    public int getTotalTopY() {
        return this.totalTopY;
    }

    @Override
    public int getTotalWidth() {
        return this.totalWidth;
    }

    @Override
    public int getTotalHeight() {
        return this.totalHeight;
    }

    @Override
    public void setUnreachable(boolean b) {
        this.unreachable = b;
    }

    /* Individual methods */

    /**
     * Determines the coordinates of the label depending on the center of this
     * node.
     * 
     * @return Point representing the coordinates for the label
     */
    public Point getLabelLocation() {
        // calculateMyBounds();
        // int newX = this.shapeCenter.x + labelOffsetX;
        int newX = this.shapeLeftX + labelOffsetX;
        int newY = this.shapeCenter.y + (shapeSize / 2) + labelOffsetY;
        Point location = new Point(newX, newY);
        return location;
    }

    @Override
    public void paintElement(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        activateAntialiasing(g2);

        calculateMyBounds();

        /*
         * Draw the interior first because the shape must be above it to be
         * visible.
         */
        if (!this.unreachable) {
            g2.setColor(ECustomColor.IVORY.getColor());
        } else {
            /* Overwrite interior color to highlight unreachable nodes! */
            g2.setColor(Color.GRAY);
        }
        drawInterior(g2);

        /* Draw the shape */
        g2.setColor(Color.BLACK);
        drawShape(g2);

        /* Draw the label */
        String labelText = this.getName();
        Point labelLocation = this.getLabelLocation();
        g2.setFont(labelFont);
        g2.drawString(labelText, labelLocation.x, labelLocation.y);

        /* Test: show boundaries and zValue */
        if (debug) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawRect(totalLeftX, totalTopY, totalWidth, totalHeight);
            String infoText = "z=" + zValue;
            int textWidth = g2.getFontMetrics().stringWidth(infoText);
            g2.drawString(infoText, shapeLeftX + totalWidth - textWidth, shapeTopY + fontSize);
        }

        /* Show selection */
        drawSelection(g2);
    }

    private void activateAntialiasing(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    abstract void drawInterior(Graphics2D g2);

    abstract void drawShape(Graphics2D g2);

    /**
     * Indicates the selection of this node.
     * 
     * @param g2
     *            The Graphics2D object
     */
    private void drawSelection(Graphics2D g2) {
        // TODO Nur Eckpunkte zum Darstellen der Selektion?
        if (this.selected) {
            g2.setColor(Color.BLUE);
            g2.drawRect(totalLeftX, totalTopY, totalWidth, totalHeight);
        }
    }

    @Override
    public String toString() {
        String returnString = "GuiNode [" + super.toString() + ", name=" + name + ", position=" + shapeCenter.x + ","
                + shapeCenter.y + ", unreachable=" + this.unreachable + "]";
        return returnString;
    }

    /* Private helper methods */

    /**
     * Calculates the bounds of the shape including the label and stores the
     * results in the attributes.
     */
    protected void calculateMyBounds() {
        this.shapeLeftX = this.shapeCenter.x - (shapeSize / 2);
        this.shapeTopY = this.shapeCenter.y - (shapeSize / 2);
        this.shapeBottomY = this.shapeCenter.y + (shapeSize / 2);

        /* label size and position influences the boundaries! */
        String labelText = this.getName();
        Rectangle2D labelTextRect = getTextBounds(labelText, this.labelFont);

        /*
         * Real calculation is necessary for:
         * 
         * - LeftX: Because some chars (e.g. "t") stick out to the left.
         * 
         * - totalWidth: like LeftX
         * 
         * - totalHeight: Because some chars (e.g. "p") stick out to the bottom.
         */
        int labelTextLeftX = (int) labelTextRect.getMinX();
        this.totalLeftX = Math.min(shapeLeftX, shapeLeftX + labelTextLeftX);

        this.totalTopY = shapeTopY;

        int labelTextRightX = (int) labelTextRect.getMaxX();
        this.totalWidth = Math.max(shapeSize, labelTextRightX);

        int labelTextBottomY = (int) labelTextRect.getMaxY();
        this.totalHeight = Math.max(shapeSize, shapeSize + labelOffsetY + labelTextBottomY);

        /* Store my drawing area for the next repaint. */
        int x = getTotalLeftX();
        int y = getTotalTopY();
        int width = getTotalWidth();
        int height = getTotalHeight();

        /*
         * Correct the values because some characters (stick out on the left
         * side of the label).
         * 
         * -> e.g. "t": Dragging a node with the label "t..." to the right side
         * leaves the most left pixel of "t" as an artifact if we repaint only
         * the theoretical area!
         * 
         * (Does this occur on the right side and bottom as well???)
         * 
         * (Are these just rounding errors???)
         */
        x = x - 1;
        y = y - 1;
        width = width + 2;
        height = height + 2;

        Rectangle rect = new Rectangle(x, y, width, height);
        this.lastDrawingArea = rect;
    }

    /**
     * Calculates the bounds of the specified text.
     * 
     * @param text
     *            The text
     * @param font
     *            The font of the specified text
     * @return
     */
    private Rectangle2D getTextBounds(String text, Font font) {
        AffineTransform tx = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(tx, true, true);

        String message = "text: " + text;
        ConsoleLogger.logIfDebug(debug, message);

        /*
         * https://stackoverflow.com/a/14832962/5944475
         */
        // int textwidth = (int) (font.getStringBounds(text, frc).getWidth());
        // System.out.println("getTextBounds, textwidth: " + textwidth);
        // int textheight = (int)(font.getStringBounds(text, frc).getHeight());
        // System.out.println("getTextBounds, textheight: " + textheight);

        /*
         * getWidth() and getHeight() give us only the size. But we need the
         * position as well to get information about how much the text sticks
         * out to the left side or the bottom!
         */
        Rectangle2D rect2d = font.getStringBounds(text, frc);

        message = "GuiNode, getTextBounds(), rect.getMinX(): " + rect2d.getMinX();
        ConsoleLogger.logIfDebug(debug, message);
        message = "GuiNode, getTextBounds(), rect.getMaxX(): " + rect2d.getMaxX();
        ConsoleLogger.logIfDebug(debug, message);
        message = "GuiNode, getTextBounds(), rect.getMinY(): " + rect2d.getMinY();
        ConsoleLogger.logIfDebug(debug, message);
        message = "GuiNode, getTextBounds(), rect.getMaxY(): " + rect2d.getMaxY();
        ConsoleLogger.logIfDebug(debug, message);

        /*
         * This gives us still the wrong size: For example 93.34 for
         * "wwwwwwwwww" instead of the real approximately 100!?
         */
        // System.err.println(rect2d);

        /* We add something to all sides */

        // TODO Get rid of this ugly "trick" as soon as possible possible!!!

        int x = (int) rect2d.getMinX();
        int y = (int) rect2d.getMinY();
        int w = (int) rect2d.getWidth();
        int h = (int) rect2d.getHeight();

        x = x - 1;
        y = y - 1;
        w = (int) (w * 1.1);
        h = (int) (h * 1.1);

        Rectangle bigger = new Rectangle(x, y, w, h);
        rect2d = bigger;
        // System.err.println(rect2d);

        return rect2d;
    }

}
