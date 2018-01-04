package de.lambeck.pned.gui.icon;

import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * Creates ImageIcons for menus etc.<BR>
 * <BR>
 * See:
 * https://docs.oracle.com/javase/tutorial/uiswing/examples/components/MenuLookDemoProject/src/components/MenuLookDemo.java
 * <BR>
 * Note:<BR>
 * Allows easy change of size for all icons in the application by changing the
 * provided parameter px (size in pixel). This is done by adding a prefix like
 * "32px-..." to the name of image files if parameter px was specified.<BR>
 * <BR>
 * Example for px = 32: "Gnome-folder.png" means "32px-Gnome-folder.png"<BR>
 * <BR>
 * Since px is a parameter, different icon sizes are possible for menu bar, tool
 * bar etc.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ImageIconCreator {

    /** The base folder containing all (different kinds of) resources */
    private final static String imagesBasePath = "/de/lambeck/pned/resources";

    /**
     * Returns an ImageIcon, or null if subfolder/name was invalid.<BR>
     * <BR>
     * Note: Calls getImageIcon(String subfolder, int px, String name, String
     * altText) with an empty altText.
     * 
     * @param subfolder
     *            The folder below imagesBasePath containing the image files.
     * @param px
     *            The size in pixel. (File names with prefix like "32px-..."
     *            allow to use different sizes of images.)
     * @param name
     *            The file name (without prefixes like "32px-...")
     * @return The ImageIcon
     */
    public static ImageIcon getImageIcon(String subfolder, int px, String name) {
        return getImageIcon(subfolder, px, name, "");
    }

    /**
     * Returns an ImageIcon, or null if subfolder/name was invalid.
     * 
     * @param subfolder
     *            The folder below imagesBasePath containing the image files.
     * @param px
     *            The size in pixel. (File names with prefix like "32px-..."
     *            allow to use different sizes of images.)
     * @param name
     *            The file name (without prefixes like "32px-...")
     * @param altText
     *            Brief textual description of the image (see: ImageIcon)
     * @return The ImageIcon
     */
    public static ImageIcon getImageIcon(String subfolder, int px, String name, String altText) {
        String fullName = imagesBasePath + "/" + subfolder + "/";

        if (px > 0)
            name = "_" + px + "px/" + name;

        fullName = fullName + name;
        fullName = fullName.replaceAll("//", "/");

        java.net.URL imgURL = ImageIconCreator.class.getResource(fullName);
        if (imgURL == null) {
            System.err.println("File not found: " + fullName);
            return null;
        }

        return new ImageIcon(imgURL, altText);
    }

    /**
     * Returns an ImageIcon, or null if subfolder/name was invalid.<BR>
     * <BR>
     * Note: Calls getScaledImageIcon(String subfolder, String name, int px,
     * String altText) with an empty altText.
     * 
     * @param subfolder
     *            The folder below imagesBasePath containing the image files.
     * @param name
     *            The file name
     * @param px
     *            The target size in pixel
     * @return The ImageIcon
     */
    public static ImageIcon getScaledImageIcon(String subfolder, String name, int px) {
        return getScaledImageIcon(subfolder, name, px, "");
    }

    /**
     * Returns an ImageIcon, or null if subfolder/name was invalid.
     * 
     * @param subfolder
     *            The folder below imagesBasePath containing the image file.
     * @param name
     *            The file name
     * @param px
     *            The target size in pixel
     * @param altText
     *            Brief textual description of the image (see: ImageIcon)
     * @return The ImageIcon
     */
    public static ImageIcon getScaledImageIcon(String subfolder, String name, int px, String altText) {
        String fullName = imagesBasePath + "/" + subfolder + "/" + name;
        fullName = fullName.replaceAll("//", "/");

        java.net.URL imgURL = ImageIconCreator.class.getResource(fullName);
        if (imgURL == null) {
            System.err.println("File not found: " + fullName);
            return null;
        }

        ImageIcon icon = new ImageIcon(imgURL, altText);
        icon.setImage(icon.getImage().getScaledInstance(px, px, Image.SCALE_SMOOTH));
        return icon;
    }

}
