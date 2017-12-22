package de.lambeck.pned.models.data.validation;

public class RecursionValidator extends AbstractValidator {

	@Override
	public IValidationMessage nextMessage() {
		if (this.dataModel != null) {
			
		}
		this.dataModel = null;
		return null;
	}

}
