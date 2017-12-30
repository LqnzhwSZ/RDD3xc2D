package de.lambeck.pned.models.data.validation;

import java.util.*;

import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.data.IDataArc;
import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.elements.data.IDataNode;
import de.lambeck.pned.exceptions.PNElementException;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;

/**
 * Checks if all nodes are on a path between the start place and the end place.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class AllNodesOnPathsValidator extends AbstractValidator {

    private static boolean debug = false;

    /** The start place of the model (if unambiguous) */
    private DataPlace myStartPlace = null;

    /** The end place of the model (if unambiguous) */
    private DataPlace myEndPlace = null;

    /**
     * A {@link List} of all {@link IDataElement} in the model; Gets data in
     * getDataFromModel(IDataModel dataModel).
     */
    private List<IDataElement> allElements = null;

    /**
     * A {@link Map} of all {@link IDataNode} in the model used to fast lookup
     * for the target of the arcs.
     */
    private Map<String, IDataNode> allNodes = new HashMap<String, IDataNode>();

    /**
     * A {@link List} of all {@link IDataElement} in the model that were reached
     * during recursive forward traversal
     */
    private List<IDataNode> forwardTraversedNodes = new ArrayList<IDataNode>();

    /**
     * A {@link List} of all {@link IDataElement} in the model that were reached
     * during recursive backward traversal
     */
    private List<IDataNode> backwardTraversedNodes = new ArrayList<IDataNode>();

    /**
     * {@link List} of all {@link IDataElement} in the model that are not
     * connected to the start place (test: recursive forward traversal).
     */
    private List<IDataNode> noPathFromStartNode = new ArrayList<IDataNode>();

    /**
     * {@link List} of all {@link IDataElement} in the model that are not
     * connected to the end place (test: recursive backward traversal).
     */
    private List<IDataNode> noPathToEndNode = new ArrayList<IDataNode>();

    /*
     * Constructor
     */

    /**
     * @param Id
     *            The ID of this validator (for validation messages)
     * @param validationController
     *            The {@link IValidationController}
     * @param dataModelController
     *            The {@link IDataModelController}
     * @param i18n
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    public AllNodesOnPathsValidator(int Id, IValidationController validationController,
            IDataModelController dataModelController, I18NManager i18n) {
        super(Id, validationController, dataModelController, i18n);
        this.validatorInfoString = "infoAllNodesOnPathsValidator";
    }

    /*
     * Validation methods
     */

    @Override
    public void startValidation(IDataModel dataModel, boolean initialModelCheck) {
        getDataFromModel(dataModel);
        // this.isInitialModelCheck = initialModelCheck;
        /* Note: This validator doesn't use "initialModelCheck". */

        addValidatorInfo();

        /*
         * Check condition 1: exactly 1 start and end place
         */
        if (!evaluateStartAndEndPlace())
            return;

        /*
         * Initialize the lists because this validator gets called for different
         * models!
         */
        initializeLists();
        resetPrevUnreachableHighlighting();

        /*
         * Check condition 2: all nodes reachable from the start place?
         */
        traverseAllNodesForward(myStartPlace);
        highlightUnreachableNodes(noPathFromStartNode);
        int noPathFromStart = noPathFromStartNode.size();
        if (noPathFromStart > 0)
            reportFailed_NoPathFromStartPlace(noPathFromStart);

        /*
         * Check condition 3: all nodes can reach the end place?
         */
        traverseAllNodesBackward(myEndPlace);
        highlightUnreachableNodes(noPathToEndNode);
        int noPathToEnd = noPathToEndNode.size();
        if (noPathToEnd > 0)
            reportFailed_NoPathToEndPlace(noPathToEnd);

        /*
         * Evaluate result of test 2 and 3
         */
        int unreachableNodesCount = noPathFromStart + noPathToEnd;
        if (unreachableNodesCount > 0)
            return;

        /*
         * All nodes are on paths between start place and end place, test
         * successful
         */
        reportValidationSuccessful();
    }

    @Override
    protected void getDataFromModel(IDataModel dataModel) {
        this.myDataModel = dataModel;
        this.myDataModelName = dataModel.getModelName();

        /* Additional info */
        List<IDataElement> modelElements = myDataModel.getElements();
        this.allElements = new ArrayList<IDataElement>(modelElements);
    }

    /*
     * For check 1
     */

    /**
     * Stores the (unique) start and end place in the local attributes
     * startPlace and endPlace.
     * 
     * @return True if start and end place are unique; otherwise false
     */
    private boolean evaluateStartAndEndPlace() {
        /*
         * Store all start and end places. (Calculate them locally to be
         * independent from other validators!)
         */
        boolean result = true;

        DataPlace unambiguousStartPlace = getUnambiguousStartPlace();
        if (unambiguousStartPlace != null) {
            this.myStartPlace = unambiguousStartPlace;
        } else {
            String message = i18n.getMessage("warningValidatorNoUnambiguousStartPlace");
            IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.CRITICAL);
            validationMessages.add(vMessage);

            result = false;
        }

        DataPlace unambiguousEndPlace = getUnambiguousEndPlace();
        if (unambiguousEndPlace != null) {
            this.myEndPlace = unambiguousEndPlace;
        } else {
            String message = i18n.getMessage("warningValidatorNoUnambiguousEndPlace");
            IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.CRITICAL);
            validationMessages.add(vMessage);

            result = false;
        }

        return result;
    }

    /*
     * For check 2 and 3
     */

    /**
     * Initializes the Lists and Maps to add/remove nodes during validation.
     * 
     * Note: Every time this validator is called to validate another model,
     * these lists need to be "reset".
     */
    private void initializeLists() {
        /* Reset */
        this.allNodes.clear();
        this.forwardTraversedNodes.clear();
        this.backwardTraversedNodes.clear();
        this.noPathFromStartNode.clear();
        this.noPathToEndNode.clear();

        /* Add all nodes to List "allNodes" */
        for (IDataElement element : allElements) {
            if (element instanceof IDataNode) {
                String nodeId = element.getId();
                IDataNode node = (IDataNode) element;
                this.allNodes.put(nodeId, node);
            }
        }

        /* Assume all nodes are unreachable. */
        for (Map.Entry<String, IDataNode> entry : allNodes.entrySet()) {
            // String id = entry.getKey();
            IDataNode node = entry.getValue();
            this.noPathFromStartNode.add(node);
            this.noPathToEndNode.add(node);
        }

        if (debug) {
            System.out.println("AllNodesOnPathsValidator.allElements.size(): " + allElements.size());
            System.out.println("AllNodesOnPathsValidator.allNodes.size(): " + allNodes.size());
            System.out.println("AllNodesOnPathsValidator.noPathFromStartNode.size(): " + noPathFromStartNode.size());
            System.out.println("AllNodesOnPathsValidator.noPathToEndNode.size(): " + noPathToEndNode.size());
        }
    }

    /**
     * Informs the data model controller to reset the "unreachable" highlighting
     * on all nodes before starting a new validation.
     */
    private void resetPrevUnreachableHighlighting() {
        for (Map.Entry<String, IDataNode> entry : allNodes.entrySet()) {
            String nodeId = entry.getKey();
            // IDataNode node = entry.getValue();
            // String nodeId = node.getId();
            myDataModelController.highlightUnreachableDataNode(myDataModelName, nodeId, false);
        }
    }

    /**
     * Recursively traverses all connected nodes in forward direction to find
     * all reachable nodes.
     * 
     * @param curNode
     *            The {@link IDataNode} to start the recursion
     */
    private void traverseAllNodesForward(IDataNode curNode) {
        /* Check if we were here already! */
        if (forwardTraversedNodes.contains(curNode))
            return;

        /* Add node to traversed and remove from unreachable nodes. */
        forwardTraversedNodes.add(curNode);
        noPathFromStartNode.remove(curNode);

        /* Go (forward) to all following nodes. */
        List<IDataArc> arcs = curNode.getSuccElems();
        for (IDataElement element : arcs) {
            /* These arcs always have a successor */
            IDataArc arc = (IDataArc) element;
            IDataNode succ = null;
            try {
                succ = arc.getSuccElem();
            } catch (PNElementException e) {
                e.printStackTrace();
            }

            /* Recursive call */
            traverseAllNodesForward(succ);
        }
    }

    /**
     * Recursively traverses all connected nodes in backward direction to find
     * all reachable nodes.
     * 
     * @param curNode
     *            The {@link IDataNode} to start the recursion
     */
    private void traverseAllNodesBackward(IDataNode curNode) {
        /* Check if we were here already! */
        if (backwardTraversedNodes.contains(curNode))
            return;

        /* Add node to traversed and remove from unreachable nodes. */
        backwardTraversedNodes.add(curNode);
        noPathToEndNode.remove(curNode);

        /* Go (backward) to all previous nodes. */
        List<IDataArc> arcs = curNode.getPredElems();
        for (IDataElement element : arcs) {
            /* These arcs always have a predecessor. */
            IDataArc arc = (IDataArc) element;
            IDataNode pred = null;
            try {
                pred = arc.getPredElem();
            } catch (PNElementException e) {
                e.printStackTrace();
            }

            /* Recursive call */
            traverseAllNodesBackward(pred);
        }
    }

    /**
     * Informs the data model controller which nodes need to be highlighted as
     * unreachable nodes.
     * 
     * @param unreachableNodes
     *            The {@link List} of unreachable {@link IDataNode}
     */
    private void highlightUnreachableNodes(List<IDataNode> unreachableNodes) {
        for (IDataNode node : unreachableNodes) {
            String nodeId = node.getId();
            myDataModelController.highlightUnreachableDataNode(myDataModelName, nodeId, true);
        }
    }

    /*
     * Messages
     */

    /**
     * Adds an {@link IValidationMsg} with the number of unreachable nodes and
     * status "CRITICAL" to the messages list.
     * 
     * @param number
     *            The number of {@link IDataNode} that have no connection to the
     *            start place
     */
    private void reportFailed_NoPathFromStartPlace(int number) {
        String message = i18n.getMessage("warningValidatorNoPathFromStartPlace");
        message = message.replace("%number%", Integer.toString(number));
        IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.CRITICAL);
        validationMessages.add(vMessage);
    }

    /**
     * Adds an {@link IValidationMsg} with the number of unreachable nodes and
     * status "CRITICAL" to the messages list.
     * 
     * @param number
     *            The number of {@link IDataNode} that have no connection to the
     *            end place
     */
    private void reportFailed_NoPathToEndPlace(int number) {
        String message = i18n.getMessage("warningValidatorNoPathToEndPlace");
        message = message.replace("%number%", Integer.toString(number));
        IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.CRITICAL);
        validationMessages.add(vMessage);
    }

    /*
     * Private helpers
     */

    /**
     * Determines the unambiguous start place of the Petri net.
     * 
     * @return A {@link DataPlace} if the start place is unambiguous; null if
     *         the number of start places is 0 or more than 1
     */
    private DataPlace getUnambiguousStartPlace() {
        /* Get all places. */
        List<IDataElement> elements = myDataModel.getElements();
        List<DataPlace> places = getDataPlaces(elements);
        if (places.size() == 0)
            return null;

        /* Determine the start places. */
        List<DataPlace> startPlaces = new LinkedList<DataPlace>();
        for (DataPlace place : places) {
            int placePredCount = place.getAllPredCount();
            if (placePredCount == 0) {
                startPlaces.add(place);
            }
        }

        if (startPlaces.size() == 1)
            return startPlaces.get(0);

        return null;
    }

    /**
     * Determines the unambiguous end place of the Petri net.
     * 
     * @return A {@link DataPlace} if the end place is unambiguous; null if the
     *         number of end places is 0 or more than 1
     */
    private DataPlace getUnambiguousEndPlace() {
        /* Get all places. */
        List<IDataElement> elements = myDataModel.getElements();
        List<DataPlace> places = getDataPlaces(elements);
        if (places.size() == 0)
            return null;

        /* Determine the end places. */
        List<DataPlace> endPlaces = new LinkedList<DataPlace>();
        for (DataPlace place : places) {
            int placeSuccCount = place.getAllSuccCount();
            if (placeSuccCount == 0) {
                endPlaces.add(place);
            }
        }

        if (endPlaces.size() == 1)
            return endPlaces.get(0);

        return null;
    }

    /**
     * Returns all {@link DataPlace} in the specified list of
     * {@link IDataElement}.
     * 
     * @param dataElements
     *            The {@link List} of type {@link IDataElement}
     * @return A {@link List} of type {@link DataPlace}
     */
    private List<DataPlace> getDataPlaces(List<IDataElement> dataElements) {
        List<DataPlace> dataPlaces = new LinkedList<DataPlace>();

        for (IDataElement element : dataElements) {
            if (element instanceof DataPlace) {
                DataPlace dataPlace = (DataPlace) element;
                dataPlaces.add(dataPlace);
            }
        }

        return dataPlaces;
    }

}
