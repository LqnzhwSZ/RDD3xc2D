package de.lambeck.pned.models.data;

import java.awt.Point;
import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.application.EStatusMessageLevel;
import de.lambeck.pned.application.ExitCode;
import de.lambeck.pned.elements.data.EPlaceToken;
import de.lambeck.pned.elements.data.IDataArc;
import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.elements.data.IDataNode;
import de.lambeck.pned.filesystem.FSInfo;
import de.lambeck.pned.filesystem.pnml.EPNMLParserExitCode;
import de.lambeck.pned.filesystem.pnml.PNMLParser;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.validation.IValidationMessagesPanel;
import de.lambeck.pned.models.data.validation.IWorkflowNetValidator;
import de.lambeck.pned.models.data.validation.ValidationMessagesPanel;
import de.lambeck.pned.models.data.validation.WorkflowNetValidator;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * Implements a controller for the data models of Petri nets. This means the
 * models that are loaded from or saved to files.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class DataModelController implements IDataModelController {

    private static boolean debug = false;

    protected ApplicationController appController = null;
    protected I18NManager i18n = null;

    /**
     * List of data models identified by their name (full name of the file)
     */
    private Map<String, IDataModel> dataModels = new HashMap<String, IDataModel>();

    /**
     * Current model is the model that corresponds to the active tab of the
     * tabbed pane.
     */
    private IDataModel currentModel = null;

    /**
     * List of validation messages panels identified by their name (full name of
     * the file)
     */
    private Map<String, IValidationMessagesPanel> validationMessagePanels = new HashMap<String, IValidationMessagesPanel>();

    // TODO Wird currentValidationMessagePanel wirklich benötigt? (Nur der
    // validator sollte darauf arbeiten.)
    /**
     * Current validation messages panel is the message panel that corresponds
     * to the active tab of the tabbed pane.
     */
    private IValidationMessagesPanel currentValidationMessagePanel = null;

    /**
     * List of validators identified by their name (full name of the file)
     */
    private Map<String, IWorkflowNetValidator> validators = new HashMap<String, IWorkflowNetValidator>();

    /**
     * Counter for the number of added elements during import ("File open").
     */
    private int elementsAddedToCurrentModel = 0;

    /**
     * Constructs a data model controller with references to the application
     * controller (the parent) and a manager for i18n strings.
     * 
     * @param controller
     *            The application controller
     * @param i18n
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    public DataModelController(ApplicationController controller, I18NManager i18n) {
        this.appController = controller;
        this.i18n = i18n;
    }

    /*
     * Methods for implemented interfaces
     */

    @Override
    public void setInfo_Status(String s, EStatusMessageLevel level) {
        appController.setInfo_Status(s, level);
    }

    @Override
    public void addDataModel(String modelName, String displayName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.addDataModel", modelName, displayName);
        }

        /*
         * Add a data model.
         */
        IDataModel newDataModel = new DataModel(modelName, displayName);
        this.dataModels.put(modelName, newDataModel);

        /*
         * Set as current data model.
         */
        this.currentModel = newDataModel;

        /*
         * Add an associated validation messages panel.
         */
        IValidationMessagesPanel validationMessagesPanel = addValidationMessagePanel(modelName);
        if (validationMessagesPanel == null)
            return;

        /*
         * Add an associated validator.
         */
        addValidator(modelName, newDataModel, validationMessagesPanel);

        if (debug) {
            System.out.println("Data models count: " + dataModels.size());
        }
    }

    @Override
    public int addDataModel(File pnmlFile) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.addDataModel", pnmlFile);
        }

        /*
         * New data model.
         */
        String canonicalPath = FSInfo.getCanonicalPath(pnmlFile);
        if (canonicalPath == null) {
            String errMessage = i18n.getMessage("errFileOpen");
            errMessage = errMessage.replace("%fullName%", canonicalPath);
            System.err.println(errMessage);
            setInfo_Status(errMessage, EStatusMessageLevel.ERROR);
            return ExitCode.OPERATION_FAILED;
        }
        String displayName = pnmlFile.getName();

        /*
         * Add the data model.
         */
        IDataModel newDataModel = new DataModel(canonicalPath, displayName);
        this.dataModels.put(canonicalPath, newDataModel);

        /*
         * Set as current data model.
         * 
         * Note: This data model must be set as current model here because the
         * PNMLParser pushes all found elements into the current data model!
         */
        this.currentModel = newDataModel;

        /*
         * Parse the file.
         */
        PNMLParser pnmlParser = new PNMLParser(pnmlFile, this);
        pnmlParser.initParser();
        this.elementsAddedToCurrentModel = 0;
        int returnValue = pnmlParser.parse();

        /*
         * Check errors during import. (Accept or discard this model?)
         */
        boolean accepted = acceptModel(canonicalPath, returnValue);
        if (!accepted) {
            /*
             * Do nothing more here. The ApplicationController will remove all
             * models in his disposeFile() method.
             */
            return ExitCode.OPERATION_CANCELLED;
        }

        /*
         * File was successfully imported.
         */

        /*
         * Add an associated validation messages panel.
         */
        IValidationMessagesPanel validationMessagesPanel = addValidationMessagePanel(canonicalPath);
        if (validationMessagesPanel == null)
            return ExitCode.OPERATION_FAILED;

        /*
         * Add an associated validator.
         */
        addValidator(canonicalPath, newDataModel, validationMessagesPanel);

        if (debug) {
            System.out.println("Data models count: " + dataModels.size());
        }
        return ExitCode.OPERATION_SUCCESSFUL;
    }

    /**
     * Called by addDataModel to add an associated
     * {@link IValidationMessagesPanel}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     * @return The {@link IValidationMessagesPanel}
     */
    private IValidationMessagesPanel addValidationMessagePanel(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.addValidationMessagesPanel", modelName);
        }

        /*
         * Add the validation messages panel.
         */
        IValidationMessagesPanel newValidationMessagesPanel = new ValidationMessagesPanel(modelName);
        this.validationMessagePanels.put(modelName, newValidationMessagesPanel);

        /*
         * Set as current validation messages panel.
         */
        this.currentValidationMessagePanel = newValidationMessagesPanel;

        if (debug) {
            System.out.println("Validation message panels count: " + validationMessagePanels.size());
        }

        return newValidationMessagesPanel;
    }

    /**
     * Called by addDataModel to add an associated
     * {@link IWorkflowNetValidator}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     * @param dataModel
     *            The data model
     * @param validationMessagesPanel
     *            The validation messages panel for this data model
     */
    private void addValidator(String modelName, IDataModel dataModel,
            IValidationMessagesPanel validationMessagesPanel) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.addValidator", modelName);
        }

        /*
         * Add a validator.
         */
        IWorkflowNetValidator newValidator = new WorkflowNetValidator(modelName, this, dataModel,
                validationMessagesPanel, i18n);
        this.validators.put(modelName, newValidator);

        // /*
        // * Set as current validator.
        // */
        // this.currentValidator = newValidator;

        if (debug) {
            System.out.println("Validators count: " + validators.size());
        }
    }

    /**
     * Returns the result of acceptModel(returnValue) and shows additional (user
     * friendly) info or error messages with the file name.
     * 
     * @param canonicalPath
     *            The canonical path of the file (used as name for the data
     *            model)
     * @param returnValue
     *            The exit code of the parser
     * @return True if the input is OK; otherwise false
     */
    private boolean acceptModel(String canonicalPath, int returnValue) {
        boolean acceptModel = true;
        acceptModel = acceptModel(returnValue);

        if (acceptModel == false) {
            /*
             * Show an error message!
             */
            dataModels.remove(canonicalPath);
            String errorMessage = i18n.getMessage("errDataModelNotAccepted");
            System.err.println(errorMessage);
        } else {
            /*
             * Show info on status bar.
             */
            String addedCount = Integer.toString(this.elementsAddedToCurrentModel);
            String infoMessage = i18n.getMessage("infoElementsLoadedFromPnml");
            infoMessage = infoMessage.replace("%number%", addedCount);
            infoMessage = infoMessage.replace("%file%", canonicalPath);
            setInfo_Status(infoMessage, EStatusMessageLevel.INFO);
        }

        return acceptModel;
    }

    /**
     * Checks which errors have occurred and returns if we can accept this input
     * from the pnml file as {@link DataModel}.
     * 
     * @param returnValue
     *            The exit code of the parser
     * @return True if the input is OK; otherwise false
     */
    private boolean acceptModel(int returnValue) {
        /*
         * Return values from the pnml parser:
         */
        @SuppressWarnings("unused")
        int flagUnknownElement = EPNMLParserExitCode.FLAG_UNKNOWN_ELEMENT.getValue();
        int flagUnknownValues = EPNMLParserExitCode.FLAG_UNKNOWN_VALUES.getValue();
        int flagMissingValues = EPNMLParserExitCode.FLAG_MISSING_VALUES.getValue();
        int flagInvalidValues = EPNMLParserExitCode.FLAG_INVALID_VALUES.getValue();
        int flagErrorReadingFile = EPNMLParserExitCode.FLAG_ERROR_READING_FILE.getValue();

        /*
         * Accept info messages. (FLAG_UNKNOWN_ELEMENT and FLAG_UNKNOWN_VALUES
         * are only info flags.)
         */
        if (returnValue <= flagUnknownValues)
            return true;

        /*
         * Error message depending on the error(s)
         */
        String errorMessage = "";

        boolean flagMissingValuesSet = ((returnValue & flagMissingValues) == flagMissingValues);
        if (flagMissingValuesSet) {
            errorMessage = i18n.getMessage("errMissingValuesInPnml");
            errorMessage = errorMessage.replace("%fullName%", currentModel.getModelName());
            System.err.println(errorMessage);
            setInfo_Status(errorMessage, EStatusMessageLevel.WARNING);
        }

        boolean flagInvalidValuesSet = ((returnValue & flagInvalidValues) == flagInvalidValues);
        if (flagInvalidValuesSet) {
            errorMessage = i18n.getMessage("errInvalidValuesInPnml");
            errorMessage = errorMessage.replace("%fullName%", currentModel.getModelName());
            System.err.println(errorMessage);
            setInfo_Status(errorMessage, EStatusMessageLevel.WARNING);
        }

        boolean flagErrorReadingFileSet = ((returnValue & flagErrorReadingFile) == flagErrorReadingFile);
        if (flagErrorReadingFileSet) {
            errorMessage = i18n.getMessage("errReadingPnmlFile");
            errorMessage = errorMessage.replace("%fullName%", currentModel.getModelName());
            System.err.println(errorMessage);
            setInfo_Status(errorMessage, EStatusMessageLevel.WARNING);
        }

        /*
         * Return false anyways
         */
        return false;
    }

    @Override
    public boolean isModifiedDataModel(String modelName) {
        boolean modified = false;
        for (Entry<String, IDataModel> entry : dataModels.entrySet()) {
            // String key = entry.getKey();
            IDataModel dataModel = entry.getValue();

            if (dataModel.getModelName().equalsIgnoreCase(modelName))
                modified = dataModel.isModified();
        }

        return modified;
    }

    @Override
    public void resetModifiedDataModel(String modelName) {
        currentModel.setModified(false);
    }

    @Override
    public void removeDataModel(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.removeDataModel", modelName);
        }

        /*
         * Reset the "current model" attribute if we remove the current model.
         */
        try {
            if (this.currentModel.getModelName().equalsIgnoreCase(modelName)) {
                this.currentModel = null;
            }
        } catch (NullPointerException ignore) {
            // Nothing to do
        }

        /*
         * Remove the model.
         */
        this.dataModels.remove(modelName);

        /*
         * Remove the associated validation messages panel.
         */
        removeValidationMessagePanel(modelName);

        /*
         * Remove the associated validator.
         */
        removeWorkflowNetValidator(modelName);

        if (debug) {
            System.out.println("Data models count: " + dataModels.size());
        }
    }

    /**
     * Called by removeDataModel() to remove the associated
     * {@link IValidationMessagesPanel}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     */
    private void removeValidationMessagePanel(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.removeValidationMessagePanel", modelName);
        }

        /*
         * Reset the "current validation message panel" attribute if we remove
         * the current validation message panel.
         */
        try {
            if (this.currentValidationMessagePanel.getModelName().equalsIgnoreCase(modelName)) {
                this.currentValidationMessagePanel = null;
            }
        } catch (NullPointerException ignore) {
            // Nothing to do
        }

        /*
         * Remove the validation message panel.
         */
        this.validationMessagePanels.remove(modelName);

        if (debug) {
            System.out.println("Validation message panels count: " + validationMessagePanels.size());
        }
    }

    /**
     * Called by removeDataModel() to remove the associated
     * {@link IWorkflowNetValidator}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     */
    private void removeWorkflowNetValidator(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.removeWorkflowNetValidator", modelName);
        }

        // /*
        // * Reset the "current validation message panel" attribute if we remove
        // * the current validation message panel.
        // */
        // try {
        // if (this.currentValidator.getModelName().equalsIgnoreCase(modelName))
        // {
        // this.currentValidator = null;
        // }
        // } catch (NullPointerException ignore) {
        // // Nothing to do
        // }

        /*
         * Remove the validator.
         */
        this.validators.remove(modelName);

        if (debug) {
            System.out.println("Validators count: " + validators.size());
        }
    }

    @Override
    public void renameDataModel(IDataModel model, String newModelName, String newDisplayName) {
        /*
         * The key for the Map of models.
         */
        String oldKey = model.getModelName();

        /*
         * Get the associated validation message panel;
         */
        IValidationMessagesPanel validationMessagePanel = getValidationMessagePanel(oldKey);

        /*
         * Rename the model and the associated validation message panel.
         */
        IModelRename renameCandidate;

        renameCandidate = (IModelRename) model;
        setModelNames(renameCandidate, newModelName, newDisplayName);

        setValidationMessagePanelNames(validationMessagePanel, newModelName);

        /*
         * Update both Maps!
         */
        IDataModel value1 = dataModels.remove(oldKey);
        dataModels.put(newModelName, value1);

        IValidationMessagesPanel value2 = validationMessagePanels.remove(oldKey);
        validationMessagePanels.put(newModelName, value2);
    }

    /**
     * Uses interface {@link IModelRename} to rename the model.
     * 
     * @param model
     *            The model as {@link IModelRename}
     * @param newModelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     * @param newDisplayName
     *            The title of the tab (= the file name)
     */
    private void setModelNames(IModelRename model, String newModelName, String newDisplayName) {
        model.setModelName(newModelName);
        model.setDisplayName(newDisplayName);
    }

    /**
     * Uses interface {@link IValidationMessagesPanel} to rename the validation
     * message panel.
     * 
     * @param validationMessagePanel
     *            The validation message panel to rename
     * @param newModelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     */
    private void setValidationMessagePanelNames(IValidationMessagesPanel validationMessagePanel, String newModelName) {
        validationMessagePanel.setModelName(newModelName);
    }

    @Override
    public IDataModel getDataModel(String modelName) {
        return this.dataModels.get(modelName);
    }

    @Override
    public IDataModel getCurrentModel() {
        return this.currentModel;
    }

    @Override
    public void setCurrentModel(IDataModel model) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.setCurrentModel", model.getModelName());
        }

        this.currentModel = model;
    }

    @Override
    public IValidationMessagesPanel getValidationMessagePanel(String modelName) {
        for (Entry<String, IValidationMessagesPanel> entry : validationMessagePanels.entrySet()) {
            // String key = entry.getKey();
            IValidationMessagesPanel validationMessagePanel = entry.getValue();

            if (validationMessagePanel.getModelName().equalsIgnoreCase(modelName))
                return validationMessagePanel;
        }

        return null;
    }

    // TODO Is setCurrentValidationMessagesPanel() necessary? (Only the
    // validator should work with it.)

    @Override
    public void setCurrentValidationMessagesPanel(IValidationMessagesPanel validationMessagesPanel) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.setCurrentValidationMessagesPanel",
                    validationMessagesPanel.getModelName());
        }

        this.currentValidationMessagePanel = validationMessagesPanel;

        /*
         * TODO Inform the validation messages panel to reset its state? (like
         * for setCurrentDrawPanel in the GUI controller)
         */
        this.currentValidationMessagePanel.reset();
    }

    @Override
    public IWorkflowNetValidator getWorkflowNetValidator(String modelName) {
        return this.validators.get(modelName);
    }

    @Override
    public List<String> getModifiedDataModels() {
        List<String> modifiedModels = new ArrayList<String>();

        for (Entry<String, IDataModel> entry : dataModels.entrySet()) {
            String key = entry.getKey();
            IDataModel model = entry.getValue();

            if (model.isModified() == true)
                modifiedModels.add(key);
        }

        return modifiedModels;
    }

    /*
     * Methods for adding, modify and removal of elements (and callbacks for
     * updates between data and GUI model controller)
     */

    @Override
    public void addPlaceToCurrentDataModel(String id, EPlaceToken initialTokens, Point position) {
        currentModel.addPlace(id, "", initialTokens, position);
    }

    @Override
    public void addPlaceToCurrentDataModel(String id, String name, EPlaceToken initialTokens, Point position) {
        currentModel.addPlace(id, name, initialTokens, position);
        // if (debug) {
        // System.out.println("Place added to data model " +
        // currentModel.getModelName());
        // }
        this.elementsAddedToCurrentModel++;

        /*
         * Update the GUI
         */
        appController.placeAddedToCurrentDataModel(id, name, initialTokens, position);
    }

    @Override
    public void addTransitionToCurrentDataModel(String id, Point position) {
        currentModel.addTransition(id, "", position);
    }

    @Override
    public void addTransitionToCurrentDataModel(String id, String name, Point position) {
        currentModel.addTransition(id, name, position);
        // if (debug) {
        // System.out.println("Transition added to data model " +
        // currentModel.getModelName());
        // }
        this.elementsAddedToCurrentModel++;

        /*
         * Update the GUI
         */
        appController.transitionAddedToCurrentDataModel(id, name, position);
    }

    @Override
    public void addArcToCurrentDataModel(String id, String sourceId, String targetId) {
        currentModel.addArc(id, sourceId, targetId);
        // if (debug) {
        // System.out.println("Arc added to data model " +
        // currentModel.getModelName());
        // }
        this.elementsAddedToCurrentModel++;

        /*
         * Update the GUI
         */
        appController.arcAddedToCurrentDataModel(id, sourceId, targetId);
    }

    /*
     * Modify methods for elements
     */

    @Override
    public void renameNode(String nodeId, String newName) {
        IDataElement element = currentModel.getElementById(nodeId);
        if (element == null) {
            System.err.println("Not found: data node id=" + nodeId);
            return;
        }

        if (!(element instanceof IDataNode)) {
            String warning = i18n.getMessage("warningUnableToRename");
            String explanation = i18n.getMessage("warningOnlyNodesAllowed");
            String message = warning + " (" + explanation + ")";

            System.out.println(message);
            setInfo_Status(message, EStatusMessageLevel.WARNING);
            return;
        }

        IDataNode node = (IDataNode) element;
        node.setName(newName);
        currentModel.setModified(true);

        /*
         * No further action required since this method should only be called
         * after renaming a node in the GUI.
         */
    }

    /*
     * Remove methods for elements
     */

    @Override
    public void removeDataElement(String elementId) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.removeDataElement", elementId);
        }

        IDataElement element = currentModel.getElementById(elementId);
        if (element == null) {
            System.err.println("Not found: data element id=" + elementId);
            return;
        }

        /*
         * Check if element is an arc an can be removed directly.
         */
        if (element instanceof IDataArc) {
            currentModel.removeElement(elementId);
            currentModel.setModified(true);
            return;
        }

        /*
         * Element is a node and we have to remove all arcs from or towards it.
         */
        IDataNode node = (IDataNode) element;
        List<IDataArc> predElements = node.getPredElems();
        List<IDataArc> succElements = node.getSuccElems();

        /*
         * Remove the node.
         */
        currentModel.removeElement(elementId);
        currentModel.setModified(true);

        /*
         * TODO Nur noch im data model controller!
         * 
         * Remove all adjacent arcs. (Just to make sure; this should have been
         * handled by the GUI controller already.)
         */
        if (predElements.size() > 0) {
            for (IDataArc arc : predElements) {
                String arcId = arc.getId();
                removeDataElement(arcId);

                /*
                 * Inform the controller to update the GUI model!
                 */
                appController.dataArcRemoved(arcId);
            }
        }
        if (succElements.size() > 0) {
            for (IDataArc arc : succElements) {
                String arcId = arc.getId();
                removeDataElement(arcId);

                /*
                 * Inform the controller to update the GUI model!
                 */
                appController.dataArcRemoved(arcId);
            }
        }
    }

    @Override
    public void removeElementFromCurrentDataModel(String id) throws NoSuchElementException {
        currentModel.removeElement(id);
    }

    @Override
    public void clearCurrentDataModel() {
        currentModel.clear();
    }

    /*
     * Mouse events in the GUI
     */

    @Override
    public void moveNode(String nodeId, Point newPosition) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.moveNode", nodeId, newPosition);
        }

        IDataElement element = currentModel.getElementById(nodeId);
        if (element == null) {
            System.err.println("Not found: data node id=" + nodeId);
            return;
        }

        if (!(element instanceof IDataNode)) {
            String warning = i18n.getMessage("warningUnableToMove");
            String explanation = i18n.getMessage("warningOnlyNodesAllowed");
            String message = warning + " (" + explanation + ")";

            System.out.println(message);
            setInfo_Status(message, EStatusMessageLevel.WARNING);
            return;
        }

        IDataNode node = (IDataNode) element;
        node.setPosition(newPosition);
        currentModel.setModified(true);

        /*
         * No further action required since this method should only be called
         * after renaming a node in the GUI.
         */
    }

    /*
     * Validation events
     */

    @Override
    public void startValidation(String modelName) {
        IDataModel model = getDataModel(modelName);
        if (model == null) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.startValidation", modelName);
            System.err.println("DataModelController.startValidation(modelName): specified model == null: " + modelName);
            return;
        }

        IWorkflowNetValidator validator = getWorkflowNetValidator(modelName);
        if (validator == null) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.startValidation", modelName);
            System.err.println(
                    "DataModelController.startValidation(modelName): specified validator == null: " + modelName);
            return;
        }

        validator.startValidation();
    }

    @Override
    public void restartValidation(String modelName) {
        IDataModel model = getDataModel(modelName);
        if (model == null) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.restartValidation", modelName);
            System.err
                    .println("DataModelController.restartValidation(modelName): specified model == null: " + modelName);
            return;
        }

        IWorkflowNetValidator validator = getWorkflowNetValidator(modelName);
        if (validator == null) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.restartValidation", modelName);
            System.err.println(
                    "DataModelController.restartValidation(modelName): specified validator == null: " + modelName);
            return;
        }

        validator.restartValidation();
    }

    @Override
    public void setStartPlace(String modelName, String placeId, boolean b) {
        /*
         * Nothing to do here. Only the GUIPlace needs this information for his
         * paintElement() method.
         */
        appController.setStartPlace(modelName, placeId, b);
    }

    @Override
    public void setEndPlace(String modelName, String placeId, boolean b) {
        /*
         * Nothing to do here. Only the GUIPlace needs this information for his
         * paintElement() method.
         */
        appController.setEndPlace(modelName, placeId, b);
    }

    /*
     * Private helpers
     */

}
