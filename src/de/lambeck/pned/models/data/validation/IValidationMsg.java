package de.lambeck.pned.models.data.validation;

/**
 * Interface for messages for {@link IValidationMsgPanel}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IValidationMsg {

    /**
     * Returns the text of the message.
     * 
     * @return The text as {@link String}
     */
    public String getMessage();

    /**
     * Returns the id of the model that was checked by the
     * {@link AbstractValidator}.
     * 
     * @return The model IDas {@link String}
     */
    public String getModelId();

    // public String getNodeId();

    /**
     * Returns the severity level of this validation message which should inform
     * the {@link IValidationController} whether the current model might be
     * invalid or not.
     * 
     * @return A value of type {@link EValidationResultSeverity}
     */
    public EValidationResultSeverity getSeverity();

}
