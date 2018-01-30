package de.lambeck.pned.gui.popupMenu;

import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import de.lambeck.pned.elements.gui.IGuiNode;
import de.lambeck.pned.models.gui.DrawPanel;
import de.lambeck.pned.models.gui.IDrawPanel;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * Popup menu for right click at Places on the {@link DrawPanel}. Holds a
 * reference to its DrawPanel to decide which menu items have to be enabled.<BR>
 * <BR>
 * Note: Implements interface PopupMenuListener to call enableMenuItems() in
 * popupMenuWillBecomeVisible().
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class PopupMenuForPlaces extends JPopupMenu implements PopupMenuListener {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -3018589253320879735L;

    /** Show debug messages? */
    private static boolean debug = false;

    /** The Map with existing Actions, suitable for this popup menu. */
    protected Map<String, AbstractAction> popupActions;

    /** Source of the popup trigger */
    private IDrawPanel myDrawPanel = null;

    /** Local reference to the current node */
    private IGuiNode node;

    // /** A popup menu "button" */
    // private AbstractAction selectAction;
    /** A popup menu "button" */
    private AbstractAction toForegroundAction;
    /** A popup menu "button" */
    private AbstractAction oneLayerUpAction;
    /** A popup menu "button" */
    private AbstractAction oneLayerDownAction;
    /** A popup menu "button" */
    private AbstractAction toBackgroundAction;
    /** A popup menu "button" */
    private AbstractAction newArcFromHereAction;

    /**
     * Constructs the popup menu with a reference to its {@link DrawPanel} and
     * the current {@link IGuiNode}.
     * 
     * @param sourceDrawPanel
     *            The {@link DrawPanel} as source of the popup trigger
     * @param node
     *            The {@link IGuiNode} at the current location on the DrawPanel
     * @param popupActions
     *            List of Actions
     */
    @SuppressWarnings("hiding")
    public PopupMenuForPlaces(IDrawPanel sourceDrawPanel, IGuiNode node, Map<String, AbstractAction> popupActions) {
        this.myDrawPanel = sourceDrawPanel;
        this.node = node;
        this.popupActions = popupActions;

        createMenuItems();
        addPopupMenuListener(this); // PopupMenuListener for enableMenuItems()
    }

    /**
     * Adds the menu items to this menu.
     */
    private void createMenuItems() {
        // selectAction = popupActions.get("ElementSelect");
        // add(selectAction);
        //
        // addSeparator();

        newArcFromHereAction = popupActions.get("NewArcFromHere");
        add(newArcFromHereAction);

        addSeparator();

        toForegroundAction = popupActions.get("ElementToTheForeground");
        add(toForegroundAction);
        oneLayerUpAction = popupActions.get("ElementOneLayerUp");
        add(oneLayerUpAction);
        oneLayerDownAction = popupActions.get("ElementOneLayerDown");
        add(oneLayerDownAction);
        toBackgroundAction = popupActions.get("ElementToTheBackground");
        add(toBackgroundAction);
    }

    /**
     * Enables the menu items depending on the current element.
     */
    private void enableMenuItems() {
        // /* All elements can be selected */
        // selectAction.setEnabled(true);

        /*
         * Enables menu items depending on the z value (height level) of the
         * current element.
         */
        int minZ = myDrawPanel.getMinZValue();
        int maxZ = myDrawPanel.getMaxZValue();
        int currZ = myDrawPanel.getZValue(this.node);

        toForegroundAction.setEnabled(currZ != maxZ);
        oneLayerUpAction.setEnabled(currZ != maxZ);
        oneLayerDownAction.setEnabled(currZ != minZ);
        toBackgroundAction.setEnabled(currZ != minZ);

        newArcFromHereAction.setEnabled(true);
    }

    /* Methods for interface PopupMenuListener */

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("PopupMenuForPlaces.popupMenuCanceled", e);
        }
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        // NOP
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        enableMenuItems();
    }
}
