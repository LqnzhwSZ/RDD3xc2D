package de.lambeck.pned.gui.menuBar;

import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the menu bar for the Petri net editor.
 * 
 * Note:
 * 
 * Holds references to
 * 
 * - A parent component (to position dialogs)
 * 
 * - The application controller (to pass this reference to action objects)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {

    /** This is the Component to position some dialogs (e.g. FileOpen). */
    protected JFrame parentComponent;

    /** The Map with existing Actions, suitable for this menu bar. */
    protected Map<String, AbstractAction> allActions;

    /** The class that creates the menu items. */
    MenuCreator itemCreator;

    /** A menu bar "button" */
    private AbstractAction fileNewAction;
    /** A menu bar "button" */
    private AbstractAction fileOpenAction;
    /** A menu bar "button" */
    private AbstractAction fileCloseAction;
    /** A menu bar "button" */
    private AbstractAction fileSaveAction;
    /** A menu bar "button" */
    private AbstractAction fileSaveAsAction;
    /** A menu bar "button" */
    private AbstractAction appExitAction;
    /** A menu bar "button" */
    private AbstractAction editRenameAction;
    /** A menu bar "button" */
    private AbstractAction editDeleteAction;
    /** A menu bar "button" */
    private AbstractAction stopSimulationAction;

    /**
     * Constructs the MenuBar with a parent component and a reference to the
     * application controller.
     * 
     * @param parent
     *            The parent component (should be the main application window)
     * @param i18n
     *            The source object for I18N strings
     * @param allActions
     *            List of Actions
     */
    @SuppressWarnings("hiding")
    public MenuBar(JFrame parent, I18NManager i18n, Map<String, AbstractAction> allActions) {
        super();
        this.parentComponent = parent;
        this.allActions = allActions;

        itemCreator = new MenuCreator(i18n);
        createMenus();
    }

    /**
     * Creates the menus of this menu bar.
     */
    private void createMenus() {
        JMenu menu;

        menu = createFileMenu();
        add(menu);

        menu = createEditMenu();
        add(menu);
    }

    /**
     * Creates the file menu with all menu items.
     * 
     * @return The menu
     */
    private JMenu createFileMenu() {
        JMenu fileMenu;

        /* Create the menu. */
        fileMenu = itemCreator.getMenu("File", "FileMenuDescription");

        /* Create the menu items. */
        fileNewAction = allActions.get("FileNew");
        fileMenu.add(fileNewAction);

        fileOpenAction = allActions.get("FileOpen...");
        fileMenu.add(fileOpenAction);

        fileCloseAction = allActions.get("FileClose");
        fileMenu.add(fileCloseAction);

        fileMenu.addSeparator();

        fileSaveAction = allActions.get("FileSave");
        fileMenu.add(fileSaveAction);

        fileSaveAsAction = allActions.get("FileSaveAs...");
        fileMenu.add(fileSaveAsAction);

        fileMenu.addSeparator();

        appExitAction = allActions.get("AppExit");
        fileMenu.add(appExitAction);

        return fileMenu;
    }

    /**
     * Creates the edit menu with all menu items.
     * 
     * @return The menu
     */
    private JMenu createEditMenu() {
        JMenu editMenu;

        /* Create the menu. */
        editMenu = itemCreator.getMenu("Edit", "EditMenuDescription");

        /* Create the menu items. */
        editRenameAction = allActions.get("EditRename...");
        editMenu.add(editRenameAction);

        editDeleteAction = allActions.get("EditDelete");
        editMenu.add(editDeleteAction);

        editMenu.addSeparator();

        stopSimulationAction = allActions.get("StopSimulation");
        editMenu.add(stopSimulationAction);

        return editMenu;
    }

}
