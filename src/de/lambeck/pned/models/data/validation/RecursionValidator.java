package de.lambeck.pned.models.data.validation;

public class RecursionValidator extends AbstractValidator {

	@Override
	public IValidationMsg nextMessage() {
		if (this.myDataModel != null) {
			
		}
		this.myDataModel = null;
		return null;
	}

}
