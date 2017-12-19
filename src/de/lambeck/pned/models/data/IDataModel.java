package de.lambeck.pned.models.data;

import java.util.List;
import java.util.NoSuchElementException;

import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.models.IModel;

/**
 * Sub type of IModel for data models. This means models with all persistent
 * information (loaded from or saved to files).
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IDataModel extends IModel {

    /*
     * Getter and Setter
     */

    /**
     * Returns a list with all elements in this model.
     * 
     * @return Elements of the petri net
     */
    List<IDataElement> getElements();

    /**
     * Returns the {@link IDataElement} with the specified id.
     * 
     * @param id
     *            The id to search for
     * @return The element if found
     * @throws NoSuchElementException
     *             if element was not found
     */
    IDataElement getElementById(String id) throws NoSuchElementException;

    /*
     * Methods for adding, modify and removal of elements
     */

}
