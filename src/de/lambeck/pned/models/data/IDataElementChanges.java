package de.lambeck.pned.models.data;

import java.awt.Point;
import java.util.List;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.elements.data.IDataArc;
import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.elements.data.IDataPlace;
import de.lambeck.pned.elements.data.IDataTransition;
import de.lambeck.pned.elements.gui.IGuiArc;
import de.lambeck.pned.elements.gui.IGuiNode;
import de.lambeck.pned.elements.gui.IGuiPlace;
import de.lambeck.pned.elements.gui.IGuiTransition;
import de.lambeck.pned.models.gui.IGuiModel;
import de.lambeck.pned.models.gui.IGuiModelController;

/**
 * Interface to inform the {@link ApplicationController} about changes to an
 * {@link IDataElement} in an {@link IDataModel}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IDataElementChanges {

    /**
     * Callback for the {@link IDataModelController} to get the
     * {@link IGuiModelController} up-to-date after adding a {@link IDataPlace}.
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
    void placeAddedToCurrentDataModel(String id, String name, EPlaceToken initialTokens, Point position);

    /**
     * Callback for the {@link IDataModelController} to get the
     * {@link IGuiModelController} up-to-date after adding a
     * {@link IDataTransition}.
     * 
     * @param id
     *            The ID of the added transition
     * @param name
     *            The name of the added transition
     * @param position
     *            The position of the added transition
     */
    void transitionAddedToCurrentDataModel(String id, String name, Point position);

    /**
     * Callback for the {@link IDataModelController} to get the
     * {@link IGuiModelController} up-to-date after adding a {@link IDataArc}.
     * 
     * @param id
     *            The ID of the added arc
     * @param sourceId
     *            The source ID for the added arc
     * @param targetId
     *            The target ID for the added arc
     */
    void arcAddedToCurrentDataModel(String id, String sourceId, String targetId);

    /**
     * Handles the {@link IDataModelController} request to remove an
     * {@link IGuiArc} from the {@link IGuiModel}.
     * 
     * @param arcId
     *            The ID of the removed {@link IDataArc}
     */
    void dataArcRemoved(String arcId);

    /* Validation events */

    /**
     * Handles the {@link IDataModelController} request to reset all start
     * places on the draw panel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void resetAllGuiStartPlaces(String modelName);

    /**
     * Handles the {@link IDataModelController} request to reset all end places
     * on the draw panel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void resetAllGuiEndPlaces(String modelName);

    /**
     * Handles the {@link IDataModelController} request to update the start
     * place on the draw panel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placeId
     *            The id of the {@link IGuiPlace}
     * @param b
     *            True to set as start place; otherwise false
     */
    void setGuiStartPlace(String modelName, String placeId, boolean b);

    /**
     * Handles the {@link IDataModelController} request to update the start
     * place candidate on the draw panel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placeId
     *            The id of the {@link IGuiPlace}
     * @param b
     *            True to set as start place candidate; otherwise false
     */
    void setGuiStartPlaceCandidate(String modelName, String placeId, boolean b);

    /**
     * Handles the {@link IDataModelController} request to update the end place
     * on the draw panel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placeId
     *            The id of the {@link IGuiPlace}
     * @param b
     *            True to set as end place; otherwise false
     */
    void setGuiEndPlace(String modelName, String placeId, boolean b);

    /**
     * Handles the {@link IDataModelController} request to update the end place
     * candidate on the draw panel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placeId
     *            The id of the {@link IGuiPlace}
     * @param b
     *            True to set as end place candidate; otherwise false
     */
    void setGuiEndPlaceCandidate(String modelName, String placeId, boolean b);

    /**
     * Handles the {@link IDataModelController} request to update the status of
     * the specified GUI node.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param nodeId
     *            The id of the {@link IGuiNode}
     * @param b
     *            True = unreachable; False = can be reached from the start
     *            place and can reach the end place
     */
    void highlightUnreachableGuiNode(String modelName, String nodeId, boolean b);

    /**
     * Handles the {@link IDataModelController} request to remove the token from
     * all places in the specified GUI model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void removeAllGuiTokens(String modelName);

    /**
     * Handles the {@link IDataModelController} request to remove the token from
     * all specified places in the specified GUI model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placesWithToken
     *            A {@link List} of type {@link String} with the IDs of the
     *            specified places
     */
    void removeGuiToken(String modelName, List<String> placesWithToken);

    /**
     * Handles the {@link IDataModelController} request to add a token to all
     * specified places in the specified GUI model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placesWithToken
     *            A {@link List} of type {@link String} with the IDs of the
     *            specified places
     */
    void addGuiToken(String modelName, List<String> placesWithToken);

    /**
     * Handles the {@link IDataModelController} request to reset the "enabled"
     * state on all transitions in the specified GUI model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void resetAllGuiTransitionsEnabledState(String modelName);

    /**
     * Handles the {@link IDataModelController} request to reset the "safe"
     * state on all transitions in the specified GUI model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void resetAllGuiTransitionsSafeState(String modelName);

    /**
     * Handles the {@link IDataModelController} request to set the "safe" state
     * on the specified transition in the specified GUI model to false.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param transitionId
     *            The id of the {@link IGuiTransition}
     */
    void setGuiTransitionUnsafe(String modelName, String transitionId);

    /**
     * Handles the {@link IDataModelController} request to set the "enabled"
     * state on the specified transition in the specified GUI model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param transitionId
     *            The id of the {@link IGuiTransition}
     */
    void setGuiTransitionEnabled(String modelName, String transitionId);

    /**
     * Handles the {@link IDataModelController} info that an
     * {@link IDataTransition} in the specified {@link IDataModel} has been
     * fired.<BR>
     * 
     * @param dataModel
     *            The specified {@link IDataModel}
     */
    void dataTransitionFired(IDataModel dataModel);

}
