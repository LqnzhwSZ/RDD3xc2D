package de.lambeck.pned.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This manager for "internationalized" ("i18n") Strings provides localized
 * messages and names.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class I18NManager {

    /** The location of the properties files with the translations */
    private static String baseName = "de.lambeck.pned.resources";

    /** Stores the specified Locale (language). */
    private Locale currentLocale; // For error messages

    /** The resources for messages */
    private ResourceBundle messages;

    /**
     * The resources for names
     * 
     * Note: Names may contain mnemonic markers like for an example "&amp;Edit"
     * which specify which accelerator (hot key) can be used for a command.
     */
    private ResourceBundle names;

    /**
     * Constructor using a specified Locale.
     * 
     * @param locale
     *            The locale for the current GUI
     */
    public I18NManager(Locale locale) {
        if (locale == null)
            System.err.println("I18NManager: locale is null!");

        this.currentLocale = locale;

        messages = ResourceBundle.getBundle(baseName + ".MessagesBundle", locale);
        names = ResourceBundle.getBundle(baseName + ".NamesBundle", locale);
        reportMissingBundleFiles(locale);
    }

    /**
     * Reports if there are missing Bundles for the specified language.
     * 
     * @param expected
     *            The expected {@link Locale}
     */
    private void reportMissingBundleFiles(Locale expected) {
        Locale foundLocale;

        foundLocale = messages.getLocale();
        if (!foundLocale.equals(expected)) {
            System.err.print(messages.getBaseBundleName() + "_" + expected.toString() + " not found!");
            System.err.println(" (Using: " + foundLocale.toString() + ".)");
        }

        foundLocale = names.getLocale();
        if (!foundLocale.equals(expected)) {
            System.err.print(names.getBaseBundleName() + "_" + expected.toString() + " not found!");
            System.err.println(" (Using: " + foundLocale.toString() + ".)");
        }
    }

    /**
     * Calls getString() with the resource bundle for messages and forwards the
     * result.
     * 
     * Note: In opposition to (button) names is an "&amp;" within a message not
     * a "mnemonic marker" but just a part of the text.
     * 
     * @param key
     *            The given key
     * @return The string for the given key
     */
    public String getMessage(String key) {
        if (key == null) {
            errorMsgMissingKey("I18NManager.getMessage(): key is empty!");
            return "";
        }

        return getString(key, messages);
    }

    /**
     * Gets the string for the given key from the specified resource bundle.
     * 
     * @param key
     *            The given key
     * @param bundle
     *            The specified resource bundle
     * @return The string for the given key
     */
    private String getString(String key, ResourceBundle bundle) {
        if (key == "")
            return "";

        String i18nString = "";
        try {
            i18nString = bundle.getString(key);
            return i18nString;
        } catch (MissingResourceException e) {
            // i18nMessage = key;
            System.err.println("Key not found in " + getSimpleResourceName(bundle) + ": " + key);
        }
        return i18nString;
    }

    /**
     * Calls getMnemonicString() with the resource bundle for names (buttons
     * names etc.) and forwards the result.
     * 
     * Note: (button) names can contain an "&amp;" as "mnemonic marker".
     * 
     * @param key
     *            The given key
     * @return A MnemonicString object with two values: The string for the given
     *         key and the mnemonic
     */
    public MnemonicString getMnemonicName(String key) {
        if (key == null) {
            errorMsgMissingKey("I18NManager.getMnemonicName(): key is empty!");
            return new MnemonicString("", -1);
        }

        return getMnemonicString(key, names);
    }

    /**
     * Calls getMnemonicString() with the resource bundle for names (buttons
     * names etc.) and forwards only the name.
     * 
     * Note: Using getMnemonicString() removes the "mnemonic marker" from the
     * name. Example "&amp;File" =&gt; "File"
     * 
     * @param key
     *            The given key
     * @return The string for the given key without the mnemonic mark
     */
    public String getNameOnly(String key) {
        if (key == null) {
            errorMsgMissingKey("I18NManager.getNameOnly(): key is empty!");
            return "";
        }

        return getMnemonicString(key, names).getText();
    }

    /**
     * Gets the string and the mnemonic (after the char "&amp;") for the given
     * key from the specified resource bundle.
     * 
     * Note: The mnemonic is a String (like "A"), not a KeyEvent (like
     * KeyEvent.VK_A). Convert the mnemonic into an int for use in a
     * setMnemonic() method!
     * 
     * @param key
     *            The given key
     * @param bundle
     *            The specified resource bundle
     * @return A MnemonicString object with two values: The string for the given
     *         key and the mnemonic
     */
    private MnemonicString getMnemonicString(String key, ResourceBundle bundle) {
        MnemonicString mnemonicString = new MnemonicString("", -1);

        if (key == "")
            return mnemonicString;

        if (key == null) {
            System.err.println("getMnemonicString() was called with a null string!");
            return mnemonicString;
        }

        String i18nString = "";
        try {
            i18nString = bundle.getString(key);
        } catch (MissingResourceException e) {
            errorMsgWrongKey("Key not found", key, bundle);
            return mnemonicString;
        } catch (ClassCastException e) {
            errorMsgWrongKey("ClassCastException for key", key, bundle);
            return mnemonicString;
        }

        /* String found, try to get the mnemonic (after the char "&"). */
        int length = i18nString.length();
        if (length == 0) {
            errorMsgWrongKey("Empty value for key", key, bundle);
            return mnemonicString;
        }

        int pos = i18nString.indexOf("&");
        if (pos == -1) {
            // No "&"
            mnemonicString.setText(i18nString);
        } else {
            if (pos == length - 1) {
                // "&" is the last char!
                errorMsgWrongKey("No char after '&' in value for key", key, bundle);
                mnemonicString.setText(i18nString.substring(0, pos));
            } else {
                if (pos >= 0) {
                    String soleString = i18nString.substring(0, pos)
                            + i18nString.substring(pos + 1, i18nString.length());
                    String mnemonic = i18nString.substring(pos + 1, pos + 2);
                    mnemonicString.setText(soleString);
                    mnemonicString.setMnemonic(mnemonic);
                }
            }
        }
        return mnemonicString;
    }

    /**
     * Returns the simple bundle name (without the package information).
     * 
     * @param bundle
     *            The resource bundle
     * @return The simple name (without package)
     */
    private String getSimpleResourceName(ResourceBundle bundle) {
        String name = bundle.getBaseBundleName();
        int pos = name.lastIndexOf(".");
        String simpleName = name.substring(pos + 1);
        return simpleName;
    }

    /**
     * Reports an empty key in a resource file.
     * 
     * @param errMsg
     *            The message to show
     */
    private void errorMsgMissingKey(String errMsg) {
        System.err.println(errMsg);
    }

    /**
     * Reports a missing key in a resource file.
     * 
     * @param errMsg
     *            The message to show
     * @param key
     *            The demanded key
     * @param bundle
     *            The {@link ResourceBundle} that was specified for this key
     */
    private void errorMsgWrongKey(String errMsg, String key, ResourceBundle bundle) {
        String simpleName = getSimpleResourceName(bundle);
        System.err.println(simpleName + "(" + currentLocale + "): " + errMsg + ": " + key);
    }

}
