package de.lambeck.pned.i18n;

/**
 * Implements a class for objects that consists of two parts: A string and a
 * mnemonic, so that both can be used to create buttons or menu items.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class MnemonicString {

    private String text;
    private int mnemonic;

    /**
     * Constructor with parameter for both values.
     * 
     * @param s
     *            The string
     * @param i
     *            The mnemonic (int value)
     */
    public MnemonicString(String s, int i) {
        this.text = s;
        this.mnemonic = i;
    }

    /**
     * Getter for the String ("part 1" of the object)
     * 
     * @return The string
     */
    public String getText() {
        return this.text;
    }

    /**
     * Setter for the String ("part 1" of the object)
     * 
     * @param s
     *            The string
     */
    public void setText(String s) {
        this.text = s;
    }

    /**
     * Getter for the memonic ("part 2" of the object)
     * 
     * @return The mnemonic
     */
    public int getMnemonic() {
        return this.mnemonic;
    }

    /**
     * Setter for the memonic ("part 2" of the object)
     * 
     * @param i
     *            The mnemonic (int value)
     */
    public void setMnemonic(int i) {
        this.mnemonic = i;
    }

    /**
     * Setter for the memonic ("part 2" of the object)
     * 
     * Note: this method determines the mnemonic from a string and uses
     * setMnemonic(int i) to set the attribute.
     * 
     * @param s
     *            The mnemonic (String)
     */
    public void setMnemonic(String s) {
        int m = (int) (s.toCharArray()[0]);
        setMnemonic(m);
    }

}
