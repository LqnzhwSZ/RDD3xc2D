package de.lambeck.pned.models.data.validation;

public interface IValidationMessage {

	public String getMessage();
	public String getModelID();
	public String getNodeID();
	public ValidationResultSeverity getSeverity();
}
