package de.lambeck.pned.models;

import java.awt.Point;

import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.elements.IArc;
import de.lambeck.pned.elements.IPlace;
import de.lambeck.pned.elements.ITransition;
import de.lambeck.pned.exceptions.PNElementCreationException;
import de.lambeck.pned.exceptions.PNNoSuchElementException;

/**
 * Interface for models representing a Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IModel {

    /* Getter and Setter */

    /**
     * Returns the full name of this model. This should be the canonical
     * (unique) path name of the file.
     * 
     * @return The full name
     */
    String getModelName();

    /* setModelName(String s) is part of interface IModelRename! */
    // void setModelName(String s);

    /**
     * Returns the display name of this model.<BR>
     * <BR>
     * Intended use: name of the tab
     * 
     * @return The display name
     */
    String getDisplayName();

    /* setDisplayName(String s) is part of interface IModelRename! */
    // void setDisplayName(String s);

    /**
     * Returns true if this model has been modified, otherwise false.
     * 
     * @return The current modified state of the model
     */
    boolean isModified();

    /**
     * Sets the "modified" attribute of this model. Use this method when
     * changing persistent data (e.g. name and position of nodes).<BR>
     * <BR>
     * Note: It should also be used by the model controller to reset the
     * "modified" attribute after loading a PNML file. Otherwise: The model
     * would be marked as "modified" before the user has modified anything.
     * 
     * @param b
     *            The new state
     */
    void setModified(boolean b);

    /* Methods for adding, modify and removal of elements */

    /* Add elements */

    /**
     * Adds an {@link IPlace} to this {@link IModel}.<BR>
     * <BR>
     * <B>Intended use:</B> adding a place after a <B>GUI event</B> when the new
     * place is without a name after creation.
     * 
     * @param id
     *            The ID of the place as Java {@link String}
     * @param initialTokens
     *            The initial tokens count of the place as {@link EPlaceToken}
     * @param position
     *            The position (center) of the place as Java {@link Point}
     */
    void addPlace(String id, EPlaceToken initialTokens, Point position);

    /**
     * Adds an {@link IPlace} to this {@link IModel}, specifying another
     * parameter <B>name</B>.<BR>
     * <BR>
     * <B>Intended use:</B> adding a place after <B>reading from a PNML file</B>
     * because these places may have a name.
     * 
     * @param id
     *            The ID of the place as Java {@link String}
     * @param name
     *            The name of the place as Java {@link String}
     * @param initialTokens
     *            The initial tokens count of the place as {@link EPlaceToken}
     * @param position
     *            The position (center) of the place as Java {@link Point}
     */
    void addPlace(String id, String name, EPlaceToken initialTokens, Point position);

    /**
     * Adds an {@link ITransition} to this {@link IModel}.<BR>
     * <BR>
     * <B>Intended use:</B> adding a transition after a <B>GUI event</B> when
     * the new transition is without a name after creation.
     * 
     * @param id
     *            The ID of the transition as Java {@link String}
     * @param position
     *            The position (center) of the transition as Java {@link Point}
     */
    void addTransition(String id, Point position);

    /**
     * Adds an {@link ITransition} to this {@link IModel}, specifying another
     * parameter <B>name</B>.<BR>
     * <BR>
     * <B>Intended use:</B> adding a transition after <B>reading from a PNML
     * file</B> because these transitions may have a name.
     * 
     * @param id
     *            The ID of the transition as Java {@link String}
     * @param name
     *            The name of the transition as Java {@link String}
     * @param position
     *            The position (center) of the transition as Java {@link Point}
     */
    void addTransition(String id, String name, Point position);

    /**
     * Adds an {@link IArc} to this {@link IModel}.<BR>
     * <BR>
     * Note: This method should be the same for GUI events and reading from a
     * PNML file because arcs will have all 3 attributes in both cases.
     * 
     * @param id
     *            The ID of the arc as Java {@link String}
     * @param sourceId
     *            The ID of the source ({@link IPlace} or {@link ITransition})
     *            as Java {@link String}
     * @param targetId
     *            The ID of the target ({@link IPlace} or {@link ITransition})
     *            as Java {@link String}
     * @throws PNElementCreationException
     *             if this {@link IArc} could not be created.
     */
    void addArc(String id, String sourceId, String targetId) throws PNElementCreationException;

    /* Remove methods for elements */

    /**
     * Removes the specified element from this model.
     *
     * @param id
     *            The id of the element
     * @throws PNNoSuchElementException
     *             If this model has no element with the specified id
     */
    void removeElement(String id) throws PNNoSuchElementException;

    /**
     * Removes all elements from this model.
     */
    void clear();

}
