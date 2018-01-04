package de.lambeck.pned.filesystem;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Class for conversion of path names (e.g. into canonical path names) and
 * checks if files exist.<BR>
 * <BR>
 * Note: canonical path names are unique and thus we can use them as identifiers
 * for the open files and models. (See:
 * https://docs.oracle.com/javase/8/docs/api/java/io/File.html#getCanonicalPath--)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class FSInfo {

    /** The standard suffix of PNML files */
    private final static String suffix = ".pnml";

    /**
     * Determines The canonical (unique) path name of a file which is a unique
     * path. (See: example in the main method)
     * 
     * @param file
     *            The file as a File object
     * @return The canonical (unique) path name
     */
    public static String getCanonicalPath(File file) {
        String canonicalPath = null;

        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            String errMessage = "Couldn't resolve the canonical path for: " + file;
            System.err.println(errMessage);
            e.printStackTrace();
        }

        return canonicalPath;
    }

    /**
     * Calls getCanonicalPath(File file) with a path String as parameter.
     * 
     * @param path
     *            The path of a file as String
     * @return The canonical (unique) path name
     */
    public static String getCanonicalPath(String path) {
        File file = new File(path);
        String canonicalPath = getCanonicalPath(file);
        return canonicalPath;
    }

    /**
     * Determines the file name of the file.
     * 
     * @param file
     *            The file as a {@link File}
     * @return The file name
     */
    public static String getFileName(File file) {
        String fileName = null;
        fileName = file.getName();
        return fileName;
    }

    /**
     * Determines the file name of the file for a String parameter.
     * 
     * @param path
     *            The path name of the file as String
     * @return The file name
     */
    public static String getFileName(String path) {
        File file = new File(path);
        String fileName = getFileName(file);
        return fileName;
    }

    /**
     * Checks if the specified file exists in the file system for a String
     * parameter.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return True if the file exists; otherwise false
     */
    public static boolean isFileSystemFile(String modelName) {
        boolean fileExists = false;
        File file = new File(modelName);
        fileExists = file.exists();
        return fileExists;
    }

    /**
     * Checks if the specified file is write-protected.
     * 
     * @param file
     *            The file as a {@link File}
     * @return True if the file is write-protected; otherwise false
     */
    private static boolean isWriteProtectedFile(File file) {
        boolean writeProtected = false;
        writeProtected = !file.canWrite();
        return writeProtected;
    }

    /**
     * Checks if the specified file is write-protected.
     * 
     * @param fullName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return True if the file is write-protected; otherwise false
     */
    public static boolean isWriteProtectedFile(String fullName) {
        boolean writeProtected = false;
        File file = new File(fullName);
        if (file.exists()) {
            writeProtected = isWriteProtectedFile(file);
        }
        return writeProtected;
    }

    // /**
    // * Returns a full path name for a new file. An alert is displayed if the
    // * user has chosen an existing file.
    // *
    // * @param parentComponent
    // * The parent component to center the dialog above. (Should be
    // * the main application window.)
    // * @return The canonical (unique) path name
    // */
    // public static String getSaveAsFullName(JFrame parentComponent) {
    // String canonicalPath = null;
    //
    // JFileChooser fileChooser = new JFileChooser();
    // FileNameExtensionFilter filter = new FileNameExtensionFilter("Petri net
    // files (*.pnml)", "pnml");
    // fileChooser.setFileFilter(filter);
    //
    // do {
    // int returnVal = fileChooser.showSaveDialog(parentComponent);
    // if (returnVal == JFileChooser.CANCEL_OPTION)
    // return null;
    //
    // File file = fileChooser.getSelectedFile();
    // canonicalPath = getCanonicalPath(file);
    //
    // canonicalPath = dontOverwriteExistingFile(parentComponent,
    // canonicalPath);
    //
    // } while (canonicalPath == null);
    //
    // return canonicalPath;
    // }

    // /**
    // * Adds an additional Parameter initialFolder to getSaveAsFullName(JFrame
    // * parentComponent);
    // *
    // * @param parentComponent
    // * The parent component (should be the main application window)
    // * @param initialFolder
    // * The folder to start with as {@link File}
    // * @return The canonical (unique) path name
    // */
    // public static String getSaveAsFullName(JFrame parentComponent, File
    // initialFolder) {
    // String canonicalPath = null;
    //
    // JFileChooser fileChooser = new JFileChooser();
    // fileChooser.setCurrentDirectory(initialFolder);
    // FileNameExtensionFilter filter = new FileNameExtensionFilter("Petri net
    // files (*.pnml)", "pnml");
    // fileChooser.setFileFilter(filter);
    //
    // do {
    // int returnVal = fileChooser.showSaveDialog(parentComponent);
    // if (returnVal == JFileChooser.CANCEL_OPTION)
    // return null;
    //
    // File file = fileChooser.getSelectedFile();
    // canonicalPath = getCanonicalPath(file);
    //
    // if (!canonicalPath.endsWith(suffix)) {
    // canonicalPath = canonicalPath + suffix;
    // }
    //
    // /* Overwrite warning? */
    // try {
    // canonicalPath = dontOverwriteExistingFile(parentComponent,
    // canonicalPath);
    // } catch (HeadlessException e) {
    // // NOP (This program should always run on a "normal" desktop.)
    // }
    //
    // } while (canonicalPath == null);
    //
    // return canonicalPath;
    // }

    /**
     * Adds additional parameters for references to
     * {@link ApplicationController} and {@link I18NManager} to
     * getSaveAsFullName(JFrame parentComponent).<BR>
     * <BR>
     * Uses the reference to the {@link ApplicationController} to get the last
     * path used by the user.<BR>
     * Uses the reference to the {@link I18NManager} for localized messages in
     * case of errors.
     * 
     * @param parentComponent
     *            The parent component (should be the main application window)
     * @param controller
     *            The application controller
     * @param i18n
     *            The manager for localized strings
     * @return Null if the user cancelled the operation; otherwise the canonical
     *         (unique) path name
     */
    public static String getSaveAsFullName(JFrame parentComponent, ApplicationController controller, I18NManager i18n) {
        String canonicalPath = null;

        JFileChooser fileChooser = new JFileChooser();

        File lastPath = controller.getCurrentDirectory("FileSaveAsAction");
        fileChooser.setCurrentDirectory(lastPath);
        /*
         * If lastPath == null (for a new, still unsaved file) the fileChooser
         * will start in "user.home" or similar.
         */

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Petri net files (*.pnml)", "pnml");
        fileChooser.setFileFilter(filter);

        int returnVal = fileChooser.showSaveDialog(parentComponent);

        // if (returnVal == JFileChooser.CANCEL_OPTION)
        // return null; // User cancelled this operation

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            canonicalPath = getCanonicalPath(file);

            /* Add the expected suffix? */
            canonicalPath = askAddingSuffix(canonicalPath, i18n, parentComponent);
            if (canonicalPath == null)
                return null; // User cancelled this operation

            /* Overwrite warning? */
            canonicalPath = dontOverwriteExistingFile(parentComponent, canonicalPath);
            if (canonicalPath == null)
                return null; // User cancelled this operation
        }

        return canonicalPath;
    }

    /**
     * Checks the suffix of the specified file name and asks the user whether to
     * add it or not if it is missing.
     * 
     * @param canonicalPath
     *            The canonical (unique) path name
     * @param i18n
     *            The manager for localized strings
     * @param parentComponent
     *            The parent component (should be the main application window)
     * @return Null if the user cancelled the operation; otherwise the canonical
     *         (unique) path name
     */
    private static String askAddingSuffix(String canonicalPath, I18NManager i18n, JFrame parentComponent) {
        if (canonicalPath.endsWith(suffix))
            return canonicalPath;

        String title = i18n.getNameOnly("WrongFileSuffix");

        String question = i18n.getMessage("questionAddStandardFileSuffix");
        question = question.replace("%suffix%", suffix);
        question = question.replace("%filename%", canonicalPath);

        int options = JOptionPane.YES_NO_CANCEL_OPTION;
        int answer = JOptionPane.showConfirmDialog(parentComponent, question, title, options);

        /* !!! ESC -> DEFAULT_OPTION, "Cancel" -> CANCEL_OPTION !!! */
        if (answer == JOptionPane.DEFAULT_OPTION)
            return null;
        if (answer == JOptionPane.CANCEL_OPTION)
            return null;
        if (answer == JOptionPane.YES_OPTION)
            canonicalPath = canonicalPath + suffix;

        return canonicalPath;
    }

    /**
     * Checks if a file already exists and asks the user whether to overwrite it
     * or not if it exists.
     * 
     * @param parentComponent
     *            The parent component (should be the main application window)
     * @param canonicalPath
     *            The canonical (unique) path name
     * @return Null if the user declined to overwrite an existing file;
     *         otherwise the canonical (unique) path name
     */
    private static String dontOverwriteExistingFile(JFrame parentComponent, String canonicalPath) {
        if (isFileSystemFile(canonicalPath)) {
            String title = "Overwrite file?";
            String question = "Overwrite existing file %filename%?".replace("%filename%", canonicalPath);

            int options = JOptionPane.YES_NO_CANCEL_OPTION;
            int answer = JOptionPane.showConfirmDialog(parentComponent, question, title, options);

            /* !!! ESC -> DEFAULT_OPTION, "Cancel" -> CANCEL_OPTION !!! */
            if (answer == JOptionPane.DEFAULT_OPTION)
                return null;
            if (answer == JOptionPane.CANCEL_OPTION)
                return null;
            if (answer == JOptionPane.NO_OPTION)
                canonicalPath = null;
        }
        return canonicalPath;
    }

    /**
     * Returns the {@link File} for a full path name as {@link String}.
     * 
     * @param pathname
     *            A pathname string
     * @return The {@link File}
     */
    public static File getFile(String pathname) {
        if (pathname == null || pathname == "")
            return null;

        File file = new File(pathname);
        return file;
    }

}
