package de.lambeck.pned.models.data.validation;

import de.lambeck.pned.models.data.IDataModel;

/**
 * The message class for {@link AbstractValidator}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ValidationMsg implements IValidationMsg {

    /** This should be the canonical (unique) path name of the file. */
    private String modelName;

    /** The message (e.g. problem description) */
    private String message;

    /** The severity (importance) of the message/problem */
    private EValidationResultSeverity severity;

    /**
     * Constructs a new validation message for an {@link AbstractValidator}.
     * 
     * @param model
     *            The {@link IDataModel}
     * @param message
     *            The message {@link String}
     * @param severity
     *            The severity of the found Problem
     *            ({@link EValidationResultSeverity})
     */
    @SuppressWarnings("hiding")
    public ValidationMsg(IDataModel model, String message, EValidationResultSeverity severity) {
        this.modelName = model.getModelName();
        this.message = message;
        this.severity = severity;
    }

    @Override
    public String toString() {
        return String.format("Model '%s': %s, severity: %s", this.modelName, this.message, this.severity);
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getModelName() {
        return this.modelName;
    }

    @Override
    public EValidationResultSeverity getSeverity() {
        return this.severity;
    }

}
