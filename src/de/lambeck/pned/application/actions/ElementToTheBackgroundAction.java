package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "ElementToTheBackground".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ElementToTheBackgroundAction extends AbstractPNAction {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -2135018880019555596L;

    /**
     * Creates the ElementToTheBackgroundAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The manager for localized strings
     */
    public ElementToTheBackgroundAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "ElementToTheBackground";
        iconPath = "icons/";
        iconName = "Send-to-back.png";
        // No shortcut at all
        keyEvent = 0;
        actionEvent = 0;

        customize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        appController.menuCmd_ElementToTheBackground();
    }
}
