package de.lambeck.pned.models.gui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import de.lambeck.pned.elements.gui.IGuiNode;
import de.lambeck.pned.elements.gui.IOverlayGuiArc;
import de.lambeck.pned.elements.gui.IPaintable;
import de.lambeck.pned.elements.gui.OverlayGuiArc;

/**
 * Implements a temporary overlay with 1 arc that should be visible above the
 * corresponding {@link IDrawPanel} when drawing a new arc using the
 * {@link MouseEvent} in {@link MyMouseAdapter}.<BR>
 * <BR>
 * Note: This overlay should only "exist between two Mouse events" on the draw
 * panel. (If the 2nd mouse event is on a valid end node: the arc is added to
 * the model. Otherwise: the overlay should simply be removed.)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class DrawArcOverlay implements IDrawArcOverlay {

    // /** Show debug messages? */
    // private static boolean debug = false;

    /* Variables for the locations */

    /** The <B>current</B> end location of the arc as {@link Point}. */
    private Point currentArcEndLocation = null;

    /** The arc on this overlay */
    private IOverlayGuiArc myOverlayArc = null;

    /* Constructor */

    /**
     * Constructs this overlay with one {@link IOverlayGuiArc}.
     * 
     * @param startNode
     *            The start node for the arc as {@link IGuiNode}
     * @param initialEndLocation
     *            The end location for the arc as {@link Point}
     */
    public DrawArcOverlay(IGuiNode startNode, Point initialEndLocation) {
        this.currentArcEndLocation = initialEndLocation;

        /* Construct the overlay arc */
        OverlayGuiArc overlayGuiArc = new OverlayGuiArc(startNode, currentArcEndLocation);
        this.myOverlayArc = overlayGuiArc;
    }

    /* Getter and Setter */

    // @Override
    // public boolean isVisible() {
    // return this.visible;
    // }

    // @Override
    // public void show() {
    // this.visible = true;
    // }

    // @Override
    // public void hide() {
    // this.visible = false;
    // }

    @Override
    public void setCurrentArcEndLocation(Point p) {
        this.myOverlayArc.setCurrentArcEndLocation(p);
    }

    @Override
    public IOverlayGuiArc getCurrentArc() {
        return this.myOverlayArc;
    }

    @Override
    public List<IPaintable> getPaintableElements() {
        List<IPaintable> elements = new ArrayList<IPaintable>();
        elements.add(myOverlayArc);
        return elements;
    }

}
