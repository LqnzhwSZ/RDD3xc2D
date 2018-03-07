package de.lambeck.pned.elements.data;

import java.io.Serializable;

import de.lambeck.pned.util.ObjectCloner;

/**
 * Superclass DataElement implements the common members for all elements.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public abstract class DataElement implements IDataElement, Serializable {

    /**
     * Generated serial version ID (necessary for the {@link ObjectCloner})
     */
    private static final long serialVersionUID = -983982676619398115L;

    /* Attributes for interface IDataElement */

    /** The ID of this element (Must not be empty!) */
    protected String id = "";

    /**
     * Constructs this element with the specified ID.
     * 
     * @param id
     *            The id of this element (Must not be empty!)
     */
    @SuppressWarnings("hiding")
    public DataElement(String id) {
        if (id == null || id.equals(""))
            System.err.println("Invalid empty ID!");

        this.id = id;
    }

    /* Getter and Setter */

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        String returnString = "DataElement [id=" + id + "]";
        return returnString;
    }

}
