package de.lambeck.pned.gui.toolBar;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.gui.icon.ImageIconCreator;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements one of the tool bars for the Petri net editor. (see super class)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public class ToolBarTesting extends AbstractPnedToolBar {

    private static int orientation = JToolBar.VERTICAL;

    /**
     * Constructs the ToolBar with a reference to the application controller.
     * 
     * @param controller
     *            The application controller
     * @param i18n
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    public ToolBarTesting(ApplicationController controller, I18NManager i18n) {
        super(controller, orientation, i18n);

        String text = i18n.getNameOnly("Testing");
        this.setName(text);

        /*
         * Override some tool bar properties?
         */
        // this.setFloatable(false);
        // this.setVisible(false);
    }

    @Override
    protected void createButtons() {
        createButton("TestingStep", "icons/gnome/", "Gnome-media-playback-start.svg.png");
        createButton("TestingComplete", "icons/gnome/", "Gnome-media-skip-forward.svg.png");
        createButton("TestingQuit", "icons/gnome/", "Gnome-media-playback-stop.svg.png");
    }

    /**
     * Creates one button for the tool bar.
     * 
     * @param actionCommand
     *            The commando
     * @param iconSubfolder
     *            The subfolder in the resource folder containing the icon
     * @param iconName
     *            The file name of the icon
     */
    private void createButton(String actionCommand, String iconSubfolder, String iconName) {
        actionCommand = i18n.getNameOnly(actionCommand);
        String toolTipText = actionCommand;
        String altText = actionCommand;
        ImageIcon icon = ImageIconCreator.getImageIcon(iconSubfolder, imagesSize.getValue(), iconName, altText);
        JButton button = ToolBarButtonCreator.getToolbarButton(actionCommand, icon, toolTipText, altText, this);
        add(button);
    }

}
