package de.lambeck.pned.elements.gui;

/**
 * Interface for elements that can be selected. (e.g. via mouse click)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface ISelectable {

    /**
     * Getter for the selected state of this node.
     * 
     * @return True if this node is selected, otherwise false
     */
    boolean isSelected();

    /**
     * Sets the "selected" state of this node.
     * 
     * @param selected
     *            The new state
     */
    void setSelected(boolean selected);

}
