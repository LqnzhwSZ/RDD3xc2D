package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "EditRename...".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class EditRenameAction extends AbstractPNAction {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -8721934953230654516L;

    /**
     * Creates the EditRenameAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The manager for localized strings
     */
    public EditRenameAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "EditRename...";
        iconPath = "icons/";
        iconName = "Empty.png";
        keyEvent = KeyEvent.VK_F2;
        // Not SHORTCUT_KEY_MASK!
        actionEvent = 0;

        customize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        appController.menuCmd_EditRename();
    }
}
