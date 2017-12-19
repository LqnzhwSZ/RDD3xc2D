package de.lambeck.pned.filesystem.parser;

import java.io.File;
import java.util.List;

import de.lambeck.pned.application.IInfo_Status;
import de.lambeck.pned.filesystem.IPNFileHandler;
import de.lambeck.pned.filesystem.PNFileHandler;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;

/**
 * Implements IPNParser.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class PNParser implements IPNParser, IInfo_Status {

    private boolean debug = true;

    private IDataModelController dataModelController;

    /**
     * Constructs the PNParser with a reference to the data model controller to
     * be able to forward status messages.
     * 
     * @param controller
     *            The data model controller
     */
    public PNParser(IDataModelController controller) {
        this.dataModelController = controller;
    }

    @Override
    public void parseFromFile(File file, IDataModel model) {
        if (debug) {
            System.out.println("PNParser.parseFromFile()");
        }

        List<String> content = readContentFromFile(file);
        if (content == null)
            return;

        if (debug) {
            for (String row : content) {
                System.out.println(row);
            }
        }

        /*
         * TODO
         * https://docs.oracle.com/cd/B28359_01/appdev.111/b28394/adx_j_parser.
         * htm#ADXDK19142
         * 
         * https://docs.oracle.com/javase/tutorial/jaxp/sax/index.html
         * 
         * https://docs.oracle.com/javase/tutorial/jaxp/dom/when.html
         */

        /*
         * @formatter:off
         * 
         *  <?xml version="1.0" encoding="UTF-8"?>
         *  <pnml>
         *      <net>
         *          <place id="...">                            -> String
         *              <name>
         *                  <value>...</value>                  -> String
         *              </name>
         *              <initialMarking>
         *                  <token>
         *                      <value>...</value>              -> int
         *                  </token>
         *              </initialMarking>
         *              <graphics>
         *                  <position x="..." y="..."/>         -> int + int
         *              </graphics>
         *          </place>
         *          
         *          <transition id="...">                       -> String
         *              <name>
         *                  <value>...</value>                  -> String
         *              </name>
         *              <graphics>
         *                  <position x="..." y="..."/>         -> int + int
         *              </graphics>
         *          </transition>
         *          
         *          <arc id="..." source="..." target="..."/>   -> String
         *      </net>
         *  </pnml>
         * 
         * @formatter:on
         */
        boolean pnmlFileStart = false;

        for (String row : content) {
            if (row.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
                // System.out.println("A PNML file");
                pnmlFileStart = true;
            }
        }
    }

    /**
     * Returns the content of the specified file.
     * 
     * @param file
     *            The file
     * @return The content
     */
    private List<String> readContentFromFile(File file) {
        IPNFileHandler fileHandler = getFileHandler();
        if (fileHandler == null)
            return null;

        List<String> content = fileHandler.readFromFile(file);
        return content;
    }

    /**
     * Returns a file handler.
     * 
     * @return The file handler
     */
    private IPNFileHandler getFileHandler() {
        IPNFileHandler filehandler;
        filehandler = new PNFileHandler();
        return filehandler;
    }

    @Override
    public void parseToFile(IDataModel model, File file) {
        if (debug) {
            System.out.println("PNParser.parseToFile()");
        }

        // TODO Auto-generated method stub

    }

    @Override
    public void setInfo_Status(String s) {
        dataModelController.setInfo_Status(s);
    }

}
