package de.lambeck.pned.i18n;

import java.util.Locale;

@SuppressWarnings("javadoc")
public class MnemonicTest {

    public static void main(String[] args) {
        I18NManager i18n = new I18NManager(new Locale("de", "DE"));

        /*
         * Sollte einen Fehler produzieren: Schlüssel "Fil" existiert nicht!
         */
        String name = "Fil";
        System.out.println("name: " + name);

        MnemonicString i18nName = i18n.getMnemonicName(name);
        System.out.println("i18nName.getText(): '" + i18nName.getText() + "'");
        System.out.println("i18nName.getMnemonic(): " + i18nName.getMnemonic());
    }

}
