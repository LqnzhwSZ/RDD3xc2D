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

        System.out.println("\t*** file 1: " + file1.toString() + " ***");
        System.out.println();
        System.out.println("File.getPath():\t\t\t" + file1.getPath());
        System.out.println("File.getAbsolutePath():\t\t" + file1.getAbsolutePath());
        System.out.println("File.getCanonicalPath():\t" + file1.getCanonicalPath());
        System.out.println("FSInfo.getCanonicalPath(test1):\t" + getCanonicalPath(test1));
        System.out.println("FSInfo.getCanonicalPath(file1):\t" + getCanonicalPath(file1));
        System.out.println("FSInfo.getFileName(file1):\t" + getFileName(file1));

        System.out.println();

        String test2 = "C:\\TEMP\\tmp123\\tmp456\\..\\..\\";
        File file2 = new File(test2);

        System.out.println("\t*** file 2: " + file2.toString() + " ***");
        System.out.println();
        System.out.println("File.getPath():\t\t\t" + file2.getPath());
        System.out.println("File.getAbsolutePath():\t\t" + file2.getAbsolutePath());
        System.out.println("File.getCanonicalPath():\t" + file2.getCanonicalPath());
        System.out.println("FSInfo.getCanonicalPath(test2):\t" + getCanonicalPath(test2));
        System.out.println("FSInfo.getCanonicalPath(file2):\t" + getCanonicalPath(file2));
        System.out.println("FSInfo.getFileName(file2):\t" + getFileName(file2));

        System.out.println();

        String test3 = "C:\\TEMP\\tmp123\\..\\Test.txt";
        File file3 = new File(test3);

        System.out.println("\t*** file 3: " + file3.toString() + " ***");
        System.out.println();
        System.out.println("File.getPath():\t\t\t" + file3.getPath());
        System.out.println("File.getAbsolutePath():\t\t" + file3.getAbsolutePath());
        System.out.println("File.getCanonicalPath():\t" + file3.getCanonicalPath());
        System.out.println("FSInfo.getCanonicalPath(test3):\t" + getCanonicalPath(test3));
        System.out.println("FSInfo.getCanonicalPath(file3):\t" + getCanonicalPath(file3));
        System.out.println("FSInfo.getFileName(file3):\t" + getFileName(file3));

        System.out.println();

        // String saveAsFullName = getSaveAsFullName(null, null, null);
        // System.out.println("saveAsFullName: " + saveAsFullName);

        System.out.println("\tTest: UNC path, mapped as network drive...");
        System.out.println();

        String test4 = "S:\\PC-LAMBECK-02\\Beispiel-01.pnml";
        File file4 = new File(test4);

        System.out.println("\t*** file 4: " + file4.toString() + " ***");
        System.out.println();
        System.out.println("Info: Real path =\t\t" + "\\\\SCHOBBAK\\Backup\\lambeck\\PC-LAMBECK-02\\Beispiel-01.pnml");
        System.out.println();
        System.out.println("File.getPath():\t\t\t" + file4.getPath());
        System.out.println("File.getAbsolutePath():\t\t" + file4.getAbsolutePath());
        System.out.println("File.getCanonicalPath():\t" + file4.getCanonicalPath());
        System.out.println("FSInfo.getCanonicalPath(test4):\t" + getCanonicalPath(test4));
        System.out.println("FSInfo.getCanonicalPath(file4):\t" + getCanonicalPath(file4));
        System.out.println("FSInfo.getFileName(file4):\t" + getFileName(file4));

        System.out.println();
    }

}
