package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "FileSave".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public class FileSaveAction extends AbstractPNAction {

    /**
     * Creates the FileSaveAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The source object for I18N strings
     */
    public FileSaveAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "FileSave";
        iconPath = "icons/gnome/";
        iconName = "Gnome-document-save.svg.png";
        keyEvent = KeyEvent.VK_S;
        actionEvent = SHORTCUT_KEY_MASK;

        customize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        appController.menuCmd_FileSave();
    }
}
