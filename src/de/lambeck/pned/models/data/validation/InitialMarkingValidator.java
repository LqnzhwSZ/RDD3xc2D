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
 * the first validation of the model.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class InitialMarkingValidator extends AbstractValidator {

    // private static boolean debug = false;

    /** The start place of the model (if unambiguous) */
    private DataPlace myStartPlace = null;

    /*
     * Constructor
     */

    /**
     * 
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
    public InitialMarkingValidator(int Id, IValidationController validationController,
            IDataModelController dataModelController, I18NManager i18n) {
        super(Id, validationController, dataModelController, i18n);
        this.validatorInfoString = "infoInitialMarkingValidator";
    }

    /*
     * Validation methods
     */

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

    /*
     * Abort conditions
     */

    /**
     * Checks abort condition 1: Model with tokens just loaded from a PNML file?
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
     * Removes the token from all {@link IDataPlace}.
     */
    private void removeExistingMarking() {
        myDataModelController.removeAllDataTokens(myDataModelName);
    }

    /**
     * Checks abort condition 2: Model already classified as invalid?
     */
    private boolean checkAbortCondition2() {
        EValidationResultSeverity currentResultsSeverity = this.myValidationController.getCurrentValidationStatus();

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
            String message = i18n.getMessage("warningValidatorNoUnambiguousStartPlace");
            IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.CRITICAL);
            validationMessages.add(vMessage);

            result = false;
        }

        return result;
    }

    private void setInitialMarking(DataPlace startPlace) {
        String placeId = startPlace.getId();

        List<String> placesWithToken = new ArrayList<String>();
        placesWithToken.add(placeId);

        myDataModelController.addDataToken(myDataModelName, placesWithToken);
    }

    /*
     * Messages
     */

    /**
     * Adds an info message to indicate that this validation is ignored for the
     * initial check of a model.
     */
    private void infoIgnoredForInitialCheck() {
        String message = i18n.getMessage("infoValidatorIgnoredForInitialCheck");
        IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.INFO);
        validationMessages.add(vMessage);
    }

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

    /*
     * Private helpers
     */

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
        List<DataPlace> places = getPlaces(elements);
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
        List<DataPlace> places = getPlaces(elements);
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

}
