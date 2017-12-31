package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "FireTransition".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public class FireTransitionAction extends AbstractPNAction {

    /**
     * Creates the FireTransitionActionAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The source object for I18N strings
     */
    public FireTransitionAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "FireTransition";
        iconPath = "icons/gnome";
        iconName = "Gnome-media-playback-start.svg.png";
        // No shortcut at all
        keyEvent = 0;
        actionEvent = 0;

        customize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /*
         * Handle the action
         */
        appController.menuCmd_FireTransition();
    }
}
