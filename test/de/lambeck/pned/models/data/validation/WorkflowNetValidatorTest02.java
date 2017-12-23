package de.lambeck.pned.models.data.validation;

import java.awt.Point;
import java.util.ArrayList;

import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.data.EPlaceToken;
import de.lambeck.pned.exceptions.PNDuplicateAddedException;
import de.lambeck.pned.exceptions.PNElementException;
import de.lambeck.pned.models.data.validation.WorkflowNetValidator2;

public class WorkflowNetValidatorTest02 {

    public static void main(String[] args) throws PNElementException, PNDuplicateAddedException {
        /*
         * Create only 1 Petri net element
         */
        DataPlace place01 = new DataPlace(new Point(100, 100), EPlaceToken.ZERO);

        /*
         * Add all to the model
         */
        Model model = new Model();

        model.addElement(place01);

        /*
         * Validate the model
         */
        ArrayList<String> invProps;

        WorkflowNetValidator2 validator2 = new WorkflowNetValidator2(model);
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
