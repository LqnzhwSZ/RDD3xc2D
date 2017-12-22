package de.lambeck.pned.models.data.validation;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Set;

import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;

public class ValidationController extends Thread {

	private IDataModelController dataModelController = null;
	private Set<AbstractValidator> validatorSet = new HashSet<AbstractValidator>();
	
	public ValidationController() {
		super();
	}

	public ValidationController(IDataModelController dataModelController) {
		super();
		if (dataModelController == null) {
			throw new InvalidParameterException("DataModelController must not be null");
		}
		this.dataModelController = dataModelController;
	}
	
	public void addValidator(AbstractValidator validator) {
		validatorSet.add(validator);
	}
	

	@Override
	public void run() {
		Thread.State state = this.getState();
		try {
			while (state != Thread.State.TERMINATED) {
				IDataModel model = dataModelController.getCurrentModel();
				if ((model != null) && (!model.isModelChecked())) {
					/*
					 * Set to true as early as possible: If the state will be set to "unchecked" due
					 * to changes to the model, it won't be overwritten at the end.
					 */
					model.setModelChecked(true);
					model.setModelValidity(false);
					boolean isModelValid = true;
					IValidationMessagesPanel msgPanel = dataModelController.getValidationMessagePanel(model.getModelName());
					for (AbstractValidator validator : this.validatorSet) {
						validator.startValidation(model);
						while ((state != Thread.State.TERMINATED) && (validator.hasMoreMessages())) {
							IValidationMessage message = validator.nextMessage();
							if (message != null) {
								isModelValid = false;
								msgPanel.addMessage(message.toString());
							}
							Thread.yield();
							if (this.getState() == Thread.State.TERMINATED) {
								break;
							}
						}
					}
					if (model.isModelChecked()) {
						model.setModelValidity(isModelValid);
					}
				}
				Thread.sleep(1000);
			}			
		} catch (InterruptedException e) {
			state = Thread.State.TERMINATED;
		}
	}

}
