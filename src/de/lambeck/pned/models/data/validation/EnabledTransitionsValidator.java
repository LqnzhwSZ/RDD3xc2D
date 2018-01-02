package de.lambeck.pned.models.data.validation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.JOptionPane;

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

    /**
     * A {@link List} of all {@link DataPlace} in the model; Gets data in
     * getDataFromModel(IDataModel dataModel).
     */
    private List<DataPlace> allDataPlaces = null;

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
        // this.isInitialModelCheck = initialModelCheck;
        /* Note: This validator doesn't use "initialModelCheck". */

        addValidatorInfo();

        removeExistingEnabledStates();

        if (checkAbortCondition1())
            return;

        /*
         * Model is valid: it must have token(s). Either on the start place if
         * the model was modified or somewhere else.
         */

        /*
         * @formatter:off
         * Checks:
         * 
         * - Determine # transitions
         * 
         * If:
         * - 1. # transitions == 0?                         -> Simulation successfully finished.
         *                                                  -> Always OK (Token on end place)
         * 
         * - Determine: # tokens
         * - Determine: token on end place?
         * - Determine: # of enabled transitions            -> To be able to visualize unsafe transitions
         *                                                     even if we quit validation on a deadlock.
         * 
         * If:
         * - 2. # tokens == 1 && token on end place         -> Simulation successfully finished
         * - 3. # tokens == 1 && no token on end place      -> Depends on the transition
         * - 4. # tokens > 1 && token on end place          -> Deadlock
         * - 5. # tokens > 1 && no token on end place       -> Depends on the transitions
         * 
         * - Determine # of enabled transitions
         * 
         * If:
         * - 6. # of enabled transitions == 0               -> Deadlock
         * 
         * Else:
         * - Success
         * 
         * @formatter:on
         */

        /* Determine # transitions */
        int numberOfTransitions = getNumberOfTransitions();
        /* Check 1 */
        if (numberOfTransitions == 0) {
            reportEndMarkingReached();
            return;
        }

        /* Determine: # tokens, token on end place?, # of enabled transitions */
        int numberOfTokens = getNumberOfTokens();

        boolean tokenOnEndPlace;
        try {
            tokenOnEndPlace = isTokenOnEndPlace();
        } catch (NoSuchElementException e) {
            reportValidationTokenOnEndPlaceFailed();
            return;
        }

        int enabledDataTransitionsCount = getEnabledDataTransitionsCount();
        if (enabledDataTransitionsCount == -2) {
            /* The model is not safe! */
            return;
        }

        /* Check 2...5 */
        if (numberOfTokens == 1 && tokenOnEndPlace) {
            reportEndMarkingReached();
            return;
        }
        if (numberOfTokens == 1 && !tokenOnEndPlace) {
            // NOP: Depends on the transition
        }
        if (numberOfTokens > 1 && tokenOnEndPlace) {
            reportValidationDeadlock();
            return;
        }
        if (numberOfTokens > 1 && !tokenOnEndPlace) {
            // NOP: Depends on the transitions
        }

        /* Determine # of enabled transitions */
        if (enabledDataTransitionsCount < 0) {
            /* Should not happen, must have been detected before. */
            return;
        }

        /* Check 6 - A deadlock somewhere? */
        if (enabledDataTransitionsCount == 0) {
            reportValidationDeadlock();
            return;
        }

        /* Success - modal info message for end marking */
        if (tokenOnEndPlace) {
            reportEndMarkingReached();
        } else {
            reportValidationSuccessful();
        }
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
        this.allDataPlaces = getDataPlaces(allDataElements);
    }

    /**
     * Remove the "enabled" state from all {@link IGuiTransition} to avoid wrong
     * GUI display (before we check whether to quit if the model is invalid!).
     */
    private void removeExistingEnabledStates() {
        myDataModelController.resetAllDataTransitionsEnabledState(myDataModelName);
    }

    /**
     * @return the total number of tokens in the model
     */
    private int getNumberOfTokens() {
        int tokensCount = 0;

        for (DataPlace dataPlace : allDataPlaces) {
            if (dataPlace.getTokensCount() == EPlaceToken.ONE)
                tokensCount++;
        }

        return tokensCount;
    }

    /**
     * @return True = token on end place, false = no token on end place
     * @throws NoSuchElementException
     *             If end place was not found
     */
    private boolean isTokenOnEndPlace() throws NoSuchElementException {
        DataPlace endPlace = getUnambiguousEndPlace(allDataPlaces);
        if (endPlace == null) {
            /*
             * This should never happen if we have run all preceding validations
             * because we have checked whether the model is already classified
             * as valid or not. -> This means it has at least 1 place: the start
             * and end place.
             */
            throw new NoSuchElementException();
        }

        if (endPlace.getTokensCount() == EPlaceToken.ONE)
            return true;
        return false;
    }

    /**
     * Invokes the checkEnabled() method on all transitions in the
     * {@link IDataModel} and returns the counter of "enabled" states.
     * 
     * @return The number of enabled transitions, -1 if this model does not
     *         contain transitions, -2 if this model is not safe
     */
    private int getEnabledDataTransitionsCount() {
        /* Get all transitions. */
        if (this.allDataTransitions.size() == 0) {
            /* Should not happen, must have been detected before. */
            reportValidationNoTransitionsFound();
            return -1;
        }

        /* Let the transitions check their state and count the enabled ones. */
        int enabledCount = 0;
        for (IDataTransition dataTransition : this.allDataTransitions) {
            boolean enabled = false;
            try {
                enabled = dataTransition.checkEnabled();
            } catch (IllegalStateException e) {
                reportValidationTransitionUnsafe(dataTransition);

                /* Update the GUI transition as well. */
                String transitionId = dataTransition.getId();
                myDataModelController.setGuiTransitionUnsafe(myDataModelName, transitionId);

                return -2;
            }

            if (enabled) {
                enabledCount = enabledCount + 1;

                /* Update the GUI transition as well. */
                String transitionId = dataTransition.getId();
                myDataModelController.setGuiTransitionEnabled(myDataModelName, transitionId);
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
        EValidationResultSeverity currentResultsSeverity = this.myValidationController
                .getCurrentValidationStatus(myDataModelName);

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

    private void reportValidationDeadlock() {
        String message = i18n.getMessage("warningValidationDeadlock");
        IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.CRITICAL);
        validationMessages.add(vMessage);
    }

    private void reportValidationNoTransitionsFound() {
        String message = i18n.getMessage("warningValidationNoTransitionsFound");
        IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.INFO);
        validationMessages.add(vMessage);
    }

    private void reportValidationTransitionUnsafe(IDataTransition unsafeTransition) {
        String message = i18n.getMessage("warningValidationTransitionUnsafe");

        String transitionId = unsafeTransition.getId();
        message = message.replace("%id%", transitionId);

        IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.CRITICAL);
        validationMessages.add(vMessage);
    }

    private void reportEndMarkingReached() {
        reportValidationSuccessful();
        showEndMarkingMessage();
    }

    private void showEndMarkingMessage() {
        String title = i18n.getNameOnly("RegularEndmarking");
        String infoMessage = i18n.getMessage("infoValidationSimulationFinished");
        infoMessage = infoMessage.replace("%modelName%", myDataModelName);
        JOptionPane.showMessageDialog(null, infoMessage, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /*
     * Private helpers
     */

    /**
     * @return the number of transitions in the model
     */
    private int getNumberOfTransitions() {
        return allDataTransitions.size();
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
     * Determines the unambiguous end place of the Petri net.
     * 
     * @param dataPlaces
     *            The {@link List} of {@link DataPlace} in this
     *            {@link IDataModel}
     * @return A {@link DataPlace} if the end place is unambiguous; null if the
     *         number of end places is 0 or more than 1
     */
    private DataPlace getUnambiguousEndPlace(List<DataPlace> dataPlaces) {
        if (dataPlaces.size() == 0)
            return null;

        /* Determine the end places. */
        List<DataPlace> endPlaces = new LinkedList<DataPlace>();
        for (DataPlace place : dataPlaces) {
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