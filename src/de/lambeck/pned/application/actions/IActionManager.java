package de.lambeck.pned.application.actions;

import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.elements.gui.IGuiElement;
import de.lambeck.pned.models.gui.IGuiModelController;

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
     * Enables or disables all {@link AbstractAction} that depend on at least 1
     * open file.<BR>
     * <BR>
     * Note: This method should mainly be used by the
     * {@link ApplicationController}.
     * 
     * @param activeFile
     *            The full path name of the current file
     */
    void enableActionsForOpenFiles(String activeFile);

    /**
     * Enables or disables all {@link AbstractAction} that depend on the number
     * of selected {@link IGuiElement}.<BR>
     * <BR>
     * Note: This method should mainly be used by the
     * {@link IGuiModelController}.
     * 
     * @param selected
     *            The {@link List} of selected {@link IGuiElement}
     */
    void enableActionsForSelectedElements(List<IGuiElement> selected);

    /**
     * Enables all appropriate {@link AbstractAction} for the Z value if exactly
     * one {@link IGuiElement} is selected or at the popup menu location.<BR>
     * <BR>
     * Note: This method should mainly be used by the
     * {@link IGuiModelController}.
     * 
     * @param element
     *            The {@link IGuiElement} that is currently selected or at the
     *            popup menu location, or null = no element selected and no
     *            element at the popup menu location
     */
    void enableZValueActions(IGuiElement element);

    /**
     * Enables {@link EditUndoAction} and {@link EditRedoAction} if edits may be
     * undone/redone.<BR>
     * <BR>
     * Note: This method should mainly be used by the
     * {@link ApplicationController} after an Undo/Redo operation or when the
     * user switches between open files.
     * 
     * @param activeFile
     *            The full path name of the current file
     */
    void enableUndoRedoActions(String activeFile);

}
