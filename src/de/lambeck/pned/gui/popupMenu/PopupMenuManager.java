package de.lambeck.pned.gui.popupMenu;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import de.lambeck.pned.elements.gui.IGuiArc;
import de.lambeck.pned.elements.gui.IGuiElement;
import de.lambeck.pned.elements.gui.IGuiNode;
import de.lambeck.pned.models.gui.IDrawPanel;
import de.lambeck.pned.models.gui.IGuiModelController;
import de.lambeck.pned.util.ConsoleLogger;

/*
 * Thomas Lambeck: On Windows 7, the PopupTrigger fires at mouseReleased(). On
 * Linux Mint, the PopupTrigger fires at mousePressed().
 */

/**
 * Implements a MouseListener to show popup menus on Petri net elements. Holds a
 * reference to its DrawPanel and uses the DrawPanels methods to decide which
 * popup menu is suitable for the current element. (This depends on the type of
 * element at the location of the popup trigger.)
 * 
 * See: PopupMenuDemo
 * (https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html)
 */
public class PopupMenuManager extends MouseAdapter {

    private static boolean debug = false;

    private IDrawPanel myDrawPanel;
    private IGuiModelController myGuiController = null;

    /**
     * The map with possible Actions
     */
    protected Map<String, AbstractAction> popupActions;

    /**
     * Constructs the PopupMenuManager with a reference to its DrawPanel and the
     * GUI model controller.
     * 
     * @param sourceDrawPanel
     *            The source of the popup trigger
     * @param controller
     *            The GUI controller
     * @param popupActions
     *            List of Actions
     */
    @SuppressWarnings("hiding")
    public PopupMenuManager(IDrawPanel sourceDrawPanel, IGuiModelController controller,
            Map<String, AbstractAction> popupActions) {
        this.myDrawPanel = sourceDrawPanel;
        this.myGuiController = controller;
        this.popupActions = popupActions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("PopupMenuManager.mousePressed");
        }

        showIfPopupTrigger(e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("PopupMenuManager.mouseReleased");
        }

        showIfPopupTrigger(e);
    }

    /**
     * Shows the suitable popup if the MouseEvent is a PopupTrigger.
     * 
     * Note: Informs the DrawPanel if a popup menu is shown. The DrawPanel can
     * use this information to catch the following event (e.g. "ESC" button or a
     * mouse click) if it is only exiting the popup menu.
     * 
     * @param e
     *            The MouseEvent
     */
    private void showIfPopupTrigger(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu popupMenu = getPopupMenu(e.getPoint());
            if (popupMenu == null)
                return;

            // myDrawPanel.setPopupMenuActive(true);
            Point p = e.getPoint();
            myDrawPanel.setPopupMenuLocation(p);

            Component invoker = e.getComponent();
            int x = p.x;
            int y = p.y;
            popupMenu.show(invoker, x, y);
        }
    }

    /**
     * Returns the suitable popup menu depending on the type of element at the
     * current mouse location.
     * 
     * @param mouseLocation
     *            The mouse location in the source DrawPanel
     * @return The popup menu
     */
    private JPopupMenu getPopupMenu(Point mouseLocation) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("PopupMenuManager.getPopupMenu");
        }

        IGuiElement element = getElement(mouseLocation);
        if (debug) {
            if (element == null)
                System.out.println("PopupMenuManager.getPopupMenu(), element == null");
        }

        IGuiNode node = getNode(element);
        IGuiArc arc = getArc(element);
        if (debug) {
            if (node == null)
                System.out.println("PopupMenuManager.getPopupMenu(), node == null");
            if (arc == null)
                System.out.println("PopupMenuManager.getPopupMenu(), arc == null");
        }

        /*
         * All of them: element, node and arc, might be null!
         */

        String simpleClassName = getSimpleClassName(element);

        switch (simpleClassName) {
        case "GuiPlace":
            return new PopupMenuForPlaces(myDrawPanel, node, popupActions);
        case "GuiTransition":
            return new PopupMenuForTransitions(myDrawPanel, node, popupActions);
        case "GuiArc":
            return new PopupMenuForArcs(myDrawPanel, arc, popupActions);
        case "":
            return new PopupMenuForEmptyAreas(myDrawPanel, popupActions);
        default:
            System.err.println("No proper popup menu found for: " + mouseLocation.toString());
            return null;
        }
    }

    /**
     * Returns the element at the current location of the DrawPanel.
     * 
     * @return An {@link IGuiElement}
     */
    private IGuiElement getElement(Point mouseLocation) {
        IGuiElement element = null;
        element = myGuiController.getSelectableElementAtLocation(mouseLocation);
        return element;
    }

    /**
     * Returns the node at the current location of the DrawPanel.
     * 
     * @param element
     *            The current element at the DrawPanel
     * @return An {@link IGuiNode}
     */
    private IGuiNode getNode(IGuiElement element) {
        if (element == null)
            return null;

        IGuiNode node = null;
        if (element instanceof IGuiNode) {
            node = (IGuiNode) element;
        }
        return node;
    }

    /**
     * Returns the arc at the current location of the DrawPanel.
     * 
     * @param element
     *            The current element at the DrawPanel
     * @return An {@link IGuiArc}
     */
    private IGuiArc getArc(IGuiElement element) {
        if (element == null)
            return null;

        IGuiArc arc = null;
        if (element instanceof IGuiArc) {
            arc = (IGuiArc) element;
        }
        return arc;
    }

    /**
     * Returns the simple class name (without the package information) of an
     * object (e.g. for switch statements).
     * 
     * @param o
     *            The object
     * @return The simple name (without package); null if o is null
     */
    private String getSimpleClassName(Object o) {
        if (o == null)
            return "";

        return o.getClass().getSimpleName();
    }

}
