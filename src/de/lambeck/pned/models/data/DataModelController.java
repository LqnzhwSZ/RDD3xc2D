package de.lambeck.pned.models.data;

import java.awt.Point;
import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.application.EStatusMessageLevel;
import de.lambeck.pned.application.ExitCode;
import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.elements.data.*;
import de.lambeck.pned.filesystem.FSInfo;
import de.lambeck.pned.filesystem.pnml.EPNMLParserExitCode;
import de.lambeck.pned.filesystem.pnml.PNMLParser;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.validation.IValidationMsgPanel;
import de.lambeck.pned.models.data.validation.ValidationController;
import de.lambeck.pned.models.data.validation.ValidationMsgPanel;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * Implements a controller for the data models of Petri nets. This means the
 * models that are loaded from or saved to files.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class DataModelController implements IDataModelController {

    private static boolean debug = true;

    /**
     * Predefined parameter because only the {@link ValidationController} should
     * change the "initial check" state of the {@link IDataModel}.
     */
    private final static boolean NEVER_REMOVE_INITIAL_CHECK_STATE = false;

    /** Reference to the {@link ApplicationController} */
    protected ApplicationController appController = null;

    /** Reference to the manager for I18N strings */
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
    private Map<String, IValidationMsgPanel> validationMessagePanels = new HashMap<String, IValidationMsgPanel>();

    /**
     * Indicates whether we are importing data from a PNML file or not. This is
     * important to avoid infinite loops when adding elements.
     * 
     * (If true: changes to a data model need to be passed to the GUI model. If
     * false: changes to a GUI model need to be passed to the data model.)
     */
    private boolean importingFromPnml = false;

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
         * 
         * -> Set "checked" state to true as soon as possible to prevent the
         * ValidationController thread from starting the validation before we
         * even have added a validation messages panel!
         */
        IDataModel newDataModel = new DataModel(modelName, displayName);
        newDataModel.setModelChecked(true, NEVER_REMOVE_INITIAL_CHECK_STATE);
        this.dataModels.put(modelName, newDataModel);

        /* Set as current data model. */
        this.currentModel = newDataModel;

        /* Add an associated validation messages panel. */
        IValidationMsgPanel validationMessagesPanel = addValidationMessagePanel(modelName);
        if (validationMessagesPanel == null)
            return;

        if (debug) {
            System.out.println("Data models count: " + dataModels.size());
        }
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

        /*
         * Add the data model.
         * 
         * -> Set "checked" state to true as soon as possible to prevent the
         * ValidationController thread from starting the validation before we
         * even have added a validation messages panel!
         */
        IDataModel newDataModel = new DataModel(canonicalPath, displayName);
        newDataModel.setModelChecked(true, NEVER_REMOVE_INITIAL_CHECK_STATE);
        this.dataModels.put(canonicalPath, newDataModel);

        /*
         * Set as current data model.
         * 
         * Note: This data model must be set as current model here because the
         * PNMLParser pushes all found elements into the current data model!
         */
        this.currentModel = newDataModel;

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
            return ExitCode.OPERATION_CANCELLED;
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
     *            name of the pnml file represented by this model.)
     * @return The {@link IValidationMsgPanel}
     */
    private IValidationMsgPanel addValidationMessagePanel(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.addValidationMessagesPanel", modelName);
        }

        /*
         * Add the validation messages panel.
         */
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
            /*
             * Show an error message!
             */
            dataModels.remove(canonicalPath);
            String infoMessage = i18n.getMessage("errDataModelNotAccepted");
            System.out.println(infoMessage);
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
        currentModel.setModified(false, false);
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

        // /*
        // * Remove the associated validator.
        // */
        // removeWorkflowNetValidator(modelName);

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
     *            name of the pnml file represented by this model.)
     */
    private void removeValidationMessagePanel(String modelName) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.removeValidationMessagePanel", modelName);
        }

        // /*
        // * Reset the "current validation message panel" attribute if we remove
        // * the current validation message panel.
        // */
        // try {
        // if
        // (this.currentValidationMessagePanel.getModelName().equalsIgnoreCase(modelName))
        // {
        // this.currentValidationMessagePanel = null;
        // }
        // } catch (NullPointerException ignore) {
        // // Nothing to do
        // }

        /*
         * Remove the validation message panel.
         */
        this.validationMessagePanels.remove(modelName);

        if (debug) {
            System.out.println("Validation message panels count: " + validationMessagePanels.size());
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
        IValidationMsgPanel validationMessagePanel = getValidationMessagePanel(oldKey);

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

        IValidationMsgPanel value2 = validationMessagePanels.remove(oldKey);
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
     * Uses interface {@link IValidationMsgPanel} to rename the validation
     * message panel.
     * 
     * @param validationMessagePanel
     *            The validation message panel to rename
     * @param newModelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
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

        this.currentModel = model;
    }

    @Override
    public IValidationMsgPanel getValidationMessagePanel(String modelName) {
        for (Entry<String, IValidationMsgPanel> entry : validationMessagePanels.entrySet()) {
            // String key = entry.getKey();
            IValidationMsgPanel validationMessagePanel = entry.getValue();

            if (validationMessagePanel.getModelName().equalsIgnoreCase(modelName))
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

    /*
     * Methods for adding, modify and removal of elements (and callbacks for
     * updates between data and GUI model controller)
     */

    /*
     * Add elements
     */

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

        /* Update the GUI */
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

        /* Update the GUI */
        appController.transitionAddedToCurrentDataModel(id, name, position);
    }

    @Override
    public void addArcToCurrentDataModel(String id, String sourceId, String targetId) {
        currentModel.addArc(id, sourceId, targetId);
        this.elementsAddedToCurrentModel++;

        if (!this.importingFromPnml)
            currentModel.setModified(true, true);

        /* Update the GUI */
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
        currentModel.setModified(true, false);

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
            currentModel.setModified(true, true);
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
        currentModel.setModified(true, true);

        /*
         * Remove all adjacent arcs.
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
        currentModel.setModified(true, true);
    }

    @Override
    public void clearCurrentDataModel() {
        currentModel.clear();
        currentModel.setModified(true, true);
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
        currentModel.setModified(true, false);

        /*
         * No further action required since this method should only be called
         * after renaming a node in the GUI.
         */
    }

    /*
     * Validation events
     */

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
    }

    /**
     * Returns the specified {@link IDataModel} with suppressed error messages
     * if not found because this error can be expected in rare cases. This
     * method is part of the validation process. And the ValidationController
     * thread might slightly lagging behind in terms of the current model (e.g.
     * if the user has suddenly closed the current file during validation).
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
    public void setGuiTransitionEnabledState(String modelName, String transitionId) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.setGuiTransitionEnabledState", modelName,
                    transitionId);
        }

        /*
         * Nothing to do here. Only the IGuiTransition needs this information.
         */
        appController.setGuiTransitionEnabledState(modelName, transitionId);
    }

    @Override
    public void fireDataTransition(String transitionId) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("DataModelController.fireDataTransition", transitionId);
        }

        IDataElement element = currentModel.getElementById(transitionId);
        if (element == null) {
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

        // TODO Now we need a revalidation - but not a complete validation!
        // currentModel.setModified(true, true);
        String validatorName = ApplicationController.enabledTransitionsValidatorName;
        appController.requestIndividualValidation(validatorName, currentModel);
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

        /* Update the GUI */
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

        /* Update the GUI */
        appController.addGuiToken(currentModel.getModelName(), placesWithAddedToken);
    }

    /**
     * Adds a token to the specified {@link IDataTransition}.
     * 
     * @param dataPlace
     *            The {@link DataPlace} to add the token to
     * @param tokensAdded
     *            Counter for added tokens
     * @param placesWithRemovedToken
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

}
