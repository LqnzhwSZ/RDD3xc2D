package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "FileClose".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class FileCloseAction extends AbstractPNAction {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -4109549728172046417L;

    /**
     * Creates the FileCloseAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The manager for localized strings
     */
    public FileCloseAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "FileClose";
        iconPath = "icons/gnome/";
        iconName = "Gnome-folder.svg.png";
        keyEvent = KeyEvent.VK_W;
        actionEvent = SHORTCUT_KEY_MASK;

        customize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        appController.menuCmd_FileClose();
    }
}
