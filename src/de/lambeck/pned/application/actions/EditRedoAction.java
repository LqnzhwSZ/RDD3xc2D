package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "EditRedo".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class EditRedoAction extends AbstractPNAction {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -8876200704590514329L;

    /**
     * Creates the EditRedoAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The manager for localized strings
     */
    public EditRedoAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "EditRedo";
        iconPath = "icons/gnome/";
        iconName = "Gnome-edit-redo.svg.png";
        keyEvent = KeyEvent.VK_Y;
        actionEvent = SHORTCUT_KEY_MASK;

        customize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        appController.menuCmd_EditRedo();
    }
}
