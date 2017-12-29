package de.lambeck.pned.models.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import de.lambeck.pned.elements.gui.IGuiArc;
import de.lambeck.pned.elements.gui.IGuiElement;
import de.lambeck.pned.elements.gui.IGuiNode;
import de.lambeck.pned.gui.popupMenu.PopupMenuForArcs;
import de.lambeck.pned.gui.popupMenu.PopupMenuForEmptyAreas;
import de.lambeck.pned.gui.popupMenu.PopupMenuForPlaces;
import de.lambeck.pned.gui.popupMenu.PopupMenuForTransitions;
import de.lambeck.pned.util.ConsoleLogger;

/*
 * Thomas Lambeck: On Windows 7, the PopupTrigger fires at mouseReleased(). On
 * Linux Mint, the PopupTrigger fires at mousePressed().
 */

/*
 * @formatter:off
 * The real sequences of mouse events:
 * - mousePressed
 * - mouseReleased
 * - mouseClicked
 * 
 * And:
 * - mousePressed
 * - mouseDragged
 * - mouseReleased
 * 
 * So, we have to process either:
 * mousePressed, mouseReleased and mouseClicked
 * 
 * Or:
 * mousePressed, mouseDragged, [mouseDragged, mouseDragged, ...] and mouseReleased
 * 
 * @formatter:on
 */

