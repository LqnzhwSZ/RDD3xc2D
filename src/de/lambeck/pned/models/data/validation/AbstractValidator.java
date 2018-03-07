package de.lambeck.pned.models.data.validation;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;

/**
 * Abstract validator for {@link IDataModel} to be used in combination with the
 * {@link ValidationController}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public abstract class AbstractValidator implements IValidator {

    /**
     * The variable for the the {@link I18NManager} to get the info string for
     * {@link addValidatorInfo}.
     */
    protected String validatorInfoString = "";

    /**
     * Reference to the {@link IValidationController}
     */
    protected IValidationController myValidationController = null;

    /**
     * Reference to the {@link IDataModelController} for important
     * messages/results
     */
    protected IDataModelController myDataModelController = null;

    /** The manager for localized strings */
    protected I18NManager i18n;

    /** The {@link IDataModel} to check */
    protected IDataModel myDataModel = null;

    /**
     * Stores whether the current check is the initial check or not.<BR>
     * <BR>
     * Note: Initial check means that the data model was just loaded from a PNML
     * file. (Abort condition for some validators)
     */
    protected boolean isInitialModelCheck = false;

    /**
     * The model name of the {@link IDataModel} for checks if the active file
     * has changed during a validation.
     */
    protected String myDataModelName = null;

    /**
     * The {@link List} of {@link ValidationMsg} produced by this
     * {@link IValidator}
     */
    protected List<IValidationMsg> validationMessages = new ArrayList<IValidationMsg>();

    /* Constructors */

    /**
     * Constructs a stand-alone validator without a reference to the
     * {@link IDataModelController}.
     */
    public AbstractValidator() {
        super();
    }

    /**
     * Constructs this validator with a reference to the
     * {@link IDataModelController} to be able to pass important
     * messages/results to it.
     * 
     * @param validationController
     *            The {@link IValidationController}
     * @param dataModelController
     *            The {@link IDataModelController}
     * @param i18n
     *            The manager for localized strings
     */
    @SuppressWarnings("hiding")
    public AbstractValidator(IValidationController validationController, IDataModelController dataModelController,
            I18NManager i18n) {
        super();
        this.myValidationController = validationController;
        this.myDataModelController = dataModelController;
        this.i18n = i18n;
    }

    /* Validation methods */

    @Override
    public void startValidation(IDataModel dataModel, boolean initialModelCheck) {
        getDataFromModel(dataModel);
        this.isInitialModelCheck = initialModelCheck;

        addValidatorInfo();
    }

    /**
     * Retrieves the necessary data from the specified {@link IDataModel}.
     * 
     * @param dataModel
     *            The specified {@link IDataModel}
     */
    protected void getDataFromModel(IDataModel dataModel) {
        this.myDataModel = dataModel;
        this.myDataModelName = dataModel.getModelName();
    }

    /**
     * Returns true if the specified {@link IDataModel} is empty (all elements
     * deleted).
     * 
     * @param dataModel
     *            The specified {@link IDataModel}
     * @return True = empty, false = not empty
     */
    protected boolean evaluateEmptyModel(IDataModel dataModel) {
        if (dataModel.isEmpty()) {
            messageInfoEmptyModel();
            return true;
        }
        return false;
    }

    /* Return methods */

    @Override
    public boolean hasMoreMessages() {
        if (!validationMessages.isEmpty())
            return true;

        this.myDataModel = null;
        return false;
    }

    @Override
    public IValidationMsg nextMessage() {
        IValidationMsg nextMessage = null;
        nextMessage = validationMessages.remove(0);
        return nextMessage;
    }

    /* Standard messages */

    /**
     * Adds a {@link IValidationMsg} message with ID and purpose of this
     * validator to the messages list.
     */
    protected void addValidatorInfo() {
        String message;
        EValidationResultSeverity severity;
        IValidationMsg vMessage;

        message = i18n.getMessage(this.validatorInfoString);
        severity = EValidationResultSeverity.INFO;

        vMessage = new ValidationMsg(myDataModel, message, severity);
        validationMessages.add(vMessage);
    }

    /**
     * Adds a {@link IValidationMsg} with {@link EValidationResultSeverity}
     * "INFO" to the messages list to indicate that this validation has been
     * stopped for an empty model.<BR>
     */
    protected void messageInfoEmptyModel() {
        String message;
        EValidationResultSeverity severity;
        IValidationMsg vMessage;

        message = i18n.getMessage("infoValidatorSkippedForEmptyModel");
        severity = EValidationResultSeverity.INFO;

        vMessage = new ValidationMsg(myDataModel, message, severity);
        validationMessages.add(vMessage);
    }

    /**
     * Adds a {@link IValidationMsg} with {@link EValidationResultSeverity}
     * "INFO" to the messages list to indicate that this validation has been
     * stopped for an invalid model.<BR>
     * <BR>
     * Note: This message is an "INFO" message because the
     * {@link ValidationController} stores only the highest
     * {@link EValidationResultSeverity} anyways.
     */
    protected void infoIgnoredForInvalidModel() {
        String message;
        EValidationResultSeverity severity;
        IValidationMsg vMessage;

        message = i18n.getMessage("infoIgnoredForInvalidModel");
        severity = EValidationResultSeverity.INFO;

        vMessage = new ValidationMsg(myDataModel, message, severity);
        validationMessages.add(vMessage);
    }

    /**
     * Adds the final {@link IValidationMsg} with
     * {@link EValidationResultSeverity} "INFO" to the messages list to indicate
     * successful validation.
     */
    protected void reportValidationSuccessful() {
        String message;
        EValidationResultSeverity severity;
        IValidationMsg vMessage;

        DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);

        message = ZonedDateTime.now().format(fmt);
        severity = EValidationResultSeverity.INFO;

        vMessage = new ValidationMsg(myDataModel, message, severity);
        validationMessages.add(vMessage);
    }

}
