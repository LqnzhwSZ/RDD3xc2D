package de.lambeck.pned.models.data.validation;

import java.util.Set;

import de.lambeck.pned.models.data.IDataModel;

/**
 * Interface for validation controllers for {@link IDataModel}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IValidationController {

    /**
     * Adds an {@link IValidator} to the {@link Set} of validators.
     * 
     * @param validator
     *            The {@link IValidator} to add
     * @param validatorName
     *            The name of the added validator
     */
    void addValidator(IValidator validator, String validatorName);

    /**
     * Returns the highest previous {@link EValidationResultSeverity} for the
     * current set of validations (for the current model).
     * 
     * Note: this is intended to be used by some {@link IValidator} to decide
     * whether to skip some checks or not.
     * 
     * @return {@link EValidationResultSeverity}
     */
    EValidationResultSeverity getCurrentValidationStatus();

    /**
     * Request to the {@link IValidationController} thread to run the specified
     * {@link IValidator} on the specified {@link IDataModel}.
     * 
     * @param validatorName
     *            The specified {@link IValidator}
     * @param dataModel
     *            The specified {@link IDataModel}
     */
    void requestIndividualValidation(String validatorName, IDataModel dataModel);

}
