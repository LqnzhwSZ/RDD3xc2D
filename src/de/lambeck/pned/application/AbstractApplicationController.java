package de.lambeck.pned.application;

import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import de.lambeck.pned.application.actions.IActionManager;
import de.lambeck.pned.gui.menuBar.MenuBar;
import de.lambeck.pned.gui.statusBar.StatusBar;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Observes the application (e.g. attempt to close with unsaved changes). Holds
 * a reference to the status bar so that the application controller can show
 * status messages etc.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public abstract class AbstractApplicationController extends WindowAdapter
        implements IInfo_MousePos, IInfo_SelectionRangeSize, IInfo_DrawingAreaSize, IInfo_Status {

    /** Reference to the main application window */
    protected JFrame mainFrame = null;

    /**
     * Indicates if all models are either unchanged or were saved to a file.
     */
    protected boolean allowedToClose = true;

    /** Reference to the manager for I18N strings */
    protected I18NManager i18n = null;

    /** Reference to the applications status bar */
    protected StatusBar statusBar = null;

    /** Reference to the applications menu bar */
    protected MenuBar menuBar = null;

    /** Reference to the {@link IActionManager} */
    protected IActionManager actionManager;

    /**
     * Lists of Action objects for menu bar and tool bar
     */
    protected Map<String, AbstractAction> allActions = new HashMap<>();

    /**
     * Lists of Action objects for popup menus
     */
    protected Map<String, AbstractAction> popupActions = new HashMap<>();

    /**
     * This attribute holds the full path name of the file represented by the
     * active tab.
     * 
     * Note: This attribute is updated whenever the TabListener detects a new
     * tab selection. The TabListener invokes setActiveFile(index).
     */
    protected String activeFile = "";

    /**
     * The last used directory
     */
    protected File currentDirectory;

    /**
     * Constructs the application controller for the specified main frame
     * (application window).
     * 
     * @param frame
     *            The main frame (window) of the application
     * @param i18n
     *            The source object for I18N strings
     * @param stBar
     *            The status bar (of this application)
     */
    @SuppressWarnings("hiding")
    public AbstractApplicationController(JFrame frame, I18NManager i18n, StatusBar stBar) {
        super();
        this.mainFrame = frame;
        this.i18n = i18n;
        this.statusBar = stBar;
    }

    /**
     * Getter for allowedToClose
     * 
     * @return allowedToClose
     */
    public abstract boolean getAllowedToClose();

    /*
     * No Setter for allowedToClose because the application controller should
     * decide this alone depending on its own state!
     */

    /**
     * Note: The implementation of windowClosing() in the concrete class should
     * intercept the attempt to close the main window of the application if the
     * user has to save open files or similar!
     */
    @Override
    public abstract void windowClosing(WindowEvent e);

    /*
     * Method for implemented interfaces (status bar)
     */

    @Override
    public void setInfo_MousePos(Point p) {
        if (statusBar == null) {
            System.err.println("No reference to the status bar!");
            return;
        }
        statusBar.setInfo_MousePos(p);
    }

    @Override
    public void setInfo_SelectionRangeSize(int width, int height) {
        if (statusBar == null) {
            System.err.println("No reference to the status bar!");
            return;
        }
        statusBar.setInfo_SelectionRangeSize(width, height);
    }

    @Override
    public void setInfo_Status(String s, EStatusMessageLevel level) {
        if (statusBar == null) {
            System.err.println("No reference to the status bar!");
            return;
        }
        statusBar.setInfo_Status(s, level);
    }

    @Override
    public void setInfo_DrawingAreaSize(int width, int height) {
        if (statusBar == null) {
            System.err.println("No reference to the status bar!");
            return;
        }
        statusBar.setInfo_DrawingAreaSize(width, height);
    }

    /*
     * Directories for file operations
     */

    /**
     * Getter for activeFile.
     * 
     * Note: FileSaveAsAction should check this to make sure that there is a
     * file to save.
     * 
     * @return the active file; null if no file is open
     */
    public String getActiveFile() {
        return this.activeFile;
    }

    /**
     * Returns the current working directory for file choosers.
     * 
     * @param action
     *            The action requesting this working directory (for an example
     *            "FileSaveAsAction").
     * @return The current directory depending on the Action; null on
     *         FileSaveAsAction for a new, still unsaved file
     */
    public File getCurrentDirectory(String action) {
        File directory = null;

        switch (action) {
        case "FileOpenAction":
            directory = this.currentDirectory;
            break;

        case "FileSaveAsAction":
            /*
             * Here: FileSaveAsAction has already checked if activeFile == null.
             */

            directory = new File(this.activeFile).getParentFile();
            /*
             * If directory == null (for a new, still unsaved file) just return
             * null, the fileChooser will start in "user.home" or similar.
             */

            break;

        default:
            directory = this.currentDirectory;
        }

        return directory;
    }

    /**
     * Sets a new current directory.
     * 
     * @param directory
     *            The new current directory as {@link File}
     */
    public void setCurrentDirectory(File directory) {
        if (directory == null)
            return;

        this.currentDirectory = directory;
    }

    /**
     * Sets a new current directory.
     * 
     * @param pathname
     *            The new current directory as String
     */
    public void setCurrentDirectory(String pathname) {
        if (pathname == null)
            return;

        File directory = new File(pathname);
        setCurrentDirectory(directory);
    }

}
