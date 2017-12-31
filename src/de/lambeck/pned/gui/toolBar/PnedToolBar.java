package de.lambeck.pned.gui.toolBar;

import java.awt.FlowLayout;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JToolBar;

import de.lambeck.pned.application.ApplicationController;
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

    /** A tool bar "button" */
    private AbstractAction toForegroundAction;
    /** A tool bar "button" */
    private AbstractAction oneLayerUpAction;
    /** A tool bar "button" */
    private AbstractAction oneLayerDownAction;
    /** A tool bar "button" */
    private AbstractAction toBackgroundAction;
    /** A tool bar "button" */
    private AbstractAction editDeleteAction;
    /** A tool bar "button" */
    private AbstractAction stopSimulationAction;

    /**
     * Constructs the ToolBar with a reference to the application controller.
     * 
     * @param controller
     *            The application controller
     * @param i18n
     *            The source object for I18N strings
     * @param allActions
     *            List of Actions
     */
    @SuppressWarnings("hiding")
    public PnedToolBar(ApplicationController controller, I18NManager i18n, Map<String, AbstractAction> allActions) {
        super(controller, orientation, allActions);

        String text = i18n.getNameOnly("WorkflowNet");
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
        toForegroundAction = allActions.get("ElementToTheForeground");
        add(toForegroundAction);

        oneLayerUpAction = allActions.get("ElementOneLayerUp");
        add(oneLayerUpAction);

        oneLayerDownAction = allActions.get("ElementOneLayerDown");
        add(oneLayerDownAction);

        toBackgroundAction = allActions.get("ElementToTheBackground");
        add(toBackgroundAction);

        addSeparator();

        editDeleteAction = allActions.get("EditDelete");
        add(editDeleteAction);

        addSeparator();

        stopSimulationAction = allActions.get("StopSimulation");
        add(stopSimulationAction);
    }

}
