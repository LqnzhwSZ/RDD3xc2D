package de.lambeck.pned.filesystem;

import java.io.File;
import java.util.List;

/**
 * Implements reading/writing from/to Petri net files.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IPNFileHandler {

    /**
     * Reads the specified file into a list of Strings.
     * 
     * @param file
     *            The file to read
     * @return The content of the file
     */
    List<String> readFromFile(File file);

    /**
     * Writes a list of Strings to the specified file.
     * 
     * @param content
     *            The content to write
     * @param file
     *            The file to write
     */
    void writeToFile(List<String> content, File file);

}
