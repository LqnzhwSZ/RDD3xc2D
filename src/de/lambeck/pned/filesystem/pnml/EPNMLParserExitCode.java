package de.lambeck.pned.filesystem.pnml;

/**
 * Possible exit codes for the pnml parser.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public enum EPNMLParserExitCode {
    /**
     * Binary: 00000 (No errors/no warnings)
     */
    ZERO(0),
    /**
     * Binary: 00001 (info)
     */
    FLAG_UNKNOWN_ELEMENT(1),
    /**
     * Binary: 00010 (info)
     */
    FLAG_UNKNOWN_VALUES(2),
    /**
     * Binary: 00100 (warning)
     */
    FLAG_MISSING_VALUES(4),
    /**
     * Binary: 01000 (warning)
     */
    FLAG_INVALID_VALUES(8),
    /**
     * Binary: 10000 (warning)
     */
    FLAG_ERROR_READING_FILE(16);

    private int value;

    private EPNMLParserExitCode(final int initValue) {
        this.value = initValue;
    }

    /**
     * @return the value of the enum element
     */
    public int getValue() {
        return this.value;
    }

}
