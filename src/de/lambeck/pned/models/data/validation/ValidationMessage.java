package de.lambeck.pned.models.data.validation;

import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.models.data.IDataModel;

public class ValidationMessage implements IValidationMessage {
	
	private String modelId;
	private String nodeId;
	private String message;
	private ValidationResultSeverity severity;
	
	public ValidationMessage(IDataModel model, IDataElement node, String message, ValidationResultSeverity severity) {
		this.modelId = model.getModelName();
		this.nodeId = node.getId();
		this.message = message;
		this.severity = severity;
	}

	@Override
	public String toString() {
		return String.format("Model ID '%s' Node ID '%s': %s", this.modelId, this.nodeId, this.message);
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public String getModelID() {
		return this.modelId;
	}

	@Override
	public String getNodeID() {
		return this.nodeId;
	}

	@Override
	public ValidationResultSeverity getSeverity() {
		return this.severity;
	}
	
	
}
