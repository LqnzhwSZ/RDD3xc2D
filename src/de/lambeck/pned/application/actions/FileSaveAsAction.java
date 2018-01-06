package de.lambeck.pned.application.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sun.xml.internal.ws.api.Component;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.filesystem.FSInfo;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Implements the Action for "FileSaveAs...".
 * 
 * Note: Holds additional references to a parent component (to position dialogs)
 * and to the application controller to store the current directory.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class FileSaveAsAction extends AbstractPNAction {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -1904549335042924049L;

    /** The parent {@link Component} to center the dialog */
    protected JFrame parentComponent;

    /** The {@link JFileChooser} that is used to look for a file name. */
    private JFileChooser fileChooser = new JFileChooser();

    /**
     * Creates the FileSaveAsAction with an additional parent component.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The manager for localized strings
     * @param parent
     *            The parent component (should be the main application window)
     */
    public FileSaveAsAction(ApplicationController controller, I18NManager i18nController, JFrame parent) {
        super(controller, i18nController);
        this.parentComponent = parent;

        internalName = "FileSaveAs...";
        iconPath = "icons/gnome/";
        iconName = "Gnome-document-save-as.svg.png";
        keyEvent = KeyEvent.VK_S;
        actionEvent = SHORTCUT_KEY_MASK + ActionEvent.SHIFT_MASK;

        customize();

        /* Add the extension filter only once! (not in actionPerformed!) */
        String fileFilterDescr = i18nController.getNameOnly("FileExtFilterDescr_PNML");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(fileFilterDescr, "pnml");
        fileChooser.setFileFilter(filter);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (appController.getActiveFile() == null)
            return; // No file to save.

//        File lastPath = appController.getCurrentDirectory("FileSaveAsAction");
//        fileChooser.setCurrentDirectory(lastPath);
//        /*
//         * If lastPath == null (for a new, still unsaved file) the fileChooser
//         * will start in "user.home" or similar.
//         */
//
//        int returnVal = fileChooser.showSaveDialog(parentComponent);
//
//        if (returnVal != JFileChooser.APPROVE_OPTION) {
//            /* Cancelled by user */
//            return;
//        }
//
//        File file = fileChooser.getSelectedFile();
//
//        if (!file.getAbsolutePath().endsWith(suffix)) {
//            file = new File(file + suffix);
//        }

        String fileSaveAsName = FSInfo.getSaveAsFullName(parentComponent, appController, i18n);
        if (fileSaveAsName == null) {
            /* Cancelled by user */
            return;
        }

        File file = FSInfo.getFile(fileSaveAsName);

        // appController.setCurrentDirectory(file);
        appController.setCurrentDirectory(file.getParent());

        if (!file.exists()) {
            appController.menuCmd_FileSaveAs(file);
            return;
        } else if (file.canWrite()) {
            appController.menuCmd_FileSaveAs(file);
            return;
        }

        String title = i18n.getNameOnly("WriteProtectedFile");
        String message = i18n.getMessage("errFileWriteProtected");
        message = message.replace("%fullName%", file.getName());

        int options = JOptionPane.OK_OPTION;

        JOptionPane.showMessageDialog(parentComponent, message, title, options);
        return;

    }
}
