package de.lambeck.pned.models.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import de.lambeck.pned.application.ApplicationController;
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
 * to show popup menus.<BR>
 * <BR>
 * Actions for mouse events:<BR>
 * - Primary mouse button:<BR>
 * &nbsp;&nbsp;- Clicked:<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;- Selects a single element (node or arc).<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-&gt; e.g. for "delete element"<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;- Clears selection if outside of the current
 * selection.<BR>
 * &nbsp;&nbsp;- Clicked + CTRL:<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;- Toggles selection of the current element.<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;- Or: Adds more elements to the current
 * selection.<BR>
 * <BR>
 * - Secondary mouse button:<BR>
 * &nbsp;&nbsp;- At a node:<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;- Show popup menu (nodes only -&gt; "change z value",
 * "add arc"...)<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;- At empty space:<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;- Show popup for creation of new nodes.<BR>
 * <BR>
 * Actions for mouse motion events:<BR>
 * - Primary mouse button held down for more than 0.5 seconds:<BR>
 * &nbsp;&nbsp;- At a node:<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;- Dragging<BR>
 * <BR>
 * Holds references to its DrawPanel and the GUI model controller to use their
 * methods to decide which popup menu is suitable for the current element.
 * (Depends on the type of element at the location of the popup trigger.)<BR>
 * <BR>
 * See: PopupMenuDemo
 * (https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class MyMouseAdapter extends MouseAdapter implements PopupMenuListener {

    /** Show debug messages? */
    private static boolean debug = false;

    /**
     * The delay (in milliseconds) before switching to dragging mode if the user
     * keeps holding the left mouse button down. (Now set to OS double click
     * interval in the constructor.)
     */
    // private static final int DRAGGING_WAIT_TIME = 500;
    private int DRAGGING_WAIT_TIME = 500;

    /** Reference to the {@link DrawPanel} */
    private IDrawPanel myDrawPanel = null;

    /** Reference to the {@link IGuiModelController} */
    private IGuiModelController myGuiController = null;

    /**
     * The map with possible Actions
     */
    protected Map<String, AbstractAction> popupActions;

    /** Reference to the {@link ApplicationController} */
    protected ApplicationController myAppController = null;

    /** Measures how long the mouse was pressed. (for dragging) */
    private java.util.Timer timer;

    /**
     * Stores whether this mouse adapter is in "draw new arc" mode or not.<BR>
     * <BR>
     * <B>If true:</B> We have to report new mouse positions to the GUI model
     * controller in order to update/repaint the (temporary) overlay with the
     * new arc while the user is moving the mouse towards the 2nd node.
     */
    private boolean drawArcMode = false;

    /* Constructor etc. */

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
     * @param appController
     *            The {@link ApplicationController}
     */
    @SuppressWarnings("hiding")
    public MyMouseAdapter(IDrawPanel drawPanel, IGuiModelController guiController,
            Map<String, AbstractAction> popupActions, ApplicationController appController) {
        this.myDrawPanel = drawPanel;
        this.myGuiController = guiController;
        this.popupActions = popupActions;
        this.myAppController = appController;

        debug = appController.getShowDebugMessages();

        setDraggingDelay();
    }

    /**
     * Sets the delay for dragging mode to the OS double click interval.<BR>
     * <BR>
     * This means the time to wait before dragging mode when the user holds the
     * left mouse button down.
     */
    private void setDraggingDelay() {
        int multiClickInterval = getSystemDoubleClickInterval();
        this.DRAGGING_WAIT_TIME = multiClickInterval;
    }

    /**
     * Returns the double click interval of the current operating system.
     * 
     * @return The system double click interval in milliseconds
     */
    private int getSystemDoubleClickInterval() {
        /* https://stackoverflow.com/a/4577475 */
        int clickInterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
        ConsoleLogger.logIfDebug(debug, "Operating System clickInterval: " + clickInterval);
        return clickInterval;
    }

    /* MouseAdapter methods */

    @Override
    public void mousePressed(MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("MyMouseAdapter.mousePressed", e);
        }

        /* Reset the last popup menu location! */
        myDrawPanel.setPopupMenuLocation(null);

        if (drawArcMode) {
            /*
             * Let the GUI model controller check whether the user has clicked
             * on the 2nd node for a new arc or not.
             */
            myGuiController.checkDrawArcFinalEndLocation(e.getPoint());
            deactivateDrawArcMode();

            // TODO Check: Is it correct to return already and do nothing more?
            return;
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            ConsoleLogger.logIfDebug(debug, "Right mouse button");

            /* Deactivate "draw new arc" mode if necessary. */
            if (drawArcMode)
                deactivateDrawArcMode();

            /* Show popup? */
            showPopupIfPopupTrigger(e); // Linux

        } else {
            /* Store the location of the mousePressed event. */
            myDrawPanel.setMousePressedLocation(e.getPoint());

            if (debug) {
                Point p = myDrawPanel.getMousePressedLocation();
                String message = "mousePressed at: " + p.x + "," + p.y;
                ConsoleLogger.logAlways(message);
            }

            /*
             * Start the mouse pressed timer to activate dragging after a delay
             * of 0.5 seconds. (Timer will be canceled in mouseReleased.)
             */
            if (timer == null) {
                timer = new java.util.Timer();
                String message = "New Timer scheduled.";
                ConsoleLogger.logIfDebug(debug, message);
            }
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    myDrawPanel.setStateMouseDragMode(true);
                    String message = "Timer has activated dragging on the draw panel.";
                    ConsoleLogger.logIfDebug(debug, message);
                }
            }, DRAGGING_WAIT_TIME);

        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("MyMouseAdapter.mouseDragged", e);
        }

        /* We drag only in the drag mode. */
        boolean dragMode = myDrawPanel.getStateMouseDragMode();
        String message = "mouseDragMode: " + dragMode;
        ConsoleLogger.logIfDebug(debug, message);
        if (!dragMode)
            return;

        /* Store the initial start and current dragging locations. */
        Point mousePressedLocation = myDrawPanel.getMousePressedLocation();
        Point initialDraggedFrom = myDrawPanel.getInitialDraggedFrom();
        Point mouseDraggedFrom = myDrawPanel.getMouseDraggedFrom();

        if (initialDraggedFrom == null) {
            initialDraggedFrom = mousePressedLocation;
            myDrawPanel.setInitialDraggedFrom(initialDraggedFrom);
        }
        if (mouseDraggedFrom == null) {
            mouseDraggedFrom = mousePressedLocation;
            myDrawPanel.setMouseDraggedFrom(mouseDraggedFrom);
        }

        Point mouseDraggedTo = e.getPoint();
        myDrawPanel.setMouseDraggedTo(mouseDraggedTo);

        /* Inform the GUI controller that dragging has happened. */
        int distance_x = mouseDraggedTo.x - mouseDraggedFrom.x;
        int distance_y = mouseDraggedTo.y - mouseDraggedFrom.y;
        if (debug) {
            message = "mouseDragged: " + distance_x + ", " + distance_y;
            ConsoleLogger.logAlways(message);
        }

        myGuiController.mouseDragged(distance_x, distance_y);

        /* New "start" in case there is another dragging step following. */
        myDrawPanel.setMouseDraggedFrom(mouseDraggedTo);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("MyMouseAdapter.mouseReleased", e);
        }

        /*
         * Cancel the mouse pressed timer. And if we were in "drag mode":
         * deactivate it and set dragOperationCompleted = true!
         */
        boolean dragOperationCompleted = false;
        {
            if (timer != null) {
                timer.cancel();
                timer = null;
                String message = "Timer has been canceled.";
                ConsoleLogger.logIfDebug(debug, message);

                boolean dragging = myDrawPanel.getStateMouseDragMode();
                if (dragging) {
                    myDrawPanel.setStateMouseDragMode(false);
                    dragOperationCompleted = true;
                    message = "Dragging canceled/completed.";
                    ConsoleLogger.logIfDebug(debug, message);
                }
            }
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            ConsoleLogger.logIfDebug(debug, "Right mouse button");

            /* Show popup? */
            showPopupIfPopupTrigger(e); // Windows

        } else {
            /* Were we in drag mode? */
            if (!dragOperationCompleted) {
                /* This is the mouseReleased event prior to mouseClicked. */

                /* -> Everything handled in mouseClicked() */

            } else {
                /*
                 * There was a dragging operation. This is the mouseReleased
                 * event after mouseDragged.
                 */

                if (debug) {
                    /* Calculate the complete way the mouse has traveled. */
                    Point p = myDrawPanel.getInitialDraggedFrom();
                    int distance_x = e.getPoint().x - p.x;
                    int distance_y = e.getPoint().y - p.y;
                    String message = "Mouse traveled: " + distance_x + "," + distance_y;
                    ConsoleLogger.logAlways(message);
                }

                /*
                 * Inform the application controller to update the position of
                 * all dragged nodes in the data model as well!
                 */
                myGuiController.updateDataNodePositions();

                /* One complete update of the drawing to make sure. */
                myDrawPanel.updateDrawing(null);

                ConsoleLogger.logIfDebug(debug, "mouseReleased event used.");

            }

        }

        /* Reset some mouse locations. */
        myDrawPanel.setInitialDraggedFrom(null);
        myDrawPanel.setMouseDraggedFrom(null);
        myDrawPanel.setMouseDraggedTo(null);

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
            /* Check which action is required. */
            boolean ctrlKey_pressed = myDrawPanel.getCtrlKeyPressed();

            /*
             * Pass both locations (mousePressed and current) to the GUI
             * controller so that the GUI controller can check if both are on
             * the same element.
             * 
             * Otherwise, this might be an unintended mouseClicked event.
             */

            // TODO Check: Is it safe to ignore mousePressedLocation now?

            Point p = myDrawPanel.getMousePressedLocation();
            if (p == null) {
                /*
                 * mousePressedLocation == null if we just have left the
                 * "draw new arc" mode with the last mousePressed event.
                 */
                String message = "MyMouseAdapter.mouseClicked(): mousePressedLocation == null";
                message = message + " (\"draw new arc\" mode left?)";
                ConsoleLogger.logIfDebug(debug, message);
                return;
            }

            if (!ctrlKey_pressed) {
                myGuiController.mouseClick_Occurred(p, e);

                if (debug) {
                    System.out.println("mouseClicked event used.");
                }

            } else if (ctrlKey_pressed) {
                myGuiController.mouseClick_WithCtrl_Occurred(p, e);

                if (debug) {
                    System.out.println("mouseClicked event used.");
                }

            }

        }

        /* Reset mouse location. */
        myDrawPanel.setMousePressedLocation(null);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        /* Always update the mouse position info. */
        myDrawPanel.updateMousePos(e.getPoint());

        /* Show tool tip on nodes. */
        showTooltip(e);

        /* Update the overlay if we are in "draw new arc" mode. */
        if (drawArcMode) {
            myGuiController.updateDrawArcCurrentEndLocation(e.getPoint());
        }
    }

    /* Interface PopupMenuListener */

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

    /* Methods for mouseMove */

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
     * Returns the information related to the specified node as HTML-formatted
     * String to allow line breaks.
     * 
     * @param node
     *            The {@link IGuiNode}
     * @return A HTML-formatted String
     */
    private String getNodeInfo(IGuiNode node) {
        String nodeInfo;

        String name = node.getName();
        if (name.equalsIgnoreCase(""))
            name = "—";
        String id = node.getId();

        /*
         * Multi-line tool tip:
         * https://docs.oracle.com/javase/tutorial/uiswing/components/html.html
         */
        nodeInfo = "<html>";
        nodeInfo = nodeInfo + "Name: " + name;
        nodeInfo = nodeInfo + "<BR>";
        nodeInfo = nodeInfo + "ID: " + id;
        nodeInfo = nodeInfo + "</html>";

        return nodeInfo;
    }

    /* Methods for popup menus */

    /**
     * Shows the suitable popup if the MouseEvent is a PopupTrigger.<BR>
     * <BR>
     * Note: Informs the DrawPanel about the popup menu location.
     * 
     * @param e
     *            The MouseEvent
     */
    private void showPopupIfPopupTrigger(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu popupMenu = getPopupMenu(e.getPoint());
            if (popupMenu == null)
                return;

            Point p = e.getPoint();
            myDrawPanel.setPopupMenuLocation(p);

            Component invoker = e.getComponent();
            int x = p.x;
            int y = p.y;
            popupMenu.show(invoker, x, y);

        } else {
            // ConsoleLogger.logIfDebug(debug, "PopupMenuManager: no popup
            // trigger");
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
         * All of them: element, node and arc, might be null here!
         * 
         * But we need the info anyways because we have to update the enabled
         * state of the "z level" Actions ("ElementToTheForeground" ...).
         */
        myAppController.enableZValueActions(element);

        /* Decide which popup to show */
        String simpleClassName = getSimpleClassName(element);
        switch (simpleClassName) {
        case "GuiPlace":
            return new PopupMenuForPlaces(myDrawPanel, node, popupActions);
        case "GuiTransition":
            return new PopupMenuForTransitions(myDrawPanel, node, popupActions);
        case "GuiArc":
            return new PopupMenuForArcs(myDrawPanel, arc, popupActions);
        case "":
            return new PopupMenuForEmptyAreas(popupActions);
        default:
            System.err.println("No proper popup menu found for: " + mouseLocation.toString());
            return null;
        }
    }

    /* For the "draw new arc" overlay */

    /**
     * Activates this mouse adapters "draw new arc" mode.
     */
    public void activateDrawArcMode() {
        this.drawArcMode = true;
    }

    /**
     * Deactivates this mouse adapters "draw new arc" mode.<BR>
     * <BR>
     * Note: This method is used locally. But it might also be needed as public
     * method e.g. for the application or GUI model controller to deactivate
     * this mode if the user switches to another file. (Which in return means
     * that we are not drawing a new arc anymore.)
     */
    public void deactivateDrawArcMode() {
        this.drawArcMode = false;
        // myGuiController.deactivateDrawArcMode();
    }

    /* Private helpers */

    /**
     * Returns the element at the current location of the DrawPanel.
     * 
     * @param mouseLocation
     *            The specified mouse location as {@link Point}
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
