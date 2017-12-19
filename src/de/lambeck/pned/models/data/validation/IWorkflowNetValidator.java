package de.lambeck.pned.models.data.validation;

import java.util.ArrayList;

/**
 * Interface for a validator that can check if a workflow net is valid.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public interface IWorkflowNetValidator {

    // ArrayList<String> getInvalidProperties(IModel model);

    /**
     * Returns an ArrayList of Strings with all properties that makes the
     * specified Petri net an invalid workflow net.
     * 
     * Returns an empty ArrayList if the workflow net is valid.
     * 
     * @param model
     *            of a Petri net
     * @return ArrayList of Strings with all invalid properties
     */
    ArrayList<String> getInvalidProperties();

}
