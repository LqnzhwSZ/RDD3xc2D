package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "EditUndo".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class EditUndoAction extends AbstractPNAction {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 1218969748625401596L;

    /**
     * Creates the EditUndoAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The manager for localized strings
     */
    public EditUndoAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "EditUndo";
        iconPath = "icons/gnome/";
        iconName = "Gnome-edit-undo.svg.png";
        keyEvent = KeyEvent.VK_Z;
        actionEvent = SHORTCUT_KEY_MASK;

        customize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        appController.menuCmd_EditUndo();
    }
}
