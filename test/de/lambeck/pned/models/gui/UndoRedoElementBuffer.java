package de.lambeck.pned.models.gui;

// TODO Not necessary if we clone the whole model via DeepCopy.

import java.util.ArrayList;
import java.util.List;

import de.lambeck.pned.elements.gui.IGuiElement;

/**
 * Can store the {@link List} of {@link IGuiElement} from the current
 * {@link IGuiModel} for Undo/Redo operations.<BR>
 * <BR>
 * Note: Visibility is limited to this package to prevent external classes from
 * interfering with Undo/Redo operations because the {@link IGuiModelController}
 * is intended to write elements back into the model without further
 * plausibility checks.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
class UndoRedoElementBuffer implements IUndoableModel {

    /**
     * List of all elements in this model
     */
    private List<IGuiElement> elements = new ArrayList<IGuiElement>();

    /**
     * List of all elements selected by the user.
     */
    private List<IGuiElement> selected = new ArrayList<IGuiElement>();

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
    public List<IGuiElement> getElements() {
        List<IGuiElement> copy = new ArrayList<IGuiElement>(this.elements);
        return copy;
    }

    @Override
    public List<IGuiElement> getSelectedElements() {
        List<IGuiElement> copy = new ArrayList<IGuiElement>(this.selected);
        return copy;
    }

    /* Setter */

    @Override
    public void clear() {
        this.elements.clear();
        this.selected.clear();
    }

    @Override
    public void setElements(List<IGuiElement> newElements) {
        List<IGuiElement> copy = new ArrayList<IGuiElement>(newElements);
        this.elements = copy;
    }

    @Override
    public void setSelectedElements(List<IGuiElement> newSelected) {
        List<IGuiElement> copy = new ArrayList<IGuiElement>(newSelected);
        this.selected = copy;
    }

}
