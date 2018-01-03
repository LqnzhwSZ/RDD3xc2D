package de.lambeck.pned.models.data.validation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;

/**
 * Sets the initial marking (token on the start place) if the model was modified
 * but leaves an existing initial marking from a PNML file unchanged in case of
 * the first validation of the model. To be used in combination with the
 * {@link ValidationController}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class InitialMarkingValidator extends AbstractValidator {

    /** The start place of the model (if unambiguous) */
    private DataPlace myStartPlace = null;

    /* Constructor */

    /**
     * 
     * @param validationController
     *            The {@link IValidationController}
     * @param dataModelController
     *            The {@link IDataModelController}
     * @param i18n
     *            The manager for localized strings
     */
    @SuppressWarnings("hiding")
    public InitialMarkingValidator(IValidationController validationController, IDataModelController dataModelController,
            I18NManager i18n) {
        super(validationController, dataModelController, i18n);
        this.validatorInfoString = "infoInitialMarkingValidator";
    }

    /* Validation methods */

    @Override
    public void startValidation(IDataModel dataModel, boolean initialModelCheck) {
        getDataFromModel(dataModel);
        this.isInitialModelCheck = initialModelCheck;

        addValidatorInfo();

        if (checkAbortCondition1())
            return;

        removeExistingMarking();

        if (checkAbortCondition2())
            return;

        /* Check condition: exactly 1 start place */
        if (!evaluateStartPlace())
            return;

        /* Set the initial marking */
        setInitialMarking(this.myStartPlace);

        /* Initial marking OK, test successful. */
        reportValidationSuccessful();
    }

    /* Abort conditions */

    /**
     * Checks abort condition 1: Model with tokens just loaded from a PNML file?
     * 
     * @return True = ignore this validation, false = run this validation
     */
    private boolean checkAbortCondition1() {
        if (this.isInitialModelCheck) {
            int tokensInModelCount = getTokensCount(this.myDataModel);
            if (tokensInModelCount > 0) {
                infoIgnoredForInitialCheck();
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the token from all {@link DataPlace}.
     */
    private void removeExistingMarking() {
        myDataModelController.removeAllDataTokens(myDataModelName);
    }

    /**
     * Checks abort condition 2: Model already classified as invalid?
     * 
     * @return true = result is critical, false = result is not critical
     */
    private boolean checkAbortCondition2() {
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

    /**
     * Stores the (unique) start place in the local attribute startPlace.
     * 
     * @return True if start place is unique; otherwise false
     */
    private boolean evaluateStartPlace() {
        /*
         * Store all start places. (Calculate them locally to be independent
         * from other validators!)
         */
        boolean result = true;

        DataPlace unambiguousStartPlace = getUnambiguousStartPlace();
        if (unambiguousStartPlace != null) {
            this.myStartPlace = unambiguousStartPlace;
        } else {
            messageCriticalNoUnambiguousStartPlace();
            result = false;
        }

        return result;
    }

    /**
     * Informs the {@link IDataModelController} to add a token to the specified
     * start place.
     * 
     * @param startPlace
     *            The specified [{@link DataPlace}
     */
    private void setInitialMarking(DataPlace startPlace) {
        String placeId = startPlace.getId();

        List<String> placesWithToken = new ArrayList<String>();
        placesWithToken.add(placeId);

        myDataModelController.addDataToken(myDataModelName, placesWithToken);
    }

    /* Messages */

    /**
     * Adds an info message to indicate that this validation is ignored for the
     * initial check of a model.
     */
    private void infoIgnoredForInitialCheck() {
        String message;
        EValidationResultSeverity severity;
        IValidationMsg vMessage;

        message = i18n.getMessage("infoValidatorIgnoredForInitialCheck");
        severity = EValidationResultSeverity.INFO;

        vMessage = new ValidationMsg(myDataModel, message, severity);
        validationMessages.add(vMessage);
    }

    /**
     * Adds a critical message to indicate that there is no unambiguous start
     * place.
     */
    protected void messageCriticalNoUnambiguousStartPlace() {
        String message;
        EValidationResultSeverity severity;
        IValidationMsg vMessage;

        message = i18n.getMessage("criticalValidatorNoUnambiguousStartPlace");
        severity = EValidationResultSeverity.CRITICAL;

        vMessage = new ValidationMsg(myDataModel, message, severity);
        validationMessages.add(vMessage);
    }

    /* Private helpers */

    /**
     * Determines the number of tokens in a {@link IDataModel}.
     * 
     * @param dataModel
     *            The data model
     * @return The number of tokens; -1 if the model does not contain places
     */
    private int getTokensCount(IDataModel dataModel) {
        /* Get all places. */
        List<IDataElement> elements = myDataModel.getElements();
        List<DataPlace> places = getDataPlaces(elements);
        if (places.size() == 0)
            return -1;

        /* Determine the number of tokens. */
        int allTokensCount = 0;
        for (DataPlace place : places) {
            allTokensCount = allTokensCount + place.getTokensCount().toInt();
        }

        return allTokensCount;
    }

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
