package de.lambeck.pned.models.data.validation;

import java.util.LinkedList;
import java.util.List;

import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.elements.data.IDataNode;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * Implements a workflow net validator that can check the properties of a
 * workflow net.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class WorkflowNetValidator implements IWorkflowNetValidator {

    private static boolean debug = true;

    /*
     * Identification of this validator and necessary references
     */

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

    /*
     * Results of the last validation
     */

    /** Stores if a validation is currently running */
    boolean validationPending = false;

    /** Stores the current status of the workflow net */
    boolean resultIsValidWorkflowNet = false;

    /** Stores the ID of the current start place */
    String resultStartPlaceId = "";

    /** Stores the ID of the current end place */
    String resultEndPlaceId = "";

    /*
     * Constructor
     */

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
     * Public Getter and Setter
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
     * Private Setter
     */

    /**
     * Stores a new start place.
     * 
     * @param newId
     *            The new ID of a Place; empty String for no start place
     */
    private void setStartPlace(String newId) {
        /*
         * The same ID (or nothing) again?
         */
        String oldId = this.resultStartPlaceId;
        if (newId == oldId)
            return;

        /*
         * Reset or store the new ID?
         */
        if (newId == "") {
            removeStartPlace(oldId);
            return;
        } else {
            addStartPlace(newId);
        }
    }

    /**
     * Used by setStartPlace() to remove the start place and inform the
     * {@link IDataModelController} to take care of the repainting.
     * 
     * @param oldId
     *            The id of the previous start place
     */
    private void removeStartPlace(String oldId) {
        this.resultStartPlaceId = "";
        myDataModelController.setStartPlace(this.modelName, oldId, false);
    }

    /**
     * Used by setStartPlace() to add the start place and inform the
     * {@link IDataModelController} to take care of the repainting.
     * 
     * @param newId
     *            The id of the new start place
     */
    private void addStartPlace(String newId) {
        this.resultStartPlaceId = newId;
        myDataModelController.setStartPlace(this.modelName, newId, true);
    }

    /**
     * Stores a new end place.
     * 
     * @param newId
     *            The new ID of a Place; empty String for no end place
     */
    private void setEndPlace(String newId) {
        /*
         * The same ID (or nothing) again?
         */
        String oldId = this.resultEndPlaceId;
        if (newId == oldId)
            return;

        /*
         * Reset or store the new ID?
         */
        if (newId == "") {
            removeEndPlace(oldId);
            return;
        } else {
            addEndPlace(newId);
        }
    }

    /**
     * Used by setEndPlace() to remove the end place and inform the
     * {@link IDataModelController} to take care of the repainting.
     * 
     * @param oldId
     *            The id of the previous end place
     */
    private void removeEndPlace(String oldId) {
        this.resultEndPlaceId = "";
        myDataModelController.setEndPlace(this.modelName, oldId, false);
    }

    /**
     * Used by setEndPlace() to add the end place and inform the
     * {@link IDataModelController} to take care of the repainting.
     * 
     * @param newId
     *            The id of the new end place
     */
    private void addEndPlace(String newId) {
        this.resultEndPlaceId = newId;
        myDataModelController.setEndPlace(this.modelName, newId, true);
    }

    /*
     * When editing a file
     */

    @Override
    public void startValidation() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("WorkflowNetValidator.startValidation");
            System.out.println("WorkflowNetValidator.modelName: " + this.modelName);
        }

        this.validationPending = true;

        /*
         * Validate this model
         */

        /*
         * Test 1
         */
        if (!test_1_OnlyOneStartPlace()) {
            abortValidation();
            return;
        }

        /*
         * Test 2
         */
        if (!test_2_OnlyOneEndPlace()) {
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
            ConsoleLogger.consoleLogMethodCall("WorkflowNetValidator.restartValidation");
            System.out.println("WorkflowNetValidator.modelName: " + this.modelName);
        }

        /*
         * Reset the last results.
         */
        resetLastResults();

        /*
         * Reset the message panel.
         */
        myValidationMessagesPanel.reset();

        /*
         * Validate this model
         */
        startValidation();
    }

    @Override
    public String getStartPlaceId() {
        return this.resultStartPlaceId;
    }

    @Override
    public String getEndPlaceId() {
        return this.resultEndPlaceId;
    }

    /*
     * Validations
     */

    /**
     * Checks if this Petri net has exactly 1 start place.
     * 
     * @return True if there is exactly 1 start place; otherwise false
     */
    private boolean test_1_OnlyOneStartPlace() {
        List<String> startPlaces = getStartPlaces();

        /*
         * There were no places at all?
         */
        if (startPlaces == null)
            return false;

        /*
         * There are places. Check the number of start places.
         */
        if (startPlaces.size() == 0) {
            myValidationMessagesPanel.setBgColor(ValidationColor.INVALID);
            String message = i18n.getMessage("warningValidatorNoStartPlace");
            myValidationMessagesPanel.addMessage(message);
            return false;

        } else if (startPlaces.size() > 1) {
            myValidationMessagesPanel.setBgColor(ValidationColor.INVALID);
            String message = i18n.getMessage("warningValidatorTooManyStartPlaces");
            myValidationMessagesPanel.addMessage(message);
            return false;

        }

        /*
         * Store the start place.
         */
        String id = startPlaces.get(0);
        setStartPlace(id);

        return true;
    }

    /**
     * Checks if this Petri net has exactly 1 end place.
     * 
     * @return True if there is exactly 1 end place; otherwise false
     */
    private boolean test_2_OnlyOneEndPlace() {
        List<String> endPlaces = getEndPlaces();

        /*
         * There were no places at all?
         */
        if (endPlaces == null)
            return false;

        /*
         * There are places. Check the number of end places.
         */
        if (endPlaces.size() == 0) {
            myValidationMessagesPanel.setBgColor(ValidationColor.INVALID);
            String message = i18n.getMessage("warningValidatorNoEndPlace");
            myValidationMessagesPanel.addMessage(message);
            return false;

        } else if (endPlaces.size() > 1) {
            myValidationMessagesPanel.setBgColor(ValidationColor.INVALID);
            String message = i18n.getMessage("warningValidatorTooManyEndPlaces");
            myValidationMessagesPanel.addMessage(message);
            return false;

        }

        /*
         * Store the end place.
         */
        String id = endPlaces.get(0);
        setEndPlace(id);

        return true;
    }

    /*
     * Private helpers
     */

    /**
     * Generates a {@link List} of start places of the Petri net.
     * 
     * @return A list with the IDs of all start places; empty if no place is a
     *         start place; null if the Petri not does not contain any places
     */
    private List<String> getStartPlaces() {
        /*
         * Check number of places
         */
        List<IDataElement> elements = myDataModel.getElements();
        List<DataPlace> places = getPlaces(elements);
        if (places.size() == 0) {
            String message = i18n.getMessage("warningValidatorNoPlaces");
            myValidationMessagesPanel.addMessage(message);
            myValidationMessagesPanel.setBgColor(ValidationColor.INVALID);

            return null; // null, not the empty List!
        }

        /*
         * Determine the start places.
         */
        List<String> startPlaces = new LinkedList<String>();
        for (DataPlace place : places) {
            // if (debug) {
            // System.out.println(place.toString() + ":");
            // System.out.println("place.getAllPredCount(): " +
            // place.getAllPredCount());
            // System.out.println("place.getAllSuccCount(): " +
            // place.getAllSuccCount());
            // }

            int placePredCount = place.getAllPredCount();
            if (placePredCount == 0) {
                String placeId = place.getId();
                startPlaces.add(placeId);

                /*
                 * Show the start place
                 */
                String message = i18n.getNameOnly("StartPlace") + ": " + place.getId();
                myValidationMessagesPanel.addMessage(message);
            }
        }

        return startPlaces;
    }

    /**
     * Determines the end places of the Petri net.
     * 
     * @return A list with the IDs of all end places; empty if no place is an
     *         end place; null if the Petri not does not contain any places
     */
    private List<String> getEndPlaces() {
        /*
         * Check number of places
         */
        List<IDataElement> elements = myDataModel.getElements();
        List<DataPlace> places = getPlaces(elements);
        if (places.size() == 0) {
            String message = i18n.getMessage("warningValidatorNoPlaces");
            myValidationMessagesPanel.addMessage(message);
            myValidationMessagesPanel.setBgColor(ValidationColor.INVALID);

            return null; // null, not the empty List!
        }

        /*
         * Determine the end places.
         */
        List<String> endPlaces = new LinkedList<String>();
        for (DataPlace place : places) {
            // if (debug) {
            // System.out.println(place.toString() + ":");
            // System.out.println("place.getAllPredCount(): " +
            // place.getAllPredCount());
            // System.out.println("place.getAllSuccCount(): " +
            // place.getAllSuccCount());
            // }

            int placeSuccCount = place.getAllSuccCount();
            if (placeSuccCount == 0) {
                String placeId = place.getId();
                endPlaces.add(placeId);

                /*
                 * Show the end place
                 */
                String message = i18n.getNameOnly("EndPlace") + ": " + place.getId();
                myValidationMessagesPanel.addMessage(message);
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
     * @return A {@link List} of type {@link IDataNode}
     */
    private List<IDataNode> getNodes(List<IDataElement> elements) {
        List<IDataNode> nodes = new LinkedList<IDataNode>();

        for (IDataElement element : elements) {
            if (element instanceof IDataNode) {
                IDataNode node = (IDataNode) element;
                nodes.add(node);
            }
        }

        return nodes;
    }

    /**
     * Returns all {@link DataPlace} in the specified list of
     * {@link IDataElement}.
     * 
     * @param elements
     *            The {@link List} of type {@link IDataElement}
     * @return A {@link List} of type {@link DataPlace}
     */
    private List<DataPlace> getPlaces(List<IDataElement> elements) {
        List<DataPlace> places = new LinkedList<DataPlace>();

        for (IDataElement element : elements) {
            if (element instanceof DataPlace) {
                DataPlace place = (DataPlace) element;
                places.add(place);
            }
        }

        return places;
    }

    /**
     * Aborts the current validation. Invoke this after the 1st failed test.
     */
    private void abortValidation() {
        /*
         * Update the results.
         */
        this.resultIsValidWorkflowNet = false;

        // TODO Alle Zwischenergebnisse resetten oder nicht?
        resetLastResults();

        /*
         * Update the message panel.
         */
        myValidationMessagesPanel.setBgColor(ValidationColor.INVALID);

        this.validationPending = false;
    }

    /**
     * Resets all local attributes that store results of the last validation.
     */
    private void resetLastResults() {
        this.resultIsValidWorkflowNet = false;

        /*
         * Inform the data model controller that start and end places are not
         * start and end place anymore.
         */
        myDataModelController.setStartPlace(this.modelName, this.resultStartPlaceId, false);
        myDataModelController.setEndPlace(this.modelName, this.resultEndPlaceId, false);

        // TODO Alle Zwischenergebnisse resetten oder nicht?
        setStartPlace("");
        this.resultEndPlaceId = "";
    }

}
