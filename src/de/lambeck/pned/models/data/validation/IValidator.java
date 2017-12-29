package de.lambeck.pned.models.data.validation;

import de.lambeck.pned.models.data.IDataModel;

/**
 * Interface for validators for the {@link ValidationController}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IValidator {

    /**
     * Starts this validation for the specified {@link IDataModel}.
     * 
     * @param dataModel
     *            The specified {@link IDataModel}
     * @param initialModelCheck
     *            True = data model was just loaded from a PNML file, false =
     *            data model has already been checked before.
     */
    void startValidation(IDataModel dataModel, boolean initialModelCheck);

    /**
     * Returns true if this {@link AbstractValidator} has more
     * {@link IValidationMsg} in his list of validation messages. Otherwise:
     * Removes the reference to the {@link IDataModel} and returns false to
     * indicate that this validation is complete.
     * 
     * @return True if there are more messages; otherwise false
     */
    boolean hasMoreMessages();

    /**
     * Returns the next message of this validator.
     * 
     * @return The next {@link IValidationMsg}
     */
    IValidationMsg nextMessage();

}
