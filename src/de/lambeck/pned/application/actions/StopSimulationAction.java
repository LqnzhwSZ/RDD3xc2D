package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "StopSimulation".
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class StopSimulationAction extends AbstractPNAction {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 5918489215819862442L;

    /**
     * Creates the StopSimulationAction without additional parameters.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The manager for localized strings
     */
    public StopSimulationAction(ApplicationController controller, I18NManager i18nController) {
        super(controller, i18nController);

        internalName = "StopSimulation";
        iconPath = "icons/gnome";
        iconName = "Gnome-media-playback-stop.svg.png";
        // No shortcut at all
        keyEvent = 0;
        actionEvent = 0;

        customize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        appController.menuCmd_StopSimulation();
    }
}
