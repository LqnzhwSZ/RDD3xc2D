package de.lambeck.pned.filesystem;

import java.io.File;
import java.io.IOException;

import sun.awt.shell.ShellFolder;
import sun.awt.shell.ShellFolderColumnInfo;

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
public class FSInfoTest2 extends FSInfo {

    /**
     * Self test...
     * 
     * Note: getCanonicalPath() can throw an IOException!
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String testpath = "S:\\PC-LAMBECK-02\\Beispiel-01.pnml";

        /* Test 1 */
        System.out.println(ShellFolder.getShellFolder(new File(testpath.substring(0, 3))).getDisplayName());
        File.listRoots();

        /* Test 2 */
        File networkDrive = new File("S:\\");
        ShellFolder shellFolder = ShellFolder.getShellFolder(networkDrive);
        ShellFolderColumnInfo[] cols = shellFolder.getFolderColumns();
        for (int i = 0; i < cols.length; i++) {

            System.out.println(cols[i].getTitle());
            System.out.println(cols[i]);
            String test = (String) shellFolder.getFolderColumnValue(i);
            if (test != null)
                System.out.println(test);

            System.out.println();

            // if ("Attributes".equals(cols[i].getTitle())) {
            if ("Attribute".equals(cols[i].getTitle())) {
                System.out.println(shellFolder.getFolderColumnValue(i));
                String uncPath = (String) shellFolder.getFolderColumnValue(i);
                System.err.println(uncPath);
                break; // don't need to look at other columns
            }
        }

        /* Test 3 */
        System.out.println("Test 3...");
        File path = new File(testpath);
        System.out.println(path.toURI());
        // System.out.println(path.listRoots().toString());
    }

}
