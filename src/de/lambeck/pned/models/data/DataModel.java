package de.lambeck.pned.models.data;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.elements.data.*;
import de.lambeck.pned.exceptions.PNDuplicateAddedException;
import de.lambeck.pned.exceptions.PNElementCreationException;
import de.lambeck.pned.exceptions.PNNoSuchElementException;
import de.lambeck.pned.models.data.validation.InitialMarkingValidator;
import de.lambeck.pned.util.ConsoleLogger;
import de.lambeck.pned.util.ObjectCloner;

/**
 * Implements the data model (for 1 Petri net).
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class DataModel implements IDataModel, IModelRename, Serializable {

    /**
     * Generated serial version ID (necessary for the {@link ObjectCloner})
     */
    private static final long serialVersionUID = 7516845139667473831L;

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
     * List of all elements in this model
     */
    private List<IDataElement> elements = new ArrayList<>();

    /**
     * This attribute is set if the model was modified in any way to make sure
     * that the user is asked for "file save" when closing the file.
     */
    private boolean modelModified = false;

    /**
     * Will be set to false after any structural modification of the model. Will
     * be set to true when the validated begins.
     */
    private volatile boolean modelChecked = false;

    /**
     * Will be set to false as soon as "modelChecked" is set to true for the
     * first time.<BR>
     * <BR>
     * Note: Used in {@link InitialMarkingValidator} to avoid resetting the
     * initial marking when loading from a PNML file.
     */
    private volatile boolean initialModelCheck = true;

    /** Will be set to true if the model is valid otherwise to false */
    private volatile boolean modelValid = false;

    /* Constructor */

    /**
     * Constructs the data model with a specified name.<BR>
     * <BR>
     * Intended use: name == path of the PNML file
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
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

    /* Getter and Setter */

    /* Interface IModel (+ IModelRename) */

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
    public boolean isInitialModelCheck() {
        return this.initialModelCheck;
    }

    @Override
    public void setModified(boolean b) {
        setModified(b, true);
    }

    @Override
    public void setModified(boolean b, boolean revalidate) {
        this.modelModified = b; // Info for FileCloseAction

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
    public void setModelChecked(boolean b, boolean removeInitialCheckState) {
        this.modelChecked = b;

        if (removeInitialCheckState) {
            /* Remove the abort condition for "initial validations". */
            this.initialModelCheck = false;
        }
    }

    @Override
    public boolean isModelValid() {
        return this.modelValid;
    }

    @Override
    public void setModelValidity(boolean b) {
        this.modelValid = b;
    }

    /* Methods for adding, modify and removal of elements */

    /* Add elements */

    @Override
    public void addPlace(String id, EPlaceToken initialTokens, Point position) {
        addPlace(id, "", initialTokens, position);
    }

    @Override
    public void addPlace(String id, String name, EPlaceToken initialTokens, Point position) {
        if (name == null)
            name = "";

        DataPlace newPlace = new DataPlace(id, name, position, initialTokens);

        /* Add the place to the model. */
        try {
            addElement(newPlace);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println("DataModel, addPlace: " + e.getMessage());
            return;
        }

        if (debug) {
            String infoMessage = "Data Place added: " + newPlace.toString();
            System.out.println(infoMessage);
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

        /* Add the transition to the model. */
        try {
            addElement(newTransition);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println("DataModel, addTransition: " + e.getMessage());
            return;
        }

        if (debug) {
            String infoMessage = "Data Transition added: " + newTransition.toString();
            System.out.println(infoMessage);
        }
    }

    @Override
    public void addArc(String id, String sourceId, String targetId) throws PNElementCreationException {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModel.addArc", id, sourceId + targetId);
        }

        /* Get source and target objects. */
        IDataNode source = null;
        try {
            source = getNodeById(sourceId);
        } catch (PNNoSuchElementException e) {
            // System.err.println(e.getMessage());
            String message = "DataModel" + this.getModelName() + "), addArc: " + "Node " + sourceId + " for arc " + id
                    + " not found!";
            throw new PNElementCreationException(message);
        }

        IDataNode target = null;
        try {
            target = getNodeById(targetId);
        } catch (PNNoSuchElementException e) {
            // System.err.println(e.getMessage());
            String message = "DataModel" + this.getModelName() + "), addArc: " + "Node " + targetId + " for arc " + id
                    + " not found!";
            throw new PNElementCreationException(message);
        }

        /* Create the arc. */
        IDataArc newArc = null;
        try {
            newArc = new DataArc(id, source, target);
        } catch (PNElementCreationException e) {
            String message = "DataModel" + this.getModelName() + "), addArc: " + e.getMessage();
            throw new PNElementCreationException(message);
        }

        /* Add the arc to the model. */
        try {
            addElement(newArc);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println("DataModel, addArc: " + e.getMessage());
            return;
        }

        if (debug) {
            String infoMessage = "Data Arc added: " + newArc.toString();
            System.out.println(infoMessage);
        }
    }

    /**
     * Adds the specified {@link IDataElement} to this {@link IDataModel}.
     * 
     * @param newElement
     *            The specified element
     * @throws PNDuplicateAddedException
     *             If the element already exists
     */
    private void addElement(IDataElement newElement) throws PNDuplicateAddedException {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModel(" + getModelName() + ").addElement", newElement);
        }

        /* Prevent duplicate IDs. */
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
     * of the affected nodes!<BR>
     * <BR>
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

        /* Get the IDs of the necessary predecessor and successor. */
        String sourceId = arc.getSourceId();
        String targetId = arc.getTargetId();

        /* Get source and target node. */
        IDataNode pred = null;
        try {
            pred = getNodeById(sourceId);
        } catch (PNNoSuchElementException e) {
            String errMessage = "Source is not a node!: " + sourceId;
            System.err.println(errMessage);
            return;
        }

        IDataNode succ = null;
        try {
            succ = getNodeById(targetId);
        } catch (PNNoSuchElementException e) {
            String errMessage = "Target is not a node!: " + targetId;
            System.err.println(errMessage);
            return;
        }

        /* Add the specified arc to the predecessors successor list. */
        try {
            pred.addSucc(arc);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println(pred.getId() + ".addSucc(." + arc.getId() + "):");
            System.err.println(e.getMessage());
        }

        /* ...and to the successors predecessor list. */
        try {
            succ.addPred(arc);
        } catch (PNDuplicateAddedException e) {
            // e.printStackTrace();
            System.err.println(succ.getId() + ".addPred(." + arc.getId() + "):");
            System.err.println(e.getMessage());
        }
    }

    /* Remove methods for elements */

    @Override
    public void removeElement(String id) throws PNNoSuchElementException {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModel(" + getModelName() + ").removeElement", id);
        }

        /* Find the element. */
        IDataElement removeElement;
        try {
            removeElement = getElementById(id);
        } catch (PNNoSuchElementException e) {
            /* Pass the exception to the invoker, no new error message. */
            throw new PNNoSuchElementException(e.getMessage());
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
     * list of all nodes!<BR>
     * <BR>
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

        /* Remove the specified arc from all predecessors and successors. */
        for (IDataElement element : this.elements) {
            if (element instanceof IDataNode) {
                IDataNode node = (IDataNode) element;

                try {
                    node.removeSucc(arc);
                } catch (PNNoSuchElementException ignore) {
                    // NOP
                }

                try {
                    node.removePred(arc);
                } catch (PNNoSuchElementException ignore) {
                    // NOP
                }
            }
        }
    }

    @Override
    public void clear() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModel(" + getModelName() + ").clear");
        }

        elements.clear();
    }

    /* Interface IDataModel */

    @Override
    public List<IDataElement> getElements() {
        List<IDataElement> copy = new ArrayList<IDataElement>(this.elements);
        return copy;
    }

    @Override
    public IDataElement getElementById(String id) throws PNNoSuchElementException {
        for (IDataElement element : this.elements) {
            String elementID = element.getId();
            if (elementID.equalsIgnoreCase(id))
                return element;
        }

        String errorMessage = "Model " + this.modelName + ": element " + id + " not found!";
        ConsoleLogger.logIfDebug(debug, errorMessage);
        throw new PNNoSuchElementException(errorMessage);
    }

    @Override
    public IDataNode getNodeById(String nodeId) throws PNNoSuchElementException {
        IDataElement element;
        try {
            element = getElementById(nodeId);
        } catch (PNNoSuchElementException e) {
            /* Pass the exception to the invoker, no new error message. */
            throw new PNNoSuchElementException(e.getMessage());
        }

        if (element instanceof IDataNode) {
            IDataNode node = (IDataNode) element;
            return node;
        }

        String errorMessage = "Model " + this.modelName + ": node " + nodeId + " not found!";
        ConsoleLogger.logIfDebug(debug, errorMessage);
        throw new PNNoSuchElementException(errorMessage);
    }

    @Override
    public IDataPlace getPlaceById(String placeId) throws PNNoSuchElementException {
        IDataElement element;
        try {
            element = getElementById(placeId);
        } catch (PNNoSuchElementException e) {
            /* Pass the exception to the invoker, no new error message. */
            throw new PNNoSuchElementException(e.getMessage());
        }

        if (element instanceof IDataPlace) {
            IDataPlace place = (IDataPlace) element;
            return place;
        }

        String errorMessage = "Model " + this.modelName + ": place " + placeId + " not found!";
        ConsoleLogger.logIfDebug(debug, errorMessage);
        throw new PNNoSuchElementException(errorMessage);
    }

    @Override
    public IDataTransition getTransitionById(String transitionId) throws PNNoSuchElementException {
        IDataElement element;
        try {
            element = getElementById(transitionId);
        } catch (PNNoSuchElementException e) {
            /* Pass the exception to the invoker, no new error message. */
            throw new PNNoSuchElementException(e.getMessage());
        }

        if (element instanceof IDataTransition) {
            IDataTransition transition = (IDataTransition) element;
            return transition;
        }

        String errorMessage = "Model " + this.modelName + ": transition " + transitionId + " not found!";
        ConsoleLogger.logIfDebug(debug, errorMessage);
        throw new PNNoSuchElementException(errorMessage);
    }

    @Override
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    /*
     * Interface IUndoRedo (The other methods of this interface are already
     * implemented.)
     */

    // @Override
    // public void setElements(List<IDataElement> newElements) {
    // List<IDataElement> copy = new ArrayList<IDataElement>(newElements);
    // this.elements = copy;
    // }

}
