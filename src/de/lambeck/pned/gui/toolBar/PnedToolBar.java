package de.lambeck.pned.gui.toolBar;

import java.awt.FlowLayout;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JToolBar;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.gui.settings.SizeSlider;
import de.lambeck.pned.gui.settings.ZoomSlider;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements a tool bar for the Petri net editor. (see super class)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class PnedToolBar extends AbstractPnedToolBar {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -7139317989133379455L;

    /** Defines the orientation of this tool bar. */
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

    // private JCheckBox debugCheckBox;

    /**
     * Constructs the ToolBar with a reference to the application controller.
     * 
     * @param controller
     *            The application controller
     * @param i18n
     *            The manager for localized strings
     * @param allActions
     *            List of Actions
     */
    @SuppressWarnings("hiding")
    public PnedToolBar(ApplicationController controller, I18NManager i18n, Map<String, AbstractAction> allActions) {
        super(controller, i18n, orientation, allActions);

        String text = i18n.getNameOnly("WorkflowNet");
        this.setName(text);

        /* Override some tool bar properties? */
        // this.setFloatable(false);
        // this.setVisible(false);

        /*
         * FlowLayout because otherwise the SizeSlider expands to the whole
         * width of the main frame.
         */
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        /* Add the debug check box. */
        // addDebugCheckBox();
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

        addSeparator();

        String sizeSliderName = i18n.getNameOnly("ElementsDisplaySize");
        SizeSlider sizeSlider = new SizeSlider(sizeSliderName, appController);
        add(sizeSlider);
        addSeparator();
        add(new ZoomSlider("Zoom", appController));
    }

    // private void addDebugCheckBox() {
    // debugCheckBox = new JCheckBox("Debug messages in console");
    // debugCheckBox.setSelected(appController.getShowDebugMessages());
    // debugCheckBox.addItemListener(new ItemListener() {
    //
    // @Override
    // public void itemStateChanged(ItemEvent e) {
    // boolean b = false;
    //
    // int state = e.getStateChange();
    // switch (state) {
    // case ItemEvent.SELECTED:
    // b = true;
    // break;
    // case ItemEvent.DESELECTED:
    // b = false;
    // default:
    // break;
    // }
    //
    // appController.setShowDebugMessages(b);
    // }
    // });
    //
    // addSeparator();
    // add(debugCheckBox);
    // }

}
