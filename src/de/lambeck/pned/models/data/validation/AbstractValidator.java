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
 * Abstract validator for {@link IDataModel}.
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

    /** The source object for I18N strings */
    protected I18NManager i18n;

    /** The {@link IDataModel} to check */
    protected IDataModel myDataModel = null;

    /**
     * Was the data model just loaded from a PNML file? (Abort condition for
     * some validators)
     */
    protected boolean isInitialModelCheck = false;

    /**
     * The model name of the {@link IDataModel} for checks if the active file
     * has changed during a validation.
     */
    protected String myDataModelName = null;

    protected List<IValidationMsg> validationMessages = new ArrayList<IValidationMsg>();

    /*
     * Constructors
     */

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
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    public AbstractValidator(IValidationController validationController, IDataModelController dataModelController,
            I18NManager i18n) {
        super();
        this.myValidationController = validationController;
        this.myDataModelController = dataModelController;
        this.i18n = i18n;
    }

    /*
     * Validation methods
     */

    @Override
    public void startValidation(IDataModel dataModel, boolean initialModelCheck) {
        getDataFromModel(dataModel);
        this.isInitialModelCheck = initialModelCheck;

        addValidatorInfo();
    }

    /**
     * Retrieves the necessary data from the data model.
     * 
     * @param dataModel
     *            The specified {@link IDataModel}
     */
    protected void getDataFromModel(IDataModel dataModel) {
        this.myDataModel = dataModel;
        this.myDataModelName = dataModel.getModelName();
    }

    /*
     * Return methods
     */

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

    /*
     * Standard messages
     */

    /**
     * Adds an info message with ID and purpose of this validator.
     */
    protected void addValidatorInfo() {
        String message;
        IValidationMsg vMessage;

        message = i18n.getMessage(this.validatorInfoString);
        vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.INFO);
        validationMessages.add(vMessage);
    }

    /**
     * Adds the final {@link IValidationMsg} with status "INFO" to the messages
     * list.
     */
    protected void reportValidationSuccessful() {
        DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);
        String message = ZonedDateTime.now().format(fmt);
        IValidationMsg vMessage = new ValidationMsg(myDataModel, message, EValidationResultSeverity.INFO);
        validationMessages.add(vMessage);
    }

}
