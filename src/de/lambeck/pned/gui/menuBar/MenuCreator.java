package de.lambeck.pned.gui.menuBar;

import javax.swing.JMenu;

import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.i18n.MnemonicString;

/**
 * Creates menus for the menu bar.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class MenuCreator {

    protected I18NManager i18n;

    /**
     * Constructs the MenuCreator.
     * 
     * @param i18n
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    public MenuCreator(I18NManager i18n) {
        super();
        this.i18n = i18n;
    }

    /**
     * Creates a menu.
     * 
     * Note: the mnemonic comes from the value in the properties (the "&" in the
     * value).
     * 
     * @param name
     *            The name of the menu
     * @param altText
     *            The description of the object (e.g. for screen readers)
     * @return The menu
     */
    public JMenu getMenu(String name, String altText) {
        MnemonicString stringAndMnemonic = i18n.getMnemonicName(name);
        name = stringAndMnemonic.getText();
        int mnemonic = stringAndMnemonic.getMnemonic();

        altText = i18n.getNameOnly(altText);

        JMenu menu = new JMenu(name);

        menu.setMnemonic(mnemonic);
        menu.getAccessibleContext().setAccessibleDescription(altText);

        return menu;
    }
}
