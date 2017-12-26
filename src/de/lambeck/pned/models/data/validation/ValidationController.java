package de.lambeck.pned.models.data.validation;

import java.security.InvalidParameterException;
import java.util.LinkedHashSet;
import java.util.Set;

import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.DataModel;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;

/**
 * Implements a {@link Thread} which regularly checks the current
 * {@link IDataModel} of the {@link IDataModelController} with a variable
 * {@link Set} of type {@link AbstractValidator}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ValidationController extends Thread {

    // private static boolean debug = true;

    /**
     * The reference to the data model controller, which provides the current
     * data model.
     */
    private IDataModelController myDataModelController = null;

    protected I18NManager i18n;

    /**
     * The Set of {@link AbstractValidator} to run on each {@link DataModel}.
     */
    private Set<AbstractValidator> validatorSet = new LinkedHashSet<AbstractValidator>();

    /**
     * Constructs this validation controller with a reference to the
     * {@link IDataModelController} and the {@link I18NManager} for localized
     * messages.
     * 
     * @param dataModelController
     *            The data model controller
     * @param i18n
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    public ValidationController(IDataModelController dataModelController, I18NManager i18n) {
        super();
        if (dataModelController == null)
            throw new InvalidParameterException("DataModelController must not be null");

        this.myDataModelController = dataModelController;
        this.i18n = i18n;
    }

    /**
     * Adds an {@link AbstractValidator} to the {@link Set} of validators.
     * 
     * @param validator
     *            The {@link AbstractValidator} to add
     */
    public void addValidator(AbstractValidator validator) {
        validatorSet.add(validator);
    }

    @Override
    public void run() {
        // Thread.State state = this.getState();
        try {
            while (this.getState() != Thread.State.TERMINATED) {
                IDataModel model = myDataModelController.getCurrentModel();

                if ((model != null) && (!model.isModelChecked())) {
                    /*
                     * Set "ModelChecked" to true as early as possible: If the
                     * state will be set to "unchecked" due to changes to the
                     * model, it won't be overwritten at the end.
                     */
                    model.setModelChecked(true);
                    model.setModelValidity(false);

                    /* Assume a valid model. */
                    boolean isModelValid = true;

                    /* Get the message panel. */
                    String modelName = model.getModelName();
                    IValidationMsgPanel msgPanel;
                    msgPanel = myDataModelController.getValidationMessagePanel(modelName);

                    if (msgPanel == null) {
                        String errMsg = "ValidationController, cannot start validation: ";
                        errMsg = errMsg + "no message panel for model '" + model.getModelName() + "'";
                        System.err.println(errMsg);

                    } else {
                        /* Reset before the first validator starts. */
                        msgPanel.reset();
                        msgPanel.setBgColor(EValidationColor.PENDING);

                        /* Run all validators on the current model. */
                        for (AbstractValidator validator : this.validatorSet) {
                            validator.startValidation(model);

                            /* Get all messages from the current validator. */
                            while ((this.getState() != Thread.State.TERMINATED) && (validator.hasMoreMessages())) {
                                IValidationMsg message = validator.nextMessage();

                                isModelValid = handleMessage(isModelValid, msgPanel, message);

                                Thread.yield();

                                if (this.getState() == Thread.State.TERMINATED) {
                                    break;
                                }
                            }

                            /*
                             * Check if the user has switched to another file.
                             * (In which case the current model would have been
                             * changed and we can quit before starting the next
                             * validator for the old model.)
                             */
                            if (myDataModelController.getCurrentModel() != model) {
                                /* Reset state because we have aborted. */
                                model.setModelChecked(false);
                                /* Leave the validators loop */
                                break;
                            }
                        }
                    }

                    /*
                     * Return the result to the model and change the background
                     * of the validation message panel if we have a result.
                     * 
                     * But we have to check if the model wasn't modified in the
                     * meantime! (In which case model.isModelChecked() would
                     * return false.)
                     * 
                     * -> In this case: the last validation is already obsolete!
                     */
                    if (model.isModelChecked()) {
                        model.setModelValidity(isModelValid);

                        if (isModelValid) {
                            msgPanel.setBgColor(EValidationColor.VALID);
                        } else {
                            msgPanel.setBgColor(EValidationColor.INVALID);
                        }
                    }
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            // state = Thread.State.TERMINATED;
            this.interrupt();
        }
    }

    /**
     * Handles the current message.
     * 
     * @param isModelValid
     *            The previous state of isModelValid
     * @param msgPanel
     *            The {@link IValidationMsgPanel}
     * @param message
     *            The current {@link IValidationMsg}
     * @return The new state of isModelValid
     */
    private boolean handleMessage(boolean isModelValid, IValidationMsgPanel msgPanel, IValidationMsg message) {
        if (message != null) {
            msgPanel.addMessage(message.getMessage());
        }

        if (message.getSeverity() == EValidationResultSeverity.CRITICAL) {
            isModelValid = false; // But continue with more messages and tests
            msgPanel.setBgColor(EValidationColor.INVALID);
        }
        return isModelValid;
    }

}
