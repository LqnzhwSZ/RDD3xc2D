package de.lambeck.pned.models.data.validation;

import de.lambeck.pned.elements.IElement;
import de.lambeck.pned.models.data.IDataModel;

public abstract class AbstractValidator {
	
	IDataModel dataModel = null;
	IElement curNode = null;

	public void startValidation(IDataModel dataModel) {
		this.dataModel = dataModel;
	}

	public Boolean hasMoreMessages() {
		if (this.curNode != null) {
			return true;
		}
		this.dataModel = null;
		return false;
	}

	public abstract IValidationMessage nextMessage();

}
