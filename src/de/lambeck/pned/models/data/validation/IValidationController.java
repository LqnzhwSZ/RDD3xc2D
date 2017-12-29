package de.lambeck.pned.models.data.validation;

import java.util.Set;

/**
 * Interface for validation controllers.
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
     */
    void addValidator(IValidator validator);

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

}
