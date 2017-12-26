package de.lambeck.pned.models.data.validation;

@SuppressWarnings("javadoc")
public class ValidationResultSeverityTest {
    public static void main(String[] args) {
        System.out.println(EValidationResultSeverity.INFO.toPnedString());
        System.out.println(EValidationResultSeverity.DEBUG.toPnedString());
        System.out.println(EValidationResultSeverity.WARNING.toPnedString());
        System.out.println(EValidationResultSeverity.CRITICAL.toPnedString());
    }
}
