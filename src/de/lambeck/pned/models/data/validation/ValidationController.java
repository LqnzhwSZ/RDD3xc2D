package de.lambeck.pned.models.data.validation;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.DataModel;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * Implements a {@link Thread} which regularly checks the current
 * {@link IDataModel} of the {@link IDataModelController} with a variable
 * {@link Map} of type {@link AbstractValidator}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ValidationController extends Thread implements IValidationController {

    /** Show debug messages? */
    private static boolean debug = false;

    /**
     * The sleep time between validation cycles. (Smaller values = faster
     * reaction to changes on the draw panel, higher values = save CPU power)
     */
    private final static int CYCLE_SLEEP_TIME = 1000;

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

    /** The manager for localized strings */
    protected I18NManager i18n;

    /**
     * The Map of {@link AbstractValidator} to run on each {@link DataModel}.
     * (validatorMap uses LinkedHashMap to preserve the order of validations.)
     */
    private Map<String, IValidator> validatorMap = new LinkedHashMap<String, IValidator>();

    /**
     * A Map to store the highest previous {@link EValidationResultSeverity} for
     * the current set of validations and the specified model.
     * 
     * Note: This is necessary because the user can switch between files!
     */
    private Map<String, EValidationResultSeverity> currentValidationStatus = new HashMap<String, EValidationResultSeverity>();

    /* Constructor */

    /**
     * Constructs this validation controller with a reference to the
     * {@link IDataModelController} and the {@link I18NManager} for localized
     * messages.
     * 
     * @param dataModelController
     *            The data model controller
     * @param i18n
     *            The manager for localized strings
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
    public void addValidator(IValidator validator, String validatorName) {
        validatorMap.put(validatorName, validator);
    }

    @Override
    public void run() {
        // Thread.State state = this.getState();
        try {
            while (this.getState() != Thread.State.TERMINATED) {
                IDataModel dataModel = myDataModelController.getCurrentModel();

                if ((dataModel != null) && (!dataModel.isModelChecked())) {
                    runAllValidations(dataModel);
                }
                Thread.sleep(CYCLE_SLEEP_TIME);
            }
        } catch (InterruptedException e) {
            // state = Thread.State.TERMINATED;
            this.interrupt();
        }
    }

    @Override
    public EValidationResultSeverity getCurrentValidationStatus(String modelName) {
        // return this.currentValidationStatus;
        return this.currentValidationStatus.get(modelName);
    }

    @Override
    public void requestIndividualValidation(String validatorName, IDataModel dataModel) {
        for (Entry<String, IValidator> entry : validatorMap.entrySet()) {
            String key = entry.getKey();
            if (key == validatorName) {
                IValidator validator = entry.getValue();
                validator.startValidation(dataModel, false);

                /* Get the message panel. */
                IValidationMsgPanel msgPanel = getMsgPanel(dataModel);
                if (msgPanel == null) {
                    /*
                     * This should never happen because we should be within a
                     * simulation now. Which means that there must be a current
                     * model (and therefore draw and message panels as well).
                     */
                    String errMsg = "ValidationController, cannot start validation: ";
                    errMsg = errMsg + "no message panel for model '" + dataModel.getModelName() + "'";
                    System.err.println(errMsg);

                    return;
                }

                /*
                 * Pass the results to the message panel, but ignore return
                 * values because "real" validation would be recognized by
                 * "modelChecked = false" in the run() method.
                 */
                getValidatorMessages(true, msgPanel, validator);
            }
        }
    }

    @Override
    public void modelRenamed(String oldModelName, String newModelName) {
        EValidationResultSeverity value = currentValidationStatus.remove(oldModelName);
        currentValidationStatus.put(newModelName, value);
    }

    /* Private methods */

    /**
     * Runs all validations in the {@link Map} of type {@link AbstractValidator}
     * on the specified {@link IDataModel}.
     * 
     * @param dataModel
     *            The specified {@link IDataModel}
     */
    private void runAllValidations(IDataModel dataModel) {
        boolean isInitialModelCheck = dataModel.isInitialModelCheck();

        /*
         * Set "ModelChecked" to true as early as possible: If the state will be
         * set to "unchecked" due to changes to the model, it won't be
         * overwritten at the end.
         */
        dataModel.setModelChecked(true, ALWAYS_REMOVE_INITIAL_CHECK_STATE);
        dataModel.setModelValidity(false);

        /* Assume a valid model. */
        boolean isModelValid = true;

        /* Get the message panel. */
        IValidationMsgPanel msgPanel = getMsgPanel(dataModel);

        if (msgPanel == null) {
            /* Import from PNML file not finished? */
            String errMsg = "ValidationController, cannot start validation: ";
            errMsg = errMsg + "no message panel for model '" + dataModel.getModelName() + "'";
            System.err.println(errMsg);

        } else {
            resetMsgPanelAndValidationStatus(msgPanel);

            /* Run all validators on the current model. */
            for (Entry<String, IValidator> entry : validatorMap.entrySet()) {
                String validatorName = entry.getKey();
                IValidator validator = entry.getValue();

                String debugMessage = "ValidationController.runAllValidations(" + dataModel.getModelName()
                        + "), next validator: " + validatorName;
                ConsoleLogger.logIfDebug(debug, debugMessage);

                validator.startValidation(dataModel, isInitialModelCheck);

                /* Get all messages from the current validator. */
                isModelValid = getValidatorMessages(isModelValid, msgPanel, validator);
                msgPanel.addMessage("");

                /*
                 * Check if validation errors already are severe enough to
                 * cancel further validators?
                 * 
                 * -> No! Never cancel further validators! (e.g. to remove the
                 * initial marking from invalid models!)
                 */
                // if (!isModelValid) {
                // /* Don't change the "checked" state. */
                // break;
                // }

                /* Leave validator loop if user has switched to another file. */
                if (myDataModelController.getCurrentModel() != dataModel) {
                    /* Reset state because we have aborted. */
                    dataModel.setModelChecked(false, ALWAYS_REMOVE_INITIAL_CHECK_STATE);
                    break;
                }
            }

            returnResultToModel(dataModel, isModelValid, msgPanel);
        }
    }

    /**
     * Returns the {@link IValidationMsgPanel} for the specified
     * {@link IDataModel}.
     * 
     * @param dataModel
     *            The specified data model
     * @return The validation messages panel
     */
    private IValidationMsgPanel getMsgPanel(IDataModel dataModel) {
        String modelName = dataModel.getModelName();
        IValidationMsgPanel msgPanel = myDataModelController.getValidationMessagePanel(modelName);
        return msgPanel;
    }

    /**
     * Resets {@link IValidationMsgPanel} and current validation status before
     * the first validator starts.
     * 
     * @param msgPanel
     *            the {@link IValidationMsgPanel} for the current file
     */
    private void resetMsgPanelAndValidationStatus(IValidationMsgPanel msgPanel) {
        msgPanel.reset();
        msgPanel.setBgColor(EValidationColor.PENDING);
        String modelName = msgPanel.getModelName();
        this.currentValidationStatus.put(modelName, EValidationResultSeverity.INFO);
    }

    /**
     * Handles all messages from the specified validator.
     * 
     * @param isModelValid
     *            Current model "validity" state
     * @param msgPanel
     *            The {@link IValidationMsgPanel}
     * @param validator
     *            The {@link IValidator}
     * @return The new "validity" state
     */
    private boolean getValidatorMessages(boolean isModelValid, IValidationMsgPanel msgPanel, IValidator validator) {
        while ((this.getState() != Thread.State.TERMINATED) && (validator.hasMoreMessages())) {
            IValidationMsg message = validator.nextMessage();

            isModelValid = handleMessage(isModelValid, msgPanel, message);

            Thread.yield();

            if (this.getState() == Thread.State.TERMINATED) {
                break;
            }
        }
        return isModelValid;
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

        EValidationResultSeverity currentSeverity = message.getSeverity();
        String modelName = msgPanel.getModelName();
        storeMaxSeverityLevel(modelName, currentSeverity);

        if (currentSeverity == EValidationResultSeverity.WARNING) {
            isModelValid = false; // But continue with more messages and tests
            msgPanel.setBgColor(EValidationColor.PENDING);
        }

        if (currentSeverity == EValidationResultSeverity.CRITICAL) {
            isModelValid = false; // But continue with more messages and tests
            msgPanel.setBgColor(EValidationColor.INVALID);
        }

        return isModelValid;
    }

    /**
     * Stores the current severity level if higher than the previous.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param nextSeverity
     *            the severity level of the last message from the current
     *            validator
     */
    private void storeMaxSeverityLevel(String modelName, EValidationResultSeverity nextSeverity) {
        int currentMaxStatus = this.currentValidationStatus.get(modelName).toInt();
        if (currentMaxStatus < nextSeverity.toInt()) {
            this.currentValidationStatus.put(modelName, nextSeverity);
        }
    }

    /**
     * Returns the result to the model and changes the background of the
     * validation message panel if we have a result.<BR>
     * <BR>
     * But checks if the model was modified in the meantime! (In which case the
     * last validation would already be obsolete.)
     * 
     * @param dataModel
     *            The validated {@link IDataModel}
     * @param isModelValid
     *            The current validity of this data model
     * @param msgPanel
     *            The {@link IValidationMsgPanel} for this data model
     */
    private void returnResultToModel(IDataModel dataModel, boolean isModelValid, IValidationMsgPanel msgPanel) {
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
