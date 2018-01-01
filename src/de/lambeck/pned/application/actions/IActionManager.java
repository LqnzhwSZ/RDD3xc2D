package de.lambeck.pned.application.actions;

import java.util.Map;

import javax.swing.AbstractAction;

import de.lambeck.pned.elements.gui.IGuiElement;

/**
 * Interface for the {@link ActionManager}
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IActionManager {

    /**
     * @return The {@link Map} with all {@link AbstractAction} for this
     *         application
     */
    Map<String, AbstractAction> getAllActions();

    /**
     * @return The {@link Map} with all {@link AbstractAction} for popup menus
     *         for this application
     */
    Map<String, AbstractAction> getPopupActions();

    /**
     * Enables or disables all {@link AbstractAction} that depend on the
     * currently selected element or the element at the popup menu location.
     * 
     * @param element
     *            The {@link IGuiElement} that is currently selected or at the
     *            popup menu location
     */
    void updateZValueDependingActions(IGuiElement element);

}
