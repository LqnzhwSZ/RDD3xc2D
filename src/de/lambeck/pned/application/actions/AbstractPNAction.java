package de.lambeck.pned.application.actions;

import java.awt.Toolkit;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.gui.icon.EIconSize;
import de.lambeck.pned.gui.icon.ImageIconCreator;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.i18n.MnemonicString;

/**
 * Abstract class for implementation of Actions for the Petri net editor.
 * 
 * Note:
 * 
 * Holds references to
 * 
 * - The application controller (to pass commands to it)
 * 
 * - An I18NManager to get localized strings for button names etc. in the
 * current language
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractPNAction extends AbstractAction {

    /*
     * docs.oracle.com: By default, this method returns Event.CTRL_MASK
     */
    protected static final int SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    /** Variable icon size, but equal for all menu/tool bar items */
    protected static EIconSize imagesSize = EIconSize.MEDIUM;

    /*
     * Customizing attributes for different buttons, menu items etc.
     */

    /** The internal name of this Action */
    protected String internalName = "";
    /** The path to the icon */
    protected String iconPath = "";
    /** The name of the icon */
    protected String iconName = "";
    /** keyCode for the KeyStroke used as the accelerator for the action */
    protected int keyEvent = 0;
    /** modifiers for the KeyStroke used as the accelerator for the action */
    protected int actionEvent = 0;

    /** Reference to the {@link ApplicationController} */
    protected ApplicationController appController = null;

    /** Reference to the manager for I18N strings */
    protected I18NManager i18n = null;

    /**
     * Creates the Action with references to the application controller and an
     * I18NManager to get localized strings.
     * 
     * @param controller
     *            The application controller
     * @param i18nController
     *            The source object for I18N strings
     */
    public AbstractPNAction(ApplicationController controller, I18NManager i18nController) {
        super();
        this.appController = controller;
        this.i18n = i18nController;
    }

    /**
     * Sets all values.
     * 
     * Note: This method should only be invoked by subclasses.
     */
    public final void customize() {
        ImageIcon icon = ImageIconCreator.getScaledImageIcon(iconPath, iconName, imagesSize.getValue());
        if (icon != null) {
            // this.putValue(LARGE_ICON_KEY, icon);
            this.putValue(SMALL_ICON, icon);
        }

        MnemonicString sm = i18n.getMnemonicName(internalName);
        String name = sm.getText();
        int mnemonic = sm.getMnemonic();
        this.putValue(NAME, name);
        this.putValue(SHORT_DESCRIPTION, name);
        this.putValue(Action.MNEMONIC_KEY, mnemonic);

        if (keyEvent != 0) {
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(keyEvent, actionEvent));
        }
    }
}
