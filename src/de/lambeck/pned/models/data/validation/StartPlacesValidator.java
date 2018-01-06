package de.lambeck.pned.models.data.validation;

import java.util.LinkedList;
import java.util.List;

import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;
import de.lambeck.pned.util.NodeInfo;

/**
 * Checks the number of start places in a workflow net. To be used in
 * combination with the {@link ValidationController}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class StartPlacesValidator extends AbstractValidator {

    /* Constructor */

    /**
     * Constructs this validator with references to the necessary controllers.
     * 
     * @param validationController
     *            The {@link IValidationController}
     * @param dataModelController
     *            The {@link IDataModelController}
     * @param i18n
     *            The manager for localized strings
     */
    @SuppressWarnings("hiding")
    public StartPlacesValidator(IValidationController validationController, IDataModelController dataModelController,
            I18NManager i18n) {
        super(validationController, dataModelController, i18n);
        this.validatorInfoString = "infoStartPlacesValidator";
    }

    /* Validation methods */

    @Override
    public void startValidation(IDataModel dataModel, boolean initialModelCheck) {
        getDataFromModel(dataModel);
        // this.isInitialModelCheck = initialModelCheck;
        /* Note: This validator doesn't use "initialModelCheck". */

        addValidatorInfo();

        /* Reset all previous start places. */
        myDataModelController.resetAllDataStartPlaces(myDataModelName);

        /* Store all start places. */
        List<String> startPlaces = getStartPlaces();

        /* Evaluate the results... */
        if (evaluateNoPlaces(startPlaces))
            return;

        if (evaluateNoStartPlaces(startPlaces))
            return;

        if (evaluateTooManyStartPlaces(startPlaces))
            return;

        /* startPlaces.size() must be 1 here! */
        highlightTheStartPlace(startPlaces.get(0));

        /* Start places are OK, test successful. */
        reportValidationSuccessful();
    }

    /**
     * Returns true if the model has no places.
     * 
     * @param startPlaces
     *            The {@link List} of start places
     * @return True = no place, false = at least 1 place
     */
    private boolean evaluateNoPlaces(List<String> startPlaces) {
        if (startPlaces == null) {
            messageCriticalNoPlaces();
            return true;
        }
        return false;
    }

    /**
     * Returns true if the model has no start place.
     * 
     * @param startPlaces
     *            The {@link List} of start places
     * @return True = no start place, false = at least 1 start place
     */
    private boolean evaluateNoStartPlaces(List<String> startPlaces) {
        if (startPlaces.size() == 0) {
            messageCriticalNoStartPlace();
            return true;
        }
        return false;
    }

    /**
     * Informs the data model controller which places need to be highlighted as
     * start place candidates and returns true if the model has too many start
     * place candidates.
     * 
     * @param startPlaces
     *            The {@link List} of start places
     * @return True = too many start places, false = start places count is OK
     */
    private boolean evaluateTooManyStartPlaces(List<String> startPlaces) {
        if (startPlaces.size() > 1) {
            /* Highlight as candidates */
            for (String placeId : startPlaces) {
                myDataModelController.setDataStartPlaceCandidate(myDataModelName, placeId, true);
            }

            /* Validation message */
            messageCriticalTooManyStartPlaces();
            return true;
        }
        return false;
    }

    /**
     * Informs the data model controller which place to highlight as the real
     * (unambiguous) start place.
     * 
     * @param placeId
     *            The id of the start place
     */
    private void highlightTheStartPlace(String placeId) {
        myDataModelController.setDataStartPlace(myDataModelName, placeId, true);
    }

    /* Messages */

    /**
     * Adds a critical message to indicate that there are no places.
     */
    protected void messageCriticalNoPlaces() {
        String message;
        EValidationResultSeverity severity;
        IValidationMsg vMessage;

        message = i18n.getMessage("criticalValidatorNoPlaces");
        severity = EValidationResultSeverity.CRITICAL;

        vMessage = new ValidationMsg(myDataModel, message, severity);
        validationMessages.add(vMessage);
    }

    /**
     * Adds a critical message to indicate that there is no start place.
     */
    protected void messageCriticalNoStartPlace() {
        String message;
        EValidationResultSeverity severity;
        IValidationMsg vMessage;

        message = i18n.getMessage("criticalValidatorNoStartPlace");
        severity = EValidationResultSeverity.CRITICAL;

        vMessage = new ValidationMsg(myDataModel, message, severity);
        validationMessages.add(vMessage);
    }

    /**
     * Adds a critical message to indicate that there are too many start places.
     */
    protected void messageCriticalTooManyStartPlaces() {
        String message;
        EValidationResultSeverity severity;
        IValidationMsg vMessage;

        message = i18n.getMessage("criticalValidatorTooManyStartPlaces");
        severity = EValidationResultSeverity.CRITICAL;

        vMessage = new ValidationMsg(myDataModel, message, severity);
        validationMessages.add(vMessage);
    }

    /* Private helpers */

    /**
     * Generates a {@link List} of start places of the Petri net.
     * 
     * @return A {@link List} of type {@link String} with the IDs of all start
     *         places; empty if no place is a start place; null if the Petri net
     *         does not contain any places
     */
    private List<String> getStartPlaces() {
        /* Get all places. */
        List<IDataElement> elements = myDataModel.getElements();
        List<DataPlace> places = getDataPlaces(elements);
        if (places.size() == 0)
            return null; // return null, not an empty List!

        /* Determine the start places. */
        List<String> startPlaces = new LinkedList<String>();
        for (DataPlace place : places) {
            int placePredCount = place.getAllPredCount();
            if (placePredCount == 0) {
                String placeId = place.getId();
                startPlaces.add(placeId);

                /* Show start places on the message panel. */
                String message = i18n.getNameOnly("StartPlace") + ": ";
                String nameAndId = NodeInfo.getMessageStringNameAndId(place);
                message = message + nameAndId;

                IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.INFO);
                validationMessages.add(vMessage);
            }
        }

        return startPlaces;
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
