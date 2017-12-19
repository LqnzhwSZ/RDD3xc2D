package de.lambeck.pned.elements.gui;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import de.lambeck.pned.gui.CustomColor;

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

    /*
     * Info for label position
     */

    /**
     * Label offset in x direction (offset from shapeLeftX, "0" means start
     * below the left border of the node)
     */
    static int labelOffsetX = 0;
    /**
     * Label offset in y direction ("0" means start directly below the node)
     */
    static int labelOffsetY = 15;

    /*
     * Info for label size
     */

    /** Label size (depending on the length of the name) */
    protected int labelWidth = 0;

    /*
     * Attributes for optical appearance
     */

    /** Size of the node */
    static int shapeSizeStandard = 50;
    protected int shapeSize = 50;

    /** The font size for the label */
    protected final int fontSize = 12;
    /** The font for the label */
    protected final Font labelFont = new Font(null, Font.BOLD, fontSize);

    /*
     * Bounds of only the shape (ignoring the size of the label)
     */

    /** x value for the leftmost point of the shape */
    protected int shapeLeftX;
    /** y value for the highest point of the shape */
    protected int shapeTopY;
    /** y value for the lowest point of the shape */
    protected int shapeBottomY;

    /*
     * Bounds including the label (for the "selected marker")
     */

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

    /*
     * Constructor etc.
     */

    /**
     * Invokes GuiNode(id, name, p, zValue, shapeSize) using the static
     * attribute shapeSizeStandard.
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
        this(id, name, p, zValue, shapeSizeStandard);
    }

    /**
     * Constructs a node at a given location and in the specified z level
     * (height level) and an additional size value.
     * 
     * @param id
     *            The id
     * @param name
     *            The name of this node
     * @param p
     *            The center point
     * @param zValue
     *            The height level
     * @param shapeSize
     *            The size of the shape
     */
    @SuppressWarnings("hiding")
    public GuiNode(String id, String name, Point p, int zValue, int shapeSize) {
        super(id, zValue);

        this.shapeCenter = p;
        this.shapeSize = shapeSize;
        this.name = name;
        calculateMyBounds(); // Depend on the other values.
    }

    /*
     * Methods for interface IGuiNode
     */

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @SuppressWarnings("hiding")
    public void setName(String name) {
        this.name = name;
        calculateMyBounds(); // Because labelWidth influences the boundaries!
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

    /*
     * Method for interface IGuiElement
     */

    @Override
    public Rectangle getLastDrawingArea() {
        return this.lastDrawingArea;
    }

    /*
     * No adding and removal of elements - the GUI nodes do not need to know
     * their predecessors/successors!
     */

    /*
     * Methods for interface IGuiNode
     */

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

    /*
     * Individual methods
     */

    /**
     * Getter for the size of this shape. (diameter for circles, edge length for
     * squares)
     * 
     * @return The size
     */
    public int getShapeSize() {
        return this.shapeSize;
    }

    /**
     * Setter for the size of this shape. (diameter for circles, edge length for
     * squares)
     * 
     * @param size
     *            The new size
     */
    public void setShapeSize(int size) {
        this.shapeSize = size;

        /*
         * Change the bounds of the shape since ovals and squares are drawn
         * within these bounds!
         */
        calculateMyBounds();
    }

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
        g2.setColor(CustomColor.IVORY.getColor());
        drawInterior(g2);

        /*
         * Draw the shape
         */
        g2.setColor(Color.BLACK);
        drawShape(g2);

        /*
         * Draw the label
         */
        String labelText = this.getName();
        Point labelLocation = this.getLabelLocation();
        g2.setFont(labelFont);
        g2.drawString(labelText, labelLocation.x, labelLocation.y);

        /*
         * Test: show boundaries and zValue
         */
        if (debug) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawRect(totalLeftX, totalTopY, totalWidth, totalHeight);
            String infoText = "z=" + zValue;
            int textWidth = g2.getFontMetrics().stringWidth(infoText);
            g2.drawString(infoText, shapeLeftX + totalWidth - textWidth, shapeTopY + fontSize);
        }

        /*
         * Show selection
         */
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
        // String returnString = super.toString() + ", name=" + this.getName();
        String returnString = "GuiNode [" + super.toString() + ", name=" + name + ", position=" + shapeCenter.x + ","
                + shapeCenter.y + "]";
        return returnString;
    }

    /*
     * Private helper methods
     */

    /**
     * Calculates the bounds of the shape including the label and stores the
     * results in the attributes.
     */
    protected void calculateMyBounds() {
        this.shapeLeftX = this.shapeCenter.x - (shapeSize / 2);
        this.shapeTopY = this.shapeCenter.y - (shapeSize / 2);
        this.shapeBottomY = this.shapeCenter.y + (shapeSize / 2);

        /*
         * labelWidth influences the boundaries!
         */
        // updateLabelWidth(g2);
        String labelText = this.getName();
        int textWidth = getTextWidth(labelText, this.labelFont);
        this.labelWidth = textWidth;

        /*
         * Real calculation is necessary only for totalWidth and totalHeight
         * because the label starts below the shape.
         */
        this.totalLeftX = shapeLeftX;
        this.totalTopY = shapeTopY;
        // this.totalWidth = Math.max(shapeSize / 2 + labelWidth, shapeSize);
        this.totalWidth = Math.max(labelWidth, shapeSize);
        this.totalHeight = shapeSize + labelOffsetY;

        /*
         * Store my size for the next repaint.
         */
        int x = getTotalLeftX();
        int y = getTotalTopY();
        int width = getTotalWidth() + 1; // +1 nötig wegen Rundungsfehlern?
        int height = getTotalHeight() + 1;
        Rectangle rect = new Rectangle(x, y, width, height);
        this.lastDrawingArea = rect;
    }

    // /**
    // * Calculates the width of the label (the name of this node).
    // */
    // private void updateLabelWidth(Graphics2D g2) {
    // String labelText = this.getName();
    // int textWidth = g2.getFontMetrics().stringWidth(labelText);
    // this.labelWidth = textWidth;
    // }

    /**
     * Calculates the length of the specified text.
     * 
     * https://stackoverflow.com/a/14832962/5944475
     * 
     * @param text
     *            The text
     * @param font
     *            The font of the specified text
     * @return
     */
    private int getTextWidth(String text, Font font) {
        AffineTransform tx = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(tx, true, true);
        int textwidth = (int) (font.getStringBounds(text, frc).getWidth());
        return textwidth;
    }

}
