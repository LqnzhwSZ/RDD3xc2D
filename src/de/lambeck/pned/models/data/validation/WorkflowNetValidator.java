package de.lambeck.pned.models.data.validation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.elements.data.IDataNode;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;

/**
 * Implements a workflow net validator that can check the properties of a
 * workflow net.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class WorkflowNetValidator implements IWorkflowNetValidator {

    private static boolean debug = true;

    /**
     * This should be the canonical (unique) path name of the file.
     */
    private String modelName = "";

    /** Reference to the data model controller */
    private IDataModelController myDataModelController;

    /** Reference to the data model */
    private IDataModel myDataModel;

    /**
     * Reference to the validation messages panel for this data model
     */
    private IValidationMessagesPanel myValidationMessagesPanel;

    /** Reference to the manager for I18N strings */
    private I18NManager i18n = null;

    /** Stores if a validation is still running */
    boolean validationPending = false;

    boolean isValidWorkflowNet = false;

    /**
     * Constructs this validator with references to controllers, model and
     * message panel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     * @param dataModelController
     *            The data model controller
     * @param dataModel
     *            The data model
     * @param validationMessagesPanel
     *            The validation messages panel for this data model
     * @param i18n
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    public WorkflowNetValidator(String modelName, IDataModelController dataModelController, IDataModel dataModel,
            IValidationMessagesPanel validationMessagesPanel, I18NManager i18n) {
        this.modelName = modelName;
        this.myDataModelController = dataModelController;
        this.myDataModel = dataModel;
        this.myValidationMessagesPanel = validationMessagesPanel;
        this.i18n = i18n;
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

    /*
     * When editing a file
     */

    @Override
    public void startValidation() {
        if (debug) {
            System.out.println("WorkflowNetValidator.startValidation()");
            System.out.println("WorkflowNetValidator.modelName: " + this.modelName);
        }

        this.validationPending = true;

        /*
         * Validate this model
         */

        /*
         * Test 1
         */
        if (!checkOneSingleStartPlace()) {
            abortValidation();
            return;
        }

        /*
         * Test 2
         */
        if (!checkOneSingleEndPlace()) {
            abortValidation();
            return;
        }

        // TODO More tests!

        /*
         * Indicate this model as valid
         */
        myValidationMessagesPanel.setBgColor(ValidationColor.VALID);
        this.validationPending = false;
    }

    @Override
    public void restartValidation() {
        if (debug) {
            System.out.println("WorkflowNetValidator.restartValidation()");
            System.out.println("WorkflowNetValidator.modelName: " + this.modelName);
        }

        /*
         * Reset former outputs.
         */
        myValidationMessagesPanel.reset();

        /*
         * Validate this model
         */
        startValidation();
    }

    @Override
    public String getStartPlaceId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEndPlaceId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ArrayList<String> getInvalidProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * Validations
     */

    /**
     * Checks if this Petri net has exactly 1 start node.
     * 
     * @return True if there is exactly 1 start node; otherwise false
     */
    private boolean checkOneSingleStartPlace() {
        List<String> startPlaces = getStartPlaces();
        /*
         * There were no nodes at all?
         */
        if (startPlaces == null)
            return false;

        /*
         * There are nodes. Check the number of start places.
         */
        if (startPlaces.size() == 0) {
            myValidationMessagesPanel.setBgColor(ValidationColor.INVALID);

            String message = i18n.getMessage("warningValidatorNoStartNode");
            myValidationMessagesPanel.addMessage(message);

            return false;

        } else if (startPlaces.size() > 1) {
            myValidationMessagesPanel.setBgColor(ValidationColor.INVALID);

            String message = i18n.getMessage("warningValidatorTooManyStartNodes");
            myValidationMessagesPanel.addMessage(message);

            return false;

        }

        return true;
    }

    /**
     * Checks if this Petri net has exactly 1 end node.
     * 
     * @return True if there is exactly 1 end node; otherwise false
     */
    private boolean checkOneSingleEndPlace() {
        List<String> endPlaces = getEndPlaces();
        /*
         * There were no nodes at all?
         */
        if (endPlaces == null)
            return false;

        /*
         * There are nodes. Check the number of end places.
         */
        if (endPlaces.size() == 0) {
            myValidationMessagesPanel.setBgColor(ValidationColor.INVALID);

            String message = i18n.getMessage("warningValidatorNoEndNode");
            myValidationMessagesPanel.addMessage(message);

            return false;

        } else if (endPlaces.size() > 1) {
            myValidationMessagesPanel.setBgColor(ValidationColor.INVALID);

            String message = i18n.getMessage("warningValidatorTooManyEndNodes");
            myValidationMessagesPanel.addMessage(message);

            return false;

        }

        return true;
    }

    /*
     * Private helpers
     */

    /**
     * Determines the start places of the Petri net.
     * 
     * @return A list with the IDs of the start places; null if there are no
     *         nodes
     */
    public List<String> getStartPlaces() {
        /*
         * Check number of nodes
         */
        List<IDataElement> elements = myDataModel.getElements();
        List<IDataNode> nodes = getNodes(elements);
        if (nodes.size() == 0) {
            String message = i18n.getMessage("warningValidatorNoNodes");
            myValidationMessagesPanel.addMessage(message);
            myValidationMessagesPanel.setBgColor(ValidationColor.INVALID);

            return null; // null, not the empty List!
        }

        /*
         * Determine the start places.
         */
        List<String> startPlaces = new LinkedList<String>();
        for (IDataNode node : nodes) {
            if (node instanceof DataPlace) {
                DataPlace place = (DataPlace) node;

                if (debug) {
                    System.out.println(place.toString() + ":");
                    System.out.println("node.getAllPredCount(): " + node.getAllPredCount());
                    System.out.println("node.getAllSuccCount(): " + node.getAllSuccCount());
                }

                int placePredCount = place.getAllPredCount();
                if (placePredCount == 0) {
                    String placeId = place.getId();
                    startPlaces.add(placeId);

                    /*
                     * Show the start place
                     */
                    String message = i18n.getNameOnly("StartNode") + ": " + place.getId();
                    myValidationMessagesPanel.addMessage(message);
                }
            }
        }

        return startPlaces;
    }

    /**
     * Determines the end places of the Petri net.
     * 
     * @return A List with the IDs of the end places
     */
    public List<String> getEndPlaces() {
        /*
         * Check number of nodes
         */
        List<IDataElement> elements = myDataModel.getElements();
        List<IDataNode> nodes = getNodes(elements);
        if (nodes.size() == 0) {
            String message = i18n.getMessage("warningValidatorNoNodes");
            myValidationMessagesPanel.addMessage(message);
            myValidationMessagesPanel.setBgColor(ValidationColor.INVALID);

            return null; // null, not the empty List!
        }

        /*
         * Determine the end places.
         */
        List<String> endPlaces = new LinkedList<String>();
        for (IDataNode node : nodes) {
            if (node instanceof DataPlace) {
                DataPlace place = (DataPlace) node;

                if (debug) {
                    System.out.println(place.toString() + ":");
                    System.out.println("node.getAllPredCount(): " + node.getAllPredCount());
                    System.out.println("node.getAllSuccCount(): " + node.getAllSuccCount());
                }

                int placeSuccCount = place.getAllSuccCount();
                if (placeSuccCount == 0) {
                    String placeId = place.getId();
                    endPlaces.add(placeId);

                    /*
                     * Show the end place
                     */
                    String message = i18n.getNameOnly("EndNode") + ": " + place.getId();
                    myValidationMessagesPanel.addMessage(message);
                }
            }
        }

        return endPlaces;
    }

    /**
     * Returns all {@link IDataNode} in the specified list of
     * {@link IDataElement}.
     * 
     * @param elements
     *            The {@link List} of type {@link IDataElement}
     * @return A list of type {@link IDataNode}
     */
    List<IDataNode> getNodes(List<IDataElement> elements) {
        List<IDataNode> nodes = new LinkedList<IDataNode>();

        for (IDataElement element : elements) {
            if (element instanceof IDataNode) {
                IDataNode node = (IDataNode) element;
                nodes.add(node);
            }
        }

        return nodes;
    }

    private void abortValidation() {
        myValidationMessagesPanel.setBgColor(ValidationColor.INVALID);
        this.isValidWorkflowNet = false;
        this.validationPending = false;
    }

}
