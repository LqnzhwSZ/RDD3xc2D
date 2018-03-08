package de.lambeck.pned.models.gui;

import java.awt.Point;
import java.util.List;

import javax.swing.AbstractAction;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.application.actions.ActionManager;
import de.lambeck.pned.application.actions.IActionManager;
import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.elements.data.IDataNode;
import de.lambeck.pned.elements.gui.*;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;

/**
 * Interface to inform the {@link ApplicationController} about changes to an
 * {@link IGuiElement} in an {@link IGuiModel}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IGuiElementChanges {

    /**
     * Callback for the {@link IGuiModelController} to get the
     * {@link IDataModelController} up-to-date after adding a {@link IGuiPlace}.
     * 
     * @param id
     *            The ID of the added place
     * @param name
     *            The name of the added place
     * @param initialTokens
     *            The initial tokens count of the added place
     * @param position
     *            The position of the added place
     */
    void placeAddedToCurrentGuiModel(String id, String name, EPlaceToken initialTokens, Point position);

    /**
     * Callback for the {@link IGuiModelController} to get the
     * {@link IDataModelController} up-to-date after adding a
     * {@link IGuiTransition}.
     * 
     * @param id
     *            The ID of the added transition
     * @param name
     *            The name of the added transition
     * @param position
     *            The position of the added transition
     */
    void transitionAddedToCurrentGuiModel(String id, String name, Point position);

    /**
     * Callback for the {@link IGuiModelController} to get the
     * {@link IDataModelController} up-to-date after adding a {@link IGuiArc}.
     * 
     * @param id
     *            The ID of the added arc
     * @param sourceId
     *            The source ID for the added arc
     * @param targetId
     *            The target ID for the added arc
     */
    void arcAddedToCurrentGuiModel(String id, String sourceId, String targetId);

    /**
     * Handles the {@link IGuiModelController} request to remove an
     * {@link IDataElement} from the {@link IDataModel}.
     * 
     * @param elementId
     *            The ID of the removed {@link IGuiElement}
     */
    void guiElementRemoved(String elementId);

    /**
     * Handles the {@link IGuiModelController} request to rename an
     * {@link IDataNode} in the {@link IDataModel}.
     * 
     * @param nodeId
     *            The ID of the renamed {@link IGuiNode}
     * @param newName
     *            The new name
     */
    void guiNodeRenamed(String nodeId, String newName);

    /**
     * Handles the {@link IGuiModelController} request to update the position of
     * an {@link IDataNode} in the {@link IDataModel}.
     *
     * @param nodeId
     *            The ID of the dragged {@link IGuiNode}
     * @param newPosition
     *            The new position
     */
    void guiNodeDragged(String nodeId, Point newPosition);

    /* Validation events */

    /**
     * Handles the {@link IGuiModelController} info that the specified
     * {@link IGuiTransition} has been fired.<BR>
     * <BR>
     * Note: This refers to the current model (active file).
     * 
     * @param transitionId
     *            The ID of the fired {@link IGuiTransition}
     */
    void guiTransitionFired(String transitionId);

    /* Z value actions */

    /**
     * Passes the request to update the "enabled" state of appropriate
     * {@link AbstractAction} to the {@link IActionManager}. This refers to
     * Actions which depend on the currently selected element or the element at
     * the popup menu location.
     * 
     * @param element
     *            The {@link IGuiElement} that is currently selected or at the
     *            popup menu location, or null = no element selected and no
     *            element at the popup menu location
     */
    void enableZValueActions(IGuiElement element);

    /**
     * Callback for the {@link IActionManager}
     * 
     * @return the minimum z value from the current {@link IGuiModel}
     */
    int getCurrentMinZValue();

    /**
     * Callback for the {@link IActionManager}
     * 
     * @return the maximum z value from the current {@link IGuiModel}
     */
    int getCurrentMaxZValue();

    /**
     * Enables or disables all {@link AbstractAction} that depend on the number
     * of selected {@link IGuiElement}.<BR>
     * <BR>
     * Note: Callback for the {@link IGuiModelController}, passes the request to
     * the {@link ActionManager}.
     * 
     * @param selected
     *            The {@link List} of selected {@link IGuiElement}
     */
    void enableActionsForSelectedElements(List<IGuiElement> selected);

}
