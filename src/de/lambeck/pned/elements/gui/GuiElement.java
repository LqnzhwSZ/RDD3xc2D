package de.lambeck.pned.elements.gui;

import java.awt.Color;
import java.io.Serializable;

import de.lambeck.pned.models.gui.DrawPanel;
import de.lambeck.pned.util.ObjectCloner;

/**
 * Superclass GuiElement implements the common members for all elements.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public abstract class GuiElement implements IGuiElement, Serializable {

    /**
     * Generated serial version ID (necessary for the {@link ObjectCloner})
     */
    private static final long serialVersionUID = -2680605249299184431L;

    /* Attributes for interface IGuiElement */

    /** The ID of this element (Must not be empty!) */
    protected String id = "";

    /* Attributes for interface IHasZValue */

    /** The z value ("height level") of this shape in the {@link DrawPanel} */
    protected int zValue;

    /* Attributes for interface ISelectable */

    /** Stores if this element was selected in the GUI */
    protected boolean selected = false;

    /* Optical appearance */

    /** The standard line width */
    protected int stdLineWidth = 1;

    /** The standard line color */
    protected Color stdLineColor = Color.BLACK;

    /* Constructor */

    /**
     * Constructs this element with the specified ID and z value (height level).
     * 
     * @param id
     *            The id of this element
     * @param zValue
     *            The height level (Must not be empty!)
     */
    @SuppressWarnings("hiding")
    public GuiElement(String id, int zValue) {
        if (id == null || id.equals(""))
            System.err.println("Invalid empty ID!");

        this.id = id;
        setZValue(zValue);
    }

    /* Getter and Setter */

    /* Methods for interface IGuiElement */

    @Override
    public String getId() {
        return this.id;
    }

    /* Methods for interface IHasZValue */

    @Override
    public int getZValue() {
        return this.zValue;
    }

    @Override
    public void setZValue(int newZValue) {
        this.zValue = newZValue;
    }

    /* Methods for interface ISelectable */

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
