package de.lambeck.pned.models.gui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import de.lambeck.pned.gui.popupMenu.PopupMenuManager;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * {@link MouseAdapter} for the {@link DrawPanel}.
 * 
 * @formatter:off
 * Actions for mouse events:
 * - Primary mouse button:
 *   - Pressed:
 *     - Selects a single element (node or arc).
 *       -> e.g. for "delete element"
 *     - Clears selection if outside of the current selection.
 *   - Pressed + CTRL:
 *     - Toggles selection of the current element.
 *     - Or: Adds more elements to the current selection.
 * 
 * - Secondary mouse button:
 *   - At a node:
 *     - Show popup menu (nodes only -> change z value)
 *   - At empty space:
 *     - Show popup for creation of new nodes.
 * 
 * Actions for mouse motion events:
 * - Primary mouse button + ALT : -> Dragging
 * 
 * @formatter:on
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class MyMouseAdapter extends MouseAdapter {

    private static boolean debug = true;

    private DrawPanel myDrawPanel = null;
    private IGuiModelController myGuiController = null;

    private boolean popupMenuLeftWithMousePress;

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
     * Constructs a mouse adapter for the specified draw panel.
     * 
     * @param drawPanel
     *            The draw panel
     * @param guiController
     *            The GUI controller
     */
    public MyMouseAdapter(DrawPanel drawPanel, IGuiModelController guiController) {
        this.myDrawPanel = drawPanel;
        this.myGuiController = guiController;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("MyMouseAdapter.mousePressed", e);
        }

        if (!isLeftMouseButtonEvent(e))
            return;

        handleMousePressed(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("MyMouseAdapter.mouseDragged", e);
        }

        /*
         * We drag only if it is allowed.
         */
        boolean altKey_pressed = myDrawPanel.altKey_pressed;
        if (!altKey_pressed)
            return;

        myDrawPanel.mouseIsDragging = true;

        /*
         * Store the initial start and current dragging locations.
         */
        if (myDrawPanel.initialDraggedFrom == null)
            myDrawPanel.initialDraggedFrom = myDrawPanel.mousePressedLocation;
        if (myDrawPanel.mouseDraggedFrom == null)
            myDrawPanel.mouseDraggedFrom = myDrawPanel.mousePressedLocation;
        myDrawPanel.mouseDraggedTo = e.getPoint();

        /*
         * Inform the GUI controller that dragging has happened.
         */
        int distance_x = myDrawPanel.mouseDraggedTo.x - myDrawPanel.mouseDraggedFrom.x;
        int distance_y = myDrawPanel.mouseDraggedTo.y - myDrawPanel.mouseDraggedFrom.y;
        if (debug) {
            System.out.println("mouseDragged: " + distance_x + ", " + distance_y);
        }
        myGuiController.mouseDragged(distance_x, distance_y);

        /*
         * New "start" in case there is another dragging step following.
         */
        myDrawPanel.mouseDraggedFrom = myDrawPanel.mouseDraggedTo;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("MyMouseAdapter.mouseReleased", e);
        }

        if (!isLeftMouseButtonEvent(e))
            return;

        /*
         * Handle the event here
         */

        /*
         * We have left a popup menu with the mousePressed before?
         */
        if (this.popupMenuLeftWithMousePress == true) {
            if (debug) {
                System.out.println("Reset this.popupMenuLeftWithMousePress = false");
            }
            this.popupMenuLeftWithMousePress = false;
            return;
        }

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
             * controller so that the GUI controller can check if both are on
             * the same element.
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
                /*
                 * Nothing
                 */

                // System.err.println("Unexpected mouse event at: " +
                // e.getPoint());
                // System.err.println("ctrlKey_pressed: " + ctrlKey_pressed);
                // System.err.println("altKey_pressed: " + altKey_pressed);
                /*
                 * Thread issue with the values of ctrlKey_pressed,
                 * altKey_pressed?
                 */

                if (debug) {
                    System.out.println("mouseReleased event not used.");
                }

            } else {
                /*
                 * Nothing
                 */

                // System.err.println("Unexpected mouse event at: " +
                // e.getPoint());
                // System.err.println("ctrlKey_pressed: " + ctrlKey_pressed);
                // System.err.println("altKey_pressed: " + altKey_pressed);

                /*
                 * Thread issue with the values of ctrlKey_pressed,
                 * altKey_pressed?
                 */

                if (debug) {
                    System.out.println("mouseReleased event not used.");
                }

            }

            /*
             * TODO Always reset CTRL and ALT from here too to avoid those
             * Unexpected mouse event! (A thread issue???)
             */
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
             * There was a dragging operation. This is the mouseReleased event
             * after mouseDragged.
             */

            if (debug) {
                /*
                 * Show the complete way the mouse has traveled
                 */
                int distance_x = e.getPoint().x - myDrawPanel.initialDraggedFrom.x;
                int distance_y = e.getPoint().y - myDrawPanel.initialDraggedFrom.y;
                System.out.println("Mouse traveled: " + distance_x + "," + distance_y);
            }

            /*
             * Inform the application controller to update the position of all
             * dragged nodes in the data model.
             */
            myGuiController.updateDataNodePositions();

            /*
             * One complete update of the drawing to make sure.
             */
            myDrawPanel.updateDrawing(null);

            if (debug) {
                System.out.println("mouseReleased event used.");
            }

        }

        /*
         * Reset mouse locations
         */
        myDrawPanel.mousePressedLocation = null;

        myDrawPanel.mouseIsDragging = false;

        myDrawPanel.initialDraggedFrom = null;
        myDrawPanel.mouseDraggedFrom = null;
        myDrawPanel.mouseDraggedTo = null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (debug) {
            System.out.println("mouseClicked");
        }

        /*
         * Nothing more. We use only mousePressed() and mouseReleased().
         */
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        /*
         * Always update the mouse position info!
         */
        myDrawPanel.updateMousePos(e.getPoint());
    }

    /*
     * Update comment?
     */

    /**
     * Checks if the mouse event requires any action in the the GUI.
     * 
     * @param e
     *            The mouse event
     */
    private void handleMousePressed(MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("MyMouseAdapter.handleMousePressed", e);
        }

        if (!isLeftMouseButtonEvent(e))
            return;

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
        if (myDrawPanel.getPopupMenuLocation() != null) {
            myDrawPanel.setPopupMenuLocation(null);
            System.out.println("Popup menu left.");
            if (debug) {
                System.out.println("Set this.popupMenuLeftWithMousePress = true");
            }
            this.popupMenuLeftWithMousePress = true;
            return; // Do nothing more!
        }

        /*
         * Store location of the mousePressedEvent.
         */
        myDrawPanel.mousePressedLocation = e.getPoint();
        if (debug) {
            System.out.println("mousePressed at: " + myDrawPanel.mousePressedLocation.x + ","
                    + myDrawPanel.mousePressedLocation.y);
        }

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
     * Private helpers
     */

    /**
     * Checks if the user has used the left mouse button.
     * 
     * Note: Right mouse button (popup menus) will be handled by the
     * {@link PopupMenuManager}.
     * 
     * @param e
     *            The mouse event
     * @return True if the button of the mouse event was the left button
     */
    private boolean isLeftMouseButtonEvent(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e))
            return true;

        if (debug) {
            System.out.println("Not the left mouse button");
        }
        return false; // Do nothing more!
    }

}
