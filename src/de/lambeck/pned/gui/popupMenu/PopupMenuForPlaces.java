package de.lambeck.pned.gui.popupMenu;

import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import de.lambeck.pned.elements.ENodeType;
import de.lambeck.pned.elements.gui.IGuiNode;
import de.lambeck.pned.models.gui.DrawPanel;
import de.lambeck.pned.models.gui.IDrawPanel;
import de.lambeck.pned.util.ConsoleLogger;

/**
 * Popup menu for right click at Places on the {@link DrawPanel}. Holds a
 * reference to its DrawPanel to decide which menu items have to be enabled.
 * 
 * Note: Implements interface PopupMenuListener to call enableMenuItems() in
 * popupMenuWillBecomeVisible().
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public class PopupMenuForPlaces extends JPopupMenu implements PopupMenuListener {

    private static boolean debug = true;

    protected Map<String, AbstractAction> popupActions;

    /**
     * Source of the popup trigger
     */
    private IDrawPanel myDrawPanel = null;

    /**
     * Local reference to the current node
     */
    private IGuiNode node;

    /**
     * The "buttons" of the popup menu
     */
    private AbstractAction selectAction;
    private AbstractAction toForegroundAction;
    private AbstractAction oneLayerUpAction;
    private AbstractAction oneLayerDownAction;
    private AbstractAction toBackgroundAction;
    private AbstractAction newArcFromHereAction;
    private AbstractAction newArcToHereAction;

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
        // super(title);
        this.myDrawPanel = sourceDrawPanel;
        // this.myLocation = mouseLocation;
        // this.myGuiController = controller;
        // this.myGuiModel = guiModel;
        this.node = node;
        this.popupActions = popupActions;

        createMenuItems();
        addPopupMenuListener(this); // PopupMenuListener for enableMenuItems()
    }

    /**
     * Adds the menu items to this menu.
     */
    private void createMenuItems() {
        selectAction = popupActions.get("ElementSelect");
        add(selectAction);

        addSeparator();

        toForegroundAction = popupActions.get("ElementToTheForeground");
        add(toForegroundAction);
        oneLayerUpAction = popupActions.get("ElementOneLayerUp");
        add(oneLayerUpAction);
        oneLayerDownAction = popupActions.get("ElementOneLayerDown");
        add(oneLayerDownAction);
        toBackgroundAction = popupActions.get("ElementToTheBackground");
        add(toBackgroundAction);

        addSeparator();

        newArcFromHereAction = popupActions.get("NewArcFromHere");
        add(newArcFromHereAction);
        newArcToHereAction = popupActions.get("NewArcToHere");
        add(newArcToHereAction);
    }

    /**
     * Enables the menu items depending on the current element.
     */
    void enableMenuItems() {
        /*
         * All elements can be selected
         */
        selectAction.setEnabled(true);

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

        /*
         * Check if we are adding a new arc (of proper type).
         */
        boolean enableNewArcFromHereAction = getEnableNewArcFromHere();
        newArcFromHereAction.setEnabled(enableNewArcFromHereAction);

        boolean enableNewArcToHereAction = getEnableNewArcToHere();
        newArcToHereAction.setEnabled(enableNewArcToHereAction);
    }

    private boolean getEnableNewArcFromHere() {
        boolean addingNewArc = myDrawPanel.getStateAddingNewArc();

        if (addingNewArc)
            return false;

        return true;
    }

    private boolean getEnableNewArcToHere() {
        boolean addingNewArc = myDrawPanel.getStateAddingNewArc();
        ENodeType sourceForNewArc = myDrawPanel.getSourceForNewArcType();

        if (!addingNewArc)
            return false;

        return (sourceForNewArc == ENodeType.TRANSITION);
    }

    /*
     * Methods for interface PopupMenuListener
     */

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        if (debug) {
            ConsoleLogger.consoleLogMethodCall("PopupMenuForPlaces.popupMenuCanceled", e);
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
