package de.lambeck.pned.models.data.validation;

public interface IValidationMsg {

    public String getMessage();

    public String getModelId();

    // public String getNodeId();

    public EValidationResultSeverity getSeverity();

}
