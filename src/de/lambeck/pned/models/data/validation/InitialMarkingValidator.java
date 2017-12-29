package de.lambeck.pned.models.data.validation;

import java.util.*;

import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.elements.data.IDataNode;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;

/**
 * Sets the initial marking (token on the start place) and checks which
 * transitions are activated.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class InitialMarkingValidator extends AbstractValidator {

    private static boolean debug = false;

    /** The start place of the model (if unambiguous) */
    private DataPlace myStartPlace = null;

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

    /*
     * Constructor
     */

    /**
     * 
     * @param Id
     *            The ID of this validator (for validation messages)
     * @param dataModelController
     *            The {@link IDataModelController}
     * @param i18n
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    public InitialMarkingValidator(int Id, IDataModelController dataModelController, I18NManager i18n) {
        super(Id, dataModelController, i18n);
        this.validatorInfoString = "infoInitialMarkingValidator";
    }

    /*
     * TODO
     * 
     * - Highlight start place
     * 
     * -> Sequence: structural model changes -> checked = false -> highlight
     * start place again
     * 
     * - Highlight activated transitions
     * 
     */

    /*
     * Validation methods
     */

    @Override
    public void startValidation(IDataModel dataModel) {
        getDataFromModel(dataModel);

        addValidatorInfo();

        /*
         * Check condition 1: exactly 1 start place
         */
        if (!evaluateStartPlace())
            return;

        /* Reset the model to the initial marking */
        resetToInitialMarking(this.myStartPlace);

        // /*
        // * Initialize the lists because this validator gets called for
        // different
        // * models!
        // */
        // initializeLists();
        // resetPrevUnreachableHighlighting();

        // /*
        // * Check condition 2: all nodes reachable from the start place?
        // */
        // traverseAllNodesForward(myStartPlace);
        // highlightUnreachableNodes(noPathFromStartNode);
        // int noPathFromStart = noPathFromStartNode.size();
        // if (noPathFromStart > 0)
        // reportFailed_NoPathFromStartPlace(noPathFromStart);

        /*
         * Evaluate result?
         */

        /*
         * ?
         */
        reportValidationSuccessful();
    }

    private void getDataFromModel(IDataModel dataModel) {
        this.myDataModel = dataModel;
        this.myDataModelName = dataModel.getModelName();

        List<IDataElement> modelElements = myDataModel.getElements();
        this.allElements = new ArrayList<IDataElement>(modelElements);
    }

    /*
     * For check 1
     */

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

    private void resetToInitialMarking(DataPlace startPlace) {
        String placeId = startPlace.getId();

        List<String> placesWithToken = new ArrayList<String>();
        placesWithToken.add(placeId);

        myDataModelController.removeAllDataTokens(myDataModelName);
        myDataModelController.addDataToken(myDataModelName, placesWithToken);
    }

    /*
     * Messages
     */

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
