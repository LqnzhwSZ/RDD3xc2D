package de.lambeck.pned.models.data.validation;

import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;

/**
 * Abstract validator for {@link IDataModel}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public abstract class AbstractValidator {

    /** The {@link IDataModelController} for important messages/results */
    protected IDataModelController myDataModelController = null;

    protected I18NManager i18n;

    /** The {@link IDataModel} to check */
    protected IDataModel myDataModel = null;

    /**
     * The model name of the {@link IDataModel} for checks if the active file
     * has changed during a validation.
     */
    protected String myDataModelName = null;

    protected IDataElement curNode = null;

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
     * @param dataModelController
     *            The {@link IDataModelController}
     * @param i18n
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    public AbstractValidator(IDataModelController dataModelController, I18NManager i18n) {
        super();
        this.myDataModelController = dataModelController;
        this.i18n = i18n;
    }

    /**
     * Starts this validation for the specified {@link IDataModel}.
     * 
     * @param dataModel
     */
    public void startValidation(IDataModel dataModel) {
        this.myDataModel = dataModel;
        this.myDataModelName = dataModel.getModelName();
    }

    /**
     * Returns true if this {@link AbstractValidator} has more messages.
     * Otherwise: Removes the reference to the {@link IDataModel} and returns
     * false to indicate that this validation has finished.
     * 
     * @return True if there are more messages; otherwise false
     */
    public Boolean hasMoreMessages() {
        if (this.curNode != null) { return true; }

        this.myDataModel = null;
        return false;
    }

    /**
     * Returns the next message of this validator.
     * 
     * @return The next {@link IValidationMsg}
     */
    public abstract IValidationMsg nextMessage();

}
