package de.lambeck.pned.models.gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.*;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.application.EStatusMessageLevel;
import de.lambeck.pned.elements.ENodeType;
import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.elements.gui.*;
import de.lambeck.pned.elements.util.NodeCheck;
import de.lambeck.pned.exceptions.PNElementException;
import de.lambeck.pned.exceptions.PNNoSuchElementException;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * Observes the state of the GUI.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class GuiModelController implements IGuiModelController {

    /** Show debug messages? */
    private static boolean debug = false;

    /** Minimum shape size for setter */
    private final static int MIN_SHAPE_SIZE = 20;

    /** Reference to the {@link ApplicationController} */
    protected ApplicationController appController = null;

    /** The manager for localized strings */
    protected I18NManager i18n;

    /** The {@link Map} with possible {@link AbstractAction} in popup menus */
    protected Map<String, AbstractAction> popupActions;

    /**
     * List of GUI models identified by their name (full name of the file)
     */
    private Map<String, IGuiModel> guiModels = new HashMap<String, IGuiModel>();

    /**
     * Current model is the model that corresponds to the active tab of the
     * tabbed pane.
     */
    private IGuiModel currentModel = null;

    /**
     * List of draw panels identified by their name (full name of the file)
     */
    private Map<String, IDrawPanel> drawPanels = new HashMap<String, IDrawPanel>();

    /**
     * Current draw panel is the draw panel that corresponds to the active tab
     * of the tabbed pane.
     */
    private IDrawPanel currentDrawPanel = null;

    /**
     * A list of nodes which have been moved during a mouse drag operation and
     * need an update in the data model after dragging has finished.
     */
    private List<IGuiNode> movedNodes = new ArrayList<IGuiNode>();

    /**
     * The source node for the new Arc to be added.<BR>
     * <BR>
     * Note: The local attribute "addingNewArc" should be true if this attribute
     * is != null.
     */
    private IGuiNode sourceNodeForNewArc = null;

    /**
     * The type of the source node for the new Arc to be added.
     */
    private ENodeType sourceForNewArcType = null;

    /**
     * Stores whether this GUI model controller is in "draw new arc" mode or
     * not. This always refers to the current GUI model.<BR>
     * <BR>
     * <B>If true:</B> The mouse adapter has to report new mouse positions in
     * order to update/repaint the (temporary) overlay with the new arc while
     * the user is moving the mouse towards the 2nd node.
     */
    private boolean drawArcMode = false;

    /* Constructor */

    /**
     * Constructs a GUI model controller with references to the application
     * controller (the parent) and a manager for localized strings.
     * 
     * @param controller
     *            The application controller
     * @param i18n
     *            The manager for localized strings
     * @param popupActions
     *            List of Actions
     */
    @SuppressWarnings("hiding")
    public GuiModelController(ApplicationController controller, I18NManager i18n,
            Map<String, AbstractAction> popupActions) {
        this.appController = controller;
        this.i18n = i18n;
        this.popupActions = popupActions;

        debug = controller.getShowDebugMessages();
    }

    /* Methods for implemented interfaces */

    @Override
    public void setInfo_MousePos(Point p) {
        appController.setInfo_MousePos(p);
    }

    @Override
    public void setInfo_SelectionRangeSize(int width, int height) {
        appController.setInfo_SelectionRangeSize(width, height);
    }

    @Override
    public void setInfo_DrawingAreaSize(int width, int height) {
        appController.setInfo_DrawingAreaSize(width, height);
    }

    @Override
    public void setInfo_Status(String s, EStatusMessageLevel level) {
        appController.setInfo_Status(s, level);
    }

    @Override
    public JFrame getMainFrame() {
        return appController.getMainFrame();
    }

    /* Methods for open files */

    @Override
    public void addGuiModel(String modelName, String displayName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.addGuiModel", modelName, displayName);
        }

        /* Some cleanup on the old model/draw panel */
        if (this.currentModel != null) {
            IGuiModel oldModel = this.currentModel;
            IDrawPanel oldDrawPanel = this.currentDrawPanel;
            modelDeactivated(oldModel, oldDrawPanel);
        }

        this.currentModel = new GuiModel(modelName, displayName, this);
        this.guiModels.put(modelName, currentModel);

        /* Add an associated draw panel as well! */
        addDrawPanel(modelName, displayName);

        if (debug) {
            System.out.println("GUI models count: " + guiModels.size());
        }
    }

    /**
     * Called by addGuiModel to add an associated {@link IDrawPanel}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param displayName
     *            The title of the tab (= the file name)
     */
    private void addDrawPanel(String modelName, String displayName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.addDrawPanel", modelName, displayName);
        }

        this.currentDrawPanel = new DrawPanel(modelName, displayName, appController, this, currentModel, popupActions);
        this.drawPanels.put(modelName, currentDrawPanel);

        if (debug) {
            System.out.println("Draw panels count: " + drawPanels.size());
        }
    }

    @Override
    public boolean isModifiedGuiModel(String modelName) {
        boolean modified = false;
        for (Entry<String, IGuiModel> entry : guiModels.entrySet()) {
            // String key = entry.getKey();
            IGuiModel guiModel = entry.getValue();

            if (guiModel.getModelName().equalsIgnoreCase(modelName))
                modified = guiModel.isModified();
        }

        return modified;

    }

    @Override
    public void resetModifiedGuiModel(String modelName) {
        currentModel.setModified(false);
    }

    @Override
    public void removeGuiModel(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.removeGuiModel", modelName);
        }

        /*
         * Reset the "current model" attribute if we remove the current model.
         */
        try {
            if (this.currentModel.getModelName().equalsIgnoreCase(modelName)) {
                this.currentModel = null;
            }
        } catch (NullPointerException ignore) {
            // Nothing to do
        }

        /* Remove the model. */
        this.guiModels.remove(modelName);

        /* Remove the associated draw panel as well. */
        removeDrawPanel(modelName);

        if (debug) {
            System.out.println("GUI models count: " + guiModels.size());
        }
    }

    /**
     * Called by removeGuiModel to remove the associated {@link IDrawPanel}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    private void removeDrawPanel(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.removeDrawPanel", modelName);
        }

        /*
         * Reset the "current draw panel" attribute if we remove the current
         * draw panel.
         */
        try {
            if (this.currentDrawPanel.getModelName().equalsIgnoreCase(modelName)) {
                this.currentDrawPanel = null;
            }
        } catch (NullPointerException ignore) {
            // Nothing to do
        }

        /* Remove the draw panel. */
        this.drawPanels.remove(modelName);

        if (debug) {
            System.out.println("Draw panels count: " + drawPanels.size());
        }
    }

    @Override
    public void renameGuiModel(IGuiModel model, String newModelName, String newDisplayName) {
        /* The old key for the Map of models. */
        String oldKey = model.getModelName();

        /* Get the associated draw panel. */
        IDrawPanel drawPanel = getDrawPanel(oldKey);

        /* Rename the model and the associated draw panel. */
        IModelRename renameCandidate;

        renameCandidate = (IModelRename) model;
        setModelNames(renameCandidate, newModelName, newDisplayName);

        renameCandidate = (IModelRename) drawPanel;
        setDrawPanelNames(renameCandidate, newModelName, newDisplayName);

        /* Update both Maps! */
        IGuiModel value1 = guiModels.remove(oldKey);
        guiModels.put(newModelName, value1);

        IDrawPanel value2 = drawPanels.remove(oldKey);
        drawPanels.put(newModelName, value2);
    }

    /**
     * Uses interface {@link IModelRename} to rename the model.
     * 
     * @param model
     *            The model as {@link IModelRename}
     * @param newModelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param newDisplayName
     *            The title of the tab (= the file name)
     */
    private void setModelNames(IModelRename model, String newModelName, String newDisplayName) {
        model.setModelName(newModelName);
        model.setDisplayName(newDisplayName);
    }

    /**
     * Uses interface {@link IModelRename} to rename the draw panel.
     * 
     * @param drawPanel
     *            The draw panel as {@link IModelRename}
     * @param newModelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param newDisplayName
     *            The title of the tab (= the file name)
     */
    private void setDrawPanelNames(IModelRename drawPanel, String newModelName, String newDisplayName) {
        drawPanel.setModelName(newModelName);
        drawPanel.setDisplayName(newDisplayName);
    }

    @Override
    public IGuiModel getGuiModel(String modelName) {
        return guiModels.get(modelName);
    }

    @Override
    public IGuiModel getCurrentModel() {
        return this.currentModel;
    }

    @Override
    public void setCurrentModel(IGuiModel model) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.setCurrentModel", model.getModelName());
        }

        /* Some cleanup on the old model/draw panel */
        if (model != this.currentModel) {
            IGuiModel oldModel = this.currentModel;
            IDrawPanel oldDrawPanel = this.currentDrawPanel;
            modelDeactivated(oldModel, oldDrawPanel);
        }

        /* Activate the new model */
        this.currentModel = model;
    }

    /**
     * Cleanup for an old {@link IGuiModel} and its {@link IDrawPanel} after the
     * model got deactivated.
     * 
     * @param oldModel
     *            The deactivated {@link IGuiModel}
     * @param oldDrawPanel
     *            The deactivated {@link IDrawPanel}
     */
    private void modelDeactivated(IGuiModel oldModel, IDrawPanel oldDrawPanel) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModel.modelDeactivated", oldModel, oldDrawPanel);
        }

        /*
         * Deactivate the "draw new arc" mode on the old IDrawPanel.
         * 
         * Note: The IDrawPanel has to deactivate it on the MyMouseAdapter
         * because the IDrawPanel has created the MyMouseAdapter and therefore
         * holds the reference to it.
         */
        oldDrawPanel.deactivateDrawArcMode();

        /* Deactivate the "draw new arc" mode on this GuiModelController. */
        deactivateDrawArcMode();

        /* Cleanup on the old IGuiModel */
        oldModel.deactivated();
    }

    @Override
    public IDrawPanel getDrawPanel(String modelName) {
        for (Entry<String, IDrawPanel> entry : drawPanels.entrySet()) {
            // String key = entry.getKey();
            IDrawPanel drawPanel = entry.getValue();

            if (drawPanel.getModelName().equalsIgnoreCase(modelName))
                return drawPanel;
        }

        return null;
    }

    @Override
    public IDrawPanel getCurrentDrawPanel() {
        return this.currentDrawPanel;
    }

    @Override
    public void setCurrentDrawPanel(IDrawPanel drawPanel) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.setCurrentDrawPanel", drawPanel.getModelName());
        }

        this.currentDrawPanel = drawPanel;

        /*
         * Inform the draw panel that it has got the focus. (In order to reset
         * its state.)
         */
        this.currentDrawPanel.resetState();
    }

    @Override
    public List<String> getModifiedGuiModels() {
        List<String> modifiedModels = new ArrayList<String>();

        for (Entry<String, IGuiModel> entry : guiModels.entrySet()) {
            String key = entry.getKey();
            IGuiModel model = entry.getValue();

            if (model.isModified() == true)
                modifiedModels.add(key);
        }

        return modifiedModels;
    }

    /*
     * Methods for adding, modify and removal of elements (and callbacks for
     * updates between data and GUI model controller)
     */

    /* Add elements */

    @Override
    public void addPlaceToCurrentGuiModel(String id, String name, EPlaceToken initialTokens, Point position) {
        currentModel.addPlace(id, name, initialTokens, position);
        currentModel.setModified(true);

        /* Update the data model */
        appController.placeAddedToCurrentGuiModel(id, name, initialTokens, position);
    }

    @Override
    public void addTransitionToCurrentGuiModel(String id, String name, Point position) {
        currentModel.addTransition(id, name, position);
        currentModel.setModified(true);

        /* Update the data model */
        appController.transitionAddedToCurrentGuiModel(id, name, position);
    }

    @Override
    public void addArcToCurrentGuiModel(String id, String sourceId, String targetId) {
        currentModel.addArc(id, sourceId, targetId);
        currentModel.setModified(true);

        /* Update the data model */
        appController.arcAddedToCurrentGuiModel(id, sourceId, targetId);
    }

    @Override
    public void createNewPlaceInCurrentGuiModel() {
        /* Check if we have a location. */
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation == null) {
            System.err.println(
                    "GuiModelController.createNewPlaceInCurrentGuiModel(): Unable to create a Place: popup menu location unknown.");
            return;
        }

        /* Create a unique ID to avoid any conflict with existing elements. */
        String uuid = UUID.randomUUID().toString();

        /* ...and a Place with this ID. */
        String name = "";
        EPlaceToken initialTokens = EPlaceToken.ZERO;
        addPlaceToCurrentGuiModel(uuid, name, initialTokens, popupMenuLocation);

        /* Update the drawing. */
        IGuiElement element;
        try {
            element = currentModel.getElementById(uuid);
        } catch (PNNoSuchElementException e) {
            System.err.println("New place not created!");
            return;
        }

        Rectangle rect = element.getLastDrawingArea();
        updateDrawing(rect);

        /*
         * Reset the popup active state of the DrawPanel since we have left the
         * popup menu with the NewPlaceAction!
         */
        currentDrawPanel.setPopupMenuLocation(null);
    }

    @Override
    public void createNewTransitionInCurrentGuiModel() {
        /* Check if we have a location. */
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation == null) {
            System.err.println(
                    "GuiModelController.createNewTransitionInCurrentGuiModel(): Unable to create a Place: popup menu location unknown.");
            return;
        }

        /* Create a unique ID to avoid any conflict with existing elements. */
        String uuid = UUID.randomUUID().toString();

        /* ...and a Transition with this ID. */
        String name = "";
        addTransitionToCurrentGuiModel(uuid, name, popupMenuLocation);

        /* Update the drawing. */
        IGuiElement element;
        try {
            element = currentModel.getElementById(uuid);
        } catch (PNNoSuchElementException e) {
            System.err.println("New transition not created!");
            return;
        }

        Rectangle rect = element.getLastDrawingArea();
        updateDrawing(rect);

        /*
         * Reset the popup active state of the DrawPanel since we have left the
         * popup menu with the NewPlaceAction!
         */
        currentDrawPanel.setPopupMenuLocation(null);
    }

    /* For the "draw new arc" overlay */

    @Override
    public void checkActivateDrawArcMode() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.checkActivateDrawArcMode");
        }

        /* Check if we have a location. */
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation == null) {
            System.err.println(
                    "GuiModelController.startDrawingArcMode(): Unable to set source location for new arc: popup menu location unknown.");
            return;
        }

        /* Check if we have a node at this location. */
        IGuiNode startNode = getNodeAtLocation(popupMenuLocation);
        if (startNode == null)
            return;

        /* Store this node as source for the new Arc. */
        sourceNodeForNewArc = startNode;

        if (startNode instanceof GuiPlace) {
            sourceForNewArcType = ENodeType.PLACE;
        } else if (startNode instanceof GuiTransition) {
            sourceForNewArcType = ENodeType.TRANSITION;
        }

        /* Activate "draw new arc" mode. */
        this.drawArcMode = true;

        /*
         * Use the current mouse position here because the mouse should usually
         * be at another location than the node since we have moved the mouse to
         * the popup menu button.
         * 
         * But we need the current position of the cursor above the draw panel!
         */
        Point initialEndLocation = getMousePositionOverDrawPanel();
        if (initialEndLocation == null) {
            System.err.println("Initial end location (over draw panel) == null!");
            return;
        }

        /* Add an overlay to the model. */
        IDrawArcOverlay overlay = new DrawArcOverlay(startNode, initialEndLocation);
        EOverlayName name = EOverlayName.DRAW_NEW_ARC_OVERLAY;
        currentModel.addOverlay(overlay, name);

        /* The created overlay contains an initial arc. */
        // IOverlayGuiArc initialArc = overlay.getCurrentArc();
        // System.out.println(initialArc.getLastDrawingArea());

        /*
         * Activate "draw new arc" mode on the MouseAdapter of the draw panel in
         * order to get the position of the 2nd node when the user produces the
         * next mouseClicked event.
         */
        currentDrawPanel.activateDrawArcMode();
    }

    /**
     * Returns the (relative) mouse position according to the coordinate system
     * of the current {@link IDrawPanel}.
     * 
     * @return Null on errors; otherwise a {@link Point}
     */
    private Point getMousePositionOverDrawPanel() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.getMousePositionOverDrawPanel");
        }

        Point absMousePos = MouseInfo.getPointerInfo().getLocation();
        ConsoleLogger.logIfDebug(debug, "absMousePos: " + absMousePos);

        Component drawPanelComponent = getDrawPanelAsSwingComponent(this.currentDrawPanel);
        if (drawPanelComponent == null)
            return null;

        Point convertedMousePos = absMousePos;
        SwingUtilities.convertPointFromScreen(convertedMousePos, drawPanelComponent);
        ConsoleLogger.logIfDebug(debug, "convertedMousePos: " + convertedMousePos);

        return convertedMousePos;
    }

    @Override
    public boolean getDrawArcModeState() {
        /*
         * Reset the state if the first of the two necessary nodes does not
         * exist anymore because the user has deleted this node in the meantime!
         */
        // TODO Check should be obsolete with the new "draw new arc" overlay!
        if (this.sourceNodeForNewArc == null)
            deactivateDrawArcMode();

        return this.drawArcMode;
    }

    @Override
    public ENodeType getSourceForNewArcType() {
        return this.sourceForNewArcType;
    }

    @Override
    public void updateDrawArcCurrentEndLocation(Point p) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.updateDrawArcCurrentEndLocation");
        }

        if (currentModel == null)
            return;

        /* Get the "draw new arc" mode overlay and the existing arc on it. */
        IOverlayGuiArc arc = getFirstOverlayGuiArc(EOverlayName.DRAW_NEW_ARC_OVERLAY);
        if (arc == null)
            return;

        /* Store old area for repainting */
        Rectangle oldArea = arc.getLastDrawingArea();

        /* Update the end position of the arc */
        arc.setCurrentArcEndLocation(p);

        /* Repaint old and new area */
        Rectangle newArea = arc.getLastDrawingArea();
        updateDrawing(oldArea);
        updateDrawing(newArea);
    }

    /**
     * Returns the first existing {@link IOverlayGuiArc} from the specified
     * {@link IOverlay}.
     * 
     * @param overlayName
     *            The specified {@link IOverlay}
     * @return Null if the overlay or the arc does not exist; otherwise a
     *         reference to the {@link IOverlayGuiArc}
     */
    private IOverlayGuiArc getFirstOverlayGuiArc(EOverlayName overlayName) {
        IOverlay overlay = currentModel.getOverlayByName(overlayName);

        // if (!(overlay instanceof IDrawArcOverlay))
        // return null;
        // IDrawArcOverlay drawArcOverlay = (IDrawArcOverlay) overlay;

        // IPaintable paintable = drawArcOverlay.getPaintableElements().get(0);
        IPaintable paintable = overlay.getPaintableElements().get(0);
        if (!(paintable instanceof IOverlayGuiArc))
            return null;

        IOverlayGuiArc arc = (IOverlayGuiArc) paintable;
        return arc;
    }

    @Override
    public void checkDrawArcFinalEndLocation(Point p) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.checkDrawArcFinalEndLocation");
        }

        /* In any case: We have to remove the overlay from the model. */
        EOverlayName name = EOverlayName.DRAW_NEW_ARC_OVERLAY;
        currentModel.removeOverlay(name);

        /* Check if we have a node at this location. */
        IGuiNode endNode = getNodeAtLocation(p);
        if (endNode == null) {
            deactivateDrawArcMode();
            return;
        }

        /* Check for different types of start and end node. */
        if (!NodeCheck.isValidConnection(sourceNodeForNewArc, endNode)) {
            String title = currentModel.getModelName();
            String errorMessage = i18n.getMessage("errArcsOnlyBetweenDifferentNodes");

            System.err.println(errorMessage);

            /* Get the main frame to center the input dialog. */
            JFrame mainFrame = appController.getMainFrame();

            JOptionPane.showMessageDialog(mainFrame, errorMessage, title, JOptionPane.WARNING_MESSAGE);

            deactivateDrawArcMode();
            return;
        }

        /* Create a unique ID to avoid any conflict with existing elements. */
        String uuid = UUID.randomUUID().toString();

        /* Get the IDs of source and target. */
        String sourceId = sourceNodeForNewArc.getId();
        String targetId = endNode.getId();

        /* Does such a arc already exist in this model? */
        if (arcAlreadyExist(sourceId, targetId)) {
            deactivateDrawArcMode();
            return;
        }

        /* Create the Arc. */
        addArcToCurrentGuiModel(uuid, sourceId, targetId);

        /* Update the drawing. */
        IGuiElement element;
        try {
            element = currentModel.getElementById(uuid);
        } catch (PNNoSuchElementException e) {
            System.err.println("New arc not created!");
            deactivateDrawArcMode();
            return;
        }

        Rectangle rect = element.getLastDrawingArea();
        updateDrawing(rect);
    }

    /**
     * Returns the specified {@link IDrawPanel} as {@link Component}.<BR>
     * <BR>
     * Note: This allows the use of methods like
     * SwingUtilities.convertPointFromScreen().
     * 
     * @param drawPanel
     *            The specified {@link IDrawPanel}
     * @return A reference of type {@link Component}}
     */
    private Component getDrawPanelAsSwingComponent(IDrawPanel drawPanel) {
        Component component = null;
        if (drawPanel instanceof Component) {
            component = (Component) drawPanel;
        }
        return component;
    }

    /**
     * Checks if an arc with the specified source and target exists in the
     * current model.
     * 
     * @param sourceId
     *            The ID of the source {@link IGuiNode}
     * @param targetId
     *            The ID of the target {@link IGuiNode}
     * @return True = such an arc already exists, false = such an arc does not
     *         exist
     */
    private boolean arcAlreadyExist(String sourceId, String targetId) {
        List<IGuiElement> guiElements = currentModel.getElements();
        List<IGuiArc> guiArcs = new ArrayList<IGuiArc>();
        boolean arcAlreadyExists = false;

        for (IGuiElement guiElement : guiElements) {
            if (guiElement instanceof IGuiArc) {
                IGuiArc guiArc = (IGuiArc) guiElement;
                guiArcs.add(guiArc);
            }
        }

        for (IGuiArc guiArc : guiArcs) {
            if (guiArc.getSourceId() == sourceId)
                if (guiArc.getTargetId() == targetId) {
                    arcAlreadyExists = true;
                }
        }

        if (arcAlreadyExists) {
            String title = currentModel.getModelName();
            String errorMessage = i18n.getMessage("errDuplicateArc");

            System.err.println(errorMessage);

            /* Get the main frame to center the input dialog. */
            JFrame mainFrame = appController.getMainFrame();

            JOptionPane.showMessageDialog(mainFrame, errorMessage, title, JOptionPane.WARNING_MESSAGE);

            return true;
        }

        return false;
    }

    @Override
    public void deactivateDrawArcMode() {
        this.sourceNodeForNewArc = null;
        this.sourceForNewArcType = null;
        this.drawArcMode = false;

        updateDrawing();
    }

    /* Modify methods for elements */

    @Override
    public IGuiElement getSelectableElementAtLocation(Point p) {
        if (p == null) {
            System.err.println("GuiModelController.getSelectableElementAtLocation(): p == null!");
            return null;
        }

        if (currentModel == null) {
            System.err.println("GuiModelController.getSelectableElementAtLocation(), currentModel == null!");
            return null;
        }

        List<IGuiElement> elements = currentModel.getElements();
        IGuiElement foundElement = null;

        for (IGuiElement element : elements) {
            if (isSelectableElement(element)) {
                boolean contains = element.contains(p);
                if (contains) {
                    foundElement = element;
                    /*
                     * Don't break on the first found element because we have to
                     * find the element with the highest z value and this will
                     * be more at the "end" of the list.
                     */
                    // break;
                }
            }
        }

        return foundElement;
    }

    @Override
    public boolean isSelectableElement(IGuiElement element) {
        if (element instanceof ISelectable)
            return true;
        return false;
    }

    @Override
    public IGuiNode getNodeAtLocation(Point p) {
        IGuiElement foundElement = getElementAtLocation(p);
        if (foundElement == null)
            return null;

        /*
         * Note: getElementAtLocation() should have returned the topmost
         * element.
         */
        IGuiNode foundNode = null;
        if (foundElement instanceof IGuiNode) {
            foundNode = (IGuiNode) foundElement;
        }

        if (foundNode == null) {
            if (debug) {
                System.out.println("No node at this Point!");
            }
            return null;
        }

        return foundNode;
    }

    @Override
    public void renameSelectedGuiElement() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.renameSelectedGuiElements");
        }

        IGuiNode selectedNode;
        try {
            selectedNode = getSingleSelectedNode();
        } catch (PNElementException e) {
            String warning = i18n.getMessage("warningUnableToRename");
            String explanation = e.getMessage();
            String message = warning + " (" + explanation + ")";

            System.out.println(message);
            setInfo_Status(message, EStatusMessageLevel.INFO);
            return;
        }

        /* OK, we have exactly 1 node and can ask for a new name. */
        String oldName = selectedNode.getName();
        String newName = askUserForNewName(oldName);
        if (newName == null)
            return; // User canceled the operation

        /*
         * We do not know whether the new name will cause the drawing area to
         * increase or shrink. To avoid artifacts, we store both values: the old
         * and the new drawing area after renaming.
         */
        Rectangle oldArea = selectedNode.getLastDrawingArea();

        selectedNode.setName(newName);
        currentModel.setModified(true);

        /* Update the data model! */
        String nodeId = selectedNode.getId();
        appController.guiNodeRenamed(nodeId, newName);

        /* Update the drawing! */
        Rectangle newArea = selectedNode.getLastDrawingArea();
        updateDrawing(oldArea);
        updateDrawing(newArea);
    }

    @Override
    public void updateDrawing(Rectangle area) {
        currentDrawPanel.updateDrawing(area);
    }

    /* Remove methods for elements */

    @Override
    public void removeSelectedGuiElements() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.removeSelectedGuiElements");
        }

        if (currentModel == null)
            return;

        /*
         * Task: Remove all selected elements *and* all adjacent arcs!
         */

        /* Get all selected elements and store their drawing area. */
        List<IGuiElement> toBeRemoved = currentModel.getSelectedElements();
        List<Rectangle> drawingAreas = getDrawingAreas(toBeRemoved);

        /*
         * Separate ID list to avoid ConcurrentModificationException when
         * removeElement() removes the element from the list!
         */
        List<String> toBeRemoved_IDs = new ArrayList<String>();

        /*
         * Additional ID list for all arcs because many arcs will be removed
         * automatically when they were adjacent to removed nodes.
         * 
         * This means that a later attempt to remove these arcs from the model
         * will cause an error. And we can reasonably ignore this error if it
         * occurs on an arc ID.
         */
        List<String> arcIDs = new ArrayList<String>();

        for (IGuiElement element : toBeRemoved) {
            toBeRemoved_IDs.add(element.getId());

            /* Add only arc IDs to the second list. */
            if (element instanceof IGuiArc)
                arcIDs.add(element.getId());
        }

        /* Remove all elements. */
        for (String id : toBeRemoved_IDs) {
            if (debug) {
                System.out.println("GuiModelController.removeSelectedGuiElements: Remove id: " + id);
            }

            try {
                currentModel.removeElement(id);
                // TODO Comment out the following command after testing!
                // if (debug)
                // debugRepaintImmediately();

                currentModel.setModified(true);

                /*
                 * Inform the application controller to remove this element from
                 * the data model!
                 */
                appController.guiElementRemoved(id);

            } catch (PNNoSuchElementException e) {
                String message = "";
                if (arcIDs.contains(id)) {
                    /* Ignore this error on arc IDs */
                    if (debug) {
                        message = i18n.getMessage("infoArcDontExistAnymore");
                        message = message.replace("%id%", id);

                        System.out.println(message);
                    }

                } else {
                    /* Serious error on other types of elements! */
                    message = i18n.getMessage("errMissingIdInModel");
                    message = message.replace("%id%", id);

                    String modelName = currentModel.getModelName();
                    message = message.replace("%modelName%", modelName);

                    System.err.println(message);
                    return;
                }
            }

            // currentModel.setModified(true);

            // /*
            // * Inform the application controller to remove this element from
            // the
            // * data model!
            // */
            // appController.guiElementRemoved(id);
        }

        /* Repaint the areas. */
        updateDrawing(drawingAreas);

        /* Update Actions (buttons) in case we removed a selected element. */
        List<IGuiElement> emptyList = new LinkedList<IGuiElement>();
        appController.enableActionsForSelectedElements(emptyList);

        String message = i18n.getMessage("infoElementsDeleted");
        message = message.replace("%number%", Integer.toString(toBeRemoved_IDs.size()));
        message = message.replace("%IDs%", toBeRemoved_IDs.toString());
        setInfo_Status(message, EStatusMessageLevel.INFO);
    }

    @Override
    public void removeGuiArc(String arcId) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.removeGuiArc", arcId);
        }

        /* Store the drawing area for repainting. */
        IGuiElement element;
        try {
            element = currentModel.getElementById(arcId);
        } catch (PNNoSuchElementException e) {
            System.err.println("Arc to remove not found!");
            return;
        }
        Rectangle rect = element.getLastDrawingArea();

        try {
            currentModel.removeElement(arcId);
        } catch (PNNoSuchElementException e) {
            String message = i18n.getMessage("errMissingIdInModel");
            message = message.replace("%id%", arcId);
            String modelName = currentModel.getModelName();
            message = message.replace("%modelName%", modelName);
            System.err.println(message);
            return;
        }

        currentModel.setModified(true);

        /* Update the drawing. */
        updateDrawing(rect);

        /* Update Actions (buttons) in case we removed a selected element. */
        List<IGuiElement> selected = currentModel.getSelectedElements();
        appController.enableActionsForSelectedElements(selected);
    }

    @Override
    public void clearCurrentGuiModel() {
        currentModel.clear();
        currentModel.setModified(true);

        /* Update the drawing. */
        updateDrawing();

        /* Update Actions (buttons) in case we removed a selected element. */
        List<IGuiElement> emptyList = new LinkedList<IGuiElement>();
        appController.enableActionsForSelectedElements(emptyList);
    }

    /* Mouse and selection events */

    @Override
    public void mouseClick_Occurred(Point mousePressedLocation, MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.mouseClick_Occurred", mousePressedLocation, e);
        }

        if (mousePressedLocation == null) {
            System.err.println("mousePressedLocation == null");
            appController.enableZValueActions(null);
            return;
        }

        IGuiElement mousePressedElement;
        mousePressedElement = getSelectableElementAtLocation(mousePressedLocation);

        // appController.updateZValueActions(mousePressedElement);

        if (mousePressedElement == null) {
            resetSelection();
            appController.enableZValueActions(null);
            return;
        }

        IGuiElement mouseReleasedElement;
        mouseReleasedElement = getSelectableElementAtLocation(e.getPoint());

        if (mousePressedElement != mouseReleasedElement) {
            /* Reject this mouseClicked event as unintended! */
            return;
        }

        selectOneElement(mousePressedElement);
    }

    /**
     * Selects this (one) element alone.
     * 
     * @param mousePressedElement
     *            The element at which the mouse event has occurred
     */
    private void selectOneElement(IGuiElement mousePressedElement) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.selectOneElement", mousePressedElement);
        }

        if (currentModel == null) {
            System.err.println("GuiModelController.selectOneElement(), currentModel == null!");
            return;
        }

        IGuiElement element = mousePressedElement;

        Rectangle oldArea = element.getLastDrawingArea();

        currentModel.selectSingleElement(element);

        Rectangle newArea = element.getLastDrawingArea();

        if (debug) {
            System.out.println("GuiModelController, Single element selected: " + element.getId());
            System.out.println("updateDrawing(" + oldArea + ")");
            System.out.println("updateDrawing(" + newArea + ")");
        }
        updateDrawing(oldArea);
        updateDrawing(newArea);

        /* Update the Actions (buttons) */
        List<IGuiElement> selected = currentModel.getSelectedElements();
        appController.enableActionsForSelectedElements(selected);
    }

    @Override
    public void mouseClick_WithCtrl_Occurred(Point mousePressedLocation, MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.mouseClick_WithCtrl_Occurred", mousePressedLocation,
                    e);
        }

        if (mousePressedLocation == null) {
            System.err.println("mousePressedLocation == null");
            appController.enableZValueActions(null);
            return;
        }

        IGuiElement mousePressedElement;
        mousePressedElement = getSelectableElementAtLocation(mousePressedLocation);

        // appController.updateZValueActions(mousePressedElement);

        if (mousePressedElement == null) {
            appController.enableZValueActions(null);
            return;
        }

        IGuiElement mouseReleasedElement;
        mouseReleasedElement = getSelectableElementAtLocation(e.getPoint());

        if (mousePressedElement != mouseReleasedElement) {
            /* Reject this mouseClicked event as unintended! */
            return;
        }

        toggleOneElementsSelection(mousePressedElement);
    }

    /**
     * Toggles the selection of this (one) element.
     * 
     * @param mousePressedElement
     *            The element at which the mouse event has occurred
     */
    private void toggleOneElementsSelection(IGuiElement mousePressedElement) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.toggleOneElementsSelection", mousePressedElement);
        }

        if (currentModel == null) {
            System.err.println("GuiModelController.toggleOneElementsSelection(), currentModel == null!");
            return;
        }

        IGuiElement element = mousePressedElement;

        Rectangle oldArea = element.getLastDrawingArea();

        currentModel.toggleSelection(element);

        Rectangle newArea = element.getLastDrawingArea();

        if (debug) {
            System.out.println("GuiModelController, Selection toggled on element: " + element.getId());
            System.out.println("updateDrawing(" + oldArea + ")");
            System.out.println("updateDrawing(" + newArea + ")");
        }
        updateDrawing(oldArea);
        updateDrawing(newArea);

        /* Update the Actions (buttons) */
        List<IGuiElement> selected = currentModel.getSelectedElements();
        appController.enableActionsForSelectedElements(selected);
    }

    @Override
    public void selectAllGuiElements() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.selectAllGuiElements");
        }

        if (currentModel == null) {
            System.err.println("GuiModelController.selectAllGuiElements(), currentModel == null!");
            return;
        }

        /*
         * Task: Select all elements.
         */

        List<IGuiElement> toBeSelected = currentModel.getElements();

        String message = "";
        for (IGuiElement element : toBeSelected) {
            if (!element.isSelected()) {
                currentModel.addToSelection(element);

                message = "GuiModelController, Added to selection: element " + element.getId();
                ConsoleLogger.logIfDebug(debug, message);
            }
        }

        /* Repaint (everything) */
        updateDrawing();

        /* Update the Actions (buttons) */
        List<IGuiElement> selected = currentModel.getSelectedElements();
        appController.enableActionsForSelectedElements(selected);

        message = i18n.getMessage("infoElementsSelected");
        message = message.replace("%number%", Integer.toString(toBeSelected.size()));
        setInfo_Status(message, EStatusMessageLevel.INFO);
    }

    @Override
    public void mouseDragged(int distance_x, int distance_y) {
        /*
         * Task: Move only the selected nodes and update the drawing.
         */

        if (currentModel == null) {
            System.err.println("GuiModelController.mouseDragged(), currentModel == null!");
            return;
        }

        /* Get all selected elements. */
        List<IGuiElement> selectedElements = currentModel.getSelectedElements();
        if (selectedElements.size() == 0)
            return;
        if (debug) {
            System.out.println(selectedElements.size() + " selected element(s)");
        }

        /* Limit the elements to the nodes. */
        List<IGuiNode> selectedNodes = new LinkedList<IGuiNode>();

        for (IGuiElement element : selectedElements) {
            if (element instanceof IGuiNode) {
                IGuiNode node = (IGuiNode) element;
                selectedNodes.add(node);
            }
        }
        if (debug) {
            System.out.println(selectedNodes.size() + " selected nodes(s)");
        }

        /*
         * Check left and top border: No change if at least one of the selected
         * nodes would cross the left or top border. (We check both directions
         * separately to be able to move along the borders.)
         */
        boolean dragToLeftAllowed = true;
        for (IGuiNode node : selectedNodes) {
            int nodeLeftX = node.getTotalLeftX();
            dragToLeftAllowed = (nodeLeftX > 0);
            if (!dragToLeftAllowed)
                break;
        }

        boolean dragToTopAllowed = true;
        for (IGuiNode node : selectedNodes) {
            int nodeTopY = node.getTotalTopY();
            dragToTopAllowed = (nodeTopY > 0);
            if (!dragToTopAllowed)
                break;
        }

        if (!dragToLeftAllowed && (distance_x < 0))
            distance_x = 0;
        if (!dragToTopAllowed && (distance_y < 0))
            distance_y = 0;

        /* Get the old drawing areas for repainting. */
        List<Rectangle> oldDrawingAreas = getDrawingAreas(selectedNodes);

        /*
         * Shift the position of the selected nodes and store the new drawing
         * areas for repainting.
         */
        for (IGuiNode node : selectedNodes) {
            Point newPosition = node.getPosition();
            newPosition.translate(distance_x, distance_y);

            node.setPosition(newPosition);

            /*
             * Store this modified node in the local field to be able to inform
             * the data model controller when the dragging has finished!
             */
            movedNodes.add(node);
        }

        List<Rectangle> newDrawingAreas = getDrawingAreas(selectedNodes);

        /*
         * Get all adjacent arcs for these nodes and store their drawing area to
         * update them explicitly.
         */
        List<IGuiArc> adjacentArcs = getAdjacentArcs(selectedNodes);
        List<Rectangle> arcAreas = getDrawingAreas(adjacentArcs);

        /* Update the drawing. */
        updateDrawing(oldDrawingAreas);
        updateDrawing(newDrawingAreas);
        updateDrawing(arcAreas);
    }

    @Override
    public void updateDataNodePositions() {
        if (currentModel == null)
            return;

        /* Only selected nodes can be dragged. */
        List<IGuiElement> selectedElements = currentModel.getSelectedElements();

        /*
         * Update all nodes. We might "update" many nodes that don't need it,
         * but this should be faster than updating all dragged nodes again and
         * again during the dragging operation.
         */
        for (IGuiElement element : selectedElements) {
            if (element instanceof IGuiNode) {
                IGuiNode node = (IGuiNode) element;

                String nodeId = node.getId();
                Point newPosition = node.getPosition();
                appController.guiNodeDragged(nodeId, newPosition);
            }
        }
    }

    /* Keyboard events */

    // @Override
    // public void keyEvent_Escape_Occurred() {
    // resetSelection();
    // }

    /**
     * De-selects all {@link IGuiElement} and updates the drawing.
     */
    private void resetSelection() {
        if (currentModel == null)
            return;

        List<IGuiElement> selected = currentModel.getSelectedElements();
        if (selected.size() == 0)
            return;

        List<Rectangle> drawingAreas = getDrawingAreas(selected);

        currentModel.clearSelection();

        if (debug) {
            System.out.println("Selection cleared.");
            System.out.println("updateDrawing(" + drawingAreas + ")");
        }

        updateDrawing(drawingAreas);
    }

    @Override
    public void keyEvent_Delete_Occurred() {
        removeSelectedGuiElements();

        if (debug) {
            System.out.println("GuiModelController: KeyEvent Delete occurred:");
            System.out.println("Selected elements removed.");
        }
    }

    @Override
    public void keyEvent_F2_Occurred() {
        renameSelectedGuiElement();

        if (debug) {
            System.out.println("GuiModelController: KeyEvent F2 occurred:");
            System.out.println("Selected element renamed.");
        }
    }

    /* ZValue Actions */

    @Override
    public int getCurrentMinZValue() {
        return currentModel.getMinZValue();
    }

    @Override
    public int getCurrentMaxZValue() {
        return currentModel.getMaxZValue();
    }

    /**
     * Returns the single {@link IGuiElement} for a ZValue Action (e.g.
     * "moveToForeground").<BR>
     * <BR>
     * This is:<BR>
     * 1. If a popup menu is active: the element at the popup menu location<BR>
     * 2. If a single element is selected: this selected element<BR>
     * 3. Otherwise: null
     * 
     * @return A {@link IGuiElement}
     * @throws PNElementException
     *             if the element was not found or is ambiguous
     */
    private IGuiElement getSingleElementForZValueAction() throws PNElementException {
        IGuiElement element = null;
        String message = null;

        /* Check variant 1: popup menu location? */
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation != null) {
            /* Call via popup menu: we use its location */
            element = getElementAtLocation(popupMenuLocation);

            if (element == null)
                message = i18n.getMessage("warningNoElementAtPopupMenu");

        } else {
            /* Check variant 2: single selected element? */
            try {
                element = getSingleSelectedElement();
            } catch (PNElementException e) {
                message = e.getMessage();
            }
        }

        if (message != null) {
            /*
             * This should not happen anymore if enabling the z value Actions
             * works properly!
             */
            System.err.println(message);
            System.err.println("Check enabling the z value Actions!");

            setInfo_Status(message, EStatusMessageLevel.INFO);
            throw new PNElementException(message);
        }

        return element;
    }

    /**
     * Returns the neighbor in z direction compared to the specified
     * {@link IGuiElement}.
     * 
     * @param currElem
     *            The specified {@link IGuiElement}
     * @param upwards
     *            true: return the neighbor upwards; false: return the neighbor
     *            downwards
     * @return A {@link IGuiElement}
     */
    private IGuiElement getZValueSwapElement(IGuiElement currElem, boolean upwards) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.getSwapElement", currElem, upwards);
        }

        if (currElem == null)   // (Invoking method should have checked this
            return null;        // already.)

        /* Is the current element already on the highest/lowest layer? */
        int currZ = currElem.getZValue();
        int maxZ = currentModel.getMaxZValue();
        int minZ = currentModel.getMinZValue();

        if (upwards && (currZ == maxZ))
            return null;
        if (!upwards && (currZ == minZ))
            return null;

        /**
         * We have to check 2 conditions to find the neighbor element to swap
         * with, depending on the direction:<BR>
         * 1. Z value has to be higher/lower than the current z value.<BR>
         * 2. But as little as possible.
         */
        List<IGuiElement> elements = currentModel.getElements();
        IGuiElement swapElement = null;

        /* We need a "safe" limit value for z value comparison. */
        int swapZ = 0;
        if (upwards)
            swapZ = maxZ + 1;
        if (!upwards)
            swapZ = minZ - 1;

        /* Check all elements to find the next element in the desired direction. */
        for (IGuiElement next : elements) {
            int nextZ = next.getZValue();
            if (upwards && (nextZ > currZ) || !upwards && (nextZ < currZ)) {
                /* We have a candidate. */
                IGuiElement candidate = next;
                int candidateZ = nextZ;
                if (upwards && (candidateZ < swapZ) || !upwards && (candidateZ > swapZ)) {
                    /* Candidate is "closer" than the current swap candidate. */
                    swapElement = candidate;
                    swapZ = candidateZ;
                }
            }
        }

        return swapElement;
    }

    @Override
    public void moveElementToForeground() {
        /* Get the element we want to move to another z layer. */
        IGuiElement moveElement = null;
        try {
            moveElement = getSingleElementForZValueAction();
        } catch (PNElementException e) {
            return;
        }

        /* OK, we have exactly 1 selected element. */
        try {
            moveToForeground(moveElement);
        } catch (PNNoSuchElementException e) {
            System.err.println(e.getMessage());
            return;
        }
    }

    /**
     * Assigns the specified {@link IGuiElement} to the foreground. (1 level
     * higher in z direction than the current top element)
     * 
     * @param element
     *            The {@link IGuiElement} to be set as the new foreground
     *            element
     * @throws PNNoSuchElementException
     *             if element is null
     */
    private void moveToForeground(IGuiElement element) throws PNNoSuchElementException {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.moveToForeground", element.getId());
        }

        if (element == null)
            throw new PNNoSuchElementException("Element must not be null.");

        int currZValue = element.getZValue();
        int currMax = currentModel.getMaxZValue();
        if (currZValue == currMax)
            return;

        int newZValue = currentModel.getIncrMaxZ();
        ConsoleLogger.logIfDebug(debug, "element.setZValue(" + newZValue + ")");
        element.setZValue(newZValue);

        /* Let the model resort the List of elements. */
        currentModel.sortElements();

        // /* Repaint this element and (if necessary) adjacent arcs. */
        // List<IGuiElement> toBeRepainted = new LinkedList<IGuiElement>();
        // toBeRepainted.add(element);
        //
        // if (element instanceof IGuiNode) {
        // IGuiNode node = (IGuiNode) element;
        // List<IGuiArc> arcs = getAdjacentArcs(node);
        // toBeRepainted.addAll(arcs);
        // }

        /*
         * Just repaint everything since there might be many adjacent arcs and
         * such an action is triggered by the user and therefore not too
         * frequently.
         */
        updateDrawing();

        /* Update the Actions (buttons) */
        enableZValueActionsDependingOnSelection();
    }

    @Override
    public void moveElementToBackground() {
        /* Get the element we want to move to another z layer. */
        IGuiElement moveElement = null;
        try {
            moveElement = getSingleElementForZValueAction();
        } catch (PNElementException e) {
            return;
        }

        /* OK, we have exactly 1 selected element. */
        try {
            moveToBackground(moveElement);
        } catch (PNNoSuchElementException e) {
            System.err.println(e.getMessage());
            return;
        }
    }

    /**
     * Assigns the specified {@link IGuiElement} to the background. (1 level
     * lower in z direction than the current bottom element)
     * 
     * @param element
     *            The {@link IGuiElement} to be set as the new background
     *            element
     * @throws PNNoSuchElementException
     *             if element is null
     */
    private void moveToBackground(IGuiElement element) throws PNNoSuchElementException {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.moveToBackground", element.getId());
        }

        if (element == null)
            throw new PNNoSuchElementException("Element must not be null.");

        int currZValue = element.getZValue();
        int currMin = currentModel.getMinZValue();
        if (currZValue == currMin)
            return;

        int newZValue = currentModel.getDecrMinZ();
        ConsoleLogger.logIfDebug(debug, "element.setZValue(" + newZValue + ")");
        element.setZValue(newZValue);

        /* Let the model resort the List of elements. */
        currentModel.sortElements();

        /*
         * Just repaint everything since there might be many adjacent arcs and
         * such an action is triggered by the user and therefore not too
         * frequently.
         */
        updateDrawing();

        /* Update the Actions (buttons) */
        enableZValueActionsDependingOnSelection();
    }

    @Override
    public void moveElementOneLayerUp() {
        /* Get the element we want to move to another z layer. */
        IGuiElement moveElement = null;
        try {
            moveElement = getSingleElementForZValueAction();
        } catch (PNElementException e) {
            return;
        }

        /* OK, we have exactly 1 selected element. */
        try {
            moveOneLayerUp(moveElement);
        } catch (PNNoSuchElementException e) {
            System.err.println(e.getMessage());
            return;
        }
    }

    /**
     * Assigns the specified {@link IGuiElement} to the next higher layer.
     * 
     * @param element
     *            The {@link IGuiElement} to be moved one layer up
     * @throws PNNoSuchElementException
     *             if element is null
     */
    private void moveOneLayerUp(IGuiElement element) throws PNNoSuchElementException {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.moveOneLayerUp", element);
        }

        if (element == null)
            throw new PNNoSuchElementException("Element must not be null.");

        /* Get the element to swap with. */
        boolean upwards = true;
        IGuiElement swapElement = getZValueSwapElement(element, upwards);
        if (swapElement == null) {
            String message = "No swap element found!";
            System.err.println(message);
            return;
        }

        /* Swap the element with the swap element. */
        int swapZ = swapElement.getZValue();
        int currZ = element.getZValue();

        ConsoleLogger.logIfDebug(debug, "element.setZValue(" + swapZ + ")");
        element.setZValue(swapZ);
        ConsoleLogger.logIfDebug(debug, "swap.setZValue(" + currZ + ")");
        swapElement.setZValue(currZ);

        /* Let the model resort the List of elements. */
        currentModel.sortElements();

        /*
         * Just repaint everything since there might be many adjacent arcs and
         * such an action is triggered by the user and therefore not too
         * frequently.
         */
        updateDrawing();

        /* Update the Actions (buttons) */
        enableZValueActionsDependingOnSelection();
    }

    @Override
    public void moveElementOneLayerDown() {
        /* Get the element we want to move to another z layer. */
        IGuiElement moveElement = null;
        try {
            moveElement = getSingleElementForZValueAction();
        } catch (PNElementException e) {
            return;
        }

        /* OK, we have exactly 1 selected element. */
        try {
            moveOneLayerDown(moveElement);
        } catch (PNNoSuchElementException e) {
            System.err.println(e.getMessage());
            return;
        }
    }

    /**
     * Assigns the specified {@link IGuiElement} to the next lower layer.
     * 
     * @param element
     *            The {@link IGuiElement} to be moved one layer down
     * @throws PNNoSuchElementException
     *             if element is null
     */
    private void moveOneLayerDown(IGuiElement element) throws PNNoSuchElementException {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.moveOneLayerUp", element);
        }

        if (element == null)
            throw new PNNoSuchElementException("Element must not be null.");

        /* Get the element to swap with. */
        boolean upwards = false;
        IGuiElement swapElement = getZValueSwapElement(element, upwards);
        if (swapElement == null) {
            String message = "No swap element found!";
            System.err.println(message);
            return;
        }

        /* Swap the element with the swap element. */
        int swapZ = swapElement.getZValue();
        int currZ = element.getZValue();

        ConsoleLogger.logIfDebug(debug, "element.setZValue(" + swapZ + ")");
        element.setZValue(swapZ);
        ConsoleLogger.logIfDebug(debug, "swap.setZValue(" + currZ + ")");
        swapElement.setZValue(currZ);

        /* Let the model resort the List of elements. */
        currentModel.sortElements();

        /*
         * Just repaint everything since there might be many adjacent arcs and
         * such an action is triggered by the user and therefore not too
         * frequently.
         */
        updateDrawing();

        /* Update the Actions (buttons) */
        enableZValueActionsDependingOnSelection();
    }

    // /**
    // * Prints the current Z values of all {@link IGuiElement} in the specified
    // * {@link IGuiModel}.
    // *
    // * @param model
    // * The specified {@link IGuiModel}.
    // */
    // private void debugPrintZValues(IGuiModel model) {
    // for (IGuiElement element : model.getElements()) {
    // System.out.println(element.getZValue() + " " + element.toString());
    // }
    // }

    /* Change shape size */

    @Override
    public void changeShapeSize(int size) {
        if (size < GuiModelController.MIN_SHAPE_SIZE) {
            System.err.println("Shape size too small: " + size);
            return;
        }

        /* Change the static attributes. */
        GuiNode.changeShapeSize(size);
        GuiArc.changeShapeSize(size);

        /*
         * Update the whole drawing. (All other draw panels should be updated
         * when switching the tab.)
         */
        if (currentDrawPanel != null) {
            Dimension area = currentDrawPanel.getPreferredSize();
            Rectangle rect = new Rectangle(area);
            updateDrawing(rect);
        }
    }

    /* Private helper methods */

    /**
     * Invokes updateDrawing(Rectangle area) for all specified areas.
     * 
     * @param areas
     *            A {@link List} of {@link Rectangle}
     */
    private void updateDrawing(List<Rectangle> areas) {
        for (Rectangle area : areas) {
            updateDrawing(area);
        }
    }

    /**
     * Invokes updateDrawing(Rectangle area) for no area (everything).
     */
    private void updateDrawing() {
        Rectangle area = null;
        updateDrawing(area);
    }

    /**
     * Forces <B>immediate</B> repaint of the current {@link IDrawPanel} for
     * debugging purposes.<BR>
     * <BR>
     * <B>Attention:</B> This needs more CPU time! Comment out the invoking line
     * after testing!
     */
    @SuppressWarnings("unused")
    private void debugRepaintImmediately() {
        if (currentDrawPanel == null) {
            String message = "GuiModelController.debugRepaintImmediately(): currentDrawPanel == null";
            ConsoleLogger.logIfDebug(debug, message);
            return;
        }

        /* Get the whole area of the draw panel. */
        Dimension dim = currentDrawPanel.getPreferredSize();
        int width = dim.width;
        int height = dim.height;
        Rectangle r = new Rectangle(0, 0, width, height);

        /* Repaint it now. */
        ((JComponent) currentDrawPanel).paintImmediately(r);
    }

    /**
     * Returns the {@link IGuiElement} at the specified Point. (Returns the one
     * with the highest z-value if there is more than 1 at this location.)
     * 
     * @param p
     *            The specified Point
     * @return The topmost {@link IGuiElement} at the specified point; null if
     *         none exists
     */
    private IGuiElement getElementAtLocation(Point p) {
        if (currentModel == null)
            return null;

        java.util.List<IGuiElement> elements = currentModel.getElements();
        IGuiElement foundElement = null;

        for (IGuiElement element : elements) {
            boolean contains = element.contains(p);
            if (contains) {
                foundElement = element;
                /*
                 * Don't break on the first found element because we have to
                 * find the element with the highest z value and this will be
                 * more at the "end" of the list.
                 */
                // break;
            }
        }

        if (foundElement == null) {
            if (debug) {
                System.out.println("No element at this Point!");
            }
            return null;
        }

        return foundElement;
    }

    /**
     * Returns the {@link IGuiTransition} (not other elements) at the specified
     * Point. Returns the one with the highest z-value if there is more than 1
     * at this location.
     * 
     * @param p
     *            The specified Point
     * @return The {@link IGuiTransition}; null if none exists
     */
    private IGuiTransition getTransitionAtLocation(Point p) {
        IGuiElement foundElement = getElementAtLocation(p);
        if (foundElement == null)
            return null;

        /*
         * Note: getElementAtLocation() should have returned the topmost
         * element.
         */
        IGuiTransition foundTransition = null;
        if (foundElement instanceof IGuiTransition) {
            foundTransition = (IGuiTransition) foundElement;
        }

        if (foundTransition == null) {
            if (debug) {
                System.out.println("No transition at this Point!");
            }
            return null;
        }

        return foundTransition;
    }

    /**
     * Checks if exactly 1 element is selected.
     * 
     * @return The selected element
     * @throws PNElementException
     *             if no or too many elements are selected
     */
    private IGuiElement getSingleSelectedElement() throws PNElementException {
        /* Check if exactly 1 element is selected. */
        java.util.List<IGuiElement> selected = currentModel.getSelectedElements();
        IGuiElement selectedElement = null;
        int count = 0;

        for (IGuiElement element : selected) {
            selectedElement = element;
            count++;
        }

        if (selectedElement == null) {
            String warning = i18n.getMessage("warningNoSingleSelectedElement");
            String explanation = i18n.getMessage("infoNoElementSelected");
            String message = warning + " (" + explanation + ")";
            throw new PNElementException(message);
        }

        if (count > 1) {
            String warning = i18n.getMessage("warningNoSingleSelectedElement");
            String explanation = i18n.getMessage("warningTooManyElementSelected");
            String message = warning + " (" + explanation + ")";
            throw new PNElementException(message);
        }

        return selectedElement;
    }

    /**
     * Checks if exactly 1 node is selected.
     * 
     * @return The selected node
     * @throws PNElementException
     *             if no or too many nodes are selected
     */
    private IGuiNode getSingleSelectedNode() throws PNElementException {
        if (currentModel == null) {
            String explanation = i18n.getMessage("warningNoCurrentModel");
            throw new PNElementException(explanation);
        }

        /* Check if exactly 1 node is selected. */
        java.util.List<IGuiElement> selected = currentModel.getSelectedElements();
        IGuiNode selectedNode = null;
        int count = 0;

        for (IGuiElement element : selected) {
            if (element instanceof IGuiNode) {
                selectedNode = (IGuiNode) element;
                count++;
            }
        }

        if (selectedNode == null) {
            String explanation = i18n.getMessage("infoNoNodeSelected");
            throw new PNElementException(explanation);
        }

        if (count > 1) {
            String explanation = i18n.getMessage("warningTooManyElementSelected");
            throw new PNElementException(explanation);
        }

        return selectedNode;
    }

    /**
     * Returns a {@link List} with the drawing areas of the specified GUI
     * elements.
     * 
     * @param elements
     *            List of {@link IGuiElement}
     * @return The List of drawing areas; empty if elements is empty
     */
    private List<Rectangle> getDrawingAreas(List<? extends IGuiElement> elements) {
        List<Rectangle> areas = new LinkedList<Rectangle>();

        Rectangle elementArea;
        for (IGuiElement element : elements) {
            elementArea = element.getLastDrawingArea();
            if (elementArea != null)
                areas.add(elementArea);
        }

        return areas;
    }

    /**
     * Returns a list of all arcs that are adjacent to the nodes in the
     * specified list.
     * 
     * @param nodes
     *            The list of nodes
     * @return A {@link List} of {@link IGuiArc} of all adjacent arcs
     */
    private List<IGuiArc> getAdjacentArcs(List<IGuiNode> nodes) {
        List<IGuiElement> elements = currentModel.getElements();
        List<IGuiArc> adjacentArcs = new LinkedList<IGuiArc>();

        for (IGuiElement element : elements) {
            if (element instanceof IGuiArc) {
                IGuiArc arc = (IGuiArc) element;
                if (isAdjacentArc(arc, nodes)) {
                    adjacentArcs.add(arc);
                }
            }
        }

        return adjacentArcs;
    }

    // /**
    // * Returns a {@link List} of all {@link IGuiArc} that are adjacent to the
    // * specified {@link IGuiNode}.
    // *
    // * @param node
    // * The specified {@link IGuiNode}
    // * @return A {@link List} of {@link IGuiArc} of all adjacent arcs
    // */
    // private List<IGuiArc> getAdjacentArcs(IGuiNode node) {
    // List<IGuiElement> elements = currentModel.getElements();
    // List<IGuiArc> adjacentArcs = new LinkedList<IGuiArc>();
    //
    // for (IGuiElement element : elements) {
    // if (element instanceof IGuiArc) {
    // IGuiArc arc = (IGuiArc) element;
    // if (isAdjacentArc(arc, node)) {
    // adjacentArcs.add(arc);
    // }
    // }
    // }
    //
    // return adjacentArcs;
    // }

    /**
     * Checks if the arc is adjacent to at least one of the nodes in the
     * specified list.
     * 
     * @param arc
     *            The specific arc
     * @param nodes
     *            The list of nodes
     * @return True if the arc is adjacent to at least one of the nodes in the
     *         list; otherwise false
     */
    private boolean isAdjacentArc(IGuiArc arc, List<IGuiNode> nodes) {
        for (IGuiNode node : nodes) {
            boolean isAdjacent = isAdjacentArc(arc, node);
            if (isAdjacent)
                return true;
        }
        return false;
    }

    /**
     * Checks if the specified arc is adjacent to the specified node.
     * 
     * @param arc
     *            The specific arc
     * @param node
     *            The specified node
     * @return True if the arc is adjacent to the specified node; otherwise
     *         false
     */
    private boolean isAdjacentArc(IGuiArc arc, IGuiNode node) {
        /* Node is predecessor of the arc? */
        try {
            if (arc.getPredElem() == node) { return true; }
        } catch (PNNoSuchElementException e) {
            System.err.println("Arc without predecessor! Arc id: " + arc.getId());
            e.printStackTrace();
        }

        /* Node is successor of the arc? */
        try {
            if (arc.getSuccElem() == node) { return true; }
        } catch (PNNoSuchElementException e) {
            System.err.println("Arc without succecessor! Arc id: " + arc.getId());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Asks the user for a new name for the specified old name.
     * 
     * @param oldName
     *            The old name of the
     * @return Null if the user canceled the input; otherwise the input String
     */
    private String askUserForNewName(String oldName) {
        String question = i18n.getMessage("questionNewName");

        /* Get the main frame to center the input dialog. */
        JFrame mainFrame = appController.getMainFrame();

        String inputValue = JOptionPane.showInputDialog(mainFrame, question, oldName);

        ConsoleLogger.logIfDebug(debug, "inputValue: " + inputValue);
        return inputValue;
    }

    /**
     * Invokes enableZValueActions() in {@link ApplicationController} with the
     * proper parameter depending on whether a (single) {@link IGuiElement} is
     * selected or not.
     */
    private void enableZValueActionsDependingOnSelection() {
        IGuiElement selectedElement;
        try {
            selectedElement = getSingleSelectedElement();
            appController.enableZValueActions(selectedElement);
        } catch (PNElementException e) {
            appController.enableZValueActions(null);
        }
    }

    /* Validation events */

    @Override
    public void resetAllGuiStartPlaces(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.resetAllGuiStartPlaces", modelName);
        }

        IGuiModel guiModel = getGuiModelForValidation(modelName);
        if (guiModel == null)
            return;

        for (IGuiElement guiElement : guiModel.getElements()) {
            if (guiElement instanceof IGuiPlace) {
                IGuiPlace guiPlace = (IGuiPlace) guiElement;
                guiPlace.setGuiStartPlace(false);
                guiPlace.setGuiStartPlaceCandidate(false);
            }
        }

        /* Repaint (everything) */
        updateDrawing();
    }

    @Override
    public void resetAllGuiEndPlaces(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.resetAllGuiEndPlaces", modelName);
        }

        IGuiModel guiModel = getGuiModelForValidation(modelName);
        if (guiModel == null)
            return;

        for (IGuiElement guiElement : guiModel.getElements()) {
            if (guiElement instanceof IGuiPlace) {
                IGuiPlace guiPlace = (IGuiPlace) guiElement;
                guiPlace.setGuiEndPlace(false);
                guiPlace.setGuiEndPlaceCandidate(false);
            }
        }

        /* Repaint (everything) */
        updateDrawing();
    }

    @Override
    public void setGuiStartPlace(String modelName, String placeId, boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.setGuiStartPlace", modelName, placeId, b);
        }

        IGuiModel guiModel = getGuiModelForValidation(modelName);
        if (guiModel == null)
            return;

        guiModel.setGuiStartPlace(placeId, b);

        /* Repaint */
        IGuiNode guiNode = null;
        try {
            guiNode = currentModel.getPlaceById(placeId);
        } catch (PNNoSuchElementException e) {
            String message = i18n.getMessage("errMissingIdInModel");
            message = message.replace("%id%", placeId);
            message = message.replace("%modelName%", modelName);
            System.err.println(message);
            return;
        }
        Rectangle rect = guiNode.getLastDrawingArea();
        updateDrawing(rect);
    }

    @Override
    public void setGuiStartPlaceCandidate(String modelName, String placeId, boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.setGuiStartPlaceCandidate", modelName, placeId, b);
        }

        IGuiModel guiModel = getGuiModelForValidation(modelName);
        if (guiModel == null)
            return;

        guiModel.setGuiStartPlaceCandidate(placeId, b);

        /* Repaint */
        IGuiNode guiNode = null;
        try {
            guiNode = currentModel.getPlaceById(placeId);
        } catch (PNNoSuchElementException e) {
            String message = i18n.getMessage("errMissingIdInModel");
            message = message.replace("%id%", placeId);
            message = message.replace("%modelName%", modelName);
            System.err.println(message);
            return;
        }
        Rectangle rect = guiNode.getLastDrawingArea();
        updateDrawing(rect);
    }

    @Override
    public void setGuiEndPlace(String modelName, String placeId, boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.setGuiEndPlace", modelName, placeId, b);
        }

        IGuiModel guiModel = getGuiModelForValidation(modelName);
        if (guiModel == null)
            return;

        guiModel.setGuiEndPlace(placeId, b);

        /* Repaint */
        IGuiNode guiNode = null;
        try {
            guiNode = currentModel.getPlaceById(placeId);
        } catch (PNNoSuchElementException e) {
            String message = i18n.getMessage("errMissingIdInModel");
            message = message.replace("%id%", placeId);
            message = message.replace("%modelName%", modelName);
            System.err.println(message);
            return;
        }
        Rectangle rect = guiNode.getLastDrawingArea();
        updateDrawing(rect);
    }

    @Override
    public void setGuiEndPlaceCandidate(String modelName, String placeId, boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.setGuiEndPlaceCandidate", modelName, placeId, b);
        }

        IGuiModel guiModel = getGuiModelForValidation(modelName);
        if (guiModel == null)
            return;

        guiModel.setGuiEndPlaceCandidate(placeId, b);

        /* Repaint */
        IGuiNode guiNode = null;
        try {
            guiNode = currentModel.getPlaceById(placeId);
        } catch (PNNoSuchElementException e) {
            String message = i18n.getMessage("errMissingIdInModel");
            message = message.replace("%id%", placeId);
            message = message.replace("%modelName%", modelName);
            System.err.println(message);
            return;
        }
        Rectangle rect = guiNode.getLastDrawingArea();
        updateDrawing(rect);
    }

    @Override
    public void highlightUnreachableGuiNode(String modelName, String nodeId, boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.highlightUnreachableGuiNode", modelName, nodeId, b);
        }

        IGuiModel guiModel = getGuiModelForValidation(modelName);
        if (guiModel == null)
            return;

        guiModel.highlightUnreachableGuiNode(nodeId, b);

        /* Repaint */
        IGuiNode guiNode = null;
        try {
            guiNode = currentModel.getNodeById(nodeId);
        } catch (PNNoSuchElementException e) {
            String message = i18n.getMessage("errMissingIdInModel");
            message = message.replace("%id%", nodeId);
            message = message.replace("%modelName%", modelName);
            System.err.println(message);
            return;
        }
        Rectangle rect = guiNode.getLastDrawingArea();
        updateDrawing(rect);
    }

    @Override
    public void removeAllGuiTokens(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.removeAllGuiTokens", modelName);
        }

        IGuiModel guiModel = getGuiModelForValidation(modelName);
        if (guiModel == null)
            return;

        /* Remove the token from all GUI places as well. */
        for (IGuiElement guiElement : guiModel.getElements()) {
            if (guiElement instanceof IGuiPlace) {
                IGuiPlace guiPlace = (IGuiPlace) guiElement;
                guiPlace.setTokens(EPlaceToken.ZERO);
            }
        }

        /* Repaint (everything) */
        updateDrawing();
    }

    @Override
    public void removeGuiToken(String modelName, List<String> placesWithToken) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.removeGuiToken", modelName, placesWithToken);
        }

        IGuiModel guiModel = getGuiModelForValidation(modelName);
        if (guiModel == null)
            return;

        /* List for the drawing areas we are going to change. */
        List<Rectangle> drawingAreas = new LinkedList<Rectangle>();

        /* Remove the token from all specified GUI places. */
        for (IGuiElement guiElement : guiModel.getElements()) {
            if (guiElement instanceof IGuiPlace) {
                IGuiPlace guiPlace = (IGuiPlace) guiElement;
                String guiPlaceId = guiPlace.getId();
                if (placesWithToken.contains(guiPlaceId)) {
                    guiPlace.setTokens(EPlaceToken.ZERO);

                    Rectangle rect = guiPlace.getLastDrawingArea();
                    drawingAreas.add(rect);
                }
            }
        }

        /* Repaint */
        updateDrawing(drawingAreas);
    }

    @Override
    public void addGuiToken(String modelName, List<String> placesWithToken) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.addGuiToken", modelName, placesWithToken);
        }

        IGuiModel guiModel = getGuiModelForValidation(modelName);
        if (guiModel == null)
            return;

        /* List for the drawing areas we are going to change. */
        List<Rectangle> drawingAreas = new LinkedList<Rectangle>();

        /* Add a token to all specified GUI places. */
        for (IGuiElement guiElement : guiModel.getElements()) {
            if (guiElement instanceof IGuiPlace) {
                IGuiPlace guiPlace = (IGuiPlace) guiElement;
                String guiPlaceId = guiPlace.getId();
                if (placesWithToken.contains(guiPlaceId)) {
                    guiPlace.setTokens(EPlaceToken.ONE);

                    Rectangle rect = guiPlace.getLastDrawingArea();
                    drawingAreas.add(rect);
                }
            }
        }

        /* Repaint */
        updateDrawing(drawingAreas);
    }

    @Override
    public void resetAllGuiTransitionsEnabledState(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.resetAllGuiTransitionsEnabledState", modelName);
        }

        IGuiModel guiModel = getGuiModelForValidation(modelName);
        if (guiModel == null)
            return;

        /* List for the drawing areas we are going to change. */
        List<Rectangle> drawingAreas = new LinkedList<Rectangle>();

        /* Reset "enabled" state on all GUI transitions. */
        for (IGuiElement guiElement : guiModel.getElements()) {
            if (guiElement instanceof IGuiTransition) {
                IGuiTransition guiTransition = (IGuiTransition) guiElement;
                guiTransition.setEnabled(false);

                Rectangle rect = guiTransition.getLastDrawingArea();
                drawingAreas.add(rect);
            }
        }

        /* Repaint */
        updateDrawing(drawingAreas);
    }

    /**
     * Returns the specified {@link IGuiModel} with suppressed error messages if
     * not found because this error can be expected in rare cases.<BR>
     * <BR>
     * This method is part of the validation process. And the validation
     * controller thread might slightly lagging behind in terms of the current
     * model (e.g. if the user has suddenly closed the current file during
     * validation).
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return The specified {@link IGuiModel} if found; otherwise null
     */
    private IGuiModel getGuiModelForValidation(String modelName) {
        IGuiModel guiModel = this.guiModels.get(modelName);
        if (guiModel == null) {
            String message = i18n.getMessage("errGuiModelNotFound");
            // System.err.println(message);

            /* -> The expected error */
            ConsoleLogger.logIfDebug(debug, message);
            return null;
        }

        return guiModel;
    }

    @Override
    public void resetAllGuiTransitionsSafeState(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.resetAllGuiTransitionsSafeState", modelName);
        }

        IGuiModel guiModel = getGuiModelForValidation(modelName);
        if (guiModel == null)
            return;

        /* List for the drawing areas we are going to change. */
        List<Rectangle> drawingAreas = new LinkedList<Rectangle>();

        /* Reset "safe" state on all GUI transitions. */
        for (IGuiElement guiElement : guiModel.getElements()) {
            if (guiElement instanceof IGuiTransition) {
                IGuiTransition guiTransition = (IGuiTransition) guiElement;
                guiTransition.setSafe(true); // Assume "safe" after reset

                Rectangle rect = guiTransition.getLastDrawingArea();
                drawingAreas.add(rect);
            }
        }

        /* Repaint */
        updateDrawing(drawingAreas);
    }

    @Override
    public void setGuiTransitionUnsafe(String modelName, String transitionId) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.setGuiTransitionUnsafe", modelName, transitionId);
        }

        IGuiModel guiModel = getGuiModelForValidation(modelName);
        if (guiModel == null)
            return;

        /* Rectangle for the drawing area we are going to change. */
        Rectangle drawingArea = new Rectangle();

        /* Set "enabled" state on the specified GUI transition. */
        for (IGuiElement guiElement : guiModel.getElements()) {
            if (guiElement instanceof IGuiTransition) {
                IGuiTransition guiTransition = (IGuiTransition) guiElement;
                String id = guiTransition.getId();
                if (id == transitionId) {
                    guiTransition.setSafe(false);
                    drawingArea = guiTransition.getLastDrawingArea();
                    break;
                }
            }
        }

        /* Repaint this transition */
        updateDrawing(drawingArea);
    }

    @Override
    public void setGuiTransitionEnabled(String modelName, String transitionId) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.setGuiTransitionEnabled", modelName, transitionId);
        }

        IGuiModel guiModel = getGuiModelForValidation(modelName);
        if (guiModel == null)
            return;

        /* Rectangle for the drawing area we are going to change. */
        Rectangle drawingArea = new Rectangle();

        /* Set "enabled" state on the specified GUI transition. */
        for (IGuiElement guiElement : guiModel.getElements()) {
            if (guiElement instanceof IGuiTransition) {
                IGuiTransition guiTransition = (IGuiTransition) guiElement;
                String id = guiTransition.getId();
                if (id == transitionId) {
                    guiTransition.setEnabled(true);
                    drawingArea = guiTransition.getLastDrawingArea();
                    break;
                }
            }
        }

        /* Repaint this transition */
        updateDrawing(drawingArea);
    }

    @Override
    public void fireGuiTransition() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.fireDataTransition");
        }

        /* Check if we have a location. */
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation == null) {
            System.err.println(
                    "GuiModelController.fireTransition(): Unable to fire transition: popup menu location unknown.");
            return;
        }

        /* Check if we have a transition at this location. */
        IGuiTransition transition = getTransitionAtLocation(popupMenuLocation);
        if (transition == null)
            return;

        /* Check if this transition is enabled. */
        if (!transition.isEnabled())
            return;

        /* Inform the application controller */
        String transitionId = transition.getId();
        appController.fireDataTransition(transitionId);
    }

    @Override
    public Rectangle getCurrentGuiModelStartPlaceArea() {
        if (currentModel == null)
            return null;

        Rectangle startPlaceArea = currentModel.getStartPlaceArea();
        return startPlaceArea;
    }

    @Override
    public List<Rectangle> getCurrentGuiModelEnabledTransitionsAreas() {
        if (currentModel == null)
            return null;

        List<Rectangle> enabledTransitionsAreas = currentModel.getEnabledTransitionsAreas();
        return enabledTransitionsAreas;
    }

}
