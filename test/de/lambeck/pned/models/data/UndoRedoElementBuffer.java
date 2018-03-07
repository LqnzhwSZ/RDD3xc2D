package de.lambeck.pned.models.data;

// TODO Not necessary if we clone the whole model via DeepCopy.

import java.util.ArrayList;
import java.util.List;

import de.lambeck.pned.elements.data.IDataElement;

/**
 * Can store the {@link List} of {@link IDataElement} from the current
 * {@link IDataModel} for Undo/Redo operations.<BR>
 * <BR>
 * Note: Visibility is limited to this package to prevent external classes from
 * interfering with Undo/Redo operations because the
 * {@link IDataModelController} is intended to write elements back into the
 * model without further plausibility checks.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
class UndoRedoElementBuffer implements IUndoableModel {

    /**
     * List of all elements in this model
     */
    private List<IDataElement> elements = new ArrayList<IDataElement>();

    /* Constructor */

    /**
     * Constructs a new {@link UndoRedoElementBuffer} with empty lists of
     * elements.
     */
    public UndoRedoElementBuffer() {
        super();
    }

    /* Getter */

    @Override
    public List<IDataElement> getElements() {
        List<IDataElement> copy = new ArrayList<IDataElement>(this.elements);
        return copy;
    }

    /* Setter */

    @Override
    public void clear() {
        this.elements.clear();
    }

    @Override
    public void setElements(List<IDataElement> newElements) {
        List<IDataElement> copy = new ArrayList<IDataElement>(newElements);
        this.elements = copy;
    }

}
