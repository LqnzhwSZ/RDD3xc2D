package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "ElementOneLayerDown".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ElementOneLayerDownAction extends AbstractPNAction {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 7371928303337068021L;

    /**
     * Creates the ElementOneLayerDownAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The manager for localized strings
     */
    public ElementOneLayerDownAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "ElementOneLayerDown";
        iconPath = "icons/";
        iconName = "Move-backward.png";
        // No shortcut at all
        keyEvent = 0;
        actionEvent = 0;

        customize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        appController.menuCmd_ElementOneLayerDown();
    }
}
