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
public class ValidationController extends Thread implements IValidationController {

    // private static boolean debug = true;

    /**
     * Predefined parameter because the {@link ValidationController} should
     * always remove the "initial check" state from the {@link IDataModel}.
     */
    private final static boolean ALWAYS_REMOVE_INITIAL_CHECK_STATE = true;

    /**
     * The reference to the data model controller, which provides the current
     * data model.
     */
    private IDataModelController myDataModelController = null;

    protected I18NManager i18n;

    /**
     * The Set of {@link AbstractValidator} to run on each {@link DataModel}.
     */
    private Set<IValidator> validatorSet = new LinkedHashSet<IValidator>();

    /**
     * Stores the highest previous {@link EValidationResultSeverity} for the
     * current set of validations (for the current model).
     */
    private EValidationResultSeverity currentValidationStatus = EValidationResultSeverity.INFO;

    /* Constructor */

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

    /* Public methods */

    @Override
    public void addValidator(IValidator startPlacesValidator) {
        validatorSet.add(startPlacesValidator);
    }

    @Override
    public void run() {
        // Thread.State state = this.getState();
        try {
            while (this.getState() != Thread.State.TERMINATED) {
                IDataModel dataModel = myDataModelController.getCurrentModel();

                if ((dataModel != null) && (!dataModel.isModelChecked())) {
                    boolean isInitialModelCheck = dataModel.isInitialModelCheck();

                    /*
                     * Set "ModelChecked" to true as early as possible: If the
                     * state will be set to "unchecked" due to changes to the
                     * model, it won't be overwritten at the end.
                     */
                    dataModel.setModelChecked(true, ALWAYS_REMOVE_INITIAL_CHECK_STATE);
                    dataModel.setModelValidity(false);

                    /* Assume a valid model. */
                    boolean isModelValid = true;

                    /* Get the message panel. */
                    String modelName = dataModel.getModelName();
                    IValidationMsgPanel msgPanel;
                    msgPanel = myDataModelController.getValidationMessagePanel(modelName);

                    if (msgPanel == null) {
                        // Import from PNML file not finished?
                        String errMsg = "ValidationController, cannot start validation: ";
                        errMsg = errMsg + "no message panel for model '" + dataModel.getModelName() + "'";
                        System.err.println(errMsg);

                    } else {
                        /* Resets before the first validator starts. */
                        msgPanel.reset();
                        msgPanel.setBgColor(EValidationColor.PENDING);
                        this.currentValidationStatus = EValidationResultSeverity.INFO;

                        /* Run all validators on the current model. */
                        for (IValidator validator : this.validatorSet) {
                            validator.startValidation(dataModel, isInitialModelCheck);

                            /* Get all messages from the current validator. */
                            while ((this.getState() != Thread.State.TERMINATED) && (validator.hasMoreMessages())) {
                                IValidationMsg message = validator.nextMessage();

                                isModelValid = handleMessage(isModelValid, msgPanel, message);

                                Thread.yield();

                                if (this.getState() == Thread.State.TERMINATED) {
                                    break;
                                }
                            }
                            msgPanel.addMessage("");

                            /*
                             * Check if validation errors already are severe
                             * enough to cancel further validators?
                             * 
                             * -> No! Never cancel further validators! (e.g. to
                             * remove the initial marking from invalid models!)
                             */
                            // if (!isModelValid) {
                            // /* Don't change the "checked" state. */
                            // break;
                            // }

                            /*
                             * Check if the user has switched to another file.
                             * (In which case the current model would have been
                             * changed and we can quit before starting the next
                             * validator for the old model.)
                             */
                            if (myDataModelController.getCurrentModel() != dataModel) {
                                /* Reset state because we have aborted. */
                                dataModel.setModelChecked(false, ALWAYS_REMOVE_INITIAL_CHECK_STATE);
                                /* Leave the validators loop */
                                break;
                            }
                        }

                        /*
                         * Return the result to the model and change the
                         * background of the validation message panel if we have
                         * a result.
                         * 
                         * But we have to check if the model wasn't modified in
                         * the meantime! (In which case model.isModelChecked()
                         * would return false.)
                         * 
                         * -> In this case: the last validation is already
                         * obsolete!
                         */
                        if (dataModel.isModelChecked()) {
                            dataModel.setModelValidity(isModelValid);

                            if (isModelValid) {
                                msgPanel.setBgColor(EValidationColor.VALID);
                            } else {
                                msgPanel.setBgColor(EValidationColor.INVALID);
                            }
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

    @Override
    public EValidationResultSeverity getCurrentValidationStatus() {
        return this.currentValidationStatus;
    }

    /* Private helpers */

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

        EValidationResultSeverity currentSeverity = message.getSeverity();
        storeMaxSeverityLevel(currentSeverity);

        if (currentSeverity == EValidationResultSeverity.CRITICAL) {
            isModelValid = false; // But continue with more messages and tests
            msgPanel.setBgColor(EValidationColor.INVALID);
        }

        return isModelValid;
    }

    /**
     * Stores the current severity level if higher than the previous.
     * 
     * @param nextSeverity
     *            the severity level of the last message from the current
     *            validator
     */
    private void storeMaxSeverityLevel(EValidationResultSeverity nextSeverity) {
        if (this.currentValidationStatus.toInt() < nextSeverity.toInt()) {
            this.currentValidationStatus = nextSeverity;
        }
    }

}
