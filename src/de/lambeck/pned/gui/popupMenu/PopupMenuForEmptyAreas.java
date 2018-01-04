package de.lambeck.pned.gui.popupMenu;

import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import de.lambeck.pned.models.gui.DrawPanel;
import de.lambeck.pned.models.gui.IDrawPanel;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * Popup menu for right click at empty areas on the {@link DrawPanel}. Holds a
 * reference to its DrawPanel to decide which menu items have to be enabled.<BR>
 * <BR>
 * Note: Implements interface PopupMenuListener to call enableMenuItems() in
 * popupMenuWillBecomeVisible().
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class PopupMenuForEmptyAreas extends JPopupMenu implements PopupMenuListener {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 3656599281041874506L;

    /** Show debug messages? */
    private static boolean debug = false;

    /** The Map with existing Actions, suitable for this popup menu. */
    protected Map<String, AbstractAction> popupActions;

    /** Source of the popup trigger */
    private IDrawPanel myDrawPanel = null;

    /** A popup menu "button" */
    private AbstractAction newPlaceAction;
    /** A popup menu "button" */
    private AbstractAction newTransitionAction;

    /**
     * Constructs the popup menu with a reference to its {@link DrawPanel}.
     * 
     * @param sourceDrawPanel
     *            The {@link DrawPanel} as source of the popup trigger
     * @param popupActions
     *            List of Actions
     */
    @SuppressWarnings("hiding")
    public PopupMenuForEmptyAreas(IDrawPanel sourceDrawPanel, Map<String, AbstractAction> popupActions) {
        this.myDrawPanel = sourceDrawPanel;
        this.popupActions = popupActions;

        createMenuItems();
        addPopupMenuListener(this); // PopupMenuListener for enableMenuItems()
    }

    /**
     * Adds the menu items to this menu.
     */
    private void createMenuItems() {
        newPlaceAction = popupActions.get("NewPlace");
        add(newPlaceAction);
        newTransitionAction = popupActions.get("NewTransition");
        add(newTransitionAction);
    }

    /**
     * Enables the menu items depending on the current element.
     */
    void enableMenuItems() {
        /* We can always create new nodes. */
        newPlaceAction.setEnabled(true);
        newTransitionAction.setEnabled(true);
    }

    /* Methods for interface PopupMenuListener */

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("PopupMenuForEmptyAreas.popupMenuCanceled", e);
        }

        myDrawPanel.popupMenuCanceled();
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        // myDrawPanel.popupMenuLeft();
        /*
         * Note: Do not invoke popupMenuLeft() here because this would be before
         * invoking the Action in the popup menu that the user might has clicked
         * at. And these Actions might need the popup menu location which will
         * be reset in popupMenuLeft()!
         */
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        enableMenuItems();
    }
}
