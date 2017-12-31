package de.lambeck.pned.gui.menuBar;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.application.actions.*;
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

    protected JFrame parentComponent;
    protected ApplicationController appController = null;
    protected I18NManager i18n;

    MenuCreator itemCreator;

    /**
     * Constructs the MenuBar with a parent component and a reference to the
     * application controller.
     * 
     * @param parent
     *            The parent component (should be the main application window)
     * @param controller
     *            The application controller
     * @param i18n
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    public MenuBar(JFrame parent, ApplicationController controller, I18NManager i18n) {
        super();
        this.parentComponent = parent;
        this.appController = controller;
        this.i18n = i18n;

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

        // menu = createViewMenu();
        // add(menu);
    }

    /**
     * Creates the file menu with all menu items.
     * 
     * @return The menu
     */
    private JMenu createFileMenu() {
        JMenu fileMenu;

        /*
         * Create the menu
         */
        fileMenu = itemCreator.getMenu("File", "FileMenuDescription");

        /*
         * Create the menu items
         */
        fileMenu.add(new FileNewAction(appController, i18n));
        fileMenu.add(new FileOpenAction(appController, i18n, parentComponent));
        fileMenu.add(new FileCloseAction(appController, i18n));

        fileMenu.addSeparator();

        fileMenu.add(new FileSaveAction(appController, i18n));
        fileMenu.add(new FileSaveAsAction(appController, i18n, parentComponent));

        fileMenu.addSeparator();

        fileMenu.add(new AppExitAction(appController, i18n));

        return fileMenu;
    }

    /**
     * Creates the edit menu with all menu items.
     * 
     * @return The menu
     */
    private JMenu createEditMenu() {
        JMenu editMenu;

        /*
         * Create the menu
         */
        editMenu = itemCreator.getMenu("Edit", "EditMenuDescription");

        /*
         * Create the menu items
         */
        editMenu.add(new EditRenameAction(appController, i18n));
        editMenu.add(new EditDeleteAction(appController, i18n));

        return editMenu;
    }

}
