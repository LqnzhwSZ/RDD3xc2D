package de.lambeck.pned.models.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.elements.gui.*;
import de.lambeck.pned.exceptions.PNDuplicateAddedException;
import de.lambeck.pned.exceptions.PNElementException;
import de.lambeck.pned.exceptions.PNNoSuchElementException;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * Implements the GUI model of a Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class GuiModel implements IGuiModel, IModelRename {

    /** Show debug messages? */
    private static boolean debug = false;

    /**
     * This should be the canonical (unique) path name of the file.
     */
    private String modelName = "";

    /**
     * This should be the name of the tab. (file name only)
     */
    private String displayName = "";

    /**
     * Reference to the GUI controller. (Mainly for returning dirty areas which
     * need repainting.)
     */
    protected IGuiModelController myGuiController = null;

    /**
     * List of all elements in this model
     */
    private List<IGuiElement> elements = new ArrayList<IGuiElement>();

    /**
     * List of all elements selected by the user.
     */
    private List<IGuiElement> selected = new ArrayList<IGuiElement>();

    /** The minimum z value of all elements in this model */
    private int minZValue = 0;

    /** The maximum z value of all elements in this model */
    private int maxZValue = 0;

    /**
     * This attribute is set if the model was modified in any way to make sure
     * that the user is asked for "file save" when closing the file.
     */
    private boolean modelModified = false;

    /** Map of present overlays (of type {@link IOverlay}) */
    private Map<EOverlayName, IOverlay> overlays = new HashMap<EOverlayName, IOverlay>();

    /* Constructor */

    /**
     * Constructs a new GUI model with the name as parameter. (Name is the full
     * path of the file to make sure it is unique.)
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param displayName
     *            The name of the tab (the file name only)
     * @param controller
     *            The GUI controller
     */
    @SuppressWarnings("hiding")
    public GuiModel(String modelName, String displayName, IGuiModelController controller) {
        super();
        this.modelName = modelName;
        this.displayName = displayName;
        this.myGuiController = controller;

        if (debug) {
            System.out.println("GuiModel created, name: " + getModelName());
        }
    }

    /* Getter and Setter */

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

    /* Interface IDataModel */

    @Override
    public List<IGuiElement> getElements() {
        List<IGuiElement> copy = new ArrayList<IGuiElement>(this.elements);
        return copy;
    }

    @Override
    public IGuiElement getElementById(String id) throws PNNoSuchElementException {
        for (IGuiElement element : elements) {
            if (element.getId().equalsIgnoreCase(id))
                return element;
        }

        String errorMessage = "Model " + this.modelName + ": element " + id + " not found!";
        ConsoleLogger.logIfDebug(debug, errorMessage);
        throw new PNNoSuchElementException(errorMessage);
    }

    @Override
    public List<IGuiElement> getSelectedElements() {
        // return this.selected;
        List<IGuiElement> copy = new ArrayList<IGuiElement>(this.selected);
        return copy;
    }

    @Override
    public IGuiNode getNodeById(String nodeId) throws PNNoSuchElementException {
        IGuiElement element;
        try {
            element = getElementById(nodeId);
        } catch (PNNoSuchElementException e) {
            /* Pass the exception to the invoker, no new error message. */
            throw new PNNoSuchElementException(e.getMessage());
        }

        if (element instanceof IGuiNode) {
            IGuiNode node = (IGuiNode) element;
            return node;
        }

        String errorMessage = "Model " + this.modelName + ": node " + nodeId + " not found!";
        ConsoleLogger.logIfDebug(debug, errorMessage);
        throw new PNNoSuchElementException(errorMessage);
    }

    @Override
    public IGuiPlace getPlaceById(String placeId) throws PNNoSuchElementException {
        IGuiElement element;
        try {
            element = getElementById(placeId);
        } catch (PNNoSuchElementException e) {
            /* Pass the exception to the invoker, no new error message. */
            throw new PNNoSuchElementException(e.getMessage());
        }

        if (element instanceof IGuiPlace) {
            IGuiPlace place = (IGuiPlace) element;
            return place;
        }

        String errorMessage = "Model " + this.modelName + ": place " + placeId + " not found!";
        ConsoleLogger.logIfDebug(debug, errorMessage);
        throw new PNNoSuchElementException(errorMessage);
    }

    @Override
    public IGuiTransition getTransitionById(String transitionId) throws PNNoSuchElementException {
        IGuiElement element;
        try {
            element = getElementById(transitionId);
        } catch (PNNoSuchElementException e) {
            /* Pass the exception to the invoker, no new error message. */
            throw new PNNoSuchElementException(e.getMessage());
        }

        if (element instanceof IGuiTransition) {
            IGuiTransition transition = (IGuiTransition) element;
            return transition;
        }

        String errorMessage = "Model " + this.modelName + ": transition " + transitionId + " not found!";
        ConsoleLogger.logIfDebug(debug, errorMessage);
        throw new PNNoSuchElementException(errorMessage);
    }

    @Override
    public int getMinZValue() {
        return this.minZValue;
    }

    @Override
    public int getMaxZValue() {
        return this.maxZValue;
    }

    @Override
    public int getZValue(IGuiElement element) {
        return element.getZValue();
    }

    @Override
    public int getDecrMinZ() {
        updateMinZ();
        if (!elements.isEmpty())
            this.minZValue--;
        return this.minZValue;
    }

    @Override
    public int getIncrMaxZ() {
        updateMaxZ();
        if (!elements.isEmpty())
            this.maxZValue++;
        return this.maxZValue;
    }

    /**
     * Getter for the z level (height level) of a specific node.
     * 
     * @param node
     *            The specified node
     * @return The z value of the specified node
     */
    public int getZValue(IGuiNode node) {
        return node.getZValue();
    }

    @Override
    public boolean isModified() {
        return this.modelModified;
    }

    @Override
    public void setModified(boolean b) {
        this.modelModified = b;
    }

    /* Sorting (for proper display on the draw panel) */

    @Override
    public void sortElements() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModel.sortElements");
        }

        /* Sort (see: https://stackoverflow.com/a/2784576) */
        Collections.sort(elements, new Comparator<IGuiElement>() {
            @Override
            public int compare(IGuiElement element1, IGuiElement element2) {
                if (element2.getZValue() > element1.getZValue()) {
                    return -1;
                } else if (element1.getZValue() == element2.getZValue()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
    }

    @Override
    public void sortSelectedElements() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModel.sortSelectedElements");
        }

        /* Sort (see: https://stackoverflow.com/a/2784576) */
        Collections.sort(selected, new Comparator<IGuiElement>() {
            @Override
            public int compare(IGuiElement element1, IGuiElement element2) {
                if (element2.getZValue() > element1.getZValue()) {
                    return -1;
                } else if (element1.getZValue() == element2.getZValue()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
    }

    /* Methods for adding, modify and removal of elements */

    @Override
    public void addPlace(String id, EPlaceToken initialTokens, Point position) {
        addPlace(id, "", initialTokens, position);
    }

    @Override
    public void addPlace(String id, String name, EPlaceToken initialTokens, Point position) {
        if (name == null)
            name = "";

        position = shiftPositionToMinXY(position);

        /* Get the next value for zOrder. */
        int zOrder = getIncrMaxZ();

        GuiPlace newPlace = new GuiPlace(id, name, position, zOrder, initialTokens);

        /* Add the place to the model. */
        try {
            addElement(newPlace);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println("GuiModel, addPlace: " + e.getMessage());
            return;
        }

        if (debug) {
            String title = this.modelName;
            String infoMessage = "Gui Place added: " + newPlace.toString();
            System.out.println(infoMessage);

            /* Get the main frame to center the input dialog. */
            JFrame mainFrame = myGuiController.getMainFrame();

            JOptionPane.showMessageDialog(mainFrame, infoMessage, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void addTransition(String id, Point position) {
        addTransition(id, "", position);
    }

    @Override
    public void addTransition(String id, String name, Point position) {
        if (name == null)
            name = "";

        position = shiftPositionToMinXY(position);

        /* Get the next value for zOrder. */
        int zOrder = getIncrMaxZ();

        GuiTransition newTransition = new GuiTransition(id, name, position, zOrder);
        newTransition.setName(name);

        /* Add the transition to the model. */
        try {
            addElement(newTransition);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println("GuiModel, addTransition: " + e.getMessage());
            return;
        }

        if (debug) {
            String title = this.modelName;
            String infoMessage = "Gui Transition added: " + newTransition.toString();
            System.out.println(infoMessage);

            /* Get the main frame to center the input dialog. */
            JFrame mainFrame = myGuiController.getMainFrame();

            JOptionPane.showMessageDialog(mainFrame, infoMessage, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void addArc(String id, String sourceId, String targetId) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModel.addArc", id, sourceId + targetId);
        }

        /* Get source and target objects. */
        IGuiNode source = null;
        try {
            source = getNodeById(sourceId);
        } catch (PNNoSuchElementException e) {
            // System.err.println(e.getMessage());
            System.err.println("Node " + sourceId + " for arc " + id + " not found!");
            return;
        }

        IGuiNode target = null;
        try {
            target = getNodeById(targetId);
        } catch (PNNoSuchElementException e) {
            // System.err.println(e.getMessage());
            System.err.println("Node " + targetId + " for arc " + id + " not found!");
            return;
        }

        /*
         * Create the arc.
         * 
         * Note: The arc constructor will throw an PNElementException if source
         * and target are an invalid combination of nodes.
         */
        IGuiArc newArc = null;

        /* Get the next (higher) value for zOrder. */
        int zOrder = getIncrMaxZ();

        try {
            newArc = new GuiArc(id, zOrder, source, target);
        } catch (PNElementException e) {
            // e.printStackTrace();
            System.err.println("GuiModel, addArc: " + e.getMessage());
            return;
        }

        /* Add the arc to the model. */
        try {
            addElement(newArc);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println("GuiModel, addArc: " + e.getMessage());
            return;
        }

        if (debug) {
            String title = this.modelName;
            String infoMessage = "Gui Arc added: " + newArc.toString();
            System.out.println(infoMessage);

            /* Get the main frame to center the input dialog. */
            JFrame mainFrame = myGuiController.getMainFrame();

            JOptionPane.showMessageDialog(mainFrame, infoMessage, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Adds the specified {@link IGuiElement} to this {@link IGuiModel}.
     * 
     * @param newElement
     *            The specified element
     * @throws PNDuplicateAddedException
     *             if the element already exists
     */
    private void addElement(IGuiElement newElement) throws PNDuplicateAddedException {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModel" + getModelName() + ").addElement", newElement);
        }

        /* Prevent duplicate IDs. */
        for (IGuiElement test : elements) {
            if (test == newElement) {
                String errMessage = "Duplicate of: " + test.toString();
                if (debug) {
                    System.err.println(errMessage);
                }
                throw new PNDuplicateAddedException(errMessage);
            }
        }

        /* Add the element */
        elements.add(newElement);
    }

    @Override
    public void removeElement(String id) throws PNNoSuchElementException {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModel(" + getModelName() + ").removeElement", id);
        }

        /* Find the element. */
        IGuiElement removeElement;
        try {
            removeElement = getElementById(id);
        } catch (PNNoSuchElementException e) {
            /* Pass the exception to the invoker, no new error message. */
            throw new PNNoSuchElementException(e.getMessage());
        }

        /* Remove from the list of selected elements! */
        this.selected.remove(removeElement);

        /* Remove the element. */
        elements.remove(removeElement);
    }

    @Override
    public void clear() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModel(" + getModelName() + ").clear");
        }

        elements.clear();
        selected.clear();

        /* Repaint everything. (Draw panel should be empty anyways.) */
        myGuiController.updateDrawing(null);
    }

    @Override
    public void selectSingleElement(IGuiElement element) {
        clearSelection();
        addToSelection(element);
    }

    @Override
    public void toggleSelection(IGuiElement element) {
        if (element == null)
            return;

        if (!selected.contains(element)) {
            addToSelection(element);
        } else {
            removeFromSelection(element);
        }
    }

    @Override
    public void addToSelection(IGuiElement element) {
        if (element == null)
            return;

        selected.add(element);
        element.setSelected(true);

        consoleLogSelection();
    }

    @Override
    public void clearSelection() {
        /*
         * Task: Invoke removeFromSelection() for all selected elements.
         */

        if (selected.size() == 0)
            return;

        /*
         * Add to a separate list first to avoid ConcurrentModificationException
         * when removeFromSelection() removes the element from the list!
         */
        List<IGuiElement> toBeRemoved = new ArrayList<IGuiElement>();
        for (IGuiElement element : selected) {
            toBeRemoved.add(element);
        }

        for (IGuiElement element : toBeRemoved) {
            removeFromSelection(element);
        }
    }

    /* Private helper methods */

    /**
     * Removes the specified element from the {@link List} of selected elements.
     * 
     * @param element
     *            The {@link IGuiElement} to remove
     */
    private void removeFromSelection(IGuiElement element) {
        if (element == null)
            return;

        /* Repaint its area after removing the selection from this element! */
        Rectangle oldArea = element.getLastDrawingArea();
        element.setSelected(false);
        myGuiController.updateDrawing(oldArea);

        /* Remove the element from the list of selected elements. */
        if (selected.size() == 0)
            return;
        if (!selected.contains(element))
            return;
        selected.remove(element);

        consoleLogSelection();
    }

    /**
     * Prints the ID of all selected elements to the standard output (stdout).
     */
    private void consoleLogSelection() {
        if (!debug)
            return;

        if (selected.size() == 0) {
            System.out.println("No selection");
        } else {
            System.out.print("GuiModel, Selected elements: ");

            String outputString = "";
            for (IGuiElement element : selected) {
                if (outputString != "")
                    outputString = outputString + ", ";
                outputString = outputString + element.getId();
            }

            System.out.println(outputString);
        }
    }

    /**
     * Increases x and y if the specified position is too far to the left or to
     * the top. (Takes the size of {@link GuiNode} into consideration.)
     * 
     * @param position
     *            The specified position
     * @return The new position
     */
    private Point shiftPositionToMinXY(Point position) {
        /* Increase x and y if we are too far to the left or to the top. */
        int pos_x = position.x;
        int pos_y = position.y;
        int min_x = GuiNode.getShapeSize() / 2;
        int min_y = GuiNode.getShapeSize() / 2;

        pos_x = Math.max(pos_x, min_x);
        pos_y = Math.max(pos_y, min_y);

        position = new Point(pos_x, pos_y);
        return position;
    }

    /**
     * Updates the attribute minZValue with the current minimum value for all
     * nodes. Sets the attribute to 0 if there are no nodes.
     */
    private void updateMinZ() {
        int min = 0;
        if (!elements.isEmpty()) {
            boolean first = true;
            for (IHasZValue element : elements) {
                int elemZValue = element.getZValue();
                if (first) {
                    min = elemZValue;
                    first = false;
                } else {
                    min = Math.min(min, elemZValue);
                }
            }
        }
        this.minZValue = min;
    }

    /**
     * Updates the attribute maxZValue with the current maximum value for all
     * nodes. Sets the attribute to 0 if there are no nodes.
     */
    private void updateMaxZ() {
        int max = 0;
        if (!elements.isEmpty()) {
            boolean first = true;
            for (IHasZValue element : elements) {
                int elemZValue = element.getZValue();
                if (first) {
                    max = elemZValue;
                    first = false;
                } else {
                    max = Math.max(max, elemZValue);
                }
            }
        }
        this.maxZValue = max;
    }

    /* Validation events */

    @Override
    public void setGuiStartPlace(String placeId, boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModel.setGuiStartPlace", placeId, b);
        }

        IGuiPlace place = null;
        try {
            place = getPlaceById(placeId);
        } catch (PNNoSuchElementException e) {
            System.err.println("Place " + placeId + " not found!");
            return;
        }

        place.setGuiStartPlace(b);
    }

    @Override
    public void setGuiStartPlaceCandidate(String placeId, boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModel.setGuiStartPlaceCandidate", placeId, b);
        }

        IGuiPlace place = null;
        try {
            place = getPlaceById(placeId);
        } catch (PNNoSuchElementException e) {
            System.err.println("Place " + placeId + " not found!");
            return;
        }

        place.setGuiStartPlaceCandidate(b);
    }

    @Override
    public void setGuiEndPlace(String placeId, boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModel.setGuiEndPlace", placeId, b);
        }

        IGuiPlace place = null;
        try {
            place = getPlaceById(placeId);
        } catch (PNNoSuchElementException e) {
            System.err.println("Place " + placeId + " not found!");
            return;
        }

        place.setGuiEndPlace(b);
    }

    @Override
    public void setGuiEndPlaceCandidate(String placeId, boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModel.setGuiEndPlaceCandidate", placeId, b);
        }

        IGuiPlace place = null;
        try {
            place = getPlaceById(placeId);
        } catch (PNNoSuchElementException e) {
            System.err.println("Place " + placeId + " not found!");
            return;
        }

        place.setGuiEndPlaceCandidate(b);
    }

    @Override
    public void highlightUnreachableGuiNode(String nodeId, boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModel.setEndPlace", nodeId, b);
        }

        IGuiNode node = null;
        try {
            node = getNodeById(nodeId);
        } catch (PNNoSuchElementException e) {
            System.err.println("Node " + nodeId + " not found!");
            return;
        }

        node.setUnreachable(b);
    }

    /* For the "draw new arc" overlay */

    @Override
    public void addOverlay(IOverlay overlay, EOverlayName name) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModel.addOverlay", overlay, name);
        }

        overlays.put(name, overlay);
        ConsoleLogger.logIfDebug(debug, "New overlays.size(): " + overlays.size());
    }

    @Override
    public void removeOverlay(EOverlayName name) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModel.removeOverlay", name);
        }

        overlays.remove(name);
        ConsoleLogger.logIfDebug(debug, "overlays.size(): " + overlays.size());
    }

    @Override
    public List<IOverlay> getAllOverlays() {
        /*
         * This model stores the overlays in a Map for random access, so we have
         * to convert it into a List.
         */
        List<IOverlay> list = new ArrayList<IOverlay>(this.overlays.values());
        return list;
    }

    @Override
    public IOverlay getOverlayByName(EOverlayName name) {
        return this.overlays.get(name);
    }

    /* Switching between files */

    @Override
    public void deactivated() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("GuiModel.deactivated");
        }

        /* Step 1: Cleanup (remove) of all overlays */
        for (Entry<EOverlayName, IOverlay> entry : overlays.entrySet()) {
            EOverlayName key = entry.getKey();
            // IOverlay overlay = entry.getValue();

            // if (key == EOverlayName.DRAW_NEW_ARC_OVERLAY)
            // removeOverlay(key);
            removeOverlay(key);
        }
    }

}
