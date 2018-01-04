package de.lambeck.pned.elements.gui;

import java.awt.Color;

import de.lambeck.pned.models.gui.DrawPanel;

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

    /** The id of this element */
    protected String id = "";

    /*
     * Attributes for interface IHasZValue
     */

    /** The z value ("height level") of this shape in the {@link DrawPanel} */
    protected int zValue;

    /*
     * Attributes for interface ISelectable
     */

    /** Stores if this element was selected in the GUI */
    protected boolean selected = false;

    /*
     * Optical appearance
     */

    /** The standard line width */
    protected int stdLineWidth = 1;

    /** The standard line color */
    protected Color stdLineColor = Color.BLACK;

    protected Double zoom = 1.0D;
    
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

	public void setZoom(Double zoom) {
		this.zoom = zoom;
	}

}
