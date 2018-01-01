package de.lambeck.pned.models.gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import de.lambeck.pned.application.*;
import de.lambeck.pned.elements.ENodeType;
import de.lambeck.pned.elements.gui.IGuiElement;
import de.lambeck.pned.elements.gui.IGuiNode;
import de.lambeck.pned.gui.ECustomColor;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * The draw panel for one Petri net. Holds a reference to the 
 * status bar so that the draw panel can show status messages etc.
 * 
 * @formatter:off
 * Actions for mouse events: See MyMouseAdapter
 * 
 * Actions for key(board) events:
 * - F2 :
 *   - With only 1 selected element:
 *     -> Rename
 * - DEL: Delete selected elements
 * 
 * Removed:
 * - ESC: clear selection (in conflict with the popupMenuCanceled 
 *        methods in the popup menus)
 * 
 * @formatter:on
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public class DrawPanel extends JPanel implements IDrawPanel, IModelRename, IInfo_MousePos, IInfo_SelectionRangeSize,
        IInfo_DrawingAreaSize, IInfo_Status {

    private static boolean debug = false;

    /** Reference to the {@link ApplicationController} */
    protected ApplicationController myAppController = null;

    /** Reference to the {@link IGuiModelController} */
    protected IGuiModelController myGuiController = null;

    /** Reference to the manager for I18N strings */
    protected I18NManager i18n;

    /** The {@link Map} with the popup Actions */
    protected Map<String, AbstractAction> popupActions;

    /**
     * This should be the canonical (unique) path name of the file.
     */
    private String modelName = "";

    /**
     * This should be the name of the tab. (file name only)
     */
    private String displayName = "";

    /** Reference to the {@link IGuiModel} for this draw panel */
    private IGuiModel myGuiModel = null;

    /**
     * Indicates the area taken up by graphics.
     */
    private Dimension graphicsArea = new Dimension(0, 0);

    /*
     * Variables for MyMouseAdapter (visible in this package)
     */

    /**
     * Location of the mousePressed event. (Mouse click or start of dragging)
     */
    private Point mousePressedLocation = null;

    /**
     * Stores whether we are in mouse dragging mode. (To allow updating the data
     * model after finishing the drag operation.)
     */
    private boolean mouseDragMode = false;

    /**
     * Location of the mouse pointer when dragging started. (e.g. for debug
     * info)
     */
    private Point initialDraggedFrom = null;

    /**
     * Location of the mouse prior to the current (intermediate) step of
     * dragging
     */
    private Point mouseDraggedFrom = null;

    /**
     * Location of the mouse after dragging
     */
    private Point mouseDraggedTo = null;

    /*
     * Variables for KeyBinding
     */

    /** Stores if the CTRL modifier has been (and is currently) pressed. */
    boolean ctrlKey_pressed = false;

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
     *            The GUI model controller
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
         * MouseListener to select and move nodes and to show popup menus and a
         * MouseMotionListener
         */
        addMouseListener(new MyMouseAdapter(this, myGuiController, popupActions, myAppController));
        addMouseMotionListener(new MyMouseAdapter(this, myGuiController, popupActions, myAppController));

        /*
         * KeyboardFocusManager replaces KeyBindings because of unexpected mouse
         * event with KeyBindings. (Releasing CTRL/ALT was not always detected.)
         * 
         * https://stackoverflow.com/a/12763850
         */
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {

                /* F2 - rename -> EditRenameAction in the menu bar! */

                /* Delete -> EditDeleteAction in the menu bar! */

                if (e.getKeyCode() == KeyEvent.VK_CONTROL && e.isControlDown()) {
                    if (debug) {
                        System.out.println("DrawPanel.KeyboardFocusManager: CTRL");
                    }
                    ctrl_pressed_Action_occurred();

                } else {
                    /* Something else */
                    if (debug) {
                        System.out.println("DrawPanel.KeyboardFocusManager: —");
                    }
                    ctrl_released_Action_occurred();

                }

                /*
                 * TODO Use cursor? (KeyEvent.VK_RIGHT etc.)
                 */

                return false;
            }
        });

        /* Activate updates for the drawing area in the status bar. */
        addComponentListener(new ComponentResizeListener(appController));

        if (debug) {
            System.out.println("DrawPanel created, name: " + getModelName());
        }
    }

    /*
     * Aus nicht ersichtlichem Grund funktioniert handleKeyEvent(KeyEvent e)
     * nicht mehr, nachdem die Reihenfolge von TabbedPane und ScrollPane
     * getauscht wurde. Ersetzt durch KeyBindings/KeyboardFocusManager.
     */

    /*
     * Keyboard events
     */

    private void ctrl_pressed_Action_occurred() {
        if (debug) {
            System.out.println("DrawPanel, ctrl_pressed_Action: We allow special selection modes...");
        }

        /*
         * Check if already allowed since CTRL_PRESSED fires again and again if
         * the user holds the CTRL button down...
         */
        if (!ctrlKey_pressed) {
            ctrlKey_pressed = true;
        }
    }

    private void ctrl_released_Action_occurred() {
        if (ctrlKey_pressed == true) {
            if (debug) {
                System.out.println("DrawPanel, ctrl_released_Action: We quit special selection modes.");
            }
        }

        ctrlKey_pressed = false;
    }

    /*
     * Painting code...
     */

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DrawPanel(" + getModelName() + ").paintComponent");
        }

        Graphics2D g2 = (Graphics2D) g;
        activateAntialiasing(g2);

        drawGridLines(g2);

        boolean areaChanged = false;

        for (IGuiElement element : myGuiModel.getElements()) {
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
        Color gridColor = ECustomColor.SNOW2.getColor();

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
                ConsoleLogger.consoleLogMethodCall("DrawPanel.updateDrawing", area);
            }
            this.repaint(area); // The specified area only
        } else {
            if (debug) {
                ConsoleLogger.consoleLogMethodCall("DrawPanel.updateDrawing");
                System.out.println("-> Repaint everything");
            }
            this.repaint(); // Everything
        }
    }

    @Override
    public Point getMousePressedLocation() {
        return this.mousePressedLocation;
    }

    @Override
    public void setMousePressedLocation(Point p) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("setMousePressedLocation", p);
        }

        this.mousePressedLocation = p;
    }

    @Override
    public boolean getStateMouseDragMode() {
        return this.mouseDragMode;
    }

    @Override
    public void setStateMouseDragMode(boolean b) {
        this.mouseDragMode = b;

        if (b) {
            setMoveCursor();
        } else {
            resetCursor();
        }
    }

    /**
     * Changes the Cursor to the "moveCursor".
     */
    private void setMoveCursor() {
        Cursor moveCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        setCursor(moveCursor);
    }

    /**
     * Resets the Cursor to "Normal".
     */
    private void resetCursor() {
        setCursor(null);
    }

    @Override
    public Point getInitialDraggedFrom() {
        return this.initialDraggedFrom;
    }

    @Override
    public void setInitialDraggedFrom(Point p) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("setInitialDraggedFrom", p);
        }

        this.initialDraggedFrom = p;
    }

    @Override
    public Point getMouseDraggedFrom() {
        return this.mouseDraggedFrom;
    }

    @Override
    public void setMouseDraggedFrom(Point p) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("setMouseDraggedFrom", p);
        }

        this.mouseDraggedFrom = p;
    }

    @Override
    public Point getMouseDraggedTo() {
        return this.mouseDraggedTo;
    }

    @Override
    public void setMouseDraggedTo(Point p) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("setMouseDraggedTo", p);
        }

        this.mouseDraggedTo = p;
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
        // this.lastMouseEvent = EMouseEvent.NONE;
        this.mousePressedLocation = null;
        this.mouseDragMode = false;
        this.initialDraggedFrom = null;
        this.mouseDraggedFrom = null;
        this.mouseDraggedTo = null;
    }

    private void resetKeyboardModifiers() {
        this.ctrlKey_pressed = false;
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
            ConsoleLogger.consoleLogMethodCall("DrawPanel.popupMenuLeft");
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

    @Override
    public ENodeType getSourceForNewArcType() {
        return myGuiController.getSourceForNewArcType();
    }

    /*
     * Other public methods
     */

    /*
     * Menu commands (for menu bar or popup menus)
     */

    /*
     * Helper methods
     */

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
