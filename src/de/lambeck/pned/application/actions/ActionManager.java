package de.lambeck.pned.application.actions;

import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.elements.gui.IGuiElement;
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

    /** Reference to the manager for I18N strings */
    protected I18NManager i18n = null;

    /** Lists of Action objects for menu bar and tool bar */
    protected Map<String, AbstractAction> allActions = new HashMap<>();

    /** Lists of Action objects for popup menus */
    protected Map<String, AbstractAction> popupActions = new HashMap<>();

    /*
     * All Actions
     */

    AbstractAction fileNewAction;
    AbstractAction fileOpenAction;
    AbstractAction fileCloseAction;
    AbstractAction fileSaveAction;
    AbstractAction fileSaveAsAction;
    AbstractAction appExitAction;

    AbstractAction editRenameAction;
    AbstractAction editDeleteAction;

    AbstractAction toForegroundAction;
    AbstractAction oneLayerUpAction;
    AbstractAction oneLayerDownAction;
    AbstractAction toBackgroundAction;

    AbstractAction elementSelectAction;
    AbstractAction fireTransitionAction;
    AbstractAction stopSimulationAction;

    AbstractAction newArcFromHereAction;
    AbstractAction newArcToHereAction;

    AbstractAction newPlaceAction;
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

        addAllActionsToHashMaps();
    }

    /**
     * Adds all {@link AbstractAction} for this application to a
     * {@link HashMap}.
     */
    private void addAllActionsToHashMaps() {
        /* Create all Actions... */
        fileNewAction = new FileNewAction(appController, i18n);
        fileOpenAction = new FileOpenAction(appController, i18n, mainFrame);
        fileCloseAction = new FileCloseAction(appController, i18n);
        fileSaveAction = new FileSaveAction(appController, i18n);
        fileSaveAsAction = new FileSaveAsAction(appController, i18n, mainFrame);
        appExitAction = new AppExitAction(appController, i18n);

        editRenameAction = new EditRenameAction(appController, i18n);
        editDeleteAction = new EditDeleteAction(appController, i18n);

        toForegroundAction = new ElementToTheForegroundAction(appController, i18n);
        oneLayerUpAction = new ElementOneLayerUpAction(appController, i18n);
        oneLayerDownAction = new ElementOneLayerDownAction(appController, i18n);
        toBackgroundAction = new ElementToTheBackgroundAction(appController, i18n);

        elementSelectAction = new ElementSelectAction(appController, i18n);
        fireTransitionAction = new FireTransitionAction(appController, i18n);
        stopSimulationAction = new StopSimulationAction(appController, i18n);

        newArcFromHereAction = new NewArcFromHereAction(appController, i18n);
        newArcToHereAction = new NewArcToHereAction(appController, i18n);

        newPlaceAction = new NewPlaceAction(appController, i18n);
        newTransitionAction = new NewTransitionAction(appController, i18n);

        /* ...and add them to the Maps... */

        // Menu "File"
        allActions.put("FileNew", fileNewAction);
        allActions.put("FileOpen...", fileOpenAction);
        allActions.put("FileClose", fileCloseAction);
        allActions.put("FileSave", fileSaveAction);
        allActions.put("FileSaveAs...", fileSaveAsAction);
        allActions.put("AppExit", appExitAction);

        // Menu "Edit"
        allActions.put("EditRename...", editRenameAction);
        allActions.put("EditDelete", editDeleteAction);

        // Tool bar "Elements"
        allActions.put("ElementToTheForeground", toForegroundAction);
        allActions.put("ElementOneLayerUp", oneLayerUpAction);
        allActions.put("ElementOneLayerDown", oneLayerDownAction);
        allActions.put("ElementToTheBackground", toBackgroundAction);

        allActions.put("StopSimulation", stopSimulationAction);

        // Popup menu "Elements"
        popupActions.put("FireTransition", fireTransitionAction);

        popupActions.put("ElementSelect", elementSelectAction);
        popupActions.put("ElementToTheForeground", toForegroundAction);
        popupActions.put("ElementOneLayerUp", oneLayerUpAction);
        popupActions.put("ElementOneLayerDown", oneLayerDownAction);
        popupActions.put("ElementToTheBackground", toBackgroundAction);

        popupActions.put("NewArcFromHere", newArcFromHereAction);
        popupActions.put("NewArcToHere", newArcToHereAction);

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
    public void updateZValueActions(IGuiElement element) {
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

}
