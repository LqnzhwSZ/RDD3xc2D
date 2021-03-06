package de.lambeck.pned.elements.gui;

import java.awt.*;
import java.awt.geom.Line2D;

import de.lambeck.pned.elements.util.NodeCheck;
import de.lambeck.pned.exceptions.PNElementCreationException;
import de.lambeck.pned.exceptions.PNNoSuchElementException;

/**
 * Implements the arcs (arrows) of the Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class GuiArc extends GuiElement implements IGuiArc {

    /** Show debug messages? */
    private static boolean debug = false;

    /** The predecessor - the node that is the source of this arc */
    private IGuiNode pred;
    /** The successor - the node that is the target of this arc */
    private IGuiNode succ;

    /* Attributes for optical appearance */

    /** The thickness of the arc (the line of the arrow) */
    static int arrowLineThickness = 1;
    /** Size of the arrow tip */
    static int arrowTipLength = 10;

    /**
     * The drawing area used during last invocation of paintElement.
     */
    Rectangle lastDrawingArea = null;

    /* Constructor */

    /**
     * Constructor with parameters for target and source. Makes sure that the
     * arc always connects a place and a transition.
     * 
     * @param id
     *            The id of this Arc
     * @param zValue
     *            The height level
     * @param source
     *            The source id
     * @param target
     *            The target id
     * @throws PNElementCreationException
     *             For an invalid combination of source and target
     */
    @SuppressWarnings("hiding")
    public GuiArc(String id, int zValue, IGuiNode source, IGuiNode target) throws PNElementCreationException {
        super(id, zValue);

        /* Check for different types of elements */
        if (!NodeCheck.isValidConnection(source, target))
            throw new PNElementCreationException("Invalid combination of source and target for Arc");

        /*
         * predElements/succElements must have only 1 entry and they are lists
         * but empty when creating this instance.
         */
        this.pred = source;
        this.succ = target;
    }

    /* Static Getter and Setter */

    /**
     * Returns the size of all shapes (static attribute).<BR>
     * <BR>
     * Note: Used for drawing the (temporary) arc on the "draw new arc" overlay.
     * 
     * @return The current shape size
     */
    public static int getShapeSize() {
        return GuiArc.arrowTipLength;
    }

    /**
     * Changes the size of all shapes (static attribute). Calculates a smaller
     * value to make arcs smaller than the nodes!
     * 
     * @param size
     *            The new size
     */
    public static void changeShapeSize(int size) {
        GuiArc.arrowTipLength = (int) (size * 0.2); // Smaller than the nodes!
    }

    /* Method for interface IGuiElement */

    @Override
    public Rectangle getLastDrawingArea() {
        return this.lastDrawingArea;
    }

    @Override
    public void paintElement(Graphics g) {
        Point startAnchor = getStartAnchor();
        Point endAnchor = getEndAnchor();
        if (startAnchor == null || endAnchor == null)
            return;

        Graphics2D g2 = (Graphics2D) g;
        activateAntialiasing(g2);
        g2.setColor(Color.BLACK);

        if (debug) {
            // Highlight the anchor positions
            highlightAnchors(g2, startAnchor, endAnchor);
        }

        drawArrowLine(g2, startAnchor, endAnchor);

        drawArrowTip(g2, startAnchor, endAnchor);

        /* Indicate selection */
        drawSelection(g2, startAnchor, endAnchor);

        /* Store the used drawing area. */
        calculateMyBounds();
    }

    // /**
    // * Draw an arrow line between two points.
    // *
    // * https://stackoverflow.com/a/27461352
    // *
    // * @param g
    // * the graphics component.
    // * @param x1
    // * x-position of first point.
    // * @param y1
    // * y-position of first point.
    // * @param x2
    // * x-position of second point.
    // * @param y2
    // * y-position of second point.
    // * @param d
    // * the width of the arrow.
    // * @param h
    // * the height of the arrow.
    // */
    // private void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2,
    // int d, int h) {
    // int dx = x2 - x1;
    // int dy = y2 - y1;
    // double D = Math.sqrt(dx * dx + dy * dy);
    // double xm = D - d;
    // double xn = xm;
    // double ym = h;
    // double yn = -h;
    // double x;
    // double sin = dy / D, cos = dx / D;
    //
    // x = xm * cos - ym * sin + x1;
    // ym = xm * sin + ym * cos + y1;
    // xm = x;
    //
    // x = xn * cos - yn * sin + x1;
    // yn = xn * sin + yn * cos + y1;
    // xn = x;
    //
    // int[] xpoints = { x2, (int) xm, (int) xn };
    // int[] ypoints = { y2, (int) ym, (int) yn };
    //
    // g.drawLine(x1, y1, x2, y2);
    // g.fillPolygon(xpoints, ypoints, 3);
    // }

    /**
     * Activates anti-aliasing for the specified {@link Graphics2D} context.
     * 
     * @param g2
     *            The specified {@link Graphics2D} context
     */
    private void activateAntialiasing(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /**
     * Returns the start point of this arc determined by the predecessor
     * ({@link GuiPlace} or {@link GuiTransition} respectively).
     * 
     * @return A {@link Point} as start anchor; null if distance between start
     *         and end is zero
     */
    private Point getStartAnchor() {
        /*
         * Get the center point of the opposite shape for calculation of the
         * arrow direction.
         */
        Point endCenter = succ.getPosition();

        /*
         * Let the shape calculate its anchor position for an arc from the
         * opposite shape.
         */
        Point startAnchor = pred.getArcAnchor(endCenter);

        return startAnchor;
    }

    /**
     * Returns the end point of this arc determined by the successor
     * ({@link GuiPlace} or {@link GuiTransition} respectively).
     * 
     * @return A {@link Point} as end anchor; null if distance between start and
     *         end is zero
     */
    private Point getEndAnchor() {
        /*
         * Get the center point of the opposite shape for calculation of the
         * arrow direction.
         */
        Point startCenter = pred.getPosition();

        /*
         * Let the shape calculate its anchor position for an arc from the
         * opposite shape.
         */
        Point endAnchor = succ.getArcAnchor(startCenter);

        return endAnchor;
    }

    /**
     * Highlights the arc anchors with a small circle when debug is true.
     * 
     * @param g2
     *            The Graphics2D object
     * @param startAnchor
     *            The start anchor
     * @param endAnchor
     *            The start anchor
     */
    private void highlightAnchors(Graphics2D g2, Point startAnchor, Point endAnchor) {
        g2.setStroke(new BasicStroke(1));
        g2.drawOval(startAnchor.x - 5, startAnchor.y - 5, 10, 10);
        g2.drawOval(endAnchor.x - 5, endAnchor.y - 5, 10, 10);
    }

    /**
     * Draws the line of the arrow from and to the anchors on two
     * {@link IGuiNode}.<BR>
     * <BR>
     * Note: {@link IGuiNode} provide their anchor for a specified direction via
     * Getter method.
     * 
     * @param g2
     *            The {@link Graphics2D} context
     * @param startAnchor
     *            The location of the anchor at the source node as {@link Point}
     * @param endAnchor
     *            The location of the anchor at the target node as {@link Point}
     */
    private void drawArrowLine(Graphics2D g2, Point startAnchor, Point endAnchor) {
        g2.setStroke(new BasicStroke(arrowLineThickness));
        Line2D line = getArrowLine(startAnchor, endAnchor);
        g2.draw(line);
    }

    /**
     * Returns the line of the arrow for use in drawArrowLine() and contains().
     * 
     * @param startAnchor
     *            The location of the anchor at the source node as {@link Point}
     * @param endAnchor
     *            The location of the anchor at the target node as {@link Point}
     * @return A {@link Line2D.Double}
     */
    private Line2D.Double getArrowLine(Point startAnchor, Point endAnchor) {
        Line2D.Double line = new Line2D.Double(startAnchor, endAnchor);
        return line;
    }

    /**
     * Draws the tip of the arrow.<BR>
     * <BR>
     * Note: {@link IGuiNode} provide their anchor for a specified direction via
     * Getter method.
     * 
     * @param g2
     *            The {@link Graphics2D} context
     * @param startAnchor
     *            The location of the anchor at the source node as {@link Point}
     * @param endAnchor
     *            The location of the anchor at the target node as {@link Point}
     */
    private void drawArrowTip(Graphics2D g2, Point startAnchor, Point endAnchor) {
        Polygon arrowHead = getArrowHead(startAnchor, endAnchor);
        g2.fillPolygon(arrowHead);
    }

    /**
     * Returns an arrow head for an arrow that is specified by start point and
     * end point.<BR>
     * <BR>
     * Note: Start point (source) and end point (target) need to be determined
     * by predecessor and successor ({@link GuiPlace} or {@link GuiTransition}
     * respectively).
     * 
     * @param startAnchor
     *            The source of the arrow
     * @param endAnchor
     *            The target of the arrow (with the arrow head)
     * @return A polygon representing the arrow head
     */
    private Polygon getArrowHead(Point startAnchor, Point endAnchor) {
        int dx = endAnchor.x - startAnchor.x;   // line length in x direction
        int dy = endAnchor.y - startAnchor.y;   // line length in y direction
        if (debug) {
            // http://www.helixsoft.nl/articles/circle/sincos.htm
            // double angle = Math.atan2(dy, dx); // line angle
            // double radians = angle;
            // double degrees = radians * 180 / Math.PI;
            // System.out.println("Arrow angle: " + degrees + "°");
        }

        double thickness = arrowTipLength * 0.35; // Arrow tip thickness always
                                                  // proportional to the length

        double lineLength = Math.sqrt(dx * dx + dy * dy);
        // System.out.println("lineLength: " + lineLength);
        double xm = lineLength - arrowTipLength;    // x of point 1 of the tip
        // System.out.println("xm: " + xm);
        double xn = xm;                             // x of point 2 on the other
                                                    // side
        // System.out.println("xn: " + xn);
        double ym = thickness;                      // y of point 1 of the tip
        // System.out.println("ym: " + ym);
        double yn = -thickness;                     // y of point 2 of the tip
        // System.out.println("yn: " + yn);
        double sin = dy / lineLength;               // The line angle
        // System.out.println("sin: " + sin);
        double cos = dx / lineLength;
        // System.out.println("cos: " + cos);

        /*
         * Affine Transform:
         * 
         * x' = a*x + b*y + c
         * 
         * y' = d*x + e*y + f
         */

        double x;
        x = xm * cos - ym * sin + startAnchor.x;    // Rotate point 1 (xm, ym)
        ym = xm * sin + ym * cos + startAnchor.y;   // and shift (startAnchor.x,
        xm = x;                                     // startAnchor.y)

        x = xn * cos - yn * sin + startAnchor.x;    // Rotate point 2 (xn, yn)
        yn = xn * sin + yn * cos + startAnchor.y;   // and shift (startAnchor.x,
        xn = x;                                     // startAnchor.y)

        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(endAnchor.x, endAnchor.y);
        arrowHead.addPoint((int) xm, (int) ym);
        arrowHead.addPoint((int) xn, (int) yn);

        return arrowHead;
    }

    /**
     * Indicates the selection of this Arc.
     * 
     * @param g2
     *            The Graphics2D object
     * @param startAnchor
     *            The Location where the arc starts as {@link Point}
     * @param endAnchor
     *            The Location where the arc ends as {@link Point}
     */
    private void drawSelection(Graphics2D g2, Point startAnchor, Point endAnchor) {

        int min_x; // The left x of the Bounds
        int max_x; // The right x ...
        int min_y; // The upper y ...
        int max_y; // The bottom y ...

        if (this.selected) {
            /*
             * If the arrow is nearly horizontal/vertical: The selection range
             * of only the line is a narrow rectangle and the "corners" of the
             * arrow head might be on the outside!
             * 
             * -> So, we have to consider the line *and* the arrow head!
             */
            Polygon arrowHead = getArrowHead(startAnchor, endAnchor);
            Rectangle arrowHeadRect = arrowHead.getBounds();
            int arrowHeadLeftX = arrowHeadRect.x;
            int arrowHeadTopY = arrowHeadRect.y;
            int arrowHeadRightX = arrowHeadLeftX + arrowHeadRect.width;
            int arrowHeadBottomY = arrowHeadTopY + arrowHeadRect.height;

            min_x = Math.min(startAnchor.x, endAnchor.x);
            min_x = Math.min(min_x, arrowHeadLeftX);

            max_x = Math.max(startAnchor.x, endAnchor.x);
            max_x = Math.max(max_x, arrowHeadRightX);

            min_y = Math.min(startAnchor.y, endAnchor.y);
            min_y = Math.min(min_y, arrowHeadTopY);

            max_y = Math.max(startAnchor.y, endAnchor.y);
            max_y = Math.max(max_y, arrowHeadBottomY);

            int width = max_x - min_x;
            int height = max_y - min_y;
            if (debug) {
                System.out.println("g2.drawRect(" + min_x + ", " + min_y + ", " + width + ", " + height + ")");
            }

            g2.setStroke(new BasicStroke(1));
            g2.setColor(Color.BLUE);
            g2.drawRect(min_x, min_y, width, height);
        }
    }

    // private void drawArrowHead(Graphics2D g2) {
    // tx.setToIdentity();
    // // g2.drawLine((int) line.x1, (int) line.y1, (int) line.x2, (int)
    // // line.y2);
    // double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
    // // tx.translate(line.x2, line.y2);
    // tx.translate(line.x2 + 2, line.y2 + 25);
    // tx.rotate((angle - Math.PI / 2d));
    //
    // Graphics2D g = (Graphics2D) g2.create();
    // g2.setTransform(tx);
    // g2.fill(arrowHead);
    // }

    @Override
    public boolean contains(Point p) {
        Point startAnchor = getStartAnchor();
        Point endAnchor = getEndAnchor();
        if (startAnchor == null || endAnchor == null)
            return false;

        /* Mouse click at the arrow head? */
        Polygon arrowHead = getArrowHead(startAnchor, endAnchor);
        if (arrowHead.contains(p))
            return true;

        /* Mouse click at the line? */
        Line2D line = getArrowLine(startAnchor, endAnchor);

        // if (line.contains(p)) return true;
        // -> Bad: Mouse click has to be exactly at the line!
        // -> Practically never true!

        // double distance = line.ptLineDist(p);
        // System.out.println("distance: " + distance);
        // if (distance < 3) return true;
        // -> Bad: This line is infinite - not just from the start to the end!
        // -> True before start node and behind end node as well!

        /*
         * We draw a small virtual square around the mouse click and check if
         * this arc is going through this square.
         */
        final int SQUARE_SIZE = 4;
        int box_x = p.x - SQUARE_SIZE / 2;
        int box_y = p.y - SQUARE_SIZE / 2;
        Rectangle rect = new Rectangle(box_x, box_y, SQUARE_SIZE, SQUARE_SIZE);
        if (line.intersects(rect))
            return true;

        return false;
    }

    /* Getter and Setter */

    // @Override
    // public Rectangle getApproxDrawArea() {
    // /*
    // * Take the area of pred and succ to calculate the area of the arc.
    // */
    // IGuiNode elem1 = null;
    // IGuiNode elem2 = null;
    // try {
    // elem1 = getPredElem();
    // elem2 = getSuccElem();
    // } catch (PNElementException e) {
    // // e.printStackTrace();
    // System.err.println("Arc " + getId() + " does not have predecessor *and*
    // successor element!");
    // return null;
    // }
    //
    // int elem1_x1 = elem1.getTotalLeftX();
    // int elem1_y1 = elem1.getTotalTopY();
    // int elem1_x2 = elem1_x1 + elem1.getTotalWidth();
    // int elem1_y2 = elem1_y1 + elem1.getTotalHeight();
    //
    // int elem2_x1 = elem2.getTotalLeftX();
    // int elem2_y1 = elem2.getTotalTopY();
    // int elem2_x2 = elem2_x1 + elem2.getTotalWidth();
    // int elem2_y2 = elem2_y1 + elem2.getTotalHeight();
    //
    // int x1 = Math.min(elem1_x1, elem2_x1);
    // int y1 = Math.min(elem1_y1, elem2_y1);
    // int x2 = Math.max(elem1_x2, elem2_x2);
    // int y2 = Math.max(elem1_y2, elem2_y2);
    //
    // Rectangle rect = new Rectangle(x1, y1, x2, y2);
    // return rect;
    // }

    @Override
    public String getSourceId() {
        return pred.getId();
    }

    @Override
    public String getTargetId() {
        return succ.getId();
    }

    @Override
    public IGuiNode getPredElem() throws PNNoSuchElementException {
        if (this.pred == null)
            throw new PNNoSuchElementException(this.toString() + " has no predecessor.");
        return this.pred;
    }

    @Override
    public IGuiNode getSuccElem() throws PNNoSuchElementException {
        if (this.succ == null)
            throw new PNNoSuchElementException(this.toString() + " has no successor.");
        return this.succ;
    }

    @Override
    public String toString() {
        String returnString = "GuiArc [" + super.toString() + ", source=" + pred.getId() + ", target=" + succ.getId()
                + "]";
        return returnString;
    }

    /**
     * Calculates the bounds of the shape (using predecessor and successor) and
     * stores the result in the attributes.
     */
    private void calculateMyBounds() {
        /* Take the area of pred and succ to calculate the area of the arc. */
        IGuiNode elem1 = null;
        IGuiNode elem2 = null;
        try {
            elem1 = getPredElem();
            elem2 = getSuccElem();
        } catch (PNNoSuchElementException e) {
            // e.printStackTrace();
            System.err.println("Arc " + getId() + " does not have predecessor *and* successor element!");
            return;
        }

        int elem1_x1 = elem1.getTotalLeftX();
        int elem1_y1 = elem1.getTotalTopY();
        int elem1_x2 = elem1_x1 + elem1.getTotalWidth();
        int elem1_y2 = elem1_y1 + elem1.getTotalHeight();

        int elem2_x1 = elem2.getTotalLeftX();
        int elem2_y1 = elem2.getTotalTopY();
        int elem2_x2 = elem2_x1 + elem2.getTotalWidth();
        int elem2_y2 = elem2_y1 + elem2.getTotalHeight();

        int x1 = Math.min(elem1_x1, elem2_x1);
        int y1 = Math.min(elem1_y1, elem2_y1);
        int x2 = Math.max(elem1_x2, elem2_x2);
        int y2 = Math.max(elem1_y2, elem2_y2);

        Rectangle rect = new Rectangle(x1, y1, x2, y2);
        this.lastDrawingArea = rect;
    }

}
