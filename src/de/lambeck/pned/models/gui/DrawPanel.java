package de.lambeck.pned.models.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Map;

import javax.swing.*;

import de.lambeck.pned.application.*;
import de.lambeck.pned.elements.gui.IGuiElement;
import de.lambeck.pned.elements.gui.IGuiNode;
import de.lambeck.pned.gui.CustomColor;
import de.lambeck.pned.gui.popupMenu.PopupMenuManager;
import de.lambeck.pned.i18n.I18NManager;

/**
 * The draw panel for one Petri net. Holds a reference to the 
 * status bar so that the draw panel can show status messages etc.
 * 
 * @formatter:off
 * Actions for mouse events: See MyMouseAdapter
 * 
 * Actions for key(board) events:
 * - ESC: clearSelection
 * - F2 :
 *   - With only 1 selected element:
 *     -> Rename
 * - DEL: Delete selected elements
 * 
 * @formatter:on
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public class DrawPanel extends JPanel implements IDrawPanel, IModelRename, IInfo_MousePos, IInfo_SelectionRangeSize,
        IInfo_DrawingAreaSize, IInfo_Status {

    private static boolean debug = true;

    protected ApplicationController myAppController = null;
    protected IGuiModelController myGuiController = null;
    protected I18NManager i18n;
    protected Map<String, AbstractAction> popupActions;

    /**
     * This should be the canonical (unique) path name of the file.
     */
    private String modelName = "";

    /**
     * This should be the name of the tab. (file name only)
     */
    private String displayName = "";

    private IGuiModel myGuiModel = null;

    /**
     * Indicates the area taken up by graphics.
     */
    private Dimension graphicsArea = new Dimension(0, 0);

    /*
     * Variables for MyMouseAdapter (visible in this package)
     */

    /**
     * Stores the last event (e.g. "mousePressed") and modifiers (e.g. "CTRL").
     * 
     * But the events will not fire until mouseReleased to give the user a
     * chance to abort an action by dragging the mouse away before releasing the
     * mouse button.
     */
    MyMouseEvent lastMouseEvent = MyMouseEvent.NONE;

    /**
     * Location of the mousePressed event. (Mouse click or start of dragging)
     */
    Point mousePressedLocation = null;

    /**
     * Stores if the mouse is actually dragging. (To allow updating the data
     * model after finishing the drag operation.)
     */
    boolean mouseIsDragging = false;

    /**
     * Location of the mouse pointer when dragging started. (e.g. for debug
     * info)
     */
    Point initialDraggedFrom = null;

    /**
     * Location of the mouse prior to the current (intermediate) step of
     * dragging
     */
    Point mouseDraggedFrom = null;

    /**
     * Location of the mouse after dragging
     */
    Point mouseDraggedTo = null;

    /*
     * Variables for KeyBinding
     */

    /**
     * Stores if the CTRL modifier has been (and is currently) pressed.
     */
    boolean ctrlKey_pressed = false;

    /**
     * Stores if the ALT modifier has been (and is currently) pressed.
     */
    boolean altKey_pressed = false;

    /*
     * Variable for the PopupMenuManager
     */

    private Point popupMenuLocation = null;

    /*
     * Constructor etc.
     */

    /**
     * Constructs the DrawPanel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     * @param displayName
     *            The name of the tab (the file name only)
     * @param appController
     *            The application controller
     * @param guiController
     *            The GUI controller
     * @param guiModel
     *            The GUI model to draw on this draw panel
     * @param popupActions
     *            List of Actions
     * @param i18n
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    public DrawPanel(String modelName, String displayName, ApplicationController appController,
            IGuiModelController guiController, IGuiModel guiModel, Map<String, AbstractAction> popupActions,
            I18NManager i18n) {
        super();
        this.modelName = modelName;
        this.displayName = displayName;
        this.myAppController = appController;
        this.myGuiController = guiController;
        this.myGuiModel = guiModel;
        this.i18n = i18n;
        this.popupActions = popupActions;

        setBorder(BorderFactory.createLineBorder(Color.black));
        setBackground(Color.WHITE);

        setLayout(new BorderLayout());

        /*
         * Two MouseListeners for "normal" MouseEvents and MouseEvents
         * requesting a popup menu and a 3rd MouseMotionListener
         */
        addMouseListener(new MyMouseAdapter(this, guiController));
        addMouseListener(new PopupMenuManager(this, guiController, popupActions));
        addMouseMotionListener(new MyMouseAdapter(this, guiController));

        /*
         * Add KeyBindings for keyboard commands. (KeyListener didn't work!)
         */
        addKeyBindings();

        /*
         * ComponentResizeListener for updates of the drawing area in the status
         * bar.
         */
        addComponentListener(new ComponentResizeListener(appController));

        if (debug) {
            System.out.println("PNDrawPanel created, name: " + getModelName());
        }
    }

    /*
     * Aus nicht ersichtlichem Grund funktioniert handleKeyEvent(KeyEvent e)
     * nicht mehr, nachdem die Reihenfolge von TabbedPane und ScrollPane
     * getauscht wurde. Ersetzt durch einwandfrei funktionierende KeyBindings.
     */
    // void handleKeyEvent(KeyEvent e)

    /**
     * Adds KeyBindings to this {@link DrawPanel}.
     * 
     * -> https://coderanch.com/t/346571/java/JTabbedPane-Keyevents
     */
    private void addKeyBindings() {
        /*
         * Escape
         */
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escape_Action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (debug) {
                    System.out.println("DrawPanel, escape_Action (KeyBinding)");
                    // System.out.println("DrawPanel.this.getPopupMenuActive():
                    // " + DrawPanel.this.getPopupMenuActive());
                    System.out
                            .println("DrawPanel.this.getPopupMenuLocation(): " + DrawPanel.this.getPopupMenuLocation());
                }

                /*
                 * Are we just "leaving" a popup menu with ESCAPE?
                 */
                // if (DrawPanel.this.getPopupMenuActive()) {
                // DrawPanel.this.setPopupMenuActive(false);
                // return; // Do nothing more!
                // }
                if (DrawPanel.this.popupMenuLocation != null) {
                    DrawPanel.this.popupMenuLocation = null;
                    return; // Do nothing more!
                }

                /*
                 * Let the GUI controller do whatever is necessary.
                 */
                myGuiController.keyEvent_Escape_Occurred();
            }
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        getActionMap().put("ESCAPE", escape_Action);

        /*
         * Delete
         */
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
        Action delete_Action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (debug) {
                    System.out.println("DrawPanel, delete_Action (KeyBinding)");
                }

                /*
                 * Let the GUI controller do whatever is necessary.
                 */
                myGuiController.keyEvent_Delete_Occurred();
            }
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(delete, "DELETE");
        getActionMap().put("DELETE", delete_Action);

        /*
         * F2 - rename
         * 
         * -> Now hidden by the EditRenameAction with keyEvent F2!
         */
        KeyStroke f2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false);
        Action f2_Action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (debug) {
                    System.out.println("DrawPanel, f2_Action (KeyBinding)");
                }

                /*
                 * Let the GUI controller do whatever is necessary.
                 */
                myGuiController.keyEvent_F2_Occurred();
            }
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f2, "F2");
        getActionMap().put("F2", f2_Action);

        /*
         * TODO Use cursor? (KeyEvent.VK_RIGHT etc.)
         */

        /*
         * Detect "CTRL" and "CTRL released" to allow special selection modes.
         */
        KeyStroke ctrl_pressed = KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, InputEvent.CTRL_DOWN_MASK, false);
        Action ctrl_pressed_Action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (debug) {
                    System.out.println("DrawPanel, ctrl_pressed_Action: We allow special selection modes...");
                }

                /*
                 * Check if already allowed since CTRL_PRESSED fires again and
                 * again if the user holds the CTRL button down...
                 */
                if (!ctrlKey_pressed) {
                    ctrlKey_pressed = true;
                }
            }
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrl_pressed, "CTRL_PRESSED");
        getActionMap().put("CTRL_PRESSED", ctrl_pressed_Action);

        KeyStroke ctrl_released = KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, 0, true);
        Action ctrl_released_Action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (debug) {
                    System.out.println("DrawPanel, ctrl_released_Action: We quit special selection modes.");
                }

                ctrlKey_pressed = false;
            }
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrl_released, "CTRL_RELEASED");
        getActionMap().put("CTRL_RELEASED", ctrl_released_Action);

        /*
         * Detect "ALT" and "ALT released" to allow and quit dragging.
         * 
         * https://www.java-forums.org/awt-swing/35240-key-binding-vk_alt-
         * vk_shift-not-working.html
         */
        KeyStroke alt_pressed = KeyStroke.getKeyStroke(KeyEvent.VK_ALT, InputEvent.ALT_DOWN_MASK, false);
        Action alt_pressed_Action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (debug) {
                    System.out.println("DrawPanel, alt_pressed_Action: We allow dragging...");
                }

                /*
                 * Check if already allowed since ALT_PRESSED fires again and
                 * again if the user holds the ALT button down...
                 */
                if (!altKey_pressed) {
                    altKey_pressed = true;

                    /*
                     * Change Cursor to "moveCursor"
                     */
                    Cursor moveCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
                    setCursor(moveCursor);
                }
            }
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(alt_pressed, "ALT_PRESSED");
        getActionMap().put("ALT_PRESSED", alt_pressed_Action);

        KeyStroke alt_released = KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, true);
        Action alt_released_Action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (debug) {
                    System.out.println("DrawPanel, alt_released_Action: We quit dragging.");
                }

                altKey_pressed = false;

                /*
                 * Reset the Cursor.
                 */
                setCursor(null);
            }
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(alt_released, "ALT_RELEASED");
        getActionMap().put("ALT_RELEASED", alt_released_Action);
    }

    /*
     * Painting code...
     */

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (debug) {
            System.out.println("PNDrawPanel(" + getModelName() + ").paintComponent()");
        }

        Graphics2D g2 = (Graphics2D) g;
        activateAntialiasing(g2);

        // if (debug)
        drawGridLines(g2);

        // TODO Wenn die Nodes sortiert werden, müssen die Elemente in gleicher
        // Reihenfolge gezeichnet werden!

        boolean areaChanged = false;

        for (IGuiElement element : myGuiModel.getElements()) {
            // System.out.println("Painting " + element.getId() + "...");
            element.paintElement(g);

            if (element instanceof IGuiNode) {
                IGuiNode node = (IGuiNode) element;
                int x = node.getTotalLeftX();
                int y = node.getTotalTopY();
                int width = node.getTotalWidth();
                int height = node.getTotalHeight();
                // Rectangle rect = new Rectangle(x, y, width, height);
                // scrollRectToVisible(rect); Nicht hier, nur beim Einfügen!!!

                /*
                 * Update the draw panels graphicsArea
                 */
                int this_width = (x + width + 5);
                if (this_width > graphicsArea.width) {
                    graphicsArea.width = this_width;
                    areaChanged = true;
                }

                int this_height = (y + height + 5);
                if (this_height > graphicsArea.height) {
                    graphicsArea.height = this_height;
                    areaChanged = true;
                }
            }
        }

        if (areaChanged) {
            // Update client's preferred size because
            // the area taken up by the graphics has
            // gotten larger or smaller (if cleared).
            this.setPreferredSize(graphicsArea);

            // Let the scroll pane know to update itself
            // and its scrollbars.
            this.revalidate();
        }

        if (debug) {
            /*
             * Indicate the graphics area.
             */
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawRect(0, 0, graphicsArea.width, graphicsArea.height);
        }

        // drawSelectionRange(g2);
    }

    /**
     * Activates anti-aliasing.
     * 
     * @param g
     *            The Graphics object
     */
    private void activateAntialiasing(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // TODO RenderingHints.KEY_TEXT_ANTIALIASING ist so OK?
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /**
     * Draws grid lines to the drawing area.
     * 
     * @param g2
     */
    private void drawGridLines(Graphics2D g2) {
        final int GRID_STEP = 100;
        Color gridColor = CustomColor.SNOW2.getColor();

        /*
         * Nothing painted yet -> graphicsArea is empty!
         */
        // int width = this.graphicsArea.width;
        // int height = this.graphicsArea.height;

        /*
         * Use the JComponent attributes.
         */
        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0)
            return;

        g2.setColor(gridColor);

        /*
         * Horizontal grid lines
         */
        for (int i = 99; i <= height; i += GRID_STEP) {
            g2.drawLine(0, i, width, i);
        }

        /*
         * Vertical grid lines
         */
        for (int i = 99; i <= width; i += GRID_STEP) {
            g2.drawLine(i, 0, i, height);
        }
    }

    void updateMousePos(Point pos) {
        setInfo_MousePos(pos);
    }

    /*
     * Getter and Setter
     */

    @Override
    public String getModelName() {
        return this.modelName;
    }

    @Override
    public void setModelName(String s) {
        this.modelName = s;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(String s) {
        this.displayName = s;
    }

    @Override
    public Dimension getPreferredSize() {
        return this.graphicsArea;
    }

    /*
     * Methods for interface IDrawPanel
     */

    @Override
    public void updateDrawing(Rectangle area) {
        if (area != null) {
            if (debug) {
                System.out.println("DrawPanel.updateDrawing(" + area + ")");
            }
            this.repaint(area); // The specified area only
        } else {
            if (debug) {
                System.out.println("DrawPanel.updateDrawing(everything)");
            }
            this.repaint(); // Everything
        }
    }

    @Override
    public void resetState() {
        resetMouseOperations();
        resetKeyboardModifiers();
        resetCursor();
    }

    /**
     * Resets old mouse operations.
     */
    private void resetMouseOperations() {
        this.lastMouseEvent = MyMouseEvent.NONE;
        this.mousePressedLocation = null;
        this.mouseIsDragging = false;
        this.initialDraggedFrom = null;
        this.mouseDraggedFrom = null;
        this.mouseDraggedTo = null;
    }

    private void resetKeyboardModifiers() {
        this.ctrlKey_pressed = false;
        this.altKey_pressed = false;
    }

    private void resetCursor() {
        setCursor(null); // "Normal" cursor
    }

    @Override
    public boolean isSelectableElement(IGuiElement element) {
        return myGuiController.isSelectableElement(element);
    }

    @Override
    public int getMinZValue() {
        return myGuiModel.getMinZValue();
    }

    @Override
    public int getMaxZValue() {
        return myGuiModel.getMaxZValue();
    }

    @Override
    public int getZValue(IGuiElement element) {
        return myGuiModel.getZValue(element);
    }

    // @Override
    // public void setPopupMenuActive(boolean active) {
    // this.popupMenuActive = active;
    // }

    @Override
    public void setPopupMenuLocation(Point p) {
        this.popupMenuLocation = p;
    }

    @Override
    public void popupMenuCanceled() {
        this.popupMenuLocation = null;
    }

    @Override
    public void popupMenuLeft() {
        if (debug) {
            System.out.println("DrawPanel.popupMenuLeft()");
        }
        this.popupMenuLocation = null;
    }

    @Override
    public Point getPopupMenuLocation() {
        return this.popupMenuLocation;
    }

    @Override
    public boolean getStateAddingNewArc() {
        return myGuiController.getStateAddingNewArc();
    }

    /*
     * Other public methods
     */

    // /**
    // * Returns true if this draw panel is currently showing a popup menu.
    // *
    // * @return True if a popup menu is active; otherwise false
    // */
    // public boolean getPopupMenuActive() {
    // return this.popupMenuActive;
    // }

    /*
     * Menu commands (for menu bar or popup menus)
     */

    /*
     * Helper methods
     */

    /**
     * Returns the node at the specified Point. Returns the one with the highest
     * z-value if there is more than 1 at this location.
     * 
     * Note: This means only nodes, not other elements!
     * 
     * @param p
     *            The specified Point
     * @return The first node at the mouse position
     */
    private IGuiNode getNodeAtLocation(Point p) {
        // TODO Auch für Pfeile? (zum Anklicken und löschen?)
        java.util.List<IGuiElement> elements = myGuiModel.getElements();
        // TODO Must the list be sorted again?
        IGuiElement foundElement = null;

        for (IGuiElement element : elements) {
            if (isNode(element)) {
                if (element.contains(p)) {
                    foundElement = element;
                }
            }
        }

        if (debug) {
            if (foundElement == null)
                System.out.println("No node at this Point!");
        }

        return (IGuiNode) foundElement;
    }

    /**
     * Checks if an element is a node (circle or square).
     * 
     * @param element
     *            The element to check
     * @return True if the element is a node; otherwise false
     */
    private boolean isNode(IGuiElement element) {
        if (element instanceof IGuiNode)
            return true;
        return false;
    }

    /*
     * For interface IInfo_Status
     */

    @Override
    public void setInfo_MousePos(Point p) {
        myGuiController.setInfo_MousePos(p);
    }

    @Override
    public void setInfo_SelectionRangeSize(int width, int height) {
        myGuiController.setInfo_SelectionRangeSize(width, height);
    }

    @Override
    public void setInfo_DrawingAreaSize(int width, int height) {
        myGuiController.setInfo_DrawingAreaSize(width, height);
    }

    @Override
    public void setInfo_Status(String s, EStatusMessageLevel level) {
        myGuiController.setInfo_Status(s, level);
    }

}