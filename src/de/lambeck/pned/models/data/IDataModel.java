package de.lambeck.pned.models.data;

import java.util.List;
import java.util.NoSuchElementException;

import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.models.IModel;
import de.lambeck.pned.models.data.validation.InitialMarkingValidator;

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
     *            Indicates whether the changes require a revalidation of the
     *            model.
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
     * This function indicates whether the next validation of this model is the
     * initial validation after reading from a PNML file.
     * 
     * Note: This function is necessary for the {@link InitialMarkingValidator}
     * to allow to decide whether to reset the marking or not.
     * 
     * @return true = model was never checked before, false = model already was
     *         checked before
     */
    boolean isInitialModelCheck();

    /**
     * The check state indicates, whether the model needs checking or not. This
     * method will set this state. The {@link IDataModelController} has to
     * invoke this method whenever changes to this data model change the
     * structure of the Petri net. (Not simple changes like moving or renaming
     * nodes.)
     * 
     * Parameter "removeInitialCheckState" allows to control which validation
     * will count as "initial validation" since "initial validation" is abort
     * condition for some validators.
     * 
     * @param b
     *            new model check state
     * @param removeInitialCheckState
     *            True = remove "initial check" state from the model, false = do
     *            not change the "initial check" state
     */
    void setModelChecked(boolean b, boolean removeInitialCheckState);

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
