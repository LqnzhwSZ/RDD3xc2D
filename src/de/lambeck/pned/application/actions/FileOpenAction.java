package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sun.xml.internal.ws.api.Component;

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
public class FileOpenAction extends AbstractPNAction {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 5801985883342144527L;

    /** The parent {@link Component} to center the dialog */
    protected JFrame parentComponent;

    /** The {@link JFileChooser} that is used to look for a file. */
    private JFileChooser fileChooser = new JFileChooser();

    /**
     * Creates the FileOpenAction with an additional parent component.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The manager for localized strings
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

        /* Add the extension filter only once! (not in actionPerformed!) */
        String fileFilterDescr = i18nController.getNameOnly("FileExtFilterDescr_PNML");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(fileFilterDescr, "pnml");
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
            // System.out.println("Open command canceled by user.");
        }
    }
}
