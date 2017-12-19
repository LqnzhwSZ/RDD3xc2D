package de.lambeck.pned.models.data;

import java.awt.Point;
import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.application.EStatusMessageLevel;
import de.lambeck.pned.application.ExitCode;
import de.lambeck.pned.elements.data.EPlaceMarking;
import de.lambeck.pned.elements.data.IDataArc;
import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.elements.data.IDataNode;
import de.lambeck.pned.filesystem.FSInfo;
import de.lambeck.pned.filesystem.pnml.EPNMLParserExitCode;
import de.lambeck.pned.filesystem.pnml.PNMLParser;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.validation.IValidationMessagesPanel;
import de.lambeck.pned.models.data.validation.ValidationMessagesPanel;
import de.lambeck.pned.models.gui.IDrawPanel;

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

    /**
     * Current validation messages panel is the message panel that corresponds
     * to the active tab of the tabbed pane.
     */
    private IValidationMessagesPanel currentValidationMessagePanel = null;

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
            System.out.println("DataModelController.addDataModel(" + modelName + ", " + displayName + ")");
        }

        this.currentModel = new DataModel(modelName, displayName);
        this.dataModels.put(modelName, currentModel);

        /*
         * Add an associated validation messages panel as well!
         */
        addValidationMessagePanel(modelName, displayName);

        if (debug) {
            System.out.println("Data models count: " + dataModels.size());
        }
    }

    @Override
    public int addDataModel(File pnmlFile) {
        if (debug) {
            System.out.println("DataModelController.addDataModel(" + pnmlFile + ")");
        }

        /*
         * New data model
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

        this.currentModel = new DataModel(canonicalPath, displayName);
        this.dataModels.put(canonicalPath, currentModel);

        /*
         * Parse the file
         */
        PNMLParser pnmlParser = new PNMLParser(pnmlFile, this);
        pnmlParser.initParser();
        this.elementsAddedToCurrentModel = 0;
        int returnValue = pnmlParser.parse();

        /*
         * Check errors during import. (Accept or discard this model?)
         */
        boolean accepted = acceptModel(canonicalPath, returnValue);
        if (!accepted)
            return ExitCode.OPERATION_CANCELLED;

        /*
         * Add an associated validation messages panel as well!
         */
        addValidationMessagePanel(canonicalPath, displayName);

        if (debug) {
            System.out.println("Data models count: " + dataModels.size());
        }
        return ExitCode.OPERATION_SUCCESSFUL;
    }

    @Override
    public void removeDataModel(String modelName) {
        if (debug) {
            System.out.println("DataModelController.removeDataModel(" + modelName + ")");
        }

        this.dataModels.remove(modelName);
        this.currentModel = null;

        /*
         * Remove the associated validation messages panel as well.
         */
        removeValidationMessagePanel(modelName);

        if (debug) {
            System.out.println("Data models count: " + dataModels.size());
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

        setValidationMessagePanelNames(validationMessagePanel, newModelName, newDisplayName);

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
     * @param newDisplayName
     *            The title of the tab (= the file name)
     */
    private void setValidationMessagePanelNames(IValidationMessagesPanel validationMessagePanel, String newModelName,
            String newDisplayName) {
        validationMessagePanel.setModelName(newModelName);
        validationMessagePanel.setDisplayName(newDisplayName);
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
    public IDataModel getDataModel(String modelName) {
        return dataModels.get(modelName);
    }

    @Override
    public IDataModel getCurrentModel() {
        return this.currentModel;
    }

    @Override
    public void setCurrentModel(IDataModel model) {
        if (debug) {
            System.out.println("DataModelController.setCurrentModel(" + model.getModelName() + ")");
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

    @Override
    public void setCurrentValidationMessagesPanel(IValidationMessagesPanel validationMessagesPanel) {
        if (debug) {
            System.out.println("DataModelController.setCurrentValidationMessagesPanel("
                    + validationMessagesPanel.getModelName() + ")");
        }

        this.currentValidationMessagePanel = validationMessagesPanel;

        /*
         * TODO Inform the validation messages panel to reset its state? (like
         * for setCurrentDrawPanel in the GUI controller)
         */
        this.currentValidationMessagePanel.reset();
    }

    /**
     * Called by addDataModel to add an associated
     * {@link IValidationMessagesPanel}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     * @param displayName
     *            The title of the tab (= the file name)
     */
    private void addValidationMessagePanel(String modelName, String displayName) {
        if (debug) {
            System.out
                    .println("DataModelController.addValidationMessagesPanel(" + modelName + ", " + displayName + ")");
        }

        this.currentValidationMessagePanel = new ValidationMessagesPanel(modelName, displayName);
        this.validationMessagePanels.put(modelName, currentValidationMessagePanel);
    }

    /**
     * Called by removeDataModel to remove the associated {@link IDrawPanel}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     */
    private void removeValidationMessagePanel(String modelName) {
        if (debug) {
            System.out.println("DataModelController.removeValidationMessagePanel(" + modelName + ")");
        }

        this.validationMessagePanels.remove(modelName);
        this.currentValidationMessagePanel = null;

        if (debug) {
            System.out.println("Validation message panels count: " + validationMessagePanels.size());
        }
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
    public void addPlaceToCurrentDataModel(String id, EPlaceMarking initialMarking, Point position) {
        currentModel.addPlace(id, "", initialMarking, position);
    }

    @Override
    public void addPlaceToCurrentDataModel(String id, String name, EPlaceMarking initialMarking, Point position) {
        currentModel.addPlace(id, name, initialMarking, position);
        // if (debug) {
        // System.out.println("Place added to data model " +
        // currentModel.getModelName());
        // }
        this.elementsAddedToCurrentModel++;

        /*
         * Update the GUI
         */
        appController.placeAddedToCurrentDataModel(id, name, initialMarking, position);
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

    // @Override
    // public void toggleMarking(String placeId, EPlaceMarking newMarking) {
    // IDataElement element = null;
    // try {
    // element = currentModel.getElementById(placeId);
    // } catch (NoSuchElementException e) {
    // // TODO: handle exception?
    // // setInfo_Status(e.getMessage(), EStatusMessageLevel.ERROR);
    // String errorMessage = i18n.getMessage("errMissingIdInModel");
    // errorMessage = errorMessage.replace("%id%", placeId);
    // errorMessage = errorMessage.replace("%modelName%",
    // currentModel.getModelName());
    // setInfo_Status(errorMessage, EStatusMessageLevel.ERROR);
    // return;
    // }
    //
    // if (!(element instanceof DataPlace)) {
    // System.err.println("No place with id " + placeId + " to update!");
    // return;
    // }
    //
    // DataPlace place = (DataPlace) element;
    // place.setMarking(newMarking);
    // currentModel.setModified(true);
    // }

    @Override
    public void removeDataElement(String elementId) {
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
            for (IDataArc arc : predElements) {
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

    @Override
    public void moveNode(String nodeId, Point newPosition) {
        if (debug) {
            System.out.println("DataModelController.moveNode(" + nodeId + "," + newPosition + ")");
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

}
