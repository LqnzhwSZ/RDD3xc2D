package de.lambeck.pned.gui.popupMenu;

import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import de.lambeck.pned.models.gui.DrawPanel;
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

    /** A popup menu "button" */
    private AbstractAction newPlaceAction;
    /** A popup menu "button" */
    private AbstractAction newTransitionAction;

    /**
     * Constructs the popup menu <B>without</B> a reference to its
     * {@link DrawPanel}.
     * 
     * @param popupActions
     *            List of Actions
     */
    @SuppressWarnings("hiding")
    public PopupMenuForEmptyAreas(Map<String, AbstractAction> popupActions) {
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
