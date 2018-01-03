package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "NewArcToHereAction".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class NewArcToHereAction extends AbstractPNAction {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -567793306395903318L;

    /**
     * Creates the ElementToTheForegroundAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The manager for localized strings
     */
    public NewArcToHereAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "NewArcToHere";
        iconPath = "icons/";
        iconName = "ArcToHere.png";
        // No shortcut at all
        keyEvent = 0;
        actionEvent = 0;

        customize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        appController.menuCmd_NewArcToHere();
    }
}
