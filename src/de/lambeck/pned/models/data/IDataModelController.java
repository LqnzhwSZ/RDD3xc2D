package de.lambeck.pned.models.data;

import java.awt.Point;
import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

import de.lambeck.pned.application.IInfo_Status;
import de.lambeck.pned.elements.data.EPlaceMarking;
import de.lambeck.pned.models.data.validation.IValidationMessagesPanel;

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
     * from a pnml file (e.g. "Untitled1", "Untitled2"... or "New1", "New2"...)
     * 
     * @param modelName
     *            The full path name of the pnml file
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
     * Removes the specified data model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
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
     *            name of the pnml file represented by this model.)
     * @param newDisplayName
     *            The title of the tab (= the file name)
     */
    void renameDataModel(IDataModel model, String newModelName, String newDisplayName);

    /**
     * Checks if the specified data model has been modified.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     * @return True if the model has been modified; otherwise false
     */
    boolean isModifiedDataModel(String modelName);

    /**
     * Resets the "modified" state of the specified model.
     * 
     * Note: Use this method after loading a pnml file because the model has not
     * been changed by the user after adding elements from the pnml file only.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     */
    void resetModifiedDataModel(String modelName);

    /**
     * Returns the specified {@link IDataModel}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
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
     *            name of the pnml file represented by this model.)
     * @return The {@link IValidationMessagesPanel}
     */
    IValidationMessagesPanel getValidationMessagePanel(String modelName);

    /**
     * Sets the specified {@link IValidationMessagesPanel} as current (active)
     * validation messages panel of the {@link DataModelController}.
     * 
     * @param validationMessagesPanel
     */
    void setCurrentValidationMessagesPanel(IValidationMessagesPanel validationMessagesPanel);

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

    /**
     * Adds a place to the current data model.
     * 
     * Intended use: adding a place after a GUI event when the new place is
     * without a name after creation.
     * 
     * @param id
     *            The ID of the place
     * @param initialMarking
     *            The initial marking
     * @param position
     *            The position (center) of the place
     */
    void addPlaceToCurrentDataModel(String id, EPlaceMarking initialMarking, Point position);

    /**
     * Adds a place to the current data model.
     * 
     * Note: Adds an additional parameter name to the other method
     * addPlaceToCurrentModel(String id, EPlaceMarking initialMarking, Point
     * position)
     * 
     * Intended use: adding a place after reading from a pnml file because these
     * places may have a name.
     * 
     * @param id
     *            The ID of the place
     * @param name
     *            The name of the place
     * @param initialMarking
     *            The initial marking
     * @param position
     *            The position (center) of the place
     */
    void addPlaceToCurrentDataModel(String id, String name, EPlaceMarking initialMarking, Point position);

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
     * Intended use: adding a transition after reading from a pnml file because
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
     * pnml file because arcs will have all 3 attributes in either cases.
     * 
     * @param id
     *            The id of the arc
     * @param sourceId
     *            The id of the source (Place or Transition)
     * @param targetId
     *            The id of the target (Place or Transition)
     */
    void addArcToCurrentDataModel(String id, String sourceId, String targetId);

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
     * Modify methods for elements
     */

    // /**
    // * Handles the application controllers info to update the marking of a
    // place
    // * in the data model.
    // *
    // * @param placeId
    // * The id of the place
    // * @param newMarking
    // * The new marking
    // */
    // void toggleMarking(String placeId, EPlaceMarking newMarking);

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
     * Handles the application controllers request to rename a node in the data
     * model. (After the node in the GUI was renamed.)
     *
     * @param nodeId
     *            The id of the node
     * @param newName
     *            The new name
     */
    void renameNode(String nodeId, String newName);

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

}