/**
 * {@link MouseAdapter} for the {@link DrawPanel} to select and move nodes and
 * to show popup menus.
 * 
 * @formatter:off
 * 
 * Actions for mouse events:
 * - Primary mouse button:
 *   - Clicked:
 *     - Selects a single element (node or arc).
 *       -> e.g. for "delete element"
 *     - Clears selection if outside of the current selection.
 *   - Clicked + CTRL:
 *     - Toggles selection of the current element.
 *     - Or: Adds more elements to the current selection.
 * 
 * - Secondary mouse button:
 *   - At a node:
 *     - Show popup menu (nodes only -> "change z value", "add arc"...)
 *   - At empty space:
 *     - Show popup for creation of new nodes.
 * 
 * Actions for mouse motion events:
 * - Primary mouse button + ALT : -> Dragging
 * 
 * @formatter:on
 * 
 * Holds references to its DrawPanel and the GUI model controller to use their
 * methods to decide which popup menu is suitable for the current element.
 * (Depends on the type of element at the location of the popup trigger.)
 * 
 * See: PopupMenuDemo
 * (https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class MyMouseAdapter extends MouseAdapter implements PopupMenuListener {

    private static boolean debug = true;

    /** Reference to the {@link DrawPanel} */
    private DrawPanel myDrawPanel = null;

    /** Reference to the {@link IGuiModelController} */
    private IGuiModelController myGuiController = null;

    /**
     * The map with possible Actions
     */
    protected Map<String, AbstractAction> popupActions;

    // /** Stores if we are showing a popup menu now. */
    // boolean popupMenuActive = false;

    // /** Stores if the last mouse event has canceled a popup menu. */
    // boolean popupMenuCanceled = false;

    // /**
    // * Stores if the want to ignore the next mouseClicked event (because it
    // * canceled a popup menu).
    // */
    // boolean ignoreNextMouseClicked = false;

    /**
     * Constructs this mouse adapter for the specified draw panel, with a
     * reference to the GUI model controller and the possible Actions for the
     * popup menus.
     * 
     * @param drawPanel
     *            The {@link DrawPanel}
     * @param guiController
     *            The {@link IGuiModelController}
     * @param popupActions
     *            The {@link Map} of {@link AbstractAction} for the popup menus
     */
    @SuppressWarnings("hiding")
    public MyMouseAdapter(DrawPanel drawPanel, IGuiModelController guiController,
            Map<String, AbstractAction> popupActions) {
        this.myDrawPanel = drawPanel;
        this.myGuiController = guiController;
        this.popupActions = popupActions;
    }

    /*
     * MouseAdapter methods
     */

    @Override
    public void mousePressed(MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("MyMouseAdapter.mousePressed", e);
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            ConsoleLogger.logIfDebug(debug, "Right mouse button");
            showPopupIfPopupTrigger(e);

        } else {
            /* Store the location of the mousePressed event. */
            myDrawPanel.mousePressedLocation = e.getPoint();

            if (debug) {
                int x = myDrawPanel.mousePressedLocation.x;
                int y = myDrawPanel.mousePressedLocation.y;
                String message = "mousePressed at: " + x + "," + y;
                ConsoleLogger.logAlways(message);
            }

        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("MyMouseAdapter.mouseDragged", e);
        }

        /* We drag only if it is allowed. */
        boolean altKey_pressed = myDrawPanel.altKey_pressed;
        if (!altKey_pressed)
            return;

        myDrawPanel.mouseIsDragging = true;

        /* Store the initial start and current dragging locations. */
        if (myDrawPanel.initialDraggedFrom == null)
            myDrawPanel.initialDraggedFrom = myDrawPanel.mousePressedLocation;
        if (myDrawPanel.mouseDraggedFrom == null)
            myDrawPanel.mouseDraggedFrom = myDrawPanel.mousePressedLocation;
        myDrawPanel.mouseDraggedTo = e.getPoint();

        /* Inform the GUI controller that dragging has happened. */
        int distance_x = myDrawPanel.mouseDraggedTo.x - myDrawPanel.mouseDraggedFrom.x;
        int distance_y = myDrawPanel.mouseDraggedTo.y - myDrawPanel.mouseDraggedFrom.y;
        if (debug) {
            String message = "mouseDragged: " + distance_x + ", " + distance_y;
            ConsoleLogger.logAlways(message);
        }

        myGuiController.mouseDragged(distance_x, distance_y);

        /* New "start" in case there is another dragging step following. */
        myDrawPanel.mouseDraggedFrom = myDrawPanel.mouseDraggedTo;

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("MyMouseAdapter.mouseReleased", e);
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            ConsoleLogger.logIfDebug(debug, "Right mouse button");
            showPopupIfPopupTrigger(e);

        } else {
            // /*
            // * We have left a popup menu with the mousePressed before?
            // */
            // if (this.popupMenuLeftWithMousePress == true) {
            // if (debug) {
            // System.out.println("Reset this.popupMenuLeftWithMousePress =
            // false");
            // }
            // this.popupMenuLeftWithMousePress = false;
            // return;
            // }

            /*
             * Is the mouse dragging?
             */
            if (!myDrawPanel.mouseIsDragging) {
                /*
                 * This is the mouseReleased event prior to mouseClicked.
                 */

                /*
                 * Check which action is required.
                 */
                boolean ctrlKey_pressed = myDrawPanel.ctrlKey_pressed;
                boolean altKey_pressed = myDrawPanel.altKey_pressed;

                /*
                 * Pass both locations (mousePressed and current) to the GUI
                 * controller so that the GUI controller can check if both are
                 * on the same element.
                 * 
                 * Otherwise, this might be an unintended mouseClicked event.
                 */
                if (!ctrlKey_pressed && !altKey_pressed) {
                    Point p = myDrawPanel.mousePressedLocation;
                    if (p == null) {
                        System.err.println("MyMouseAdapter.mouseReleased(): mousePressedLocation == null");
                        return;
                    }
                    myGuiController.mouseClick_Occurred(p, e);

                    if (debug) {
                        System.out.println("mouseReleased event used.");
                    }

                } else if (ctrlKey_pressed && !altKey_pressed) {
                    Point p = myDrawPanel.mousePressedLocation;
                    if (p == null) {
                        System.err.println("MyMouseAdapter.mouseReleased(): mousePressedLocation == null");
                        return;
                    }
                    myGuiController.mouseClick_WithCtrl_Occurred(p, e);

                    if (debug) {
                        System.out.println("mouseReleased event used.");
                    }

                } else if (!ctrlKey_pressed && altKey_pressed) {
                    // NOP
                    ConsoleLogger.logIfDebug(debug, "mouseReleased event not used.");

                    // System.err.println("Unexpected mouse event at: " +
                    // e.getPoint());
                    // System.err.println("ctrlKey_pressed: " +
                    // ctrlKey_pressed);
                    // System.err.println("altKey_pressed: " + altKey_pressed);

                    // /*
                    // * -> Thread issue with the values of ctrlKey_pressed,
                    // * altKey_pressed?
                    // */

                } else {
                    // NOP
                    ConsoleLogger.logIfDebug(debug, "mouseReleased event not used.");

                    // System.err.println("Unexpected mouse event at: " +
                    // e.getPoint());
                    // System.err.println("ctrlKey_pressed: " +
                    // ctrlKey_pressed);
                    // System.err.println("altKey_pressed: " + altKey_pressed);

                    // /*
                    // * Thread issue with the values of ctrlKey_pressed,
                    // * altKey_pressed?
                    // */

                }

                // /*
                // * TODO Always reset CTRL and ALT from here too to avoid these
                // * unexpected mouse events! (A thread issue???)
                // */

                // if (!e.isControlDown()) {
                // System.err.println("MyMouseAdapter.mouseReleased(), calling
                // myDrawPanel.ctrl_released_Action_occurred()");
                // myDrawPanel.ctrl_released_Action_occurred();
                // }
                // if (!e.isAltDown()) {
                // System.err.println("MyMouseAdapter.mouseReleased(), calling
                // myDrawPanel.alt_released_Action_occurred()");
                // myDrawPanel.alt_released_Action_occurred();
                // }

            } else {
                /*
                 * There was a dragging operation. This is the mouseReleased
                 * event after mouseDragged.
                 */

                if (debug) {
                    /* Calculate the complete way the mouse has traveled. */
                    int distance_x = e.getPoint().x - myDrawPanel.initialDraggedFrom.x;
                    int distance_y = e.getPoint().y - myDrawPanel.initialDraggedFrom.y;
                    String message = "Mouse traveled: " + distance_x + "," + distance_y;
                    ConsoleLogger.logAlways(message);
                }

                /*
                 * Inform the application controller to update the position of
                 * all dragged nodes in the data model.
                 */
                myGuiController.updateDataNodePositions();

                /* One complete update of the drawing to make sure. */
                myDrawPanel.updateDrawing(null);

                ConsoleLogger.logIfDebug(debug, "mouseReleased event used.");

            }

        }

        /* Reset mouse locations. */
        myDrawPanel.mousePressedLocation = null;

        myDrawPanel.mouseIsDragging = false;

        myDrawPanel.initialDraggedFrom = null;
        myDrawPanel.mouseDraggedFrom = null;
        myDrawPanel.mouseDraggedTo = null;

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("MyMouseAdapter.mouseClicked", e);
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            /* Ignore mouseClicked completely for right mouse button. */
            return;

        } else {
            /*
             * Ignore if this mouseClicked event has canceled the last popup
             * menu.
             */
            // if (this.ignoreNextMouseClicked == true) {
            // debugLog("Ignoring this mouseClicked event.");
            // this.ignoreNextMouseClicked = false;
            // return;
            // }

            /*
             * -> Different behavior between Linux and Windows!
             * 
             * (mouseClicked event canceling a popup is ignored under Linux, but
             * recognized as mouseClicked under Windows.)
             */

            handleMouseClicked(e);

        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        /* Always update the mouse position info. */
        myDrawPanel.updateMousePos(e.getPoint());

        /* Show tool tip on nodes. */
        showTooltip(e);

    }

    /*
     * Interface PopupMenuListener
     */

    @Override
    public void popupMenuCanceled(PopupMenuEvent arg0) {
        ConsoleLogger.logIfDebug(debug, "popupMenuCanceled()");

        /* arg0.getSource() is the same for Escape and another mouse event! */
        // this.popupMenuCanceled = true;
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
        ConsoleLogger.logIfDebug(debug, "popupMenuWillBecomeInvisible()");
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
        ConsoleLogger.logIfDebug(debug, "popupMenuWillBecomeVisible()");
    }

    /*
     * Methods for mouseClicked
     */

    /*
     * Update comment?
     */

    /**
     * Checks if the mouse event requires any action in the the GUI.
     * 
     * @param e
     *            The mouse event
     */
    private void handleMouseClicked(MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("MyMouseAdapter.handleMouseClicked", e);
        }

        /*
         * TODO Always check CTRL and ALT from here too to avoid those
         * Unexpected mouse event! (A thread issue???)
         */
        // if (!e.isControlDown()) {
        // System.out.println("MyMouseAdapter.handleMousePressed(), calling
        // myDrawPanel.ctrl_released_Action_occurred()");
        // myDrawPanel.ctrl_released_Action_occurred();
        // }
        // if (!e.isAltDown()) {
        // System.out.println("MyMouseAdapter.handleMousePressed(), calling
        // myDrawPanel.alt_released_Action_occurred()");
        // myDrawPanel.alt_released_Action_occurred();
        // }

        /*
         * We "leave" a popup menu with a new mouse event on the DrawPanel?
         */

        // if (myDrawPanel.getPopupMenuActive()) {
        // myDrawPanel.setPopupMenuActive(false);
        // System.out.println("Popup menu left.");
        // return; // Do nothing more!
        // }

        // if (myDrawPanel.getPopupMenuLocation() != null) {
        // myDrawPanel.setPopupMenuLocation(null);
        // System.out.println("Popup menu left.");
        // if (debug) {
        // System.out.println("Set this.popupMenuLeftWithMousePress = true");
        // }
        // this.popupMenuLeftWithMousePress = true;
        // return; // Do nothing more!
        // }

        // /* Store the location of the mousePressed event. */
        // myDrawPanel.mousePressedLocation = e.getPoint();
        // if (debug) {
        // System.out.println("mousePressed at: " +
        // myDrawPanel.mousePressedLocation.x + ","
        // + myDrawPanel.mousePressedLocation.y);
        // }

        boolean ctrlKey_pressed = myDrawPanel.ctrlKey_pressed;
        boolean altKey_pressed = myDrawPanel.altKey_pressed;
        MyMouseEvent event;

        /*
         * Only left mouse button pressed?
         * 
         * -> Select a single element.
         */
        if (!ctrlKey_pressed && !altKey_pressed) {
            // myGuiController.mouseClick_Occurred(e);
            event = MyMouseEvent.MOUSE_PRESSED;
            myDrawPanel.lastMouseEvent = event;
            if (debug)
                System.out.println(event.getValue());
            return;
        }

        /*
         * Left mouse button pressed + CTRL?
         * 
         * -> Toggle selection.
         */
        if (ctrlKey_pressed && !altKey_pressed) {
            // myGuiController.mouseClick_WithCtrl_Occurred(e);
            event = MyMouseEvent.MOUSE_PRESSED_CTRL;
            myDrawPanel.lastMouseEvent = event;
            if (debug)
                System.out.println(event.getValue());
            return;
        }

        /*
         * Left mouse button pressed + ALT?
         * 
         * -> We want to allow dragging.
         */
        if (altKey_pressed && !ctrlKey_pressed) {
            // myGuiController.mouseClick_WithAlt_Occurred(e);
            event = MyMouseEvent.MOUSE_PRESSED_ALT;
            myDrawPanel.lastMouseEvent = event;
            if (debug)
                System.out.println(event.getValue());

            /*
             * TODO Allow every mouse location or only over nodes?
             */

            return;
        }
    }

    /*
     * Methods for mouseMove
     */

    /**
     * Shows the ID and the name of an {@link IGuiNode} in a tool tip on
     * mouseover.
     * 
     * @param e
     *            The {@link MouseEvent}
     */
    private void showTooltip(MouseEvent e) {
        IGuiElement element = myGuiController.getSelectableElementAtLocation(e.getPoint());
        if (element == null) {
            myDrawPanel.setToolTipText(null);
            return;
        }

        if (!(element instanceof IGuiNode)) {
            myDrawPanel.setToolTipText(null);
            return;
        }

        IGuiNode node = (IGuiNode) element;

        String nodeInfo = getNodeInfo(node);
        myDrawPanel.setToolTipText(nodeInfo);
    }

    /**
     * Returns the information to the specified node as HTML-formatted String to
     * allow line breaks.
     * 
     * @param node
     *            The {@link IGuiNode}
     * @return A HTML-formatted String
     */
    private String getNodeInfo(IGuiNode node) {
        String nodeInfo;

        /*
         * Multi-line tool tip:
         * https://docs.oracle.com/javase/tutorial/uiswing/components/html.html
         */
        nodeInfo = "<html>";
        nodeInfo = nodeInfo + "Name: " + node.getName();
        nodeInfo = nodeInfo + "<BR>";
        nodeInfo = nodeInfo + "ID: " + node.getId();
        nodeInfo = nodeInfo + "</html>";

        return nodeInfo;
    }

    /*
     * Methods for popup menus
     */

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
    private void showPopupIfPopupTrigger(MouseEvent e) {
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
        } else {
            System.out.println("PopupMenuManager: no popup trigger");
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
                System.out.println("PopupMenuManager.getPopupMenu(): element == null");
        }

        IGuiNode node = getNode(element);
        IGuiArc arc = getArc(element);
        if (debug) {
            if (node == null)
                System.out.println("PopupMenuManager.getPopupMenu(): node == null");
            if (arc == null)
                System.out.println("PopupMenuManager.getPopupMenu(): arc == null");
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

    /*
     * Private helpers
     */

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

    // /**
    // * Sets "ignoreNextMouseClicked" to true if the last mouse event has
    // * canceled a popup menu. (This mouse event should not result in any other
    // * reaction.)
    // *
    // * Note: "popupMenuCanceled" is set to true in the popupMenuCanceled()
    // * method for Interface PopupMenuListener.
    // */
    // private void decideToIgnoreEvent() {
    // ConsoleLogger.logIfDebug(debug, "decideToIgnoreEvent()");
    //
    // if (this.popupMenuCanceled == true) {
    // ConsoleLogger.logIfDebug(debug, "popupMenuCanceled was canceled.");
    // this.popupMenuCanceled = false;
    // ConsoleLogger.logIfDebug(debug, "Ignore the next mouseClicked event.");
    // this.ignoreNextMouseClicked = true;
    // }
    // }

}
