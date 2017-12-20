package de.lambeck.pned.models.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.JOptionPane;

import de.lambeck.pned.elements.data.EPlaceMarking;
import de.lambeck.pned.elements.gui.*;
import de.lambeck.pned.exceptions.PNDuplicateAddedException;
import de.lambeck.pned.exceptions.PNElementException;

/**
 * Implements the GUI model of a Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class GuiModel implements IGuiModel, IModelRename {

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
    private ArrayList<IGuiElement> elements = new ArrayList<>();

    @Override
    public IGuiElement getElementById(String id) throws NoSuchElementException {
        for (IGuiElement element : elements) {
            if (element.getId() == id)
                return element;
        }

        throw new NoSuchElementException();
    }

    /**
     * List of all elements selected by the user.
     */
    private ArrayList<IGuiElement> selected = new ArrayList<>();

    private int minZValue = 0;
    private int maxZValue = 0;

    /**
     * This attribute is set if the model was modified in any way to make sure
     * that the user is asked for "file save" when closing the file.
     */
    private boolean modelModified = false;

    /**
     * Constructs a new GUI model with the name as parameter. (Name is the full
     * path of the file to make sure it is unique.)
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
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
            System.out.println("PNGuiModel created, name: " + getModelName());
        }
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

    @Override
    public List<IGuiElement> getElements() {
        return this.elements;
    }

    @Override
    public List<IGuiElement> getSelectedElements() {
        return selected;
    }

    /*
     * Methods for adding, modify and removal of elements
     */

    @Override
    public void addPlace(String id, EPlaceMarking initialMarking, Point position) {
        addPlace(id, "", initialMarking, position);
    }

    @Override
    public void addPlace(String id, String name, EPlaceMarking initialMarking, Point position) {
        if (name == null)
            name = "";

        position = shiftPositionToMinXY(position);

        /*
         * Get the next value for zOrder
         */
        int zOrder = getIncrMaxZ();

        GuiPlace newPlace = new GuiPlace(id, name, position, zOrder, initialMarking);

        /*
         * Add the place to the model
         */
        try {
            addElement(newPlace);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println("PNGuiModel, addPlace: " + e.getMessage());
            return;
        }

        if (debug) {
            String title = this.modelName;
            String infoMessage = "Gui Place added: " + newPlace.toString();
            System.out.println(infoMessage);
            JOptionPane.showMessageDialog(null, infoMessage, title, JOptionPane.INFORMATION_MESSAGE);
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

        /*
         * Get the next value for zOrder
         */
        int zOrder = getIncrMaxZ();

        GuiTransition newTransition = new GuiTransition(id, name, position, zOrder);
        newTransition.setName(name);

        /*
         * Add the transition to the model
         */
        try {
            addElement(newTransition);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println("PNGuiModel, addTransition: " + e.getMessage());
            return;
        }

        if (debug) {
            String title = this.modelName;
            String infoMessage = "Gui Transition added: " + newTransition.toString();
            System.out.println(infoMessage);
            JOptionPane.showMessageDialog(null, infoMessage, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void addArc(String id, String sourceId, String targetId) {
        /*
         * Get source and target objects
         */
        IGuiNode source = null;
        IGuiNode target = null;

        List<IGuiElement> currentElements = getElements();
        for (IGuiElement element : currentElements) {
            if (element.getId().equals(sourceId)) {
                if (element instanceof IGuiNode)
                    source = (IGuiNode) element;
            }
            if (element.getId().equals(targetId)) {
                if (element instanceof IGuiNode)
                    target = (IGuiNode) element;
            }
            if (source != null && target != null)
                break;
        }

        /*
         * Create the arc.
         * 
         * Note: The constructor of Arc will throw an PNElementException if
         * source and target are a invalid combination of nodes.
         */
        IGuiArc newArc = null;

        /*
         * Get the next value for zOrder
         */
        int zOrder = getIncrMaxZ();

        try {
            newArc = new GuiArc(id, zOrder, source, target);
        } catch (PNElementException e) {
            // e.printStackTrace();
            System.err.println("PNGuiModel, addArc: " + e.getMessage());
            return;
        }

        /*
         * Add the arc to the model
         */
        try {
            addElement(newArc);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println("PNGuiModel, addArc: " + e.getMessage());
            return;
        }

        if (debug) {
            String title = this.modelName;
            String infoMessage = "Gui Arc added: " + newArc.toString();
            System.out.println(infoMessage);
            JOptionPane.showMessageDialog(null, infoMessage, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addElement(IGuiElement newElement) throws PNDuplicateAddedException {
        if (debug) {
            System.out.println("PNGuiModel(" + getModelName() + ").addElement()");
        }

        for (IGuiElement test : elements) {
            if (test == newElement) { throw new PNDuplicateAddedException("Duplicate of: " + test.toString()); }
        }
        elements.add(newElement);
        this.modelModified = true;
    }

    @Override
    public void removeElement(String id) throws NoSuchElementException {
        for (IGuiElement test : elements) {
            if (test.getId() == id) {
                /*
                 * Remove from the list of selected elements!
                 */
                this.selected.remove(test);

                /*
                 * Remove the element.
                 */
                this.modelModified = elements.remove(test);
                return;
            }
        }

        throw new NoSuchElementException("Id " + id + " not found in GUI model " + this.getModelName());
    }

    @Override
    public void clear() {
        if (debug) {
            System.out.println("PNGuiModel(" + getModelName() + ").clear()");
        }

        elements.clear();
        selected.clear();
        this.modelModified = true;

        /*
         * Repaint everything (Drawing should be empty anyways.)
         */
        myGuiController.updateDrawing(null);
    }

    /*
     * Helper methods
     */

    /**
     * Increases x and y if the specified position is too far to the left or to
     * the top. (Takes the size of {@link GuiNode} into consideration.)
     * 
     * @param position
     *            The specified position
     * @return The new position
     */
    private Point shiftPositionToMinXY(Point position) {
        /*
         * Increase x and y if we are too far to the left or to the top.
         */
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

    /*
     * Methods for adding, modify and removal of elements
     */

    @Override
    public void selectSingleElement(IGuiElement element) {
        clearSelection();
        addToSelection(element);
    }

    @Override
    public void toggleSelection(IGuiElement element) {
        if (element == null)
            return;

        // List<IGuiElement> selected = myGuiModel.getSelectedElements();

        if (!selected.contains(element)) {
            addToSelection(element);
        } else {
            removeFromSelection(element);
        }
    }

    // @Override
    // public void toggleMarking(IGuiPlace place) {
    // // TODO obsolete?
    // if (place == null)
    // return;
    // }

    /*
     * Private helper methods
     */

    private void addToSelection(IGuiElement element) {
        if (element == null)
            return;

        // List<IGuiElement> selected = currentModel.getSelectedElements();

        selected.add(element);
        element.setSelected(true);

        consoleLogSelection();
    }

    /**
     * Removes the specified element from the selected elements.
     * 
     * @param element
     *            The element to remove from selection
     */
    private void removeFromSelection(IGuiElement element) {
        if (element == null)
            return;

        /*
         * Repaint its area after removing the selection from this element!
         */
        Rectangle oldArea = element.getLastDrawingArea();
        element.setSelected(false);
        myGuiController.updateDrawing(oldArea);

        /*
         * Remove the element from the list of selected elements.
         */
        if (selected.size() == 0)
            return;
        if (!selected.contains(element))
            return;
        selected.remove(element);

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

    private void consoleLogSelection() {
        if (selected.size() == 0) {
            System.out.println("No selection");
        } else {
            System.out.print("Selected elements: ");

            String outputString = "";
            for (IGuiElement element : selected) {
                if (outputString != "")
                    outputString = outputString + ", ";
                outputString = outputString + element.getId();
            }

            System.out.println(outputString);
        }
    }

}
