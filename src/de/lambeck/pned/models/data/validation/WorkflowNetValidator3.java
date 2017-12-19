package de.lambeck.pned.models.data.validation;

import java.util.ArrayList;

import de.lambeck.pned.elements.data.IDataArc;
import de.lambeck.pned.elements.data.IDataElement;

/**
 * Implements a validator for workflow nets.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public class WorkflowNetValidator3 implements IWorkflowNetValidator {

    private boolean debug = true;

    private IModel model;

    /**
     * Constructs a validator for the specified petri net model.
     * 
     * @param model
     */
    @SuppressWarnings("hiding")
    public WorkflowNetValidator3(IModel model) {
        super();
        this.model = model;
    }

    @Override
    public ArrayList<String> getInvalidProperties() {
        ArrayList<String> invProps = new ArrayList<String>();

        invProps.addAll(getStartPlacesErrors());
        invProps.addAll(getEndPlacesErrors());

        invProps.addAll(getArcErrors());

        // TODO All nodes between start and end place?

        return invProps;
    }

    private ArrayList<String> getStartPlacesErrors() {
        ArrayList<String> invStartProps = new ArrayList<String>();

        /*
         * Exactly 1 start place?
         */
        int allStartPlacesCount = getAllStartPlacesCount();
        switch (allStartPlacesCount) {
        case 0:
            invStartProps.add("No start place!");
            break;
        case 1:
            if (debug)
                System.out.println("Exactly 1 start place: OK");
            break;
        default:
            invStartProps.add(allStartPlacesCount + " start places!");
            break;
        }

        return invStartProps;
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

    private ArrayList<String> getEndPlacesErrors() {
        ArrayList<String> invEndProps = new ArrayList<String>();

        /*
         * Exactly 1 end place?
         */
        int allEndPlacesCount = getAllEndPlacesCount();
        switch (allEndPlacesCount) {
        case 0:
            invEndProps.add("No end place!");
            break;
        case 1:
            if (debug)
                System.out.println("Exactly 1 end place: OK");
            break;
        default:
            invEndProps.add(allEndPlacesCount + " end places!");
            break;
        }

        return invEndProps;
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

    private ArrayList<String> getArcErrors() {
        ArrayList<String> invArcProps = new ArrayList<String>();

        /*
         * No lonely arcs?
         */
        int allLonelyArcsCount = getAllLonelyArcsCount();
        switch (allLonelyArcsCount) {
        case 0:
            if (debug)
                System.out.println("No lonely arcs: OK");
            break;
        default:
            invArcProps.add(allLonelyArcsCount + " lonely arcs!");
            break;
        }

        return invArcProps;
    }

    private int getAllLonelyArcsCount() {
        if (model.getElements() == null) { return 0; }

        int count = 0;
        for (IDataElement test : model.getElements()) {
            if (test instanceof IDataArc) {
                if (((IDataArc) test).getAllPredCount() == 0 || ((IDataArc) test).getAllSuccCount() == 0) {
                    count++;
                }
            }
        }
        return count;
    }

}
