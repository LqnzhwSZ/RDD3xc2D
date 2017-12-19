package de.lambeck.pned.models.data.validation;

import java.util.ArrayList;

import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.models.IModel;

/**
 * Implements a validator for workflow nets.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class WorkflowNetValidator1 implements IWorkflowNetValidator {

    private IModel model;

    /**
     * Constructs a validator for the specified petri net model.
     * 
     * @param model
     */
    public WorkflowNetValidator1(IModel model) {
        super();
        this.model = model;
    }

    @Override
    public ArrayList<String> getInvalidProperties() {
        ArrayList<String> invProps = new ArrayList<String>();

        /*
         * Exactly 1 start place?
         */
        int allStartPlacesCount = getAllStartPlacesCount();
        switch (allStartPlacesCount) {
        case 0:
            invProps.add("No start place!");
            break;
        case 1:
            break;
        default:
            invProps.add(allStartPlacesCount + " start places!");
            break;
        }

        /*
         * Exactly 1 end place?
         */
        int allEndPlacesCount = getAllEndPlacesCount();
        switch (allEndPlacesCount) {
        case 0:
            invProps.add("No end place!");
            break;
        case 1:
            break;
        default:
            invProps.add(allEndPlacesCount + " end places!");
            break;
        }

        // TODO All nodes between start and end place?

        return invProps;
    }

    private int getAllStartPlacesCount() {
        if (model.getElements() == null) { return 0; }

        int count = 0;
        for (IDataElement test : model.getElements()) {
            if (test.getAllPredCount() == 0) {
                count++;
            }
        }
        return count;
    }

    private int getAllEndPlacesCount() {
        if (model.getElements() == null) { return 0; }

        int count = 0;
        for (IDataElement test : model.getElements()) {
            if (test.getAllSuccCount() == 0) {
                count++;
            }
        }
        return count;
    }

}
