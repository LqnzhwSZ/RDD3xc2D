package de.lambeck.pned.models.data.validation;

import java.util.LinkedList;
import java.util.List;

import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;

/**
 * Checks the number of end places in a workflow net.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class EndPlacesValidator extends AbstractValidator {

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
    public EndPlacesValidator(int Id, IValidationController validationController,
            IDataModelController dataModelController, I18NManager i18n) {
        super(Id, validationController, dataModelController, i18n);
        this.validatorInfoString = "infoEndPlacesValidator";
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

        /* Reset all previous end places. */
        myDataModelController.resetAllDataEndPlaces(myDataModelName);

        /* Store all end places. */
        List<String> endPlaces = getEndPlaces();

        /* Evaluate the results... */
        if (evaluateNoPlaces(endPlaces))
            return;

        if (evaluateNoEndPlaces(endPlaces))
            return;

        /* At least 1 end place; highlight all */
        highlightEndPlaces(endPlaces);

        if (evaluateTooManyEndPlaces(endPlaces))
            return;

        /* End places are OK, test successful. */
        reportValidationSuccessful();
    }

    /**
     * Returns true if the model has no places.
     * 
     * @param endPlaces
     *            The {@link List} of end places
     */
    private boolean evaluateNoPlaces(List<String> endPlaces) {
        if (endPlaces == null) {
            String message = i18n.getMessage("warningValidatorNoPlaces");
            IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.CRITICAL);
            validationMessages.add(vMessage);

            return true;
        }
        return false;
    }

    /**
     * Returns true if the model has no end place.
     * 
     * @param endPlaces
     *            The {@link List} of end places
     */
    private boolean evaluateNoEndPlaces(List<String> endPlaces) {
        if (endPlaces.size() == 0) {
            String message = i18n.getMessage("warningValidatorNoEndPlace");
            IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.CRITICAL);
            validationMessages.add(vMessage);

            return true;
        }
        return false;
    }

    /**
     * Informs the data model controller which places need to be highlighted as
     * end places.
     * 
     * @param endPlaces
     *            The {@link List} of end places
     */
    private void highlightEndPlaces(List<String> endPlaces) {
        for (String placeId : endPlaces) {
            myDataModelController.setDataEndPlace(myDataModelName, placeId, true);
        }
    }

    /**
     * Returns true if the model has too many end places.
     * 
     * @param endPlaces
     *            The {@link List} of end places
     */
    private boolean evaluateTooManyEndPlaces(List<String> endPlaces) {
        if (endPlaces.size() > 1) {
            String message = i18n.getMessage("warningValidatorTooManyEndPlaces");
            IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.CRITICAL);
            validationMessages.add(vMessage);

            return true;
        }
        return false;
    }

    /*
     * Private helpers
     */

    /**
     * Generates a {@link List} of end places of the Petri net.
     * 
     * @return A {@link List} of type {@link String} with the IDs of all end
     *         places; empty if no place is a end place; null if the Petri net
     *         does not contain any places
     */
    private List<String> getEndPlaces() {
        /* Get all places. */
        List<IDataElement> elements = myDataModel.getElements();
        List<DataPlace> places = getPlaces(elements);
        if (places.size() == 0)
            return null; // return null, not an empty List!

        /* Determine the end places. */
        List<String> endPlaces = new LinkedList<String>();
        for (DataPlace place : places) {
            int placeSuccCount = place.getAllSuccCount();
            if (placeSuccCount == 0) {
                String placeId = place.getId();
                endPlaces.add(placeId);

                /* Show end places on the message panel. */
                String message = i18n.getNameOnly("EndPlace") + ": " + place.getId();
                IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.INFO);
                validationMessages.add(vMessage);
            }
        }

        return endPlaces;
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
