package de.lambeck.pned.models.data;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.application.EStatusMessageLevel;
import de.lambeck.pned.application.ExitCode;
import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.elements.data.*;
import de.lambeck.pned.exceptions.PNElementCreationException;
import de.lambeck.pned.exceptions.PNNoSuchElementException;
import de.lambeck.pned.exceptions.PNObjectNotClonedException;
import de.lambeck.pned.filesystem.FSInfo;
import de.lambeck.pned.filesystem.pnml.EPNMLParserExitCode;
import de.lambeck.pned.filesystem.pnml.PNMLParser;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.validation.IValidationController;
import de.lambeck.pned.models.data.validation.IValidationMsgPanel;
import de.lambeck.pned.models.data.validation.ValidationController;
import de.lambeck.pned.models.data.validation.ValidationMsgPanel;
import de.lambeck.pned.util.ConsoleLogger;
import de.lambeck.pned.util.ObjectCloner;

/**
 * Implements a controller for the data models of Petri nets. This means the
 * models that are loaded from or saved to files.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class DataModelController implements IDataModelController {

    /** Show debug messages? */
    private static boolean debug = false;

    /**
     * Predefined parameter because only the {@link ValidationController} should
     * change the "initial check" state of the {@link IDataModel}.
     */
    private final static boolean NEVER_REMOVE_INITIAL_CHECK_STATE = false;

    /** Reference to the {@link ApplicationController} */
    protected ApplicationController appController = null;

    /** The manager for localized strings */
    protected I18NManager i18n = null;

    /**
     * Map of data models identified by their name (full name of the file)
     */
    private Map<String, IDataModel> dataModels = new HashMap<String, IDataModel>();

    /**
     * Map of (Undo) stacks for old versions of {@link IDataModel} identified by
     * their name (full name of the file)
     */
    private Map<String, IDataModelStack> undoStacks = new HashMap<String, IDataModelStack>();

    /**
     * Map of (Redo) stacks for "new" versions of {@link IDataModel} identified
     * by their name (full name of the file)
     */
    private Map<String, IDataModelStack> redoStacks = new HashMap<String, IDataModelStack>();

    /**
     * Current model is the {@link IDataModel} that corresponds to the active
     * tab (active file) of the applications {@link JTabbedPane}.
     */
    private IDataModel currentModel = null;

    /**
     * List of validation messages panels identified by their name (full name of
     * the file)
     */
    private Map<String, IValidationMsgPanel> validationMessagePanels = new HashMap<String, IValidationMsgPanel>();

    /**
     * Indicates whether we are importing data from a PNML file or not. This is
     * important to avoid infinite loops when adding elements.<BR>
     * <BR>
     * If true: changes to a data model need to be passed to the GUI model.<BR>
     * If false: changes to a GUI model need to be passed to the data model.)
     */
    private boolean importingFromPnml = false;

    /**
     * Counter for the number of added elements during import ("File open").
     */
    private int elementsAddedToCurrentModel = 0;

    /* Constructor */

    /**
     * Constructs a data model controller with references to the application
     * controller (the parent) and a manager for localized strings.
     * 
     * @param controller
     *            The application controller
     * @param i18n
     *            The manager for localized strings
     */
    @SuppressWarnings("hiding")
    public DataModelController(ApplicationController controller, I18NManager i18n) {
        this.appController = controller;
        this.i18n = i18n;

        debug = controller.getShowDebugMessages();
    }

    /* Methods for implemented interfaces */

    @Override
    public void setInfo_Status(String s, EStatusMessageLevel level) {
        appController.setInfo_Status(s, level);
    }

    @Override
    public void addDataModel(String modelName, String displayName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.addDataModel", modelName, displayName);
        }

        /* Create the data model. */
        IDataModel newDataModel = createDataModel(modelName, displayName);

        /* Set as current data model. */
        // this.currentModel = newDataModel;
        setCurrentModel(newDataModel);

        /* Create the Undo and Redo stacks for this model. */
        createUndoAndRedoStack(modelName);

        /* Add an associated validation messages panel. */
        IValidationMsgPanel validationMessagesPanel = addValidationMessagePanel(modelName);
        if (validationMessagesPanel == null)
            return;

        if (debug) {
            System.out.println("Data models count: " + dataModels.size());
        }
    }

    /**
     * Creates a new {@link IDataModel} and returns it.<BR>
     * <BR>
     * Sets the "checked" state to true as soon as possible to prevent the
     * {@link IValidationController} thread from starting the validation before
     * we even have added a {@link IValidationMsgPanel}!
     * 
     * @param modelName
     *            The full path name of the PNML file
     * @param displayName
     *            The title of the tab (= the file name)
     * @return The created {@link IDataModel}
     */
    private IDataModel createDataModel(String modelName, String displayName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.createDataModel", modelName, displayName);
        }

        // IDataModel newDataModel = new DataModel(modelName, displayName,
        // this);
        IDataModel newDataModel = new DataModel(modelName, displayName);
        newDataModel.setModelChecked(true, NEVER_REMOVE_INITIAL_CHECK_STATE);
        this.dataModels.put(modelName, newDataModel);

        return newDataModel;
    }

    /**
     * Creates the Undo and Redo stack for the specified model.
     * 
     * @param modelName
     *            The full path name of the PNML file
     */
    private void createUndoAndRedoStack(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.createUndoAndRedoStack", modelName);
        }

        IDataModelStack undoStack = new DataModelStack();
        this.undoStacks.put(modelName, undoStack);

        IDataModelStack redoStack = new DataModelStack();
        this.redoStacks.put(modelName, redoStack);
    }

    @Override
    public int addDataModel(File pnmlFile) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.addDataModel", pnmlFile);
        }

        /* New data model. */
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
         * Set "importingFromPnml" to true, so that methods like addPlace() do
         * not set the "checked" state of the current model to false before we
         * have finished the import.
         * 
         * (Which would consume unnecessary computing time for validations on an
         * incomplete model and might even produce confusing temporary outputs.)
         */
        this.importingFromPnml = true;

        /* Create the data model. */
        IDataModel newDataModel = createDataModel(canonicalPath, displayName);

        /*
         * Set as current data model.
         * 
         * Note: This data model must be set as current model here because the
         * PNMLParser pushes all found elements into the current data model!
         */
        // this.currentModel = newDataModel;
        setCurrentModel(newDataModel);

        /* Create the Undo and Redo stacks for this model. */
        createUndoAndRedoStack(canonicalPath);

        /* Parse the file. */
        PNMLParser pnmlParser = new PNMLParser(pnmlFile, this);
        pnmlParser.initParser();
        this.elementsAddedToCurrentModel = 0;
        int returnValue = pnmlParser.parse();

        /* Check import errors. (Accept or discard model?) */
        boolean accepted = acceptModel(canonicalPath, returnValue);
        if (!accepted) {
            /*
             * Do nothing more here: The ApplicationController will remove all
             * models in his disposeFile() method.
             */
            return ExitCode.OPERATION_CANCELED;
        }

        /* File import was successful. */

        /* Add an associated validation messages panel. */
        IValidationMsgPanel validationMessagesPanel = addValidationMessagePanel(canonicalPath);
        if (validationMessagesPanel == null)
            return ExitCode.OPERATION_FAILED;

        /*
         * Set "checked" state to false so that the ValidationController will
         * start the first validation in his next cycle.
         */
        newDataModel.setModelChecked(false, NEVER_REMOVE_INITIAL_CHECK_STATE);

        /*
         * Reset "importingFromPnml" as well, so that methods like addPlace() or
         * removeElement() can set the "checked" state of the current model to
         * false if they make structural changes in the model.
         */
        this.importingFromPnml = false;

        if (debug) {
            System.out.println("Data models count: " + dataModels.size());
        }

        return ExitCode.OPERATION_SUCCESSFUL;
    }

    /**
     * Called by addDataModel to add an associated {@link IValidationMsgPanel}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return The {@link IValidationMsgPanel}
     */
    private IValidationMsgPanel addValidationMessagePanel(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.addValidationMessagesPanel", modelName);
        }

        /* Add the validation messages panel. */
        String title = i18n.getNameOnly("ValidatorMessages");
        IValidationMsgPanel newValidationMessagesPanel = new ValidationMsgPanel(modelName, title);
        this.validationMessagePanels.put(modelName, newValidationMessagesPanel);

        if (debug) {
            System.out.println("Validation message panels count: " + validationMessagePanels.size());
        }

        return newValidationMessagesPanel;
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
            /* Show an error message! */
            dataModels.remove(canonicalPath);
            String infoMessage = i18n.getMessage("errDataModelNotAccepted");
            System.out.println(infoMessage);
        } else {
            /* Show info on status bar. */
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
     * from the PNML file as {@link DataModel}.
     * 
     * @param returnValue
     *            The exit code of the parser
     * @return True if the input is OK; otherwise false
     */
    private boolean acceptModel(int returnValue) {
        /* Return values from the PNML parser: */
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

        /* Error message depending on the error(s) */
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
            System.out.println(errorMessage);
            setInfo_Status(errorMessage, EStatusMessageLevel.WARNING);
        }

        boolean flagErrorReadingFileSet = ((returnValue & flagErrorReadingFile) == flagErrorReadingFile);
        if (flagErrorReadingFileSet) {
            errorMessage = i18n.getMessage("errReadingPnmlFile");
            errorMessage = errorMessage.replace("%fullName%", currentModel.getModelName());
            System.err.println(errorMessage);
            setInfo_Status(errorMessage, EStatusMessageLevel.WARNING);
        }

        /* Return false anyways. */
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
        currentModel.setModified(false, false);
    }

    @Override
    public void removeDataModel(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.removeDataModel", modelName);
        }

        /* Reset "current model" attribute if we remove the current model. */
        if (this.currentModel != null && this.currentModel.getModelName().equalsIgnoreCase(modelName)) {
            this.currentModel = null;
        }

        /* Remove the model from the Maps. */
        removeModelFromModelNameDependentMaps(modelName);

        /* Remove the associated validation messages panel. */
        removeValidationMessagePanel(modelName);

        if (debug) {
            System.out.println("Data models count: " + dataModels.size());
        }
    }

    /**
     * Called by removeDataModel() to remove the associated
     * {@link IValidationMsgPanel}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    private void removeValidationMessagePanel(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.removeValidationMessagePanel", modelName);
        }

        /* Remove the validation message panel. */
        this.validationMessagePanels.remove(modelName);

        if (debug) {
            System.out.println("Validation message panels count: " + validationMessagePanels.size());
        }
    }

    @Override
    public void renameDataModel(IDataModel model, String newModelName, String newDisplayName) {
        String oldModelName = model.getModelName(); // For the message panel
        IModelRename renameCandidate;

        /* Rename the model and the associated validation message panel. */
        renameCandidate = (IModelRename) model;
        setModelNames(renameCandidate, newModelName, newDisplayName);

        IValidationMsgPanel validationMessagePanel = getValidationMessagePanel(oldModelName);
        setValidationMessagePanelNames(validationMessagePanel, newModelName);

        /* Update all Maps where the model name is the key! */
        // updateKeyInModelNameDependentMaps(model, newModelName);
        updateKeyInModelNameDependentMaps(oldModelName, newModelName);
    }

    /**
     * Uses interface {@link IModelRename} to rename the model.
     * 
     * @param model
     *            The model as {@link IModelRename}
     * @param newModelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param newDisplayName
     *            The title of the tab (= the file name)
     */
    private void setModelNames(IModelRename model, String newModelName, String newDisplayName) {
        model.setModelName(newModelName);
        model.setDisplayName(newDisplayName);
    }

    /**
     * Uses interface {@link IValidationMsgPanel} to rename the validation
     * message panel.
     * 
     * @param validationMessagePanel
     *            The validation message panel to rename
     * @param newModelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    private void setValidationMessagePanelNames(IValidationMsgPanel validationMessagePanel, String newModelName) {
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

        /* Activate the new model */
        this.currentModel = model;

        /*
         * Update the Maps for the models.
         * 
         * This is necessary for Undo or Redo operations. In this case, the
         * model will change but the model name is the same. This means that we
         * have to make sure that the object referenced in the Maps is the
         * correct model!
         */
        String key = model.getModelName();
        updateModelInModelNameDependentMaps(key, model);
    }

    @Override
    public IValidationMsgPanel getValidationMessagePanel(String modelName) {
        for (Entry<String, IValidationMsgPanel> entry : validationMessagePanels.entrySet()) {
            // String key = entry.getKey();
            IValidationMsgPanel validationMessagePanel = entry.getValue();
            String messagePanelName = validationMessagePanel.getModelName();
            if (messagePanelName.equalsIgnoreCase(modelName))
                return validationMessagePanel;
        }

        return null;
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

    @Override
    public JFrame getMainFrame() {
        return appController.getMainFrame();
    }

    /* Map updates */

    // /**
    // * Replaces the entry for the specified {@link IDataModel} with a new
    // entry
    // * associated with the specified new key in all affected Maps.
    // *
    // * @param model
    // * the specified {@link IDataModel}
    // * @param newKey
    // * the specified new key
    // */
    // private void updateKeyInModelNameDependentMaps(IDataModel model, String
    // newKey) {
    // String oldKey = model.getModelName();
    //
    // IDataModel value1 = dataModels.remove(oldKey);
    // dataModels.put(newKey, value1);
    //
    // IValidationMsgPanel value2 = validationMessagePanels.remove(oldKey);
    // validationMessagePanels.put(newKey, value2);
    //
    // IDataModelStack value3 = undoStacks.remove(oldKey);
    // undoStacks.put(newKey, value3);
    //
    // IDataModelStack value4 = redoStacks.remove(oldKey);
    // redoStacks.put(newKey, value4);
    // }

    /**
     * Replaces the old key with the new key in every {@link Map} that depends
     * on the name of an {@link IDataModel}.<BR>
     * <BR>
     * This is realized by removing the entry associated with that old key and
     * re-inserting that entry with the new key.
     * 
     * @param oldKey
     *            the specified old key representing the name of the model
     * @param newKey
     *            the specified new key representing the name of the model
     */
    private void updateKeyInModelNameDependentMaps(String oldKey, String newKey) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.updateKeyInModelNameDependentMaps", oldKey, newKey);
        }

        if (isDuplicateKeyInModelNameDependentMaps(newKey))
            return;

        /* Replace the keys */
        IDataModel value1 = dataModels.remove(oldKey);
        dataModels.put(newKey, value1);

        IValidationMsgPanel value2 = validationMessagePanels.remove(oldKey);
        validationMessagePanels.put(newKey, value2);

        IDataModelStack value3 = undoStacks.remove(oldKey);
        undoStacks.put(newKey, value3);

        IDataModelStack value4 = redoStacks.remove(oldKey);
        redoStacks.put(newKey, value4);
    }

    /**
     * Checks whether the specified key already exists in a {@link Map} that
     * depends on the name of an {@link IDataModel}.
     * 
     * @param newKey
     *            the specified key representing the name of a model
     * @return true = newKey is a duplicate in at least 1 Map; false = newKey is
     *         no duplicate in any of the Maps
     */
    private boolean isDuplicateKeyInModelNameDependentMaps(String newKey) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.isDuplicateKeyInModelNameDependentMaps", newKey);
        }

        IDataModel value1 = dataModels.get(newKey);
        if (value1 != null) {
            String message = "-> Duplicate entry: " + newKey + " in Map 'dataModels'";
            System.err.println(message);
            return true;
        }

        IValidationMsgPanel value2 = validationMessagePanels.remove(newKey);
        if (value2 != null) {
            String message = "-> Duplicate entry: " + newKey + " in Map 'validationMessagePanels'";
            System.err.println(message);
            return true;
        }

        IDataModelStack value3 = undoStacks.remove(newKey);
        if (value3 != null) {
            String message = "-> Duplicate entry: " + newKey + " in Map 'undoStacks'";
            System.err.println(message);
            return true;
        }

        IDataModelStack value4 = redoStacks.remove(newKey);
        if (value4 != null) {
            String message = "-> Duplicate entry: " + newKey + " in Map 'redoStacks'";
            System.err.println(message);
            return true;
        }

        return false;
    }

    /**
     * Replaces the value ({@link IDataModel}) that is associated with the
     * specified key with a new {@link IDataModel} in all affected Maps.
     * 
     * @param key
     *            the specified key
     * @param newModel
     *            the specified new {@link IDataModel}
     */
    private void updateModelInModelNameDependentMaps(String key, IDataModel newModel) {
        dataModels.put(key, newModel);
    }

    /**
     * Removes the specified {@link IDataModel} from all Maps.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    private void removeModelFromModelNameDependentMaps(String modelName) {
        this.dataModels.remove(modelName);

        /* Remove Undo and Redo stack for this model. */
        this.undoStacks.remove(modelName);
        this.redoStacks.remove(modelName);
    }

    /*
     * Methods for adding, modify and removal of elements (and callbacks for
     * updates between data and GUI model controller)
     */

    /* Add elements */

    @Override
    public void addPlaceToCurrentDataModel(String id, EPlaceToken initialTokens, Point position) {
        currentModel.addPlace(id, "", initialTokens, position);

        if (!this.importingFromPnml)
            currentModel.setModified(true, true);
    }

    @Override
    public void addPlaceToCurrentDataModel(String id, String name, EPlaceToken initialTokens, Point position) {
        currentModel.addPlace(id, name, initialTokens, position);
        this.elementsAddedToCurrentModel++;

        // TODO The following command should be obsolete for nodes.
        if (!this.importingFromPnml)
            currentModel.setModified(true, true);

        /* Update the GUI if the place does not come from a GUI event. */
        if (this.importingFromPnml)
            appController.placeAddedToCurrentDataModel(id, name, initialTokens, position);
    }

    @Override
    public void addTransitionToCurrentDataModel(String id, Point position) {
        currentModel.addTransition(id, "", position);

        if (!this.importingFromPnml)
            currentModel.setModified(true, true);
    }

    @Override
    public void addTransitionToCurrentDataModel(String id, String name, Point position) {
        currentModel.addTransition(id, name, position);
        this.elementsAddedToCurrentModel++;

        // TODO The following command should be obsolete for nodes.
        if (!this.importingFromPnml)
            currentModel.setModified(true, true);

        /* Update the GUI if the transition does not come from a GUI event. */
        if (this.importingFromPnml)
            appController.transitionAddedToCurrentDataModel(id, name, position);
    }

    @Override
    public void addArcToCurrentDataModel(String id, String sourceId, String targetId) {
        try {
            currentModel.addArc(id, sourceId, targetId);
        } catch (PNElementCreationException e) {
            System.err.println(e.getMessage());
            return;
        }

        this.elementsAddedToCurrentModel++;

        if (!this.importingFromPnml)
            currentModel.setModified(true, true);

        /* Update the GUI if the arc does not come from a GUI event. */
        if (this.importingFromPnml)
            appController.arcAddedToCurrentDataModel(id, sourceId, targetId);
    }

    /* Modify methods for elements */

    @Override
    public void renameNode(String nodeId, String newName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.renameNode");
        }

        IDataNode node;
        try {
            node = currentModel.getNodeById(nodeId);
        } catch (PNNoSuchElementException e) {
            // System.err.println("Not found: data node id=" + nodeId);

            String warning = i18n.getMessage("warningUnableToRename");
            String explanation = i18n.getMessage("warningOnlyNodesAllowed");
            String message = warning + " (" + explanation + ")";

            System.err.println(message);
            setInfo_Status(message, EStatusMessageLevel.WARNING);
            return;
        }

        node.setName(newName);
        currentModel.setModified(true, false);

        /*
         * No further action required since this method should only be called
         * after renaming a node in the GUI.
         */
    }

    /* Remove methods for elements */

    @Override
    public void removeDataElement(String elementId) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.removeDataElement", elementId);
        }

        IDataElement element;
        try {
            element = currentModel.getElementById(elementId);
        } catch (PNNoSuchElementException e) {
            System.err.println("Not found: data element id=" + elementId);
            return;
        }

        /* Check if element is an arc an can be removed directly. */
        if (element instanceof IDataArc) {
            try {
                currentModel.removeElement(elementId);
            } catch (PNNoSuchElementException e) {
                System.err.println(e.getMessage());
                return;
            }
            currentModel.setModified(true, true);
            return;
        }

        /*
         * Element is a node and we have to remove all arcs from or towards it.
         */
        IDataNode node = (IDataNode) element;
        List<IDataArc> predElements = node.getPredElems();
        List<IDataArc> succElements = node.getSuccElems();

        /* Remove the node. */
        try {
            currentModel.removeElement(elementId);
        } catch (PNNoSuchElementException e) {
            System.err.println(e.getMessage());
            return;
        }
        currentModel.setModified(true, true);

        /* Remove all adjacent arcs. */
        if (predElements.size() > 0) {
            for (IDataArc arc : predElements) {
                String arcId = arc.getId();
                removeDataElement(arcId);

                /* Inform the controller to update the GUI model! */
                appController.dataArcRemoved(arcId);
            }
        }
        if (succElements.size() > 0) {
            for (IDataArc arc : succElements) {
                String arcId = arc.getId();
                removeDataElement(arcId);

                /* Inform the controller to update the GUI model! */
                appController.dataArcRemoved(arcId);
            }
        }
    }

    // @Override
    // public void removeElementFromCurrentDataModel(String id) throws
    // PNNoSuchElementException {
    // currentModel.removeElement(id);
    // currentModel.setModified(true, true);
    // }

    // @Override
    // public void clearCurrentDataModel() {
    // currentModel.clear();
    // currentModel.setModified(true, true);
    // }

    /* Mouse events in the GUI */

    @Override
    public void moveNode(String nodeId, Point newPosition) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.moveNode", nodeId, newPosition);
        }

        IDataElement element;
        try {
            element = currentModel.getElementById(nodeId);
        } catch (PNNoSuchElementException e) {
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
        currentModel.setModified(true, false);

        /*
         * No further action required since this method should only be called
         * after renaming a node in the GUI.
         */
    }

    /* Validation events */

    @Override
    public void resetAllDataStartPlaces(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.resetAllDataStartPlaces", modelName);
        }

        /*
         * Nothing to do here. Only the GUIPlaces need this information for
         * their paintElement() method.
         */
        appController.resetAllGuiStartPlaces(modelName);
    }

    @Override
    public void resetAllDataEndPlaces(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.resetAllDataEndPlaces", modelName);
        }

        /*
         * Nothing to do here. Only the GUIPlaces need this information for
         * their paintElement() method.
         */
        appController.resetAllGuiEndPlaces(modelName);
    }

    @Override
    public void setDataStartPlace(String modelName, String placeId, boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.setDataStartPlace", modelName, placeId, b);
        }

        /*
         * Nothing to do here. Only the GUIPlace needs this information for his
         * paintElement() method.
         */
        appController.setGuiStartPlace(modelName, placeId, b);
    }

    @Override
    public void setDataStartPlaceCandidate(String modelName, String placeId, boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.setDataStartPlaceCandidate", modelName, placeId, b);
        }

        /*
         * Nothing to do here. Only the GUIPlace needs this information for his
         * paintElement() method.
         */
        appController.setGuiStartPlaceCandidate(modelName, placeId, b);
    }

    @Override
    public void setDataEndPlace(String modelName, String placeId, boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.setDataEndPlace", modelName, placeId, b);
        }

        /*
         * Nothing to do here. Only the GUIPlace needs this information for his
         * paintElement() method.
         */
        appController.setGuiEndPlace(modelName, placeId, b);
    }

    @Override
    public void setDataEndPlaceCandidate(String modelName, String placeId, boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.setDataEndPlaceCandidate", modelName, placeId, b);
        }

        /*
         * Nothing to do here. Only the GUIPlace needs this information for his
         * paintElement() method.
         */
        appController.setGuiEndPlaceCandidate(modelName, placeId, b);
    }

    @Override
    public void highlightUnreachableDataNode(String modelName, String nodeId, boolean b) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.highlightUnreachableDataNode", modelName, nodeId,
                    b);
        }

        /*
         * Nothing to do here. Only the GUINode needs this information for his
         * paintElement() method.
         */
        appController.highlightUnreachableGuiNode(modelName, nodeId, b);
    }

    @Override
    public void removeAllDataTokens(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.removeAllDataTokens", modelName);
        }

        IDataModel dataModel = getDataModelForValidation(modelName);
        if (dataModel == null)
            return;

        /*
         * Remove the token from all data places and pass the info to the GUI
         * model controller.
         */
        for (IDataElement dataElement : dataModel.getElements()) {
            if (dataElement instanceof DataPlace) {
                DataPlace dataPlace = (DataPlace) dataElement;
                dataPlace.setTokens(EPlaceToken.ZERO);
            }
        }

        appController.removeAllGuiTokens(modelName);
    }

    @Override
    public void addDataToken(String modelName, List<String> placesWithToken) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.addDataToken", modelName, placesWithToken);
        }

        IDataModel dataModel = getDataModelForValidation(modelName);
        if (dataModel == null)
            return;

        /*
         * Add a token to all specified data places and pass the info to the GUI
         * model controller.
         */
        for (IDataElement dataElement : dataModel.getElements()) {
            if (dataElement instanceof DataPlace) {
                DataPlace dataPlace = (DataPlace) dataElement;
                String dataPlaceId = dataPlace.getId();
                if (placesWithToken.contains(dataPlaceId)) {
                    dataPlace.setTokens(EPlaceToken.ONE);
                }
            }
        }

        appController.addGuiToken(modelName, placesWithToken);
    }

    @Override
    public void resetAllDataTransitionsEnabledState(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.resetAllDataTransitionsEnabledState", modelName);
        }

        IDataModel dataModel = getDataModelForValidation(modelName);
        if (dataModel == null)
            return;

        /* Reset the "enabled" state of all transitions. */
        for (IDataElement dataElement : dataModel.getElements()) {
            if (dataElement instanceof IDataTransition) {
                IDataTransition dataTransition = (IDataTransition) dataElement;
                dataTransition.resetEnabled();
            }
        }

        /* Pass the info to the GUI model controller. */
        appController.resetAllGuiTransitionsEnabledState(modelName);
        appController.resetAllGuiTransitionsSafeState(modelName);
    }

    /**
     * Returns the specified {@link IDataModel} with suppressed error messages
     * if not found because this error can be expected in rare cases.<BR>
     * <BR>
     * This method is part of the validation process. And the validation
     * controller thread might slightly lagging behind in terms of the current
     * model (e.g. if the user has suddenly closed the current file during
     * validation).
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return The specified {@link IDataModel} if found; otherwise null
     */
    private IDataModel getDataModelForValidation(String modelName) {
        IDataModel dataModel = this.dataModels.get(modelName);
        if (dataModel == null) {
            String message = i18n.getMessage("errDataModelNotFound");
            // System.err.println(message);

            /* -> The expected error */
            ConsoleLogger.logIfDebug(debug, message);
            return null;
        }

        return dataModel;
    }

    @Override
    public void setGuiTransitionUnsafe(String modelName, String transitionId) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.setGuiTransitionUnsafe", modelName, transitionId);
        }

        /*
         * Nothing to do here. Only the IGuiTransition needs this information.
         */
        appController.setGuiTransitionUnsafe(modelName, transitionId);
    }

    @Override
    public void setGuiTransitionEnabled(String modelName, String transitionId) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.setGuiTransitionEnabled", modelName, transitionId);
        }

        /*
         * Nothing to do here. Only the IGuiTransition needs this information.
         */
        appController.setGuiTransitionEnabled(modelName, transitionId);
    }

    @Override
    public void fireDataTransition(String transitionId) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.fireDataTransition", transitionId);
        }

        IDataElement element;
        try {
            element = currentModel.getElementById(transitionId);
        } catch (PNNoSuchElementException e) {
            System.err.println("Not found: data transition id=" + transitionId);
            return;
        }

        if (!(element instanceof IDataTransition)) {
            String warning = i18n.getMessage("warningNoTransition");
            // String explanation = i18n.getMessage("warningOnlyNodesAllowed");
            String message = warning; // + " (" + explanation + ")";

            System.out.println(message);
            setInfo_Status(message, EStatusMessageLevel.WARNING);
            return;
        }

        IDataTransition transition = (IDataTransition) element;
        removeTokenFromAllInputPlaces(transition);
        addTokenToAllOutputPlaces(transition);

        /* Inform the application controller */
        appController.dataTransitionFired(currentModel);
    }

    /**
     * Removes the token from all input places ({@link DataPlace}) for the
     * specified {@link IDataTransition}.
     * 
     * @param transition
     *            The specified transition
     */
    private void removeTokenFromAllInputPlaces(IDataTransition transition) {
        List<DataPlace> placesToRemoveToken = transition.getPredPlaces();
        int tokensRemoved = 0;
        List<String> placesWithRemovedToken = new ArrayList<String>();

        for (DataPlace dataPlace : placesToRemoveToken) {
            removeToken(dataPlace, tokensRemoved, placesWithRemovedToken);
        }
        ConsoleLogger.logIfDebug(debug, tokensRemoved + " tokens removed.");

        /* Update the GUI. */
        appController.removeGuiToken(currentModel.getModelName(), placesWithRemovedToken);
    }

    /**
     * Removes the token from the specified {@link IDataTransition}.
     * 
     * @param dataPlace
     *            The {@link DataPlace} to remove the token from
     * @param tokensRemoved
     *            Counter for removed tokens
     * @param placesWithRemovedToken
     *            List with the ID of all processed places
     */
    private void removeToken(DataPlace dataPlace, int tokensRemoved, List<String> placesWithRemovedToken) {
        if (!(dataPlace.getTokensCount() == EPlaceToken.ONE)) {
            String errMsg = dataPlace.getId() + " has no token to remove!";
            System.err.println(errMsg);
            return;
        }

        dataPlace.setTokens(EPlaceToken.ZERO);
        tokensRemoved++;

        String placeId = dataPlace.getId();
        placesWithRemovedToken.add(placeId);

        ConsoleLogger.logIfDebug(debug, "Token removed from: " + placeId);
    }

    /**
     * Adds a token to all output places ({@link DataPlace}) for the specified
     * {@link IDataTransition}.
     * 
     * @param transition
     *            The specified transition
     */
    private void addTokenToAllOutputPlaces(IDataTransition transition) {
        List<DataPlace> placesToAddToken = transition.getSuccPlaces();
        int tokensAdded = 0;
        List<String> placesWithAddedToken = new ArrayList<String>();

        for (DataPlace dataPlace : placesToAddToken) {
            addToken(dataPlace, tokensAdded, placesWithAddedToken);
        }
        ConsoleLogger.logIfDebug(debug, tokensAdded + " tokens added.");

        /* Update the GUI. */
        appController.addGuiToken(currentModel.getModelName(), placesWithAddedToken);
    }

    /**
     * Adds a token to the specified {@link IDataTransition}.
     * 
     * @param dataPlace
     *            The {@link DataPlace} to add the token to
     * @param tokensAdded
     *            Counter for added tokens
     * @param placesWithAddedToken
     *            List with the ID of all processed places
     */
    private void addToken(DataPlace dataPlace, int tokensAdded, List<String> placesWithAddedToken) {
        if (!(dataPlace.getTokensCount() == EPlaceToken.ZERO)) {
            String errMsg = dataPlace.getId() + " already has a token!";
            System.err.println(errMsg);
            return;
        }

        dataPlace.setTokens(EPlaceToken.ONE);
        tokensAdded++;

        String placeId = dataPlace.getId();
        placesWithAddedToken.add(placeId);

        ConsoleLogger.logIfDebug(debug, "Token added to: " + placeId);
    }

    @Override
    public void stopSimulation() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.stopSimulation");
        }

        if (currentModel == null)
            return;

        currentModel.setModelChecked(false, NEVER_REMOVE_INITIAL_CHECK_STATE);
    }

    /* Undo + Redo */

    @Override
    public boolean canUndo() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.canUndo");
        }

        IDataModelStack currentUndoStack = getCurrentModelUndoStack();
        if (currentUndoStack == null)
            return false;

        boolean canBeUndone = !currentUndoStack.empty();
        return canBeUndone;
    }

    /**
     * Returns the Undo stack ({@link IDataModelStack}) for the current
     * {@link IDataModel}.
     * 
     * @return the Undo stack as {@link IDataModelStack}; or null if current
     *         model is null or the stack does not exist.
     */
    private IDataModelStack getCurrentModelUndoStack() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.getCurrentModelUndoStack");
        }

        if (currentModel == null)
            return null;

        String fullName = currentModel.getModelName();
        IDataModelStack undoStack = null;
        try {
            undoStack = undoStacks.get(fullName);
        } catch (ClassCastException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }

        return undoStack;
    }

    @Override
    public boolean canRedo() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.canRedo");
        }

        IDataModelStack currentRedoStack = getCurrentModelRedoStack();
        if (currentRedoStack == null)
            return false;

        boolean canBeRedone = !currentRedoStack.empty();
        return canBeRedone;
    }

    /**
     * Returns the Redo stack ({@link IDataModelStack}) for the current
     * {@link IDataModel}.
     * 
     * @return the Redo stack as {@link IDataModelStack}; or null if current
     *         model is null or the stack does not exist.
     */
    private IDataModelStack getCurrentModelRedoStack() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.getCurrentModelRedoStack");
        }

        if (currentModel == null)
            return null;

        String fullName = currentModel.getModelName();
        IDataModelStack redoStack = null;
        try {
            redoStack = redoStacks.get(fullName);
        } catch (ClassCastException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }

        return redoStack;
    }

    @Override
    public int makeUndoable() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.makeUndoable");
        }

        if (currentModel == null)
            return 1;

        // UndoRedoElementBuffer elementBuffer = createUndoRedoElementBuffer();
        // if (elementBuffer == null)
        // return 2;
        IDataModel copy = cloneCurrentModel();
        if (copy == null)
            return 2;

        IDataModelStack undoStack = getCurrentModelUndoStack();
        if (undoStack == null)
            return 3;

        // undoStack.push(elementBuffer);
        undoStack.push(copy);

        return 0;
    }

    /**
     * Clones the current {@link IDataModel}.
     *
     * @return a copy of the current {@link IDataModel}; or null if errors
     *         occurred.
     */
    private IDataModel cloneCurrentModel() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.cloneCurrentModel");
        }

        // IDataModel old = this.currentModel;
        // if (old == null) {
        // System.err.println("Unable to determine the current data model!");
        // return null;
        // }

        IDataModel copy = null;
        try {
            copy = (IDataModel) ObjectCloner.deepCopy(this.currentModel);
        } catch (PNObjectNotClonedException e) {
            String message = "Unable to clone the current data model!";
            System.err.println(message);
            return null;
        }
        return copy;
    }

    @Override
    public int makeRedoable() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.makeRedoable");
        }

        if (currentModel == null)
            return 1;

        // UndoRedoElementBuffer elementBuffer = createUndoRedoElementBuffer();
        // if (elementBuffer == null)
        // return 2;
        IDataModel copy = cloneCurrentModel();
        if (copy == null)
            return 2;

        IDataModelStack redoStack = getCurrentModelRedoStack();
        if (redoStack == null)
            return 3;

        // redoStack.push(elementBuffer);
        redoStack.push(copy);

        return 0;
    }

    // /**
    // * Creates an {@link UndoRedoElementBuffer} with all {@link IDataElement}
    // of
    // * the current {@link IDataModel}.
    // *
    // * @return {@link UndoRedoElementBuffer}; null if there is no current
    // * {@link IDataModel}
    // */
    // private UndoRedoElementBuffer createUndoRedoElementBuffer() {
    // if (debug) {
    // ConsoleLogger.consoleLogMethodCall("DataModelController.createUndoRedoElementBuffer");
    // }
    //
    // if (currentModel == null)
    // return null;
    //
    // UndoRedoElementBuffer elementBuffer = new UndoRedoElementBuffer();
    //
    // List<IDataElement> newElements = currentModel.getElements();
    // elementBuffer.setElements(newElements);
    //
    // return elementBuffer;
    // }

    @Override
    public void clearRedoStack() {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.clearRedoStack");
        }

        if (currentModel == null)
            return;

        IDataModelStack redoStack = getCurrentModelRedoStack();
        if (redoStack == null)
            return;

        redoStack.clear();
    }

    @Override
    public void Undo() throws CannotUndoException {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.Undo");
        }

        // UndoRedoElementBuffer last = null;
        IDataModel last = null;
        try {
            // last = getUndoElementBuffer();
            last = getUndoModel();
        } catch (CannotUndoException e) {
            throw e;
        }

        makeRedoable();

        /* This is the actual Undo operation. */
        // List<IDataElement> newElements = last.getElements();
        // this.currentModel.setElements(newElements);

        // this.currentModel = last;
        setCurrentModel(last);
    }

    // /**
    // * Returns the last Undo buffer with the {@link List} of
    // * {@link IDataElement} for the current {@link IDataModel}.
    // *
    // * @return An {@link UndoRedoElementBuffer} from the Undo stack
    // * @throws CannotUndoException
    // * if currentModel or undoStack == null or undoStack.empty()
    // */
    // private UndoRedoElementBuffer getUndoElementBuffer() throws
    // CannotUndoException {
    // /* Checks */
    //
    // if (currentModel == null)
    // throw new CannotUndoException();
    //
    // IDataModelStack undoStack = getCurrentModelUndoStack();
    // if (undoStack == null)
    // throw new CannotUndoException();
    //
    // /* Avoid EmptyStackException in undoStack.pop() */
    // if (undoStack.empty())
    // throw new CannotUndoException();
    //
    // /* Undo operation is possible. */
    //
    // UndoRedoElementBuffer last = undoStack.pop();
    // return last;
    // }

    /**
     * Returns the last copy of the current {@link IDataModel}.
     * 
     * @return An {@link IDataModel} from the Undo stack
     * @throws CannotUndoException
     *             if currentModel or undoStack == null or undoStack.empty()
     */
    private IDataModel getUndoModel() throws CannotUndoException {
        /* Checks */

        if (currentModel == null)
            throw new CannotUndoException();

        IDataModelStack undoStack = getCurrentModelUndoStack();
        if (undoStack == null)
            throw new CannotUndoException();

        /* Avoid EmptyStackException in undoStack.pop() */
        if (undoStack.empty())
            throw new CannotUndoException();

        /* Undo operation is possible. */

        IDataModel last = undoStack.pop();
        return last;
    }

    @Override
    public void Redo() throws CannotRedoException {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.Redo");
        }

        // UndoRedoElementBuffer next = null;
        IDataModel next = null;
        try {
            // next = getRedoElementBuffer();
            next = getRedoModel();
        } catch (CannotRedoException e) {
            throw e;
        }

        makeUndoable();

        /* This is the actual Redo operation. */
        // List<IDataElement> newElements = next.getElements();
        // this.currentModel.setElements(newElements);

        // this.currentModel = next;
        setCurrentModel(next);
    }

    // /**
    // * Returns the next Redo buffer with the {@link List} of
    // * {@link IDataElement} for the current {@link IDataModel}.
    // *
    // * @return An {@link UndoRedoElementBuffer} from the Redo stack
    // * @throws CannotRedoException
    // * if currentModel or redoStack == null or redoStack.empty()
    // */
    // private UndoRedoElementBuffer getRedoElementBuffer() throws
    // CannotRedoException {
    // /* Checks */
    //
    // if (currentModel == null)
    // throw new CannotRedoException();
    //
    // IDataModelStack redoStack = getCurrentModelRedoStack();
    // if (redoStack == null)
    // throw new CannotRedoException();
    //
    // /* Avoid EmptyStackException in redoStack.pop() */
    // if (redoStack.empty())
    // throw new CannotRedoException();
    //
    // /* Redo operation is possible. */
    //
    // UndoRedoElementBuffer succ = redoStack.pop();
    // return succ;
    // }

    /**
     * Returns the next copy of the current {@link IDataModel}.
     * 
     * @return An {@link IDataModel} from the Redo stack
     * @throws CannotRedoException
     *             if currentModel or redoStack == null or redoStack.empty()
     */
    private IDataModel getRedoModel() throws CannotRedoException {
        /* Checks */

        if (currentModel == null)
            throw new CannotRedoException();

        IDataModelStack redoStack = getCurrentModelRedoStack();
        if (redoStack == null)
            throw new CannotRedoException();

        /* Avoid EmptyStackException in redoStack.pop() */
        if (redoStack.empty())
            throw new CannotRedoException();

        /* Redo operation is possible. */

        IDataModel next = redoStack.pop();
        return next;
    }

    @Override
    public void undoOrRedoFinished() {
        /*
         * Revalidate this model because validation results are not included in
         * Undo or Redo operations.
         */
        this.currentModel.setModelChecked(false, NEVER_REMOVE_INITIAL_CHECK_STATE);
    }

}
