package de.lambeck.pned.models.data.validation;

import de.lambeck.pned.models.data.DataModel;
import de.lambeck.pned.models.data.IDataModel;

/**
 * Self test for {@link ValidationMsg}
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ValidationMsgTest {

    @SuppressWarnings("javadoc")
    public static void main(String[] args) {
        IDataModel model = new DataModel("/home/documents/test.pnml", "test.pnml", null);
        String message = "Test message";
        EValidationResultSeverity severity = EValidationResultSeverity.WARNING;

        ValidationMsg validationMessage = new ValidationMsg(model, message, severity);
        System.out.println(validationMessage);
    }

}
