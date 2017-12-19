package de.lambeck.pned.elements.data;

/**
 * Superclass DataElement implements the common members for all nodes.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public abstract class DataElement implements IDataElement {

    /*
     * Attributes for interface IDataElement
     */
    protected String id = "";

    /**
     * 
     * @param id
     *            The id of this element
     */
    @SuppressWarnings("hiding")
    public DataElement(String id) {
        this.id = id;
    }

    /*
     * Getter
     */

    @Override
    public String getId() {
        return this.id;
    }

}
