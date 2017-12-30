package de.lambeck.pned.models.data.validation;

import de.lambeck.pned.elements.data.DataTransition;
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
public class ActivatedTransitionsValidator extends AbstractValidator {

    // private static boolean debug = false;

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
    public ActivatedTransitionsValidator(int Id, IValidationController validationController,
            IDataModelController dataModelController, I18NManager i18n) {
        super(Id, validationController, dataModelController, i18n);
        this.validatorInfoString = "infoActivatedTransitionsValidator";
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

        if (checkAbortCondition1())
            return;

        // removeExistingActivatedStates();

        // if (checkAbortCondition2())
        // return;

        // /* Check condition: exactly 1 start place */
        // if (!evaluateStartPlace())
        // return;

        // /* Set the initial marking */
        // setInitialMarking(this.myStartPlace);

        // /* Initial marking OK, test successful. */
        // reportValidationSuccessful();
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

    /**
     * Removes the "activated" state from all {@link DataTransition}.
     */
    private void removeExistingActivatedStates() {
        // myDataModelController.removeExistingActivatedStates(myDataModelName);

        // TODO DataTransition has checkActivated() and isActivated() methods!
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

    /*
     * Private helpers
     */

}
