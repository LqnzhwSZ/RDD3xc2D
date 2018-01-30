package de.lambeck.pned.elements.gui;

import java.awt.*;
import java.awt.geom.Line2D;

import de.lambeck.pned.models.gui.IDrawArcOverlay;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * Implements the arcs (arrows) of the Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class OverlayGuiArc implements IOverlayGuiArc {

    /** Show debug messages? */
    private static boolean debug = false;

    /**
     * The predecessor - the node that is the source of this arc<BR>
     * <BR>
     * Note: We need the {@link IGuiNode} as source of the arc because the
     * anchor point of the arrow needs to have the same behavior as on the real
     * drawing when the user moves the mouse over the draw panel.
     */
    private IGuiNode startNode = null;

    /**
     * The <B>current</B> end location as {@link Point}.<BR>
     * <BR>
     * Note: We need a {@link Point} as target of the arc because the arrow tip
     * has to point directly towards the mouse position - not towards an anchor
     * point of another node.
     */
    private Point currentEndLocation = null;

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
     * Constructs an arc for the {@link IDrawArcOverlay} with parameters for
     * start and end of the arc.
     * 
     * @param sourceNode
     *            The source node {@link IGuiNode}
     * @param end
     *            The <B>current</B> end location as {@link Point}
     */
    public OverlayGuiArc(IGuiNode sourceNode, Point end) {
        this.startNode = sourceNode;
        this.currentEndLocation = end;

        /*
         * Get the current size of the "normal" arrows on the DrawPanel so that
         * the user can draw a new arc with the same size.
         */
        OverlayGuiArc.arrowTipLength = GuiArc.getShapeSize();

        /* First size calculation */
        calculateMyBounds();
    }

    /* Getter and Setter */

    @Override
    public void setCurrentArcEndLocation(Point p) {
        this.currentEndLocation = p;
    }

    @Override
    public Rectangle getLastDrawingArea() {
        return this.lastDrawingArea;
    }

    /**
     * Changes the size of all shapes (static attribute). Calculates a smaller
     * value to make arcs smaller than the nodes!
     * 
     * @param size
     *            The new size
     */
    public static void changeShapeSize(int size) {
        OverlayGuiArc.arrowTipLength = (int) (size * 0.2); // Smaller than the
                                                           // nodes!
    }

    @Override
    public void paintElement(Graphics g) {
        Point startAnchor = this.startNode.getArcAnchor(currentEndLocation);
        if (startAnchor == null)
            return;

        Graphics2D g2 = (Graphics2D) g;
        activateAntialiasing(g2);

        drawArrowLine(g2, startAnchor, currentEndLocation);
        drawArrowTip(g2, startAnchor, currentEndLocation);

        /* Store the used drawing area. */
        calculateMyBounds();
    }

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
        /* Create a copy of the Graphics instance. */
        Graphics2D g2copy = (Graphics2D) g2.create();

        /* Line width, (dashed stroke) and color */
        Stroke stroke = getDrawNewArcStroke();
        g2copy.setStroke(stroke);

        Color color = getDrawNewArcColor();
        g2copy.setColor(color);

        Line2D line = getArrowLine(startAnchor, endAnchor);
        g2copy.draw(line);
    }

    /**
     * Returns the stroke for the (temporary) arc for the "draw new arc" mode.
     * 
     * @return A {@link Stroke}
     */
    private Stroke getDrawNewArcStroke() {
        int borderWidth = 1;

        /* dashed */
        Stroke stroke = new BasicStroke(borderWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 5 },
                0);

        return stroke;
    }

    /**
     * Returns the color for the (temporary) arc for the "draw new arc" mode.
     * 
     * @return A {@link Color}
     */
    private Color getDrawNewArcColor() {
        Color arcColor = Color.BLUE;
        return arcColor;
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
        /* Create a copy of the Graphics instance. */
        Graphics2D g2copy = (Graphics2D) g2.create();

        Color color = getDrawNewArcColor();
        g2copy.setColor(color);

        Polygon arrowHead = getArrowHead(startAnchor, endAnchor);
        g2copy.fillPolygon(arrowHead);
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

    /* Getter and Setter */

    @Override
    public String toString() {
        String returnString = "OverlayGuiArc [" + super.toString() + ", source=" + startNode.getId() + ", end="
                + currentEndLocation + "]";
        return returnString;
    }

    /* Private helpers */

    /**
     * Calculates the bounds of the shape <B>roughly</B> (using start and end
     * location and the size of the arrow tip).<BR>
     * <BR>
     * Note: The width of the arrow tip (perpendicular to the axis of the arc)
     * is definitely smaller than the length of the arrow tip.<BR>
     * Example: If the length of the arrow tip is 20, its overall width is
     * approximately 15.
     */
    private void calculateMyBounds() {
        int x1;
        int y1;
        int x2;
        int y2;
        int width;
        int height;

        int extra = arrowTipLength / 2;

        Point startAnchor = this.startNode.getArcAnchor(currentEndLocation);
        if (startAnchor == null) {
            this.lastDrawingArea = null;
            return;
        }

        x1 = Math.min(startAnchor.x, currentEndLocation.x);
        x1 = x1 - extra;
        if (x1 < 0)
            x1 = 0; // No negative values

        y1 = Math.min(startAnchor.y, currentEndLocation.y);
        y1 = y1 - extra;
        if (y1 < 0)
            y1 = 0; // No negative values

        x2 = Math.max(startAnchor.x, currentEndLocation.x);
        x2 = x2 + extra;

        y2 = Math.max(startAnchor.y, currentEndLocation.y);
        y2 = y2 + extra;

        width = x2 - x1;
        height = y2 - y1;

        Rectangle rect = new Rectangle(x1, y1, width, height);
        this.lastDrawingArea = rect;

        ConsoleLogger.logIfDebug(debug, "OverlayGuiArc: " + startAnchor.x + "," + startAnchor.y + " -> "
                + currentEndLocation.x + "," + currentEndLocation.y);
        ConsoleLogger.logIfDebug(debug, "calculateMyBounds: " + rect);
    }

}
