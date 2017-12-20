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
import de.lambeck.pned.elements.data.EPlaceMarking;
import de.lambeck.pned.elements.gui.*;
import de.lambeck.pned.exceptions.PNElementException;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Observes the state of the GUI.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class GuiModelController implements IGuiModelController {

    private static boolean debug = false;

    protected ApplicationController appController = null;
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
            System.out.println("GuiModelController.addGuiModel(" + modelName + ", " + displayName + ")");
        }

        this.currentModel = new GuiModel(modelName, displayName, this);
        this.guiModels.put(modelName, currentModel);

        /*
         * Add an associated draw panel as well!
         */
        addDrawPanel(modelName, displayName);

        if (debug) {
            System.out.println("GUI models count: " + guiModels.size());
        }
    }

    @Override
    public void removeGuiModel(String modelName) {
        if (debug) {
            System.out.println("GuiModelController.removeDataModel(" + modelName + ")");
        }

        this.guiModels.remove(modelName);
        this.currentModel = null;

        /*
         * Remove the associated draw panel as well.
         */
        removeDrawPanel(modelName);

        if (debug) {
            System.out.println("GUI models count: " + guiModels.size());
        }
    }

    @Override
    public void renameGuiModel(IGuiModel model, String newModelName, String newDisplayName) {
        /*
         * The key for the Map of models.
         */
        String oldKey = model.getModelName();

        /*
         * Get the associated draw panel;
         */
        IDrawPanel drawPanel = getDrawPanel(oldKey);

        /*
         * Rename the model and the associated draw panel.
         */
        IModelRename renameCandidate;

        renameCandidate = (IModelRename) model;
        setModelNames(renameCandidate, newModelName, newDisplayName);

        renameCandidate = (IModelRename) drawPanel;
        setDrawPanelNames(renameCandidate, newModelName, newDisplayName);

        /*
         * Update both Maps!
         */
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
     *            name of the pnml file represented by this model.)
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
     *            name of the pnml file represented by this model.)
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
            System.out.println("GuiModelController.setCurrentModel(" + model.getModelName() + ")");
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

    /**
     * Called by addGuiModel to add an associated {@link IDrawPanel}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     * @param displayName
     *            The title of the tab (= the file name)
     */
    private void addDrawPanel(String modelName, String displayName) {
        if (debug) {
            System.out.println("GuiModelController.addDrawPanel(" + modelName + ", " + displayName + ")");
        }

        this.currentDrawPanel = new DrawPanel(modelName, displayName, appController, this, currentModel, popupActions,
                i18n);
        this.drawPanels.put(modelName, currentDrawPanel);
    }

    /**
     * Called by removeGuiModel to remove the associated {@link IDrawPanel}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     */
    private void removeDrawPanel(String modelName) {
        if (debug) {
            System.out.println("GuiModelController.removeDrawPanel(" + modelName + ")");
        }

        this.drawPanels.remove(modelName);
        this.currentDrawPanel = null;

        if (debug) {
            System.out.println("Draw panels count: " + drawPanels.size());
        }
    }

    @Override
    public IDrawPanel getCurrentDrawPanel() {
        return this.currentDrawPanel;
    }

    @Override
    public void setCurrentDrawPanel(IDrawPanel drawPanel) {
        if (debug) {
            System.out.println("GuiModelController.setCurrentDrawPanel(" + drawPanel.getModelName() + ")");
        }

        this.currentDrawPanel = drawPanel;

        /*
         * Inform the draw panel that is has got the focus. (In order to reset
         * its state.)
         */
        this.currentDrawPanel.resetState();
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

    @Override
    public void addPlaceToCurrentGuiModel(String id, String name, EPlaceMarking initialMarking, Point position) {
        currentModel.addPlace(id, name, initialMarking, position);
        // if (debug) {
        // System.out.println("Place added to GUI model " +
        // currentModel.getModelName());
        // }

        /*
         * Update the data model
         */
        appController.placeAddedToCurrentGuiModel(id, name, initialMarking, position);
    }

    @Override
    public void addTransitionToCurrentGuiModel(String id, String name, Point position) {
        currentModel.addTransition(id, name, position);
        // if (debug) {
        // System.out.println("Transition added to GUI model " +
        // currentModel.getModelName());
        // }

        /*
         * Update the data model
         */
        appController.transitionAddedToCurrentGuiModel(id, name, position);
    }

    @Override
    public void addArcToCurrentGuiModel(String id, String sourceId, String targetId) {
        currentModel.addArc(id, sourceId, targetId);
        // if (debug) {
        // System.out.println("Arc added to GUI model " +
        // currentModel.getModelName());
        // }

        /*
         * Update the data model
         */
        appController.arcAddedToCurrentGuiModel(id, sourceId, targetId);
    }

    @Override
    public void createNewPlaceInCurrentGuiModel() {
        /*
         * Check if we have a location.
         */
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation == null) {
            System.err.println(
                    "GuiModelController.createNewPlaceInCurrentGuiModel(): Unable to create a Place: popup menu location unknown.");
            return;
        }

        /*
         * Create a unique ID to avoid any conflict with existing elements.
         */
        String uuid = UUID.randomUUID().toString();

        /*
         * ...and a Place with this ID.
         */
        String name = "";
        EPlaceMarking initialMarking = EPlaceMarking.ZERO;
        addPlaceToCurrentGuiModel(uuid, name, initialMarking, popupMenuLocation);

        /*
         * Update the drawing
         */
        IGuiElement element = currentModel.getElementById(uuid);
        if (element == null)
            return;

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
        /*
         * Check if we have a location.
         */
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation == null) {
            System.err.println(
                    "GuiModelController.createNewTransitionInCurrentGuiModel(): Unable to create a Place: popup menu location unknown.");
            return;
        }

        /*
         * Create a unique ID to avoid any conflict with existing elements.
         */
        String uuid = UUID.randomUUID().toString();

        /*
         * ...and a Transition with this ID.
         */
        String name = "";
        addTransitionToCurrentGuiModel(uuid, name, popupMenuLocation);

        /*
         * Update the drawing
         */
        IGuiElement element = currentModel.getElementById(uuid);
        if (element == null)
            return;

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
        /*
         * Check if we have a location.
         */
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation == null) {
            System.err.println(
                    "GuiModelController.setSourceLocationForNewArc(): Unable to set source location for new arc: popup menu location unknown.");
            return;
        }

        /*
         * Check if we have a node at this location.
         */
        IGuiNode node = getNodeAtLocation(popupMenuLocation);
        if (node == null)
            return;

        /*
         * Store this node as source for the new Arc.
         */
        sourceNodeForNewArc = node;

        if (node instanceof GuiPlace) {
            sourceForNewArcType = ENodeType.PLACE;
        } else if (node instanceof GuiTransition) {
            sourceForNewArcType = ENodeType.TRANSITION;
        }

        /*
         * Set my state.
         */
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
        /*
         * Check the current state!
         */
        if (!getStateAddingNewArc())
            return;

        /*
         * Check if we have a location.
         */
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation == null) {
            System.err.println(
                    "GuiModelController.setSourceLocationForNewArc(): Unable to set source location for new arc: popup menu location unknown.");
            return;
        }

        /*
         * Check if we have a node at this location.
         */
        IGuiNode node = getNodeAtLocation(popupMenuLocation);
        if (node == null)
            return;

        /*
         * TODO Check if both nodes are of different type!
         * 
         * -> Or already in the popup menu?
         */

        /*
         * Create a unique ID to avoid any conflict with existing elements.
         */
        String uuid = UUID.randomUUID().toString();

        /*
         * Get the IDs of source and target.
         */
        String sourceId = sourceNodeForNewArc.getId();
        String targetId = node.getId();

        /*
         * And create an Arc with these IDs.
         */
        addArcToCurrentGuiModel(uuid, sourceId, targetId);

        /*
         * Reset my state.
         */
        resetStateAddingNewArc();

        /*
         * Update the drawing
         */
        IGuiElement element = currentModel.getElementById(uuid);
        if (element == null)
            return;

        Rectangle rect = element.getLastDrawingArea();
        updateDrawing(rect);

        /*
         * Reset the popup active state of the DrawPanel since we have left the
         * popup menu with the NewPlaceAction!
         */
        currentDrawPanel.setPopupMenuLocation(null);
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
                    break;
                }
            }
        }

        if (debug) {
            if (foundElement == null)
                System.out.println("No selectable element at this Point!");
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
    public void mouseClick_Occurred(Point mousePressedLocation, MouseEvent e) {
        if (mousePressedLocation == null) {
            System.err.println("GuiModelController.mouseClick_Occurred(): mousePressedLocation == null");
            return;
        }

        IGuiElement mousePressedElement;
        mousePressedElement = getSelectableElementAtLocation(mousePressedLocation);
        if (mousePressedElement == null) {
            resetSelection();
            return;
        }

        IGuiElement mouseReleasedElement;
        mouseReleasedElement = getSelectableElementAtLocation(e.getPoint());
        if (mousePressedElement != mouseReleasedElement) {
            /*
             * Reject this mouseClicked event as unintended!
             */
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
        IGuiElement element = mousePressedElement;

        Rectangle oldArea = element.getLastDrawingArea();

        currentModel.selectSingleElement(element);

        Rectangle newArea = element.getLastDrawingArea();

        if (debug) {
            System.out.println("GuiModelController: MouseClick occurred:");
            System.out.println("Single element selected: " + element.getId());
            System.out.println("updateDrawing(" + oldArea + ")");
            System.out.println("updateDrawing(" + newArea + ")");
        }
        updateDrawing(oldArea);
        updateDrawing(newArea);
    }

    @Override
    public void mouseClick_WithCtrl_Occurred(Point mousePressedLocation, MouseEvent e) {
        if (mousePressedLocation == null) {
            System.err.println("GuiModelController.mouseClick_WithCtrl_Occurred(): mousePressedLocation == null");
            return;
        }

        IGuiElement mousePressedElement;
        mousePressedElement = getSelectableElementAtLocation(mousePressedLocation);
        if (mousePressedElement == null) { return; }

        IGuiElement mouseReleasedElement;
        mouseReleasedElement = getSelectableElementAtLocation(e.getPoint());
        if (mousePressedElement != mouseReleasedElement) {
            /*
             * Reject this mouseClicked event as unintended!
             */
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
        IGuiElement element = mousePressedElement;

        Rectangle oldArea = element.getLastDrawingArea();

        currentModel.toggleSelection(element);

        Rectangle newArea = element.getLastDrawingArea();

        if (debug) {
            System.out.println("GuiModelController: MouseClick with CTRL occurred:");
            System.out.println("Selection toggled on element: " + element.getId());
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

        /*
         * Get all selected elements.
         */
        List<IGuiElement> selectedElements = currentModel.getSelectedElements();
        if (selectedElements.size() == 0)
            return;
        if (debug) {
            System.out.println(selectedElements.size() + " element(s) selected.");
        }

        /*
         * Limit the elements to the nodes.
         */
        List<IGuiNode> selectedNodes = new LinkedList<IGuiNode>();

        for (IGuiElement element : selectedElements) {
            if (element instanceof IGuiNode) {
                IGuiNode node = (IGuiNode) element;
                selectedNodes.add(node);
            }
        }
        if (debug) {
            System.out.println(selectedNodes.size() + " nodes(s) selected.");
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

        /*
         * Get the old drawing areas for repainting.
         */
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

        /*
         * Update the drawing.
         */
        updateDrawing(oldDrawingAreas);
        updateDrawing(newDrawingAreas);
        updateDrawing(arcAreas);
    }

    // @Override
    // public void mouseDragging_Finished() {
    // String nodeId;
    // Point newPosition;
    //
    // if (movedNodes.size() == 0)
    // return;
    //
    // for (IGuiNode node : movedNodes) {
    // nodeId = node.getId();
    // newPosition = node.getPosition();
    // appController.guiNodeDragged(nodeId, newPosition);
    // }
    // }

    @Override
    public void updateDataNodePositions() {
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

    @Override
    public void keyEvent_Escape_Occurred() {
        resetSelection();
    }

    private void resetSelection() {
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
        // for (Rectangle rect : drawingAreas) {
        // updateDrawing(rect);
        // }
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
        renameSelectedGuiElements();

        if (debug) {
            System.out.println("GuiModelController: KeyEvent F2 occurred:");
            System.out.println("Selected element renamed.");
        }
    }

    @Override
    public void updateDrawing(Rectangle area) {
        currentDrawPanel.updateDrawing(area);
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

        /*
         * Call via menu bar: We need one (single) selected element.
         */
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

        /*
         * OK, we have exactly 1 element.
         */
        moveToForeground(selectedElement);
    }

    private void moveElementAtPopupMenuToForeground(Point popupMenuLocation) {
        IGuiElement element = getElementAtLocation(popupMenuLocation);
        if (element == null)
            return;

        moveToForeground(element);
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
            System.out.println("moveToForeground(" + element.getId() + ")");
        }

        int currZValue = element.getZValue();
        int currMax = currentModel.getMaxZValue();
        if (currZValue == currMax)
            return;

        int newZValue = currentModel.getIncrMaxZ();
        element.setZValue(newZValue);

        // TODO Call some kind of sorting algorithm in the GUI model to update
        // its lists?
        // /*
        // * Sort (see: https://stackoverflow.com/a/2784576)
        // */
        // java.util.List<IGuiElement> elements = myGuiModel.getElements();
        //
        // Collections.sort(elements, new Comparator<IGuiElement>() {
        // @Override
        // public int compare(IGuiElement element1, IGuiElement element2) {
        // if (element2.getZValue() > element1.getZValue()) {
        // return -1;
        // } else if (element1.getZValue() == element2.getZValue()) {
        // return 0;
        // } else {
        // return 1;
        // }
        // }
        // });

        // Collections.sort(elements, new Comparator<IGuiElement>() {
        // @Override
        // public int compare(IGuiElement element1, IGuiElement element2) {
        // if (element2.getZValue() > element1.getZValue()) {
        // return -1;
        // } else if (element1.getZValue() == element2.getZValue()) {
        // return 0;
        // } else {
        // return 1;
        // }
        // }
        // });

        /*
         * Repaint this element and (if necessary) adjacent arcs.
         */
        List<IGuiElement> toBeRepainted = new LinkedList<IGuiElement>();
        toBeRepainted.add(element);

        if (element instanceof IGuiNode) {
            IGuiNode node = (IGuiNode) element;
            List<IGuiArc> arcs = getAdjacentArcs(node);
            toBeRepainted.addAll(arcs);
        }

        List<Rectangle> areas = getDrawingAreas(toBeRepainted);
        updateDrawing(areas);
    }

    @Override
    public void moveElementToBackground() {
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation != null) {
            /*
             * Call via popup menu: there is a location
             */
            moveElementAtPopupMenuToBackground(popupMenuLocation);
            return;
        }

        /*
         * Call via menu bar: We need one (single) selected element.
         */
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

        /*
         * OK, we have exactly 1 element.
         */
        moveToBackground(selectedElement);
    }

    private void moveElementAtPopupMenuToBackground(Point popupMenuLocation) {
        IGuiElement element = getElementAtLocation(popupMenuLocation);
        if (element == null)
            return;

        moveToBackground(element);
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
            System.out.println("moveToBackground(" + element.getId() + ")");
        }

        int currZValue = element.getZValue();
        int currMin = currentModel.getMinZValue();
        if (currZValue == currMin)
            return;

        int newZValue = currentModel.getDecrMinZ();
        element.setZValue(newZValue);

        // TODO Call some kind of sorting algorithm in the GUI model to update
        // its lists?

        /*
         * Repaint this element and (if necessary) adjacent arcs.
         */
        List<IGuiElement> toBeRepainted = new LinkedList<IGuiElement>();
        toBeRepainted.add(element);

        if (element instanceof IGuiNode) {
            IGuiNode node = (IGuiNode) element;
            List<IGuiArc> arcs = getAdjacentArcs(node);
            toBeRepainted.addAll(arcs);
        }

        List<Rectangle> areas = getDrawingAreas(toBeRepainted);
        updateDrawing(areas);
    }

    @Override
    public void moveElementOneLayerUp() {
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation != null) {
            /*
             * Call via popup menu: there is a location
             */
            moveElementAtPopupMenuOneLayerUp(popupMenuLocation);
            return;
        }

        /*
         * Call via menu bar: We need one (single) selected element.
         */
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

        /*
         * OK, we have exactly 1 element.
         */
        moveOneLayerUp(selectedElement);
    }

    private void moveElementAtPopupMenuOneLayerUp(Point popupMenuLocation) {
        IGuiElement element = getElementAtLocation(popupMenuLocation);
        if (element == null)
            return;

        moveOneLayerUp(element);
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
            System.out.println("moveOneLayerUp(" + element.getId() + ")");
        }

        int currZValue = element.getZValue();
        int currMax = currentModel.getMaxZValue();
        if (currZValue == currMax)
            return;

        /*
         * Switch layer with the next higher element.
         */
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

        /*
         * Swap element and swap element
         */
        element.setZValue(swapZValue);
        swap.setZValue(currZValue);

        // TODO Call some kind of sorting algorithm in the GUI model to update
        // its lists?

        /*
         * Repaint this element and (if necessary) adjacent arcs.
         */
        List<IGuiElement> toBeRepainted = new LinkedList<IGuiElement>();
        toBeRepainted.add(element);

        if (element instanceof IGuiNode) {
            IGuiNode node = (IGuiNode) element;
            List<IGuiArc> arcs = getAdjacentArcs(node);
            toBeRepainted.addAll(arcs);
        }

        List<Rectangle> areas = getDrawingAreas(toBeRepainted);
        updateDrawing(areas);
    }

    @Override
    public void moveElementOneLayerDown() {
        Point popupMenuLocation = currentDrawPanel.getPopupMenuLocation();
        if (popupMenuLocation != null) {
            /*
             * Call via popup menu: there is a location
             */
            moveElementAtPopupMenuOneLayerDown(popupMenuLocation);
            return;
        }

        /*
         * Call via menu bar: We need one (single) selected element.
         */
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

        /*
         * OK, we have exactly 1 element.
         */
        moveOneLayerDown(selectedElement);
    }

    private void moveElementAtPopupMenuOneLayerDown(Point popupMenuLocation) {
        IGuiElement element = getElementAtLocation(popupMenuLocation);
        if (element == null)
            return;

        moveOneLayerDown(element);
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
            System.out.println("moveOneLayerDown(" + element.getId() + ")");
        }

        int currZValue = element.getZValue();
        int currMin = currentModel.getMinZValue();
        if (currZValue == currMin)
            return;

        /*
         * Switch layer with the next higher element.
         */
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

        /*
         * Swap element and swap element
         */
        element.setZValue(swapZValue);
        swap.setZValue(currZValue);

        // TODO Call some kind of sorting algorithm in the GUI model to update
        // its lists?

        /*
         * Repaint this element and (if necessary) adjacent arcs.
         */
        List<IGuiElement> toBeRepainted = new LinkedList<IGuiElement>();
        toBeRepainted.add(element);

        if (element instanceof IGuiNode) {
            IGuiNode node = (IGuiNode) element;
            List<IGuiArc> arcs = getAdjacentArcs(node);
            toBeRepainted.addAll(arcs);
        }

        List<Rectangle> areas = getDrawingAreas(toBeRepainted);
        updateDrawing(areas);
    }

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

    @Override
    public void changeShapeSize(int size) {
        if (size < 20) {
            System.err.println("Shape size too small: " + size);
            return;
        }

        /*
         * Change the static attributes.
         */
        GuiNode.changeShapeSize(size);
        GuiArc.changeShapeSize(size);

        /*
         * Update the whole drawing. (All other draw panels should be updated
         * when switching the tab.)
         */
        Dimension area = currentDrawPanel.getPreferredSize();
        Rectangle rect = new Rectangle(area);
        updateDrawing(rect);
    }

    /*
     * Remove methods for elements
     */

    @Override
    public void removeSelectedGuiElements() {
        if (debug) {
            System.out.println("GuiModelController.removeSelectedGuiElements()");
        }

        /*
         * Task: Remove all selected elements *and* all adjacent arcs!
         */

        /*
         * Get all selected elements.
         */
        List<IGuiElement> toBeRemoved = currentModel.getSelectedElements();

        /*
         * Sort out the nodes (which can have adjacent arcs).
         */
        List<IGuiNode> nodes = new LinkedList<IGuiNode>();
        for (IGuiElement element : toBeRemoved) {
            if (element instanceof IGuiNode) {
                IGuiNode node = (IGuiNode) element;
                nodes.add(node);
            }
        }

        /*
         * Get all adjacent arcs for these nodes.
         */
        List<IGuiArc> adjacentArcs = getAdjacentArcs(nodes);

        /*
         * Add the adjacent arcs to the list
         */
        toBeRemoved = combineElementsAndArcs(toBeRemoved, adjacentArcs);

        /*
         * Store the drawing area for repainting
         */
        List<Rectangle> drawingAreas = getDrawingAreas(toBeRemoved);

        /*
         * Add to a separate list first to avoid ConcurrentModificationException
         * when removeElement() removes the element from the list!
         */
        List<String> toBeRemoved_IDs = new ArrayList<String>();
        for (IGuiElement element : toBeRemoved) {
            toBeRemoved_IDs.add(element.getId());
        }

        /*
         * Remove all elements
         */
        for (String id : toBeRemoved_IDs) {
            if (debug) {
                System.out.println("Remove id: " + id);
            }
            currentModel.removeElement(id);
            currentModel.setModified(true);

            /*
             * Inform the application controller to remove this element from the
             * data model!
             */
            appController.guiElementRemoved(id);
        }

        /*
         * Repaint the areas.
         */
        // for (Rectangle rect : drawingAreas) {
        // updateDrawing(rect);
        // }
        updateDrawing(drawingAreas);
    }

    @Override
    public void renameSelectedGuiElements() {
        if (debug) {
            System.out.println("GuiModelController.renameSelectedGuiElements()");
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

        /*
         * OK, we have exactly 1 node and can ask for a new name.
         */
        // IGuiNode selectedNode = (IGuiNode) selectedElement;
        String newName = askUserForNewName();

        if (newName == null)
            return; // User cancelled the operation

        /*
         * We have a new name.
         */
        selectedNode.setName(newName);
        currentModel.setModified(true);

        /*
         * Update the data model!
         */
        String nodeId = selectedNode.getId();
        appController.guiNodeRenamed(nodeId, newName);

        /*
         * Update the drawing!
         */
        Rectangle area = selectedNode.getLastDrawingArea();
        updateDrawing(area);
        // area = selectedNode.getLastDrawingArea();
        // updateDrawing(area);
    }

    @Override
    public void removeGuiArc(String arcId) {
        currentModel.removeElement(arcId);
        currentModel.setModified(true);
    }

    /*
     * Private helper methods
     */

    private IGuiElement getElementAtLocation(Point p) {
        java.util.List<IGuiElement> elements = currentModel.getElements();
        IGuiElement foundElement = null;

        // TODO Sort according to z value and return the topmost element!
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

    private IGuiNode getNodeAtLocation(Point p) {
        IGuiElement foundElement = getElementAtLocation(p);
        if (foundElement == null)
            return null;

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
     * Checks if exactly 1 element is selected.
     * 
     * @return The selected element
     * @throws PNElementException
     *             if no or too many elements are selected
     */
    private IGuiElement getSingleSelectedElement() throws PNElementException {
        /*
         * Check if exactly 1 element is selected.
         */
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
        /*
         * Check if exactly 1 node is selected.
         */
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

    // private IGuiNode getNodeAtLocation(Point p) {
    // java.util.List<IGuiElement> elements = currentModel.getElements();
    // IGuiNode foundNode = null;
    //
    // // TODO Sort according to z value and return the topmost element!
    // for (IGuiElement element : elements) {
    // if (element.contains(p)) {
    // if (isNode(element)) {
    // foundNode = (IGuiNode) element;
    // break;
    // }
    // }
    // }
    //
    // if (debug) {
    // if (foundNode == null)
    // System.out.println("No node at this Point!");
    // }
    //
    // return foundNode;
    // }

    // /**
    // * Checks if an {@link IGuiElement} is a {@link IGuiNode} (place or
    // * transition).
    // *
    // * @param element
    // * The element to check
    // * @return True if the element is a node; otherwise false
    // */
    // private boolean isNode(IGuiElement element) {
    // if (element instanceof IGuiNode)
    // return true;
    // return false;
    // }

    // private IGuiPlace getPlaceAtLocation(Point p) {
    // java.util.List<IGuiElement> elements = currentModel.getElements();
    // IGuiPlace foundPlace = null;
    //
    // // TODO Sort according to z value and return the topmost element!
    // for (IGuiElement element : elements) {
    // if (element.contains(p)) {
    // if (isPlace(element)) {
    // foundPlace = (IGuiPlace) element;
    // break;
    // }
    // }
    // }
    //
    // if (debug) {
    // if (foundPlace == null)
    // System.out.println("No place at this Point!");
    // }
    //
    // return foundPlace;
    // }

    // /**
    // * Checks if an {@link IGuiElement} is a {@link IGuiPlace}.
    // *
    // * @param element
    // * The element to check
    // * @return True if the element is a place; otherwise false
    // */
    // private boolean isPlace(IGuiElement element) {
    // if (element instanceof IGuiPlace)
    // return true;
    // return false;
    // }

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
            // try {
            // if (arc.getPredElem() == node) { return true; }
            // } catch (PNElementException e) {
            // System.err.println("Arc without predecessor! Arc id: " +
            // arc.getId());
            // e.printStackTrace();
            // }
            // try {
            // if (arc.getSuccElem() == node) { return true; }
            // } catch (PNElementException e) {
            // System.err.println("Arc without succecessor! Arc id: " +
            // arc.getId());
            // e.printStackTrace();
            // }
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
        /*
         * Node is predecessor of the arc?
         */
        try {
            if (arc.getPredElem() == node) { return true; }
        } catch (PNElementException e) {
            System.err.println("Arc without predecessor! Arc id: " + arc.getId());
            e.printStackTrace();
        }

        /*
         * Node is successor of the arc?
         */
        try {
            if (arc.getSuccElem() == node) { return true; }
        } catch (PNElementException e) {
            System.err.println("Arc without succecessor! Arc id: " + arc.getId());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Combines the two lists without duplicate entries.
     * 
     * @param elements
     *            A {@link List} of type {@link IGuiElement}
     * @param arcs
     *            A {@link List} of type {@link IGuiArc}
     * @return The combined {@link List} of type {@link IGuiElement} without
     *         duplicates
     */
    private List<IGuiElement> combineElementsAndArcs(List<IGuiElement> elements, List<IGuiArc> arcs) {
        for (IGuiElement element : arcs) {
            if (!elements.contains(element)) {
                elements.add(element);
            }
        }

        /*
         * Better methods in Java 8?:
         */
        // List<?> newList = Stream.of(elements,
        // arcs).flatMap(List::stream).collect(Collectors.toList());

        // List<IGuiElement> newList = Stream.of(elements,
        // arcs).collect(ArrayList::new, List::addAll, List::addAll);

        return elements;
    }

    /**
     * Asks the user for a new name.
     * 
     * @return Null if the user cancelled the input; otherwise the input String
     */
    private String askUserForNewName() {
        String question = i18n.getMessage("questionNewName");
        String inputValue = JOptionPane.showInputDialog(question);
        System.out.println("inputValue: " + inputValue);
        return inputValue;
    }

}
