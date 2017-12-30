package de.lambeck.pned.models.data;

import java.awt.Point;
import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

import de.lambeck.pned.application.IInfo_Status;
import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.data.IDataNode;
import de.lambeck.pned.models.data.validation.AllNodesOnPathsValidator;
import de.lambeck.pned.models.data.validation.EndPlacesValidator;
import de.lambeck.pned.models.data.validation.IValidationMsgPanel;
import de.lambeck.pned.models.data.validation.StartPlacesValidator;

/**
 * Interface for controllers for data models representing a Petri net. This
 * means models with all persistent information (loaded from/saved to files).
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IDataModelController extends IInfo_Status {

    /**
     * Adds a data model for a non-existing file.
     * 
     * Note: This is intended to be used to add new models which are not coming
     * from a PNML file (e.g. "Untitled1", "Untitled2"... or "New1", "New2"...)
     * 
     * @param modelName
     *            The full path name of the PNML file
     * @param displayName
     *            The title of the tab (= the file name)
     */
    void addDataModel(String modelName, String displayName);

    /**
     * Adds a data model for an existing file.
     * 
     * Exit codes:
     * 
     * - ExitCode.OPERATION_SUCCESSFUL: OK
     * 
     * - ExitCode.OPERATION_FAILED: Error: Could not open the file!
     * 
     * - ExitCode.OPERATION_CANCELLED: Error: Data model not accepted!
     * 
     * @param pnmlFile
     *            The file to use for read-in.
     * @return The exit code
     */
    int addDataModel(File pnmlFile);

    /**
     * Checks if the specified data model has been modified.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return True if the model has been modified; otherwise false
     */
    boolean isModifiedDataModel(String modelName);

    /**
     * Resets the "modified" state of the specified model.
     * 
     * Note: Use this method after loading a PNML file because the model has not
     * been changed by the user after adding elements from the PNML file only.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void resetModifiedDataModel(String modelName);

    /**
     * Removes the specified data model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void removeDataModel(String modelName);

    /**
     * Renames the specified {@link IDataModel}. (Use this in case the user has
     * saved the file under a new file name.)
     * 
     * @param model
     *            The {@link IDataModel}
     * @param newModelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param newDisplayName
     *            The title of the tab (= the file name)
     */
    void renameDataModel(IDataModel model, String newModelName, String newDisplayName);

    /**
     * Returns the specified {@link IDataModel}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return The specified data model
     */
    IDataModel getDataModel(String modelName);

    /**
     * Returns the current (active) {@link IDataModel} of the
     * {@link DataModelController}.
     * 
     * @return The {@link DataModel}
     */
    IDataModel getCurrentModel();

    /**
     * Sets the specified {@link IDataModel} as current (active) model of the
     * {@link DataModelController}.
     * 
     * @param model
     */
    void setCurrentModel(IDataModel model);

    /**
     * Returns the validation message panel for a file name.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return The {@link IValidationMsgPanel}
     */
    IValidationMsgPanel getValidationMessagePanel(String modelName);

    /**
     * Returns a list of data models which have been modified and need to be
     * saved.
     * 
     * Note: The model names (String) because they have to match with the names
     * of the open files.
     * 
     * @return List of modified data models
     */
    List<String> getModifiedDataModels();

    /*
     * Methods for adding, modify and removal of elements (and callbacks for
     * updates between data and GUI model controller)
     */

    /*
     * Add elements
     */

    /**
     * Adds a place to the current data model.
     * 
     * Intended use: adding a place after a GUI event when the new place is
     * without a name after creation.
     * 
     * @param id
     *            The ID of the place
     * @param initialTokens
     *            The initial tokens count of this place
     * @param position
     *            The position (center) of the place
     */
    void addPlaceToCurrentDataModel(String id, EPlaceToken initialTokens, Point position);

    /**
     * Adds a place to the current data model.
     * 
     * Note: Adds an additional parameter name to the other method
     * addPlaceToCurrentModel(String id, EPlaceToken initialTokens, Point
     * position)
     * 
     * Intended use: adding a place after reading from a PNML file because these
     * places may have a name.
     * 
     * @param id
     *            The ID of the place
     * @param name
     *            The name of the place
     * @param initialTokens
     *            The initial tokens count of this place
     * @param position
     *            The position (center) of the place
     */
    void addPlaceToCurrentDataModel(String id, String name, EPlaceToken initialTokens, Point position);

    /**
     * Adds a transition to the current data model.
     * 
     * Intended use: adding a transition after a GUI event when the new
     * transition is without a name after creation.
     * 
     * @param id
     *            The ID of the transition
     * @param position
     *            The position (center) of the transition
     */
    void addTransitionToCurrentDataModel(String id, Point position);

    /**
     * Adds a transition to the current data model.
     * 
     * Note: Adds an additional parameter name to the other method
     * addTransitionToCurrentModel(String id, Point position)
     * 
     * Intended use: adding a transition after reading from a PNML file because
     * these transitions may have a name.
     * 
     * @param id
     *            The ID of the transition
     * @param name
     *            The name of the transition
     * @param position
     *            The position (center) of the transition
     */
    void addTransitionToCurrentDataModel(String id, String name, Point position);

    /**
     * Adds an arc to the current data model.
     * 
     * Note: This method should be the same for GUI events and reading from a
     * PNML file because arcs will have all 3 attributes in either cases.
     * 
     * @param id
     *            The id of the arc
     * @param sourceId
     *            The id of the source (Place or Transition)
     * @param targetId
     *            The id of the target (Place or Transition)
     */
    void addArcToCurrentDataModel(String id, String sourceId, String targetId);

    /*
     * Modify methods for elements
     */

    /**
     * Handles the application controllers request to rename a node in the data
     * model. (After the node in the GUI was renamed.)
     *
     * @param nodeId
     *            The id of the node
     * @param newName
     *            The new name
     */
    void renameNode(String nodeId, String newName);

    /*
     * Remove methods for elements
     */

    /**
     * Handles the application controllers info to remove an element from the
     * data model.
     * 
     * @param elementId
     *            The id of the element
     */
    void removeDataElement(String elementId);

    /**
     * Removes the specified element from the current data model.
     *
     * @param id
     *            The id of the element
     * @throws NoSuchElementException
     *             if element does not exist
     */
    void removeElementFromCurrentDataModel(String id) throws NoSuchElementException;

    /**
     * Removes all elements from the current data model.
     */
    void clearCurrentDataModel();

    /*
     * Mouse events in the GUI
     */

    /**
     * Handles the application controllers request to update the position of a
     * node in the data model.
     * 
     * @param nodeId
     *            The id of the node
     * @param newPosition
     *            The new position
     */
    void moveNode(String nodeId, Point newPosition);

    /*
     * Validation events
     */

    /**
     * Callback for the {@link StartPlacesValidator} to reset all start places.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void resetAllDataStartPlaces(String modelName);

    /**
     * Callback for the {@link EndPlacesValidator} to reset all end places.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void resetAllDataEndPlaces(String modelName);

    /**
     * Callback for the {@link StartPlacesValidator} to set the specified
     * {@link DataPlace} as the real (unambiguous) start place.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placeId
     *            The id of the {@link DataPlace}
     * @param b
     *            True to set as the real (unambiguous) start place; otherwise
     *            false
     */
    void setDataStartPlace(String modelName, String placeId, boolean b);

    /**
     * Callback for the {@link StartPlacesValidator} to set the specified
     * {@link DataPlace} as a start place candidate.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placeId
     *            The id of the {@link DataPlace}
     * @param b
     *            True to set as a start place candidate; otherwise false
     */
    void setDataStartPlaceCandidate(String modelName, String placeId, boolean b);

    /**
     * Callback for the {@link EndPlacesValidator} to set the specified
     * {@link DataPlace} as the real (unambiguous) end place.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placeId
     *            The id of the {@link DataPlace}
     * @param b
     *            True to set as the real (unambiguous) end place; otherwise
     *            false
     */
    void setDataEndPlace(String modelName, String placeId, boolean b);

    /**
     * Callback for the {@link EndPlacesValidator} to set the specified
     * {@link DataPlace} as an end place candidate.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placeId
     *            The id of the {@link DataPlace}
     * @param b
     *            True to set as an end place candidate; otherwise false
     */
    void setDataEndPlaceCandidate(String modelName, String placeId, boolean b);

    /**
     * Callback for the {@link AllNodesOnPathsValidator} to highlight nodes that
     * cannot be reached from the start place or cannot reach the end place.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param nodeId
     *            The id of the {@link IDataNode}
     * @param b
     *            True = unreachable; False = can be reached from the start
     *            place and can reach the end place
     */
    void highlightUnreachableDataNode(String modelName, String nodeId, boolean b);

    /**
     * Removes the token from all {@link DataPlace} in the specified data model.
     * 
     * Intended use: After structural changes in the model
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void removeAllDataTokens(String modelName);

    /**
     * Adds a token to all specified {@link DataPlace} in the specified data
     * model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placesWithToken
     *            A {@link List} of type {@link String} with the IDs of the
     *            specified places
     */
    void addDataToken(String modelName, List<String> placesWithToken);

}
