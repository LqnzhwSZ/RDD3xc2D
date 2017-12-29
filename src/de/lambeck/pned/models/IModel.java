package de.lambeck.pned.models;

import java.awt.Point;
import java.util.NoSuchElementException;

import de.lambeck.pned.elements.EPlaceToken;

/**
 * Interface for models representing a Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IModel {

    /*
     * Setter and Getter
     */

    /**
     * Returns the full name of this model. This should be the canonical
     * (unique) path name of the file.
     * 
     * @return The full name
     */
    String getModelName();

    /*
     * setModelName(String s) is part of interface IModelRename!
     */
    // void setModelName(String s);

    /**
     * Returns the display name of this model.
     * 
     * Intended use: name of the tab
     * 
     * @return The display name
     */
    String getDisplayName();

    /*
     * setDisplayName(String s) is part of interface IModelRename!
     */
    // void setDisplayName(String s);

    /**
     * Returns true if this model has been modified, otherwise false.
     * 
     * @return The current modified state of the model
     */
    boolean isModified();

    /**
     * Sets the "modified" attribute of this model. Use this method when
     * changing persistent data (e.g. name and position of nodes).
     * 
     * Note: It should also be used by the model controller to reset the
     * "modified" attribute after loading a PNML file. Otherwise: The model
     * would be marked as "modified" before the user has modified anything.
     * 
     * @param b
     *            The new state
     */
    void setModified(boolean b);

    /*
     * Methods for adding, modify and removal of elements
     */

    /*
     * Add elements
     */

    /**
     * Adds a place to this model.
     * 
     * Intended use: adding a place after a GUI event when the new place is
     * without a name after creation.
     * 
     * @param id
     *            The ID of the place
     * @param initialTokens
     *            The initial tokens count of the place
     * @param position
     *            The position (center) of the place
     */
    void addPlace(String id, EPlaceToken initialTokens, Point position);

    /**
     * Adds a place to this model.
     * 
     * Note: Adds an additional parameter name to the other method
     * addPlace(String id, EPlaceTokens initialTokens, Point position)
     * 
     * Intended use: adding a place after reading from a PNML file because these
     * places may have a name.
     * 
     * @param id
     *            The ID of the place
     * @param name
     *            The name of the place
     * @param initialTokens
     *            The initial tokens count of the place
     * @param position
     *            The position (center) of the place
     */
    void addPlace(String id, String name, EPlaceToken initialTokens, Point position);

    /**
     * Adds a transition to this model.
     * 
     * Intended use: adding a transition after a GUI event when the new
     * transition is without a name after creation.
     * 
     * @param id
     *            The ID of the transition
     * @param position
     *            The position (center) of the transition
     */
    void addTransition(String id, Point position);

    /**
     * Adds a transition to this model.
     * 
     * Note: Adds an additional parameter name to the other method
     * addTransition(String id, Point position)
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
    void addTransition(String id, String name, Point position);

    /**
     * Adds an arc to this model.
     * 
     * Note: This method should be the same for GUI events and reading from a
     * PNML file because arcs will have all 3 attributes in both cases.
     * 
     * @param id
     *            The id of the arc
     * @param sourceId
     *            The id of the source (Place or Transition)
     * @param targetId
     *            The id of the target (Place or Transition)
     */
    void addArc(String id, String sourceId, String targetId);

    /*
     * Remove methods for elements
     */

    /**
     * Removes the specified element from this model.
     *
     * @param id
     *            The id of the element
     * @throws NoSuchElementException
     *             if element does not exist
     */
    void removeElement(String id) throws NoSuchElementException;

    /**
     * Removes all elements from this model.
     */
    void clear();

}
