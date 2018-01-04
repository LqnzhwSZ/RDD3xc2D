package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "NewPlaceAction".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class NewPlaceAction extends AbstractPNAction {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 2982078806851050053L;

    /**
     * Creates the ElementToTheForegroundAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The manager for localized strings
     */
    public NewPlaceAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "NewPlace";
        iconPath = "icons/";
        iconName = "New-Place.png";
        // No shortcut at all
        keyEvent = 0;
        actionEvent = 0;

        customize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        appController.menuCmd_NewPlace();
    }
}
