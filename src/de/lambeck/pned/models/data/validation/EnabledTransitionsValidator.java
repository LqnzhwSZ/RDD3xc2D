package de.lambeck.pned.models.data.validation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.elements.data.IDataTransition;
import de.lambeck.pned.elements.gui.IGuiTransition;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;

/**
 * Sets the initial marking (token on the start place) if the model was modified
 * but leaves an existing initial marking from a PNML file unchanged in case of
 * the first validation of the model.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class EnabledTransitionsValidator extends AbstractValidator {

    // private static boolean debug = false;

    /**
     * A {@link List} of all {@link IDataElement} in the model; Gets data in
     * getDataFromModel(IDataModel dataModel).
     */
    private List<IDataElement> allDataElements = null;

    /**
     * A {@link List} of all {@link IDataTransition} in the model; Gets data in
     * getDataFromModel(IDataModel dataModel).
     */
    private List<IDataTransition> allDataTransitions = null;

    /*
     * Constructor
     */

    /**
     * @param validationController
     *            The {@link IValidationController}
     * @param dataModelController
     *            The {@link IDataModelController}
     * @param i18n
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    public EnabledTransitionsValidator(IValidationController validationController,
            IDataModelController dataModelController, I18NManager i18n) {
        super(validationController, dataModelController, i18n);
        this.validatorInfoString = "infoEnabledTransitionsValidator";
    }

    /*
     * Validation methods
     */

    @Override
    public void startValidation(IDataModel dataModel, boolean initialModelCheck) {
        getDataFromModel(dataModel);
        this.isInitialModelCheck = initialModelCheck;
        /* Note: This validator doesn't use "initialModelCheck". */

        addValidatorInfo();

        removeExistingEnabledStates();

        if (checkAbortCondition1())
            return;

        /*
         * Model is valid: it must have token(s). Either on the start place if
         * the model was modified or somewhere else.
         */

        /* Is a token on the end place? (Simulation finished) */
        if (evaluateTokenOnEndPlace())
            return;

        /* Evaluate the number of enabled transitions! */
        int enabledDataTransitionsCount = getEnabledDataTransitionsCount();
        if (enabledDataTransitionsCount == -1)
            return;

        /* Initial marking OK, test successful. */
        reportValidationSuccessful();
    }

    @Override
    protected void getDataFromModel(IDataModel dataModel) {
        this.myDataModel = dataModel;
        this.myDataModelName = dataModel.getModelName();

        /* Additional info */
        List<IDataElement> modelElements = myDataModel.getElements();
        this.allDataElements = new ArrayList<IDataElement>(modelElements);

        /* Store all places and transitions in lists */
        this.allDataTransitions = getDataTransitions(allDataElements);
    }

    /**
     * Remove the "enabled" state from all {@link IGuiTransition} to avoid wrong
     * GUI display (before we check whether to quit if the model is invalid!).
     */
    private void removeExistingEnabledStates() {
        myDataModelController.resetAllDataTransitionsEnabledState(myDataModelName);
    }

    /**
     * Checks whether the end place has a token (simulation finished) or not.
     * 
     * Note: We should be allowed to assume that this model has an unambiguous
     * end place because we have passed the first check (model is valid?) for
     * abort conditions.
     * 
     * @return True = end place has a token, otherwise false
     */
    private boolean evaluateTokenOnEndPlace() {
        DataPlace endPlace = getUnambiguousEndPlace();
        if (endPlace == null) {
            reportValidationTokenOnEndPlaceFailed();
            return true; // Return true to quit this validation!
        }

        if (endPlace.getTokensCount() == EPlaceToken.ONE) {
            reportValidationTokenOnEndPlaceSucceeded();
            return true;
        }

        return false;
    }

    /**
     * Invokes the checkEnabled() method on all transitions in the
     * {@link IDataModel} and returns the counter of "enabled" states.
     * 
     * @return The number of enabled transitions, -1 if this model does not
     *         contain transitions
     */
    private int getEnabledDataTransitionsCount() {
        /* Get all transitions. */
        if (this.allDataTransitions.size() == 0) {
            reportValidationNoTransitionsFound();
            return -1;
        }

        /* Determine the number of enabled transitions. */
        int enabledCount = 0;
        for (IDataTransition dataTransition : this.allDataTransitions) {
            boolean enabled = dataTransition.checkEnabled();
            if (enabled) {
                enabledCount = enabledCount + 1;

                /* Update the GUI transition as well. */
                String transitionId = dataTransition.getId();
                myDataModelController.setGuiTransitionEnabledState(myDataModelName, transitionId);
            }
        }

        return enabledCount;
    }

    /*
     * Abort conditions
     */

    /**
     * Checks abort condition 1: Model already classified as invalid?
     */
    private boolean checkAbortCondition1() {
        EValidationResultSeverity currentResultsSeverity = this.myValidationController.getCurrentValidationStatus();

        int current = currentResultsSeverity.toInt();
        int critical = EValidationResultSeverity.CRITICAL.toInt();
        if (current < critical)
            return false;

        /* This result is critical! */
        infoIgnoredForInvalidModel();
        return true;
    }

    /*
     * Messages
     */

    /**
     * Adds an info message to indicate that this validation is stopped for an
     * invalid model.
     * 
     * Note: This message is an "INFO" message because the
     * {@link ValidationController} stores only the highest
     * {@link EValidationResultSeverity} anyways.
     */
    private void infoIgnoredForInvalidModel() {
        String message = i18n.getMessage("infoIgnoredForInvalidModel");
        IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.INFO);
        validationMessages.add(vMessage);
    }

    /**
     * Adds an error message to indicate that the token count on the end place
     * could not be checked and the validation has stopped.
     */
    private void reportValidationTokenOnEndPlaceFailed() {
        String message = i18n.getMessage("warningValidationTokenOnEndPlaceFailed");
        IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.CRITICAL);
        validationMessages.add(vMessage);
    }

    /**
     * Adds an info message to indicate that the end place has a token and that
     * this validation has succeeded.
     */
    private void reportValidationTokenOnEndPlaceSucceeded() {
        String message = i18n.getMessage("infoValidationTokenOnEndPlaceSucceeded");
        IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.INFO);
        validationMessages.add(vMessage);
    }

    private void reportValidationNoTransitionsFound() {
        String message = i18n.getMessage("warningValidationNoTransitionsFound");
        IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.INFO);
        validationMessages.add(vMessage);
    }

    /*
     * Private helpers
     */

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

    /**
     * Returns all {@link IDataTransition} in the specified list of
     * {@link IDataElement}.
     * 
     * @param dataElements
     *            The {@link List} of type {@link IDataElement}
     * @return A {@link List} of type {@link DataPlace}
     */
    private List<IDataTransition> getDataTransitions(List<IDataElement> dataElements) {
        List<IDataTransition> dataTransitions = new LinkedList<IDataTransition>();

        for (IDataElement element : dataElements) {
            if (element instanceof IDataTransition) {
                IDataTransition dataTransition = (IDataTransition) element;
                dataTransitions.add(dataTransition);
            }
        }

        return dataTransitions;
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

}
