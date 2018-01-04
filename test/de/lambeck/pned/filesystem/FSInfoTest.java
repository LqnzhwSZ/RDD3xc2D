package de.lambeck.pned.filesystem;

import java.io.File;
import java.io.IOException;

/**
 * Class for conversion of path names (e.g. into canonical path names) and
 * checks if files exist.
 * 
 * Note: canonical path names are unique and thus we can use them as identifiers
 * for the open files and models. (See:
 * https://docs.oracle.com/javase/8/docs/api/java/io/File.html#getCanonicalPath--)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class FSInfoTest extends FSInfo {

    /**
     * Self test...
     * 
     * Note: getCanonicalPath() can throw an IOException!
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String test1 = "C:\\TEMP\\tmp123\\..\\";
        File file1 = new File(test1);

        System.out.println(file1.getPath());
        System.out.println(file1.getAbsolutePath());
        System.out.println(file1.getCanonicalPath());
        System.out.println(getCanonicalPath(file1));
        System.out.println(getFileName(file1));

        System.out.println();

        String test2 = "C:\\TEMP\\tmp123\\tmp456\\..\\..\\";
        File file2 = new File(test2);

        System.out.println(file2.getPath());
        System.out.println(file2.getAbsolutePath());
        System.out.println(file2.getCanonicalPath());
        System.out.println(getCanonicalPath(file2));

        System.out.println();

        System.out.println(getCanonicalPath(test1));
        System.out.println(getCanonicalPath(test2));

        System.out.println();

        String test3 = "C:\\TEMP\\tmp123\\..\\Test.txt";
        File file3 = new File(test3);

        System.out.println(getFileName(file1));
        System.out.println(getFileName(file2));
        System.out.println(getFileName(file3));

        System.out.println();

        System.out.println(getFileName(test3));

        System.out.println();

        // String saveAsFullName = getSaveAsFullName(null, null, null);
        // System.out.println("saveAsFullName: " + saveAsFullName);
    }

}
