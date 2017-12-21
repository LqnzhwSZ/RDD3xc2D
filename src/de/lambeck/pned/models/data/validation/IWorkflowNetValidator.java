package de.lambeck.pned.models.data.validation;

import java.util.ArrayList;

import de.lambeck.pned.models.data.IDataModel;

/**
 * Interface for a workflow net validator that can check the properties of a
 * workflow net.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public interface IWorkflowNetValidator {

    /**
     * Returns the full name of this validator. This should be the canonical
     * (unique) path name of the file.
     * 
     * @return The full name
     */
    String getModelName();

    /**
     * Sets the name of this validator. (Use this method to rename this
     * validator in case the user has saved the model under a new file name.)
     * 
     * @param s
     *            The new full name
     */
    void setModelName(String s);

    /*
     * Open a file
     */

    /*
     * When editing a file
     */

    /**
     * Starts the validation of this validators {@link IDataModel}.
     */
    void startValidation();

    /**
     * Restarts the validation of this validators {@link IDataModel}. Resets
     * former outputs on the {@link ValidationMessagesPanel}.
     * 
     * Note: Use this method after structural changes in the Petri net.
     */
    void restartValidation();

    /**
     * Returns the ID of the start place or an empty String if the Petri net has
     * no or more than 1 (invalid workflow net) start places.
     * 
     * @return The id of the (sole) start place; otherwise an empty String
     */
    String getStartPlaceId();

    /**
     * Returns the ID of the end place or an empty String if the Petri net has
     * no or more than 1 (invalid workflow net) end places.
     * 
     * @return The id of the (sole) start place; otherwise an empty String
     */
    String getEndPlaceId();

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
