package de.lambeck.pned.gui.toolBar;

import java.awt.FlowLayout;

import javax.swing.JToolBar;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.application.actions.*;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements a tool bar for the Petri net editor. (see super class)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public class PnedToolBar extends AbstractPnedToolBar {

    private static int orientation = JToolBar.HORIZONTAL;

    /**
     * Constructs the ToolBar with a reference to the application controller.
     * 
     * @param controller
     *            The application controller
     * @param i18n
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    public PnedToolBar(ApplicationController controller, I18NManager i18n) {
        super(controller, orientation, i18n);

        String text = i18n.getNameOnly("Elements");
        this.setName(text);

        /*
         * Override some tool bar properties?
         */
        // this.setFloatable(false);
        // this.setVisible(false);

        /*
         * FlowLayout because otherwise the SizeSlider expands to the whole
         * width of the main frame.
         */
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    @Override
    protected void createButtons() {
        add(new ElementToTheForegroundAction(appController, i18n));
        add(new ElementOneLayerUpAction(appController, i18n));
        add(new ElementOneLayerDownAction(appController, i18n));
        add(new ElementToTheBackgroundAction(appController, i18n));

        addSeparator();

        add(new EditDeleteAction(appController, i18n));

        addSeparator();

        add(new TestingStepAction(appController, i18n));
        add(new TestingCompleteAction(appController, i18n));
        add(new TestingQuitAction(appController, i18n));
    }

}
