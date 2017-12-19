package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "EditDelete".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public class EditDeleteAction extends AbstractPNAction {

    /**
     * Creates the EditDeleteAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The source object for I18N strings
     */
    public EditDeleteAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "EditDelete";
        iconPath = "icons/tango/";
        iconName = "Image-missing.svg.png";
        keyEvent = KeyEvent.VK_DELETE;
        // Not SHORTCUT_KEY_MASK!
        actionEvent = 0;

        customize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /*
         * Handle the action
         */
        appController.menuCmd_EditDelete();
    }
}
