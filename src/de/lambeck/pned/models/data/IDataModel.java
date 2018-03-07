package de.lambeck.pned.models.data;

import java.util.List;

import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.elements.data.IDataNode;
import de.lambeck.pned.elements.data.IDataPlace;
import de.lambeck.pned.elements.data.IDataTransition;
import de.lambeck.pned.exceptions.PNNoSuchElementException;
import de.lambeck.pned.models.IModel;
import de.lambeck.pned.models.data.validation.IValidator;
import de.lambeck.pned.models.data.validation.InitialMarkingValidator;

/**
 * Sub type of {@link IModel} for data models (with all persistent information
 * loaded from or saved to PNML files).
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
// public interface IDataModel extends IModel, IUndoableModel {
public interface IDataModel extends IModel {

    /* Getter and Setter */

    // @Override
    /**
     * Returns a list with all {@link IDataElement} in this {@link IDataModel}.
     * 
     * @return all {@link IDataElement} as Java {@link List}
     */
    List<IDataElement> getElements();

    /**
     * Adds parameter "revalidate" to setModified(boolean b) in {@link IModel}
     * because not all changes to the model change the structure of the workflow
     * net. (e.g. renaming a node)<BR>
     * <BR>
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
     * Returns the {@link IDataElement} with the specified id.
     * 
     * @param id
     *            The id to search for
     * @return The {@link IDataElement} if found
     * @throws PNNoSuchElementException
     *             If this model has no element with the specified id
     */
    IDataElement getElementById(String id) throws PNNoSuchElementException;

    /**
     * Returns the {@link IDataNode} with the specified id.
     * 
     * @param id
     *            The id to search for
     * @return The {@link IDataNode} if found
     * @throws PNNoSuchElementException
     *             If this model has no node with the specified id
     */
    IDataNode getNodeById(String id) throws PNNoSuchElementException;

    /**
     * Returns the {@link IDataPlace} with the specified id.
     * 
     * @param id
     *            The id to search for
     * @return The {@link IDataPlace} if found
     * @throws PNNoSuchElementException
     *             If this model has no place with the specified id
     */
    IDataPlace getPlaceById(String id) throws PNNoSuchElementException;

    /**
     * Returns the {@link IDataTransition} with the specified id.
     * 
     * @param id
     *            The id to search for
     * @return The {@link IDataTransition} if found
     * @throws PNNoSuchElementException
     *             If this model has no transition with the specified id
     */
    IDataTransition getTransitionById(String id) throws PNNoSuchElementException;

    /* Methods for validation */

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
     * initial validation after reading from a PNML file.<BR>
     * <BR>
     * Note: This function is necessary for the {@link InitialMarkingValidator}
     * to allow to decide whether to reset the marking or not.
     * 
     * @return true = model was never checked before, false = model already was
     *         checked before
     */
    boolean isInitialModelCheck();

    /**
     * The check state indicates, whether the model needs checking or not. This
     * method will set this state.<BR>
     * <BR>
     * The {@link IDataModelController} has to invoke this method whenever
     * changes to this data model change the structure of the Petri net. (Not
     * simple changes like moving or renaming nodes.)<BR>
     * <BR>
     * Note: Parameter "removeInitialCheckState" allows to control which
     * validation counts as "initial validation" because "initial validation" is
     * abort condition for some implementations of {@link IValidator}. <BR>
     * <BR>
     * Example: Used in {@link InitialMarkingValidator} to avoid resetting the
     * initial marking when loading from a PNML file.
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

    /**
     * Checks whether this {@link IDataModel} is empty.
     * 
     * @return true = this {@link IDataModel} is empty; false = not empty
     */
    boolean isEmpty();

    /* Methods for adding, modify and removal of elements */

}
