package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "NewArcFromHereAction".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public class NewArcFromHereAction extends AbstractPNAction {

    /**
     * Creates the ElementToTheForegroundAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The source object for I18N strings
     */
    public NewArcFromHereAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "NewArcFromHere";
        iconPath = "icons/";
        iconName = "ArcFromHere.png";
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
        appController.menuCmd_NewArcFromHere();
    }
}
