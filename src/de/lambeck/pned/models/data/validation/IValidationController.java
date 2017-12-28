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

}
