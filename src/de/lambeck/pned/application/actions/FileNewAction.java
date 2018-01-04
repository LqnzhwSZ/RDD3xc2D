package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "FileNew".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class FileNewAction extends AbstractPNAction {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 7354219256525049071L;

    /**
     * Creates the FileNewAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The manager for localized strings
     */
    public FileNewAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "FileNew";
        iconPath = "icons/gnome/";
        iconName = "Gnome-document-new.svg.png";
        keyEvent = KeyEvent.VK_N;
        actionEvent = SHORTCUT_KEY_MASK;

        customize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        appController.menuCmd_FileNew();
    }
}
