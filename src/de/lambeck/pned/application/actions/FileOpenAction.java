package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "FileOpen...".
 * 
 * Note: Holds additional references to a parent component (to position dialogs)
 * and to the application controller to store the current directory.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public class FileOpenAction extends AbstractPNAction {

    protected JFrame parentComponent;

    private JFileChooser fileChooser = new JFileChooser();

    /**
     * Creates the FileOpenAction with an additional parent component.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The source object for I18N strings
     * @param parent
     *            The parent component (should be the main application window)
     */
    public FileOpenAction(ApplicationController controller, I18NManager i18nController, JFrame parent) {
        super(controller, i18nController);
        this.parentComponent = parent;

        internalName = "FileOpen...";
        iconPath = "icons/gnome/";
        iconName = "Gnome-document-open.svg.png";
        keyEvent = KeyEvent.VK_O;
        actionEvent = SHORTCUT_KEY_MASK;

        customize();

        /*
         * Add the extension filter only 1x (not in every actionPerformed)!
         */
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Petri net files (*.pnml)", "pnml");
        fileChooser.setFileFilter(filter);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File lastPath = appController.getCurrentDirectory("FileOpenAction");
        fileChooser.setCurrentDirectory(lastPath);

        int returnVal = fileChooser.showOpenDialog(parentComponent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            appController.setCurrentDirectory(file);

            appController.menuCmd_FileOpen(file);

        } else {
            // System.out.println("Open command cancelled by user.");
        }
    }
}
