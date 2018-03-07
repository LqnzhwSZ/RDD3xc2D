package de.lambeck.pned.application.actions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.elements.gui.IGuiElement;
import de.lambeck.pned.filesystem.FSInfo;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Manager for {@link AbstractAction} and their "enabled" state.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ActionManager implements IActionManager {

    /** Reference to the main application window */
    protected JFrame mainFrame = null;

    /** Reference to the {@link ApplicationController} */
    protected ApplicationController appController = null;

    /** The manager for localized strings */
    protected I18NManager i18n = null;

    /** Lists of Action objects for menu bar and tool bar */
    protected Map<String, AbstractAction> allActions = new HashMap<>();

    /** Lists of Action objects for popup menus */
    protected Map<String, AbstractAction> popupActions = new HashMap<>();

    /*
     * All Actions
     */

    /** The {@link FileNewAction} */
    AbstractAction fileNewAction;
    /** The {@link FileOpenAction} */
    AbstractAction fileOpenAction;
    /** The {@link FileCloseAction} */
    AbstractAction fileCloseAction;
    /** The {@link FileSaveAction} */
    AbstractAction fileSaveAction;
    /** The {@link FileSaveAsAction} */
    AbstractAction fileSaveAsAction;
    /** The {@link AppExitAction} */
    AbstractAction appExitAction;

    /** The {@link EditUndoAction} */
    AbstractAction editUndoAction;
    /** The {@link EditRedoAction} */
    AbstractAction editRedoAction;

    /** The {@link EditRenameAction} */
    AbstractAction editRenameAction;
    /** The {@link SelectAllAction} */
    AbstractAction selectAllAction;
    /** The {@link EditDeleteAction} */
    AbstractAction editDeleteAction;

    /** The {@link ElementToTheForegroundAction} */
    AbstractAction toForegroundAction;
    /** The {@link ElementOneLayerUpAction} */
    AbstractAction oneLayerUpAction;
    /** The {@link ElementOneLayerDownAction} */
    AbstractAction oneLayerDownAction;
    /** The {@link ElementToTheBackgroundAction} */
    AbstractAction toBackgroundAction;

    /** The {@link FireTransitionAction} */
    AbstractAction fireTransitionAction;
    /** The {@link StopSimulationAction} */
    AbstractAction stopSimulationAction;

    /** The {@link NewArcFromHereAction} */
    AbstractAction newArcFromHereAction;

    /** The {@link NewPlaceAction} */
    AbstractAction newPlaceAction;
    /** The {@link NewTransitionAction} */
    AbstractAction newTransitionAction;

    /*
     * Constructor
     */

    /**
     * Constructs this ActionManager with references to different controllers
     * and main application window.
     * 
     * @param appController
     *            The {@link ApplicationController}
     * @param i18n
     *            The {@link I18NManager}
     * @param mainFrame
     *            Reference to the main application window
     */
    @SuppressWarnings("hiding")
    public ActionManager(ApplicationController appController, I18NManager i18n, JFrame mainFrame) {
        super();
        this.appController = appController;
        this.i18n = i18n;
        this.mainFrame = mainFrame;

        createAllActions();
        disableInitiallyUnnecessaryActions();
        addAllActionsToHashMaps();
    }

    /**
     * Creates all {@link AbstractAction} for this application.
     */
    private void createAllActions() {
        fileNewAction = new FileNewAction(appController, i18n);
        fileOpenAction = new FileOpenAction(appController, i18n, mainFrame);
        fileCloseAction = new FileCloseAction(appController, i18n);
        fileSaveAction = new FileSaveAction(appController, i18n);
        fileSaveAsAction = new FileSaveAsAction(appController, i18n, mainFrame);
        appExitAction = new AppExitAction(appController, i18n);

        editUndoAction = new EditUndoAction(appController, i18n);
        editRedoAction = new EditRedoAction(appController, i18n);

        editRenameAction = new EditRenameAction(appController, i18n);
        selectAllAction = new SelectAllAction(appController, i18n);
        editDeleteAction = new EditDeleteAction(appController, i18n);

        toForegroundAction = new ElementToTheForegroundAction(appController, i18n);
        oneLayerUpAction = new ElementOneLayerUpAction(appController, i18n);
        oneLayerDownAction = new ElementOneLayerDownAction(appController, i18n);
        toBackgroundAction = new ElementToTheBackgroundAction(appController, i18n);

        fireTransitionAction = new FireTransitionAction(appController, i18n);
        stopSimulationAction = new StopSimulationAction(appController, i18n);

        newArcFromHereAction = new NewArcFromHereAction(appController, i18n);

        newPlaceAction = new NewPlaceAction(appController, i18n);
        newTransitionAction = new NewTransitionAction(appController, i18n);
    }

    /**
     * Disables all {@link AbstractAction} that will not be needed at program
     * start.
     */
    private void disableInitiallyUnnecessaryActions() {
        enableActionsForOpenFiles("");
    }

    /**
     * Adds all {@link AbstractAction} for this application to a
     * {@link HashMap}.
     */
    private void addAllActionsToHashMaps() {
        // Menu "File"
        allActions.put("FileNew", fileNewAction);
        allActions.put("FileOpen...", fileOpenAction);
        allActions.put("FileClose", fileCloseAction);
        allActions.put("FileSave", fileSaveAction);
        allActions.put("FileSaveAs...", fileSaveAsAction);
        allActions.put("AppExit", appExitAction);

        // Menu "Edit"
        allActions.put("EditUndo", editUndoAction);
        allActions.put("EditRedo", editRedoAction);

        allActions.put("EditRename...", editRenameAction);
        allActions.put("SelectAll", selectAllAction);
        allActions.put("EditDelete", editDeleteAction);

        // Tool bar "Elements"
        allActions.put("ElementToTheForeground", toForegroundAction);
        allActions.put("ElementOneLayerUp", oneLayerUpAction);
        allActions.put("ElementOneLayerDown", oneLayerDownAction);
        allActions.put("ElementToTheBackground", toBackgroundAction);

        allActions.put("StopSimulation", stopSimulationAction);

        // Popup menu "Elements"
        popupActions.put("FireTransition", fireTransitionAction);

        popupActions.put("ElementToTheForeground", toForegroundAction);
        popupActions.put("ElementOneLayerUp", oneLayerUpAction);
        popupActions.put("ElementOneLayerDown", oneLayerDownAction);
        popupActions.put("ElementToTheBackground", toBackgroundAction);

        popupActions.put("NewArcFromHere", newArcFromHereAction);

        // Popup menu "Empty area"
        popupActions.put("NewPlace", newPlaceAction);
        popupActions.put("NewTransition", newTransitionAction);
    }

    @Override
    public Map<String, AbstractAction> getAllActions() {
        return this.allActions;
    }

    @Override
    public Map<String, AbstractAction> getPopupActions() {
        return this.popupActions;
    }

    @Override
    public void enableActionsForOpenFiles(String activeFile) {
        if (activeFile == null || activeFile.equals("")) {
            fileCloseAction.setEnabled(false);
            fileSaveAction.setEnabled(false);
            fileSaveAsAction.setEnabled(false);

            selectAllAction.setEnabled(false);

            List<IGuiElement> emptyList = new LinkedList<IGuiElement>();
            enableActionsForSelectedElements(emptyList);

            stopSimulationAction.setEnabled(false);

            /* Undo? */
            editUndoAction.setEnabled(false);
            editRedoAction.setEnabled(false);

        } else {
            /* Close and SaveAs are always possible for all files. */
            fileCloseAction.setEnabled(true);
            fileSaveAsAction.setEnabled(true);

            /* Save (directly) not for new and write-protected files */
            boolean isFileSystemFile = FSInfo.isFileSystemFile(activeFile);
            boolean isWriteProtected = FSInfo.isWriteProtectedFile(activeFile);
            if (isFileSystemFile && !isWriteProtected) {
                fileSaveAction.setEnabled(true);
            } else {
                fileSaveAction.setEnabled(false);
            }

            selectAllAction.setEnabled(true);

            stopSimulationAction.setEnabled(true);

            /* Undo? */
            editUndoAction.setEnabled(appController.canUndo());
            editRedoAction.setEnabled(appController.canRedo());

        }
    }

    @Override
    public void enableActionsForSelectedElements(List<IGuiElement> selected) {
        if (selected.size() == 0) {
            editRenameAction.setEnabled(false);
            editDeleteAction.setEnabled(false);

            enableZValueActions(null);

        } else if (selected.size() == 1) {
            editRenameAction.setEnabled(true);
            editDeleteAction.setEnabled(true);

            IGuiElement singleElement = selected.get(0);
            enableZValueActions(singleElement);

        } else {
            editRenameAction.setEnabled(false);
            editDeleteAction.setEnabled(true);

            enableZValueActions(null);

        }
    }

    @Override
    public void enableZValueActions(IGuiElement element) {
        if (element == null) {
            toForegroundAction.setEnabled(false);
            oneLayerUpAction.setEnabled(false);
            oneLayerDownAction.setEnabled(false);
            toBackgroundAction.setEnabled(false);
            return;
        }

        int elementZ = element.getZValue();
        int minZ = appController.getCurrentMinZValue();
        int maxZ = appController.getCurrentMaxZValue();

        toForegroundAction.setEnabled(elementZ != maxZ);
        oneLayerUpAction.setEnabled(elementZ != maxZ);
        oneLayerDownAction.setEnabled(elementZ != minZ);
        toBackgroundAction.setEnabled(elementZ != minZ);
    }

    @Override
    public void enableUndoRedoActions(String activeFile) {
        editUndoAction.setEnabled(appController.canUndo());
        editRedoAction.setEnabled(appController.canRedo());
    }

}
