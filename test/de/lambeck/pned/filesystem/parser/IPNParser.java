package de.lambeck.pned.filesystem.parser;

import java.io.File;

import de.lambeck.pned.models.data.IDataModel;

/**
 * Parser for conversion of read-in (Strings) to petri net elements and vice
 * versa.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IPNParser {

    /**
     * Reads the data for a Petri net data model from a file.
     * 
     * @param file
     *            The file to read
     * @param model
     *            The data model
     */
    void parseFromFile(File file, IDataModel model);

    /**
     * Writes the data from a Petri net data model to a file.
     * 
     * @param model
     *            The data model
     * @param file
     *            The file to write
     */
    void parseToFile(IDataModel model, File file);

}
