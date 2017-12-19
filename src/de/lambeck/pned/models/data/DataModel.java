package de.lambeck.pned.models.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.JOptionPane;

import de.lambeck.pned.elements.data.*;
import de.lambeck.pned.exceptions.PNDuplicateAddedException;
import de.lambeck.pned.exceptions.PNElementException;

/**
 * Implements the data model (for 1 Petri net).
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class DataModel implements IDataModel, IModelRename {

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
     * List of all elements in this model
     */
    private ArrayList<IDataElement> elements = new ArrayList<>();

    /**
     * This attribute is set if the model was modified in any way to make sure
     * that the user is asked for "file save" when closing the file.
     */
    private boolean modelModified = false;

    /**
     * Constructs the date model with a specified name.
     * 
     * Intended use: name == path of the pnml file
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     * @param displayName
     *            The name of the tab (the file name only)
     */
    @SuppressWarnings("hiding")
    public DataModel(String modelName, String displayName) {
        super();
        this.modelName = modelName;
        this.displayName = displayName;

        if (debug) {
            System.out.println("DataModel created, name: " + getDisplayName() + ", " + getModelName());
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
    public boolean isModified() {
        return this.modelModified;
    }

    @Override
    public void setModified(boolean b) {
        this.modelModified = b;
    }

    @Override
    public List<IDataElement> getElements() {
        return elements;
    }

    @Override
    public IDataElement getElementById(String id) throws NoSuchElementException {
        for (IDataElement element : elements) {
            if (element.getId() == id)
                return element;
        }

        throw new NoSuchElementException();
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

        DataPlace newPlace = new DataPlace(id, name, position, initialMarking);

        /*
         * Add the place to the model
         */
        try {
            addElement(newPlace);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println("DataModel, addPlace: " + e.getMessage());
            return;
        }

        if (debug) {
            String title = this.modelName;
            String infoMessage = "Data Place added: " + newPlace.toString();
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

        DataTransition newTransition = new DataTransition(id, name, position);
        newTransition.setName(name);

        /*
         * Add the transition to the model
         */
        try {
            addElement(newTransition);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println("DataModel, addTransition: " + e.getMessage());
            return;
        }

        if (debug) {
            String title = this.modelName;
            String infoMessage = "Data Transition added: " + newTransition.toString();
            System.out.println(infoMessage);
            JOptionPane.showMessageDialog(null, infoMessage, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void addArc(String id, String sourceId, String targetId) {
        IDataNode source = null;
        IDataNode target = null;
        IDataArc newArc = null;

        /*
         * Get source and target objects
         */
        List<IDataElement> currentElements = getElements();
        for (IDataElement element : currentElements) {
            if (element.getId().equals(sourceId)) {
                if (element instanceof IDataNode)
                    source = (IDataNode) element;
            }
            if (element.getId().equals(targetId)) {
                if (element instanceof IDataNode)
                    target = (IDataNode) element;
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
        try {
            newArc = new DataArc(id, source, target);
        } catch (PNElementException e) {
            // e.printStackTrace();

            /*
             * Show an error message
             */
            String title = "Arc, id = " + id;
            // String errorMessage = e.getMessage();
            String errorMessage = "DataModel, addArc: " + e.getMessage();
            System.err.println(errorMessage);
            JOptionPane.showMessageDialog(null, errorMessage, title, JOptionPane.WARNING_MESSAGE);

            return;
        }

        /*
         * Add the arc to the model
         */
        try {
            addElement(newArc);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println("DataModel, addArc: " + e.getMessage());
            return;
        }

        if (debug) {
            String title = this.modelName;
            String infoMessage = "Data Arc added: " + newArc.toString();
            System.out.println(infoMessage);
            JOptionPane.showMessageDialog(null, infoMessage, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addElement(IDataElement newElement) throws PNDuplicateAddedException {
        if (debug) {
            System.out.println("DataModel(" + getModelName() + ").addElement()");
        }

        for (IDataElement test : elements) {
            if (test == newElement) { throw new PNDuplicateAddedException("Duplicate of: " + test.toString()); }
        }
        elements.add(newElement);
        this.modelModified = true;
    }

    @Override
    public void removeElement(String id) throws NoSuchElementException {
        for (IDataElement test : elements) {
            if (test.getId() == id) {
                this.modelModified = elements.remove(test);
                return;
            }
        }

        throw new NoSuchElementException("Id " + id + " not found in data model " + this.getModelName());
    }

    @Override
    public void clear() {
        if (debug) {
            System.out.println("DataModel(" + getModelName() + ").clear()");
        }

        elements.clear();
        this.modelModified = true;
    }

}
