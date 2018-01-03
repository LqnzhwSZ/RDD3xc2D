package de.lambeck.pned.models.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.application.EStatusMessageLevel;
import de.lambeck.pned.elements.ENodeType;
import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.elements.gui.*;
import de.lambeck.pned.exceptions.PNElementException;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * Observes the state of the GUI.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class GuiModelController implements IGuiModelController {

    private static boolean debug = false;

    /** Minimum shape size for setter */
    private final static int MIN_SHAPE_SIZE = 20;

    /** Reference to the {@link ApplicationController} */
    protected ApplicationController appController = null;

    /** Reference to the manager for I18N strings */
    protected I18NManager i18n;

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
     * The source node for the new Arc to be added.
     * 
     * Note: Attribute {@link addingNewArc} should be true if this is != null.
     */
    private IGuiNode sourceNodeForNewArc = null;

    /**
     * The type of the source node for the new Arc to be added.
     */
    private ENodeType sourceForNewArcType = null;

    /**
     * Stores if this GUI model controller is in the state of "adding a new Arc"
     * to the current GUI model. Which means that he is waiting for the second
     * (target) node.
     */
    private boolean stateAddingNewArc = false;

    /**
     * Constructs a GUI model controller with references to the application
     * controller (the parent) and a manager for i18n strings.
     * 
     * @param controller
     *            The application controller
     * @param i18n
     *            The source object for I18N strings
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

    /*
     * Methods for implemented interfaces
     */

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

    /*
     * Methods for open files
     */

    @Override
    public void addGuiModel(String modelName, String displayName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.addGuiModel", modelName, displayName);
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

        this.currentDrawPanel = new DrawPanel(modelName, displayName, appController, this, currentModel, popupActions,
                i18n);
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
        /* The key for the Map of models. */
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
     * @param model
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

        this.currentModel = model;
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

    /*
     * Add elements
     */

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
        } catch (NoSuchElementException e) {
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
        } catch (NoSuchElementException e) {
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

    @Override
    public void setSourceNodeForNewArc() {
        /* Check if we have a location. */
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation == null) {
            System.err.println(
                    "GuiModelController.setSourceLocationForNewArc(): Unable to set source location for new arc: popup menu location unknown.");
            return;
        }

        /* Check if we have a node at this location. */
        IGuiNode node = getNodeAtLocation(popupMenuLocation);
        if (node == null)
            return;

        /* Store this node as source for the new Arc. */
        sourceNodeForNewArc = node;

        if (node instanceof GuiPlace) {
            sourceForNewArcType = ENodeType.PLACE;
        } else if (node instanceof GuiTransition) {
            sourceForNewArcType = ENodeType.TRANSITION;
        }

        /* Set my state. */
        setStateAddingNewArc();
    }

    /**
     * Sets the local state "AddingNewArc".
     * 
     * Note: This is a private method because it should only be invoked after
     * successfully finishing setSourceNodeForNewArc().
     */
    private void setStateAddingNewArc() {
        this.stateAddingNewArc = true;
    }

    @Override
    public boolean getStateAddingNewArc() {
        /*
         * Reset the state if the first of the two necessary nodes does not
         * exist anymore because the user has deleted this node in the meantime!
         * 
         * -> This should force the PopupMenuManager to enable the first of the
         * two Actions (NewArcFromHereAction)
         */
        if (this.sourceNodeForNewArc == null) {
            this.stateAddingNewArc = false;
            this.sourceForNewArcType = null;
        }

        return this.stateAddingNewArc;
    }

    @Override
    public ENodeType getSourceForNewArcType() {
        return this.sourceForNewArcType;
    }

    @Override
    public void setTargetNodeForNewArc() {
        /* Check the current state! */
        if (!getStateAddingNewArc())
            return;

        /* Check if we have a location. */
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation == null) {
            System.err.println(
                    "GuiModelController.setSourceLocationForNewArc(): Unable to set source location for new arc: popup menu location unknown.");
            return;
        }

        /* Check if we have a node at this location. */
        IGuiNode node = getNodeAtLocation(popupMenuLocation);
        if (node == null)
            return;

        /*
         * Create a unique ID to avoid any conflict with existing elements.
         */
        String uuid = UUID.randomUUID().toString();

        /* Get the IDs of source and target. */
        String sourceId = sourceNodeForNewArc.getId();
        String targetId = node.getId();

        /* Does such a arc already exist in this model? */
        if (arcAlreadyExist(sourceId, targetId)) {
            /* Leave the "add new arc" mode and return. */
            resetStateAddingNewArc();
            return;
        }

        /* Create the Arc and leave the "add new arc" mode.. */
        addArcToCurrentGuiModel(uuid, sourceId, targetId);
        resetStateAddingNewArc();

        /* Update the drawing. */
        IGuiElement element;
        try {
            element = currentModel.getElementById(uuid);
        } catch (NoSuchElementException e) {
            System.err.println("New arc not created!");
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
            // TODO let the application controller show all messages to have a
            // parent component?
            JOptionPane.showMessageDialog(null, errorMessage, title, JOptionPane.WARNING_MESSAGE);

            return true;
        }

        return false;
    }

    @Override
    public void resetStateAddingNewArc() {
        this.sourceNodeForNewArc = null;
        this.sourceForNewArcType = null;
        this.stateAddingNewArc = false;
    }

    /*
     * Modify methods for elements
     */

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
        // TODO Must the list be sorted again?
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

        // if (debug) {
        // if (foundElement == null)
        // System.out.println("GuiModelController.getSelectableElementAtLocation:
        // No selectable element at this Point!");
        // }

        return foundElement;
    }

    @Override
    public boolean isSelectableElement(IGuiElement element) {
        if (element instanceof ISelectable)
            return true;
        return false;
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
        String newName = askUserForNewName();
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

    /*
     * Remove methods for elements
     */

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

        /* Get all selected elements. */
        List<IGuiElement> toBeRemoved = currentModel.getSelectedElements();

        /* Store the drawing area for repainting. */
        List<Rectangle> drawingAreas = getDrawingAreas(toBeRemoved);

        /*
         * Add to a separate list first to avoid ConcurrentModificationException
         * when removeElement() removes the element from the list!
         */
        List<String> toBeRemoved_IDs = new ArrayList<String>();
        for (IGuiElement element : toBeRemoved) {
            toBeRemoved_IDs.add(element.getId());
        }

        /* Remove all elements. */
        for (String id : toBeRemoved_IDs) {
            if (debug) {
                System.out.println("GuiModelController.removeSelectedGuiElements: Remove id: " + id);
            }
            currentModel.removeElement(id);
            currentModel.setModified(true);

            /*
             * Inform the application controller to remove this element from the
             * data model!
             */
            appController.guiElementRemoved(id);
        }

        /* Repaint the areas. */
        updateDrawing(drawingAreas);
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
        } catch (NoSuchElementException e) {
            System.err.println("Arc to remove not found!");
            return;
        }
        Rectangle rect = element.getLastDrawingArea();

        currentModel.removeElement(arcId);
        currentModel.setModified(true);

        /* Update the drawing. */
        updateDrawing(rect);
    }

    @Override
    public void clearCurrentGuiModel() {
        currentModel.clear();
        currentModel.setModified(true);
    }

    /*
     * Mouse events
     */

    @Override
    public void mouseClick_Occurred(Point mousePressedLocation, MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.mouseClick_Occurred", mousePressedLocation, e);
        }

        if (mousePressedLocation == null) {
            System.err.println("mousePressedLocation == null");
            appController.updateZValueActions(null);
            return;
        }

        IGuiElement mousePressedElement;
        mousePressedElement = getSelectableElementAtLocation(mousePressedLocation);

        appController.updateZValueActions(mousePressedElement);

        if (mousePressedElement == null) {
            resetSelection();
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
    }

    @Override
    public void mouseClick_WithCtrl_Occurred(Point mousePressedLocation, MouseEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.mouseClick_WithCtrl_Occurred", mousePressedLocation,
                    e);
        }

        if (mousePressedLocation == null) {
            System.err.println("mousePressedLocation == null");
            appController.updateZValueActions(null);
            return;
        }

        IGuiElement mousePressedElement;
        mousePressedElement = getSelectableElementAtLocation(mousePressedLocation);

        appController.updateZValueActions(mousePressedElement);

        if (mousePressedElement == null) { return; }

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

        /*
         * Only selected nodes can be dragged (together with ALT).
         */
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

    /*
     * Keyboard events
     */

    @Override
    public void keyEvent_Escape_Occurred() {
        resetSelection();
    }

    /**
     * used by keyEvent_Escape_Occurred()
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
            System.out.println("GuiModelController: KeyEvent Escape occurred:");
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

    @Override
    public void selectElementAtPopupMenu() {
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();

        IGuiElement element = getElementAtLocation(popupMenuLocation);
        if (element == null)
            return;

        selectOneElement(element);
    }

    @Override
    public void moveElementToForeground() {
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation != null) {
            /*
             * Call via popup menu: there is a location
             */
            moveElementAtPopupMenuToForeground(popupMenuLocation);
            return;
        }

        /* Call via menu bar: We need one (single) selected element. */
        IGuiElement selectedElement;
        try {
            selectedElement = getSingleSelectedElement();
        } catch (PNElementException e) {
            String warning = i18n.getMessage("warningUnableToAssignToForeground");
            String explanation = e.getMessage();
            String message = warning + " (" + explanation + ")";

            System.out.println(message);
            setInfo_Status(message, EStatusMessageLevel.INFO);
            return;
        }

        /* OK, we have exactly 1 element. */
        moveToForeground(selectedElement);

        /* Update the Actions (buttons) */
        updateZValueActionsDependingOnSelection();
    }

    /**
     * Used by moveElementToForeground().
     * 
     * @param popupMenuLocation
     *            The popup menu location on the current draw panel
     */
    private void moveElementAtPopupMenuToForeground(Point popupMenuLocation) {
        IGuiElement element = getElementAtLocation(popupMenuLocation);
        if (element == null)
            return;

        moveToForeground(element);

        /* Update the Actions (buttons) */
        updateZValueActionsDependingOnSelection();
    }

    /**
     * Assigns the specified element to the foreground. (1 level higher in z
     * direction than the current top element)
     * 
     * @param element
     *            The element to be set as the new foreground element.
     */
    private void moveToForeground(IGuiElement element) {
        if (element == null)
            throw new NoSuchElementException();

        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.moveToForeground", element.getId());
        }

        int currZValue = element.getZValue();
        int currMax = currentModel.getMaxZValue();
        if (currZValue == currMax)
            return;

        int newZValue = currentModel.getIncrMaxZ();
        ConsoleLogger.logIfDebug(debug, "element.setZValue(" + newZValue + ")");
        element.setZValue(newZValue);

        /* Let the model resort the List of elements. */
        currentModel.sortElements();

        /* Repaint this element and (if necessary) adjacent arcs. */
        List<IGuiElement> toBeRepainted = new LinkedList<IGuiElement>();
        toBeRepainted.add(element);

        if (element instanceof IGuiNode) {
            IGuiNode node = (IGuiNode) element;
            List<IGuiArc> arcs = getAdjacentArcs(node);
            toBeRepainted.addAll(arcs);
        }

        updateDrawing();
    }

    @Override
    public void moveElementToBackground() {
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation != null) {
            /* Call via popup menu: there is a location. */
            moveElementAtPopupMenuToBackground(popupMenuLocation);
            return;
        }

        /* Call via menu bar: We need one (single) selected element. */
        IGuiElement selectedElement;
        try {
            selectedElement = getSingleSelectedElement();
        } catch (PNElementException e) {
            String warning = i18n.getMessage("warningUnableToAssignToBackground");
            String explanation = e.getMessage();
            String message = warning + " (" + explanation + ")";

            System.out.println(message);
            setInfo_Status(message, EStatusMessageLevel.INFO);
            return;
        }

        /* OK, we have exactly 1 element. */
        moveToBackground(selectedElement);

        /* Update the Actions (buttons) */
        updateZValueActionsDependingOnSelection();
    }

    /**
     * Used by moveElementToBackground().
     * 
     * @param popupMenuLocation
     *            The popup menu location on the current draw panel
     */
    private void moveElementAtPopupMenuToBackground(Point popupMenuLocation) {
        IGuiElement element = getElementAtLocation(popupMenuLocation);
        if (element == null)
            return;

        moveToBackground(element);

        /* Update the Actions (buttons) */
        updateZValueActionsDependingOnSelection();
    }

    /**
     * Assigns the specified element to the background. (1 level lower in z
     * direction than the current bottom element)
     * 
     * @param element
     *            The element to be set as the new background element.
     */
    private void moveToBackground(IGuiElement element) {
        if (element == null)
            throw new NoSuchElementException();

        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.moveToBackground", element.getId());
        }

        int currZValue = element.getZValue();
        int currMin = currentModel.getMinZValue();
        if (currZValue == currMin)
            return;

        int newZValue = currentModel.getDecrMinZ();
        ConsoleLogger.logIfDebug(debug, "element.setZValue(" + newZValue + ")");
        element.setZValue(newZValue);

        /* Let the model resort the List of elements. */
        currentModel.sortElements();

        /* Repaint this element and (if necessary) adjacent arcs. */
        List<IGuiElement> toBeRepainted = new LinkedList<IGuiElement>();
        toBeRepainted.add(element);

        if (element instanceof IGuiNode) {
            IGuiNode node = (IGuiNode) element;
            List<IGuiArc> arcs = getAdjacentArcs(node);
            toBeRepainted.addAll(arcs);
        }

        updateDrawing();
    }

    @Override
    public void moveElementOneLayerUp() {
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation != null) {
            /* Call via popup menu: there is a location. */
            moveElementAtPopupMenuOneLayerUp(popupMenuLocation);
            return;
        }

        /* Call via menu bar: We need one (single) selected element. */
        IGuiElement selectedElement;
        try {
            selectedElement = getSingleSelectedElement();
        } catch (PNElementException e) {
            String warning = i18n.getMessage("warningUnableToAssignOneLayerUp");
            String explanation = e.getMessage();
            String message = warning + " (" + explanation + ")";

            System.out.println(message);
            setInfo_Status(message, EStatusMessageLevel.INFO);
            return;
        }

        /* OK, we have exactly 1 element. */
        moveOneLayerUp(selectedElement);

        /* Update the Actions (buttons) */
        updateZValueActionsDependingOnSelection();
    }

    /**
     * used by moveElementOneLayerUp().
     * 
     * @param popupMenuLocation
     *            The popup menu location on the current draw panel
     */
    private void moveElementAtPopupMenuOneLayerUp(Point popupMenuLocation) {
        IGuiElement element = getElementAtLocation(popupMenuLocation);
        if (element == null)
            return;

        moveOneLayerUp(element);

        /* Update the Actions (buttons) */
        updateZValueActionsDependingOnSelection();
    }

    /**
     * Assigns the specified element to the next higher layer.
     * 
     * @param element
     *            The element to be moved one layer up.
     */
    private void moveOneLayerUp(IGuiElement element) {
        if (element == null)
            throw new NoSuchElementException();

        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.moveOneLayerUp", element.getId());
        }

        int currZValue = element.getZValue();
        int currMax = currentModel.getMaxZValue();
        if (currZValue == currMax)
            return;

        /* Switch layer with the next higher element. */
        List<IGuiElement> elements = currentModel.getElements();
        IGuiElement swap = null;
        int swapZValue = currMax + 1; // A "safe" value to start with

        for (IGuiElement next : elements) {
            int nextZValue = next.getZValue();
            if (nextZValue > currZValue) {
                IGuiElement candidate = next;
                int candidateZValue = nextZValue;
                if (candidateZValue < swapZValue) {
                    swap = candidate;
                    swapZValue = candidateZValue;
                }
            }
        }

        /* Swap element and swap element. */
        ConsoleLogger.logIfDebug(debug, "element.setZValue(" + swapZValue + ")");
        element.setZValue(swapZValue);
        ConsoleLogger.logIfDebug(debug, "swap.setZValue(" + currZValue + ")");
        swap.setZValue(currZValue);

        /* Let the model resort the List of elements. */
        currentModel.sortElements();

        /* Repaint this element and (if necessary) adjacent arcs. */
        List<IGuiElement> toBeRepainted = new LinkedList<IGuiElement>();
        toBeRepainted.add(element);

        if (element instanceof IGuiNode) {
            IGuiNode node = (IGuiNode) element;
            List<IGuiArc> arcs = getAdjacentArcs(node);
            toBeRepainted.addAll(arcs);
        }

        updateDrawing();
    }

    @Override
    public void moveElementOneLayerDown() {
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation != null) {
            /* Call via popup menu: there is a location. */
            moveElementAtPopupMenuOneLayerDown(popupMenuLocation);
            return;
        }

        /* Call via menu bar: We need one (single) selected element. */
        IGuiElement selectedElement;
        try {
            selectedElement = getSingleSelectedElement();
        } catch (PNElementException e) {
            String warning = i18n.getMessage("warningUnableToAssignOneLayerDown");
            String explanation = e.getMessage();
            String message = warning + " (" + explanation + ")";

            System.out.println(message);
            setInfo_Status(message, EStatusMessageLevel.INFO);
            return;
        }

        /* OK, we have exactly 1 element. */
        moveOneLayerDown(selectedElement);

        /* Update the Actions (buttons) */
        updateZValueActionsDependingOnSelection();
    }

    /**
     * used by moveElementOneLayerDown().
     * 
     * @param popupMenuLocation
     *            The popup menu location on the current draw panel
     */
    private void moveElementAtPopupMenuOneLayerDown(Point popupMenuLocation) {
        IGuiElement element = getElementAtLocation(popupMenuLocation);
        if (element == null)
            return;

        moveOneLayerDown(element);

        /* Update the Actions (buttons) */
        updateZValueActionsDependingOnSelection();
    }

    /**
     * Assigns the specified element to the next lower layer.
     * 
     * @param element
     *            The element to be moved one layer down.
     */
    private void moveOneLayerDown(IGuiElement element) {
        if (element == null)
            throw new NoSuchElementException();

        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModelController.moveOneLayerDown", element.getId());
        }

        int currZValue = element.getZValue();
        int currMin = currentModel.getMinZValue();
        if (currZValue == currMin)
            return;

        /* Switch layer with the next higher element. */
        List<IGuiElement> elements = currentModel.getElements();
        IGuiElement swap = null;
        int swapZValue = currMin - 1; // A "safe" value to start with

        for (IGuiElement next : elements) {
            int nextZValue = next.getZValue();
            if (nextZValue < currZValue) {
                IGuiElement candidate = next;
                int candidateZValue = nextZValue;
                if (candidateZValue > swapZValue) {
                    swap = candidate;
                    swapZValue = candidateZValue;
                }
            }
        }

        /* Swap element and swap element. */
        ConsoleLogger.logIfDebug(debug, "element.setZValue(" + swapZValue + ")");
        element.setZValue(swapZValue);
        ConsoleLogger.logIfDebug(debug, "swap.setZValue(" + currZValue + ")");
        swap.setZValue(currZValue);

        /* Let the model resort the List of elements. */
        currentModel.sortElements();

        /* Repaint this element and (if necessary) adjacent arcs. */
        List<IGuiElement> toBeRepainted = new LinkedList<IGuiElement>();
        toBeRepainted.add(element);

        if (element instanceof IGuiNode) {
            IGuiNode node = (IGuiNode) element;
            List<IGuiArc> arcs = getAdjacentArcs(node);
            toBeRepainted.addAll(arcs);
        }

        updateDrawing();
    }

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

    /*
     * Private helper methods
     */

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
     * Invokes updateDrawing(Rectangle area) for no area -> everything.
     */
    private void updateDrawing() {
        Rectangle area = null;
        updateDrawing(area);
    }

    /**
     * Returns the element at the specified Point. (Returns the one with the
     * highest z-value if there is more than 1 at this location.)
     * 
     * @param p
     *            The specified Point
     * @return The topmost element at the specified point
     */
    private IGuiElement getElementAtLocation(Point p) {
        java.util.List<IGuiElement> elements = currentModel.getElements();
        IGuiElement foundElement = null;

        for (IGuiElement element : elements) {
            if (element.contains(p)) {
                foundElement = element;
                break;
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
     * Returns the node (place or transition, not other elements) at the
     * specified Point.
     * 
     * @param p
     *            The specified Point
     * @return The node at the specified point
     */
    private IGuiNode getNodeAtLocation(Point p) {
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

    /**
     * Returns the transition (not other elements) at the specified Point.
     * 
     * @param p
     *            The specified Point
     * @return The transition at the specified point
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
            String explanation = i18n.getMessage("infoNoElementSelected");
            throw new PNElementException(explanation);
        }

        if (count > 1) {
            String explanation = i18n.getMessage("warningTooManyElementSelected");
            throw new PNElementException(explanation);
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

    /**
     * Returns a list of all arcs that are adjacent to the nodes in the
     * specified list.
     * 
     * @param nodes
     *            The list of nodes
     * @return A {@link List} of {@link IGuiArc} of all adjacent arcs
     */
    private List<IGuiArc> getAdjacentArcs(IGuiNode node) {
        List<IGuiElement> elements = currentModel.getElements();
        List<IGuiArc> adjacentArcs = new LinkedList<IGuiArc>();

        for (IGuiElement element : elements) {
            if (element instanceof IGuiArc) {
                IGuiArc arc = (IGuiArc) element;
                if (isAdjacentArc(arc, node)) {
                    adjacentArcs.add(arc);
                }
            }
        }

        return adjacentArcs;
    }

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
        } catch (PNElementException e) {
            System.err.println("Arc without predecessor! Arc id: " + arc.getId());
            e.printStackTrace();
        }

        /* Node is successor of the arc? */
        try {
            if (arc.getSuccElem() == node) { return true; }
        } catch (PNElementException e) {
            System.err.println("Arc without succecessor! Arc id: " + arc.getId());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Asks the user for a new name.
     * 
     * @return Null if the user canceled the input; otherwise the input String
     */
    private String askUserForNewName() {
        String question = i18n.getMessage("questionNewName");
        String inputValue = JOptionPane.showInputDialog(question);
        System.out.println("inputValue: " + inputValue);
        return inputValue;
    }

    /**
     * Invokes updateZValueActions() in {@link ApplicationController} with the
     * proper parameter depending on whether a (single) {@link IGuiElement} is
     * selected or not.
     */
    private void updateZValueActionsDependingOnSelection() {
        IGuiElement selectedElement;
        try {
            selectedElement = getSingleSelectedElement();
            appController.updateZValueActions(selectedElement);
        } catch (PNElementException e) {
            appController.updateZValueActions(null);
        }
    }

    /*
     * Validation events
     */

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
        IGuiNode guiNode = currentModel.getNodeById(placeId);
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
        IGuiNode guiNode = currentModel.getNodeById(placeId);
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
        IGuiNode guiNode = currentModel.getNodeById(placeId);
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
        IGuiNode guiNode = currentModel.getNodeById(placeId);
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
        IGuiNode guiNode = currentModel.getNodeById(nodeId);
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
     * not found because this error can be expected in rare cases. This method
     * is part of the validation process. And the ValidationController thread
     * might slightly lagging behind in terms of the current model (e.g. if the
     * user has suddenly closed the current file during validation).
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
    public int getCurrentMinZValue() {
        return currentModel.getMinZValue();
    }

    @Override
    public int getCurrentMaxZValue() {
        return currentModel.getMaxZValue();
    }

}
