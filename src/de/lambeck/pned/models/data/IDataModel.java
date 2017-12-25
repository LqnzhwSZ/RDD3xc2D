package de.lambeck.pned.models.data;

import java.util.List;
import java.util.NoSuchElementException;

import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.models.IModel;

/**
 * Sub type of IModel for data models (with all persistent information loaded
 * from or saved to files).
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

    /**
     * The check state indicates, whether the model needs checking or not. This
     * function indicates the current state
     * 
     * @return false = model needs checking, true = model already checked
     * 
     */
    public boolean isModelChecked();

    /**
     * The check state indicates, whether the model needs checking or not. This
     * method will set this state. Any changes to the model will set this state
     * to false.
     * 
     * @param b
     *            new model check state
     */
    public void setModelChecked(boolean b);

    /**
     * The validity state indicates, whether the model is valid or not. This
     * function indicates the current state. Any changes to the model will set
     * this state to false.
     * 
     * @return false = model is invalid / has errors, true = model is valid
     * 
     */
    public boolean isModelValid();

    /**
     * The validity state indicates, whether the model is valid or not. This
     * method will set this state.
     * 
     * @param b
     *            new model check state
     */
    public void setModelValidity(boolean b);

    /*
     * Methods for adding, modify and removal of elements
     */

}
