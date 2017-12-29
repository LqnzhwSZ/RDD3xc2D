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
     * Adds parameter "revalidate" to setModified(boolean b) in {@link IModel}
     * because not all changes to the model change the structure of the workflow
     * net. (e.g. renaming a node)
     * 
     * Note: Use this method in the {@link IDataModelController} only because
     * some changes in the data model (e.g. firing a transition) must leave the
     * validation unchanged (because validators may reset the marking of the
     * Petri net to the initial marking)!
     * 
     * @param b
     *            The new state
     * @param revalidate
     *            Indicates if the changes require a revalidation of the model.
     */
    void setModified(boolean b, boolean revalidate);

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
    boolean isModelChecked();

    /**
     * The check state indicates, whether the model needs checking or not. This
     * method will set this state. Any changes to the model will set this state
     * to false.
     * 
     * @param b
     *            new model check state
     */
    void setModelChecked(boolean b);

    /**
     * The validity state indicates, whether the model is valid or not. This
     * function indicates the current state. Any changes to the model will set
     * this state to false.
     * 
     * @return false = model is invalid / has errors, true = model is valid
     * 
     */
    boolean isModelValid();

    /**
     * The validity state indicates, whether the model is valid or not. This
     * method will set this state.
     * 
     * @param b
     *            new model check state
     */
    void setModelValidity(boolean b);

    /*
     * Methods for adding, modify and removal of elements
     */

}
