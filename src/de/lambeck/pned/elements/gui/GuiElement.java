package de.lambeck.pned.elements.gui;

/**
 * Superclass GuiElement implements the common members for all nodes.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public abstract class GuiElement implements IGuiElement {

    /*
     * Attributes for interface IGuiElement
     */
    protected String id = "";

    /*
     * Attributes for interface IHasZValue
     */
    protected int zValue; // The "height level" of the shape in the DrawPanel

    /*
     * Attributes for interface ISelectable
     */
    protected boolean selected = false;

    /**
     * 
     * @param id
     *            The id of this element
     * @param zValue
     *            The height level
     */
    @SuppressWarnings("hiding")
    public GuiElement(String id, int zValue) {
        this.id = id;
        setZValue(zValue);
    }

    /*
     * Getter
     */

    /*
     * Methods for interface IGuiElement
     */

    @Override
    public String getId() {
        return this.id;
    }

    /*
     * Methods for interface IHasZValue
     */

    @Override
    public int getZValue() {
        return this.zValue;
    }

    @Override
    public void setZValue(int newZValue) {
        this.zValue = newZValue;
    }

    /*
     * Methods for interface ISelectable
     */

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    @SuppressWarnings("hiding")
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        String returnString = "GuiElement [id=" + id + ", zValue=" + getZValue() + ", selected=" + isSelected() + "]";
        return returnString;
    }

}
