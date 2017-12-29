package de.lambeck.pned.models.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.JOptionPane;

import de.lambeck.pned.elements.data.*;
import de.lambeck.pned.exceptions.PNDuplicateAddedException;
import de.lambeck.pned.exceptions.PNElementException;
import de.lambeck.pned.util.ConsoleLogger;

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
    private List<IDataElement> elements = new ArrayList<>();

    /**
     * This attribute is set if the model was modified in any way to make sure
     * that the user is asked for "file save" when closing the file.
     */
    private boolean modelModified = false;

    /**
     * Will be set to false any time the model will be changed/modified Will be
     * set to true after the model has been validated to true or false
     */
    private boolean modelChecked = false;

    /**
     * Will be set to true if the model is valid other wise to false
     */
    private boolean modelValid = false;

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

    /*
     * Interface IModel (+ IModelRename)
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
        setModified(b, true);
    }

    @Override
    public void setModified(boolean b, boolean revalidate) {
        this.modelModified = b; // Info for FileClose

        if (revalidate) {
            this.modelChecked = false; // Info for the ValidationController
            this.modelValid = false; // Info for the ValidationController
        }
    }

    @Override
    public boolean isModelChecked() {
        return this.modelChecked;
    }

    @Override
    public void setModelChecked(boolean b) {
        this.modelChecked = b;
    }

    @Override
    public boolean isModelValid() {
        return this.modelValid;
    }

    @Override
    public void setModelValidity(boolean b) {
        this.modelValid = b;
    }

    /*
     * Methods for adding, modify and removal of elements
     */

    /*
     * Add elements
     */

    @Override
    public void addPlace(String id, EPlaceToken initialTokens, Point position) {
        addPlace(id, "", initialTokens, position);
    }

    @Override
    public void addPlace(String id, String name, EPlaceToken initialTokens, Point position) {
        if (name == null)
            name = "";

        DataPlace newPlace = new DataPlace(id, name, position, initialTokens);

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
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModel.addArc", id, sourceId + targetId);
        }

        IDataNode source = null;
        IDataNode target = null;
        IDataArc newArc = null;

        /*
         * Get source and target objects
         */
        List<IDataElement> currentElements = getElements();
        for (IDataElement element : currentElements) {
            String elementId = element.getId();

            if (elementId.equals(sourceId)) {
                if (debug) {
                    System.out.println("elementId.equals(sourceId): " + sourceId);
                }

                if (element instanceof IDataNode)
                    source = (IDataNode) element;
            }

            if (elementId.equals(targetId)) {
                if (debug) {
                    System.out.println("elementId.equals(targetId): " + targetId);
                }

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

    /**
     * Adds the specified {@link IDataElement} to this {@link DataModel}.
     * 
     * @param newElement
     *            The new element
     */
    private void addElement(IDataElement newElement) throws PNDuplicateAddedException {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModel(" + getModelName() + ").addElement", newElement.getId());
        }

        /*
         * Prevent duplicate IDs.
         */
        for (IDataElement test : elements) {
            if (test == newElement) {
                String errMessage = "Duplicate of: " + test.toString();
                if (debug) {
                    System.err.println(errMessage);
                }
                throw new PNDuplicateAddedException(errMessage);
            }
        }

        /* Add the element */
        this.elements.add(newElement);

        /*
         * If the added element was an arc: update the predecessor and successor
         * list of the affected nodes!
         */
        if (newElement instanceof DataArc) {
            DataArc arc = (DataArc) newElement;
            addArcToAffectedNodes(arc);
        }
    }

    /**
     * Adds the specified {@link DataArc} to the predecessor and successor list
     * of the affected nodes!
     * 
     * Note: A DataArc is specified by the own ID and the IDs of predecessor and
     * successor.
     * 
     * @param arc
     *            The specified arc
     */
    private void addArcToAffectedNodes(DataArc arc) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModel(" + getModelName() + ").addArcToAffectedNodes", arc.getId());
        }

        /*
         * Get the IDs of the necessary predecessor and successor.
         */
        String sourceId = arc.getSourceId();
        String targetId = arc.getTargetId();

        /*
         * Get source and target node.
         */
        IDataElement sourceElement = getElementById(sourceId);
        if (!(sourceElement instanceof IDataNode)) {
            String errMessage = "Source is not a node!: " + sourceId;
            System.err.println(errMessage);
            return;
        }
        IDataNode pred = (IDataNode) sourceElement;

        IDataElement targetElement = getElementById(targetId);
        if (!(targetElement instanceof IDataNode)) {
            String errMessage = "Target is not a node!: " + targetId;
            System.err.println(errMessage);
            return;
        }
        IDataNode succ = (IDataNode) targetElement;

        /*
         * Add the specified arc to the predecessors successor list.
         */
        try {
            pred.addSucc(arc);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println(pred.getId() + ".addSucc(." + arc.getId() + "):");
            System.err.println(e.getMessage());
        }

        /*
         * ...and to the successors predecessor list.
         */
        try {
            succ.addPred(arc);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println(succ.getId() + ".addPred(." + arc.getId() + "):");
            System.err.println(e.getMessage());
        }
    }

    /*
     * Remove methods for elements
     */

    @Override
    public void removeElement(String id) throws NoSuchElementException {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModel(" + getModelName() + ").removeElement", id);
        }

        /*
         * Find the element.
         */
        IDataElement removeElement = null;
        for (IDataElement test : elements) {
            if (test.getId() == id) {
                removeElement = test;
                break;
            }
        }

        if (removeElement == null) {
            String errorMessage = "Id " + id + " not found in data model " + this.getModelName();
            if (debug) {
                System.err.println(errorMessage);
            }
            throw new NoSuchElementException(errorMessage);
        }

        /* Remove the element */
        elements.remove(removeElement);

        /*
         * If the removed element was an arc: update the predecessor and
         * successor list of the affected nodes!
         */
        if (removeElement instanceof IDataArc) {
            IDataArc arc = (IDataArc) removeElement;
            removeArcFromAllNodes(arc);
        }
    }

    /**
     * Removes the specified {@link DataArc} from the predecessor and successor
     * list of all nodes!
     * 
     * Note: A DataArc is specified by the own ID and the IDs of predecessor and
     * successor.
     * 
     * @param arc
     *            The specified arc
     */
    private void removeArcFromAllNodes(IDataArc arc) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModel(" + getModelName() + ").removeArcFromAffectedNodes",
                    arc.getId());
        }

        /*
         * Remove the specified arc from all predecessors and successors.
         */
        for (IDataElement element : elements) {
            if (element instanceof IDataNode) {
                IDataNode node = (IDataNode) element;

                try {
                    node.removeSucc(arc);
                } catch (NoSuchElementException ignore) {
                    // Do nothing
                }

                try {
                    node.removePred(arc);
                } catch (NoSuchElementException ignore) {
                    // Do nothing
                }
            }
        }
    }

    // /**
    // * Returns a List of all {@link DataNode} in this {@link DataModel} which
    // * have the specified {@link DataArc} in their List of successors.
    // *
    // * @param arc
    // * The specified DataArc
    // * @return A List of DataNodes
    // */
    // private List<IDataNode> getPredNodes(IDataArc arc) {
    // if (debug) {
    // System.out.println("DataModel(" + getModelName() + ").getPredNodes(" +
    // "id=" + arc.getId() + ")");
    // }
    //
    // List<IDataNode> nodes = new LinkedList<IDataNode>();
    // List<IDataNode> predNodes = new LinkedList<IDataNode>();
    //
    // /*
    // * Get all nodes.
    // */
    // for (IDataElement element : this.elements) {
    // if (element instanceof IDataNode) {
    // IDataNode node = (IDataNode) element;
    // nodes.add(node);
    // }
    // }
    //
    // /*
    // * Get all predecessors.
    // */
    // for (IDataNode node : nodes) {
    // if (node.succListContains(arc)) {
    // predNodes.add(node);
    // }
    // }
    //
    // return predNodes;
    // }

    // /**
    // * Returns a List of all {@link DataNode} in this {@link DataModel} which
    // * have the specified {@link DataArc} in their List of predecessors.
    // *
    // * @param arc
    // * The specified DataArc
    // * @return A List of DataNodes
    // */
    // private List<IDataNode> getSuccNodes(IDataArc arc) {
    // if (debug) {
    // System.out.println("DataModel(" + getModelName() + ").getSuccNodes(" +
    // "id=" + arc.getId() + ")");
    // }
    //
    // List<IDataNode> nodes = new LinkedList<IDataNode>();
    // List<IDataNode> succNodes = new LinkedList<IDataNode>();
    //
    // /*
    // * Get all nodes.
    // */
    // for (IDataElement element : this.elements) {
    // if (element instanceof IDataNode) {
    // IDataNode node = (IDataNode) element;
    // nodes.add(node);
    // }
    // }
    //
    // /*
    // * Get all successors.
    // */
    // for (IDataNode node : nodes) {
    // if (node.predListContains(arc)) {
    // succNodes.add(node);
    // }
    // }
    //
    // return succNodes;
    // }

    @Override
    public void clear() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModel(" + getModelName() + ").clear");
        }

        elements.clear();
    }

    /*
     * Interface IDataModel
     */

    @Override
    public List<IDataElement> getElements() {
        // return this.elements;
        List<IDataElement> copy = new ArrayList<IDataElement>(this.elements);
        return copy;
    }

    @Override
    public IDataElement getElementById(String id) throws NoSuchElementException {
        for (IDataElement element : this.elements) {
            if (element.getId() == id)
                return element;
        }

        throw new NoSuchElementException();
    }

}
