package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "ApplicationExit".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class AppExitAction extends AbstractPNAction {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -479756584153154138L;

    /**
     * Creates the AppExitAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The manager for localized strings
     */
    public AppExitAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "AppExit";
        iconPath = "icons/crystal_clear/";
        iconName = "Crystal_Clear_action_exit.png";
        keyEvent = KeyEvent.VK_F4;
        // Not SHORTCUT_KEY_MASK!
        actionEvent = ActionEvent.ALT_MASK;

        customize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        appController.menuCmd_AppExit();
    }
}
