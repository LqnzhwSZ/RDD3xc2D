package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "ElementToTheForeground".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ElementToTheForegroundAction extends AbstractPNAction {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 6534157099703488239L;

    /**
     * Creates the ElementToTheForegroundAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The manager for localized strings
     */
    public ElementToTheForegroundAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "ElementToTheForeground";
        iconPath = "icons/";
        iconName = "Bring-to-front.png";
        // No shortcut at all
        keyEvent = 0;
        actionEvent = 0;

        customize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        appController.menuCmd_ElementToTheForeground();
    }
}
