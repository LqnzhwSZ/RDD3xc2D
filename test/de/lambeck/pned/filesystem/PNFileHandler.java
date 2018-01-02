package de.lambeck.pned.filesystem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements PNFileHandler.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class PNFileHandler implements IPNFileHandler {

    private static boolean debug = true;

    @SuppressWarnings("javadoc")
    public PNFileHandler() {
        // Empty
    }

    @Override
    public List<String> readFromFile(File file) {
        if (debug) {
            System.out.println("PNFileHandler.readFromFile()");
        }

        List<String> content = new ArrayList<String>();
        Reader fileReader = null;
        BufferedReader bufferedReader = null;

        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();
            while (line != null) {
                content.add(line);
                line = bufferedReader.readLine();
            }

            bufferedReader.close();
            fileReader.close();

        } catch (IOException e) {
            System.err.println("IOException for file: " + file.getPath());
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
                // Ignore
            }
        }

        return content;
    }

    @Override
    public void writeToFile(List<String> content, File file) {
        if (debug) {
            System.out.println("PNFileHandler.writeToFile()");
        }

        // TODO Auto-generated method stub

    }

}
