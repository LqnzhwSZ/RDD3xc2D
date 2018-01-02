package de.lambeck.pned.gui.toolBar;

import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JToolBar;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Abstract base class for tool bar for the Petri net editor. Holds a reference
 * to the application controller (to pass this reference to action objects).
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractPnedToolBar extends JToolBar {

    /** The application controller (has to handle the commands) */
    protected ApplicationController appController = null;

    /** Reference to the manager for I18N strings */
    protected I18NManager i18n = null;

    /** The Map with existing Actions, suitable for tool bars. */
    protected Map<String, AbstractAction> allActions;

    /**
     * Constructs the ToolBar with a reference to the application controller.
     * 
     * @param controller
     *            The application controller
     * @param i18n
     *            The source object for I18N strings
     * @param orientation
     *            The initial orientation
     * @param allActions
     *            List of Actions
     */
    @SuppressWarnings("hiding")
    public AbstractPnedToolBar(ApplicationController controller, I18NManager i18n, int orientation,
            Map<String, AbstractAction> allActions) {
        super(orientation);
        this.appController = controller;
        this.i18n = i18n;
        this.allActions = allActions;

        createButtons();

        /* Set some tool bar properties. */
        // this.setFloatable(true);
        // this.setRollover(true);
        // this.setBorderPainted(true);
        this.setVisible(true);
    }

    /**
     * Creates the buttons for this tool bar. This class is abstract because
     * different tool bars will have different buttons.
     */
    protected abstract void createButtons();

}
