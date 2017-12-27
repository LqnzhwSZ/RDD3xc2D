package de.lambeck.pned.models.data.validation;

import de.lambeck.pned.models.data.IDataModel;

/**
 * The message class for {@link AbstractValidator}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ValidationMsg implements IValidationMsg {

    private String modelId;

    // private String nodeId;

    private String message;

    private EValidationResultSeverity severity;

    // /**
    // * Constructs a new validation message for an {@link AbstractValidator}.
    // *
    // * @param model
    // * The {@link IDataModel}
    // * @param node
    // * @param message
    // * The message {@link String}
    // * @param severity
    // * The severity of the found Problem ({@link EValidationResultSeverity})
    // */
    // @SuppressWarnings("hiding")
    // public ValidationMessage(IDataModel model, IDataElement node, String
    // message, EValidationResultSeverity severity) {
    // this.modelId = model.getModelName();
    // this.nodeId = node.getId();
    // this.message = message;
    // this.severity = severity;
    // }

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
        this.modelId = model.getModelName();
        this.message = message;
        this.severity = severity;
    }

    // @Override
    // public String toString() {
    // return String.format("Model ID '%s' Node ID '%s': %s", this.modelId,
    // this.nodeId, this.message);
    // }

    @Override
    public String toString() {
        return String.format("Model ID '%s': %s", this.modelId, this.message);
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getModelId() {
        return this.modelId;
    }

    // @Override
    // public String getNodeId() {
    // return this.nodeId;
    // }

    @Override
    public EValidationResultSeverity getSeverity() {
        return this.severity;
    }

}
