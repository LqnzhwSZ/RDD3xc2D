package de.lambeck.pned.models.data.validation;

import java.awt.Point;
import java.util.ArrayList;

import de.lambeck.pned.elements.data.DataArc;
import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.data.DataTransition;
import de.lambeck.pned.elements.data.EPlaceMarking;
import de.lambeck.pned.exceptions.PNDuplicateAddedException;
import de.lambeck.pned.exceptions.PNElementException;
import de.lambeck.pned.models.data.validation.WorkflowNetValidator1;
import de.lambeck.pned.models.data.validation.WorkflowNetValidator2;

public class WorkflowNetValidatorTest01 {

    public static void main(String[] args) throws PNElementException, PNDuplicateAddedException {
        /*
         * Create Petri net elements
         */
        DataPlace place01 = new DataPlace(new Point(100, 100), EPlaceMarking.ZERO);
        DataPlace place02 = new DataPlace(new Point(500, 100), EPlaceMarking.ZERO);

        DataTransition transition01 = new DataTransition(new Point(300, 100));

        DataArc arc01 = new DataArc(place01, transition01);
        DataArc arc02 = new DataArc(transition01, place02);

        /*
         * Connect them!
         */
        place01.addSucc(arc01);
        transition01.addPred(arc01);

        transition01.addSucc(arc02);
        place01.addPred(arc02);

        /*
         * Add all to the model
         */
        Model model = new Model();

        model.addElement(place01);
        model.addElement(transition01);
        model.addElement(arc01);
        model.addElement(place02);
        model.addElement(arc02);

        /*
         * Validate the model
         */

        // WorkflowNetValidator validator = new WorkflowNetValidator();
        // ArrayList<String> invProps = validator.getInvalidProperties(model);

        ArrayList<String> invProps;

        WorkflowNetValidator1 validator1 = new WorkflowNetValidator1(model);
        invProps = validator1.getInvalidProperties();
        if (invProps.size() == 0) {
            System.out.println("This workflow net is valid.");
        } else {
            for (String msg : invProps) {
                System.out.println("Invalid property: '" + msg + "'");
            }
        }

        WorkflowNetValidator2 validator2 = new WorkflowNetValidator2(model);
        invProps = validator2.getInvalidProperties();
        if (invProps.size() == 0) {
            System.out.println("This workflow net is valid.");
        } else {
            for (String msg : invProps) {
                System.out.println("Invalid property: '" + msg + "'");
            }
        }

        /*
         * Call the validator again to check if the result is reset before
         * producing a new result!
         */
        invProps = validator2.getInvalidProperties();
        if (invProps.size() == 0) {
            System.out.println("This workflow net is valid.");
        } else {
            for (String msg : invProps) {
                System.out.println("Invalid property: '" + msg + "'");
            }
        }

    }

}
