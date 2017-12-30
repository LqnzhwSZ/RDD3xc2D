package de.lambeck.pned.models.data.validation;

import java.util.LinkedList;
import java.util.List;

import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;

/**
 * Checks the number of start places in a workflow net.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class StartPlacesValidator extends AbstractValidator {

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
    public StartPlacesValidator(int Id, IValidationController validationController,
            IDataModelController dataModelController, I18NManager i18n) {
        super(Id, validationController, dataModelController, i18n);
        this.validatorInfoString = "infoStartPlacesValidator";
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
     */
    private boolean evaluateNoPlaces(List<String> startPlaces) {
        if (startPlaces == null) {
            String message = i18n.getMessage("warningValidatorNoPlaces");
            IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.CRITICAL);
            validationMessages.add(vMessage);

            return true;
        }
        return false;
    }

    /**
     * Returns true if the model has no start place.
     * 
     * @param startPlaces
     *            The {@link List} of start places
     */
    private boolean evaluateNoStartPlaces(List<String> startPlaces) {
        if (startPlaces.size() == 0) {
            String message = i18n.getMessage("warningValidatorNoStartPlace");
            IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.CRITICAL);
            validationMessages.add(vMessage);

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
     */
    private boolean evaluateTooManyStartPlaces(List<String> startPlaces) {
        if (startPlaces.size() > 1) {
            /* Highlight as candidates */
            for (String placeId : startPlaces) {
                myDataModelController.setDataStartPlaceCandidate(myDataModelName, placeId, true);
            }

            /* Validation message */
            String message = i18n.getMessage("warningValidatorTooManyStartPlaces");
            IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.CRITICAL);
            validationMessages.add(vMessage);

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

    /*
     * Private helpers
     */

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
        List<DataPlace> places = getPlaces(elements);
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
                String message = i18n.getNameOnly("StartPlace") + ": " + place.getId();
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
