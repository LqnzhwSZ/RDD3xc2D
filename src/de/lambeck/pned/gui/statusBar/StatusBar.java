package de.lambeck.pned.gui.statusBar;

import java.awt.*;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import de.lambeck.pned.application.*;
import de.lambeck.pned.gui.icon.EIconSize;
import de.lambeck.pned.gui.icon.ImageIconCreator;
import de.lambeck.pned.i18n.I18NManager;

/**
 * @formatter:off
 * 
 * Implements the status bar for the Petri net editor.
 * 
 * Left side:
 * - 3 fixed-size areas for pre-defined information:
 *   - Mouse position
 *   - Size of the selection range
 *   - Size of the drawing area
 * 
 * Right side:
 * - Variable-length status message area
 * 
 * @formatter:on
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public class StatusBar extends JPanel
        implements IInfo_MousePos, IInfo_SelectionRangeSize, IInfo_DrawingAreaSize, IInfo_Status {

    private final static String imagesSubFolder = "images/";
    private final static EIconSize imagesSize = EIconSize.NONE; // No different
                                                                // sizes
                                                                // available

    /** Reference to the manager for I18N strings */
    protected I18NManager i18n;

    /** JLabel for the current mouse position */
    private JLabel mousePos = new JLabel("");
    /** JLabel for the size of the current selection frame */
    private JLabel selectionRangeSize = new JLabel("");
    /** JLabel for the size of the drawing area */
    private JLabel drawingAreaSize = new JLabel("");
    /** JLabel for the current status (last event) */
    private JLabel status = new JLabel("");

    /** Offset for font and EtchedBorder */
    private static int borderOffset = 5;

    /**
     * Constructor for a status bar with a reference to an i18n manager.
     * (Internationalized strings are used for tool tips.)
     * 
     * @param i18n
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    public StatusBar(I18NManager i18n) {
        this.i18n = i18n;
        createStatusBar();
    }

    /**
     * Creates the elements of the status bar
     */
    private void createStatusBar() {
        createLabels();

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
        JPanel center = new JPanel(new BorderLayout(1, 0));

        /* Add all labels to their panels. */
        left.add(mousePos);
        left.add(selectionRangeSize);
        left.add(drawingAreaSize);
        center.add(status, BorderLayout.CENTER);

        status.setOpaque(true);

        /* Define the status bar itself. */
        this.setLayout(new BorderLayout(1, 0));
        this.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        this.add(left, BorderLayout.WEST);
        this.add(center, BorderLayout.CENTER); // CENTER because: "component may
                                               // stretch both horizontally and
                                               // vertically to fill any space
                                               // left over."
    }

    /**
     * Creates all labels for the StatusBar
     */
    private void createLabels() {
        String text;

        /* Define the fonts. */
        setPlainFont(mousePos);
        setPlainFont(selectionRangeSize);
        setPlainFont(drawingAreaSize);
        setPlainFont(status);

        /*
         * Define the width of the 3 leftmost JLabels (same size) and 1 height
         * to give the StatusBar a minimum height via the PreferredSize.
         */
        int prefWidth = getPreferredWidthForPixelLabels();
        int fontHeight = getMaxFontHeight();
        mousePos.setPreferredSize(new Dimension(prefWidth, fontHeight));
        selectionRangeSize.setPreferredSize(new Dimension(prefWidth, fontHeight));
        drawingAreaSize.setPreferredSize(new Dimension(prefWidth, fontHeight));
        status.setPreferredSize(new Dimension(prefWidth, fontHeight));

        /* Define mousePos label. */
        text = i18n.getMessage("infoCurrentMousePosition");
        mousePos.setToolTipText(text);
        mousePos.setPreferredSize(new Dimension(prefWidth, fontHeight));

        /* Define selectionFrameSize label. */
        text = i18n.getMessage("infoCurrentSizeOfSelectionFrame");
        selectionRangeSize.setToolTipText(text);
        selectionRangeSize.setPreferredSize(new Dimension(prefWidth, fontHeight));

        /* Define drawingAreaSize label. */
        text = i18n.getMessage("infoCurrentSizeOfDrawingArea");
        drawingAreaSize.setToolTipText(text);
        drawingAreaSize.setPreferredSize(new Dimension(prefWidth, fontHeight));

        /* Define status label. */
        status.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        text = i18n.getMessage("infoStatusMessages");
        status.setToolTipText(text);
        status.setPreferredSize(new Dimension(prefWidth, fontHeight));

        /* Borders (https://stackoverflow.com/a/22384566) */
        Border border = javax.swing.BorderFactory.createEtchedBorder();
        Border margin = new EmptyBorder(borderOffset, borderOffset, borderOffset, borderOffset);

        mousePos.setBorder(new CompoundBorder(border, margin));
        selectionRangeSize.setBorder(new CompoundBorder(border, margin));
        drawingAreaSize.setBorder(new CompoundBorder(border, margin));
        status.setBorder(new CompoundBorder(border, margin));

        /*
         * Add small pictures to the labels with the "pixel information" to
         * explain their meaning.
         */
        ImageIcon icon;
        icon = ImageIconCreator.getImageIcon(imagesSubFolder, imagesSize.getValue(), "MousePos.png");
        mousePos.setIcon(icon);
        icon = ImageIconCreator.getImageIcon(imagesSubFolder, imagesSize.getValue(), "SelectionRangeSize.png");
        selectionRangeSize.setIcon(icon);
        icon = ImageIconCreator.getImageIcon(imagesSubFolder, imagesSize.getValue(), "DrawingAreaSize.png");
        drawingAreaSize.setIcon(icon);
    }

    /**
     * Sets the font of the specified label to pain text.
     * 
     * @param label
     */
    private void setPlainFont(JLabel label) {
        Font labelFont = label.getFont();
        label.setFont(new Font(labelFont.getName(), Font.PLAIN, labelFont.getSize()));
    }

    /**
     * Returns the maximum font height value within the different labels.
     * 
     * @return The maximum int value
     */
    private int getMaxFontHeight() {
        int max = 0;
        int next = 0;

        next = getFontHeight(mousePos);
        max = Math.max(max, next);

        next = getFontHeight(selectionRangeSize);
        max = Math.max(max, next);

        next = getFontHeight(drawingAreaSize);
        max = Math.max(max, next);

        next = getFontHeight(status);
        max = Math.max(max, next);

        max = max + borderOffset;
        return max;
    }

    /**
     * Returns the font height value for the specified JComponent.
     * 
     * @param c
     *            The JComponent
     * @return The font height of the JComponent
     */
    private int getFontHeight(JComponent c) {
        Font font = c.getFont();
        FontMetrics metrics = getFontMetrics(font);
        int fontHeight = metrics.getHeight();
        return fontHeight;
    }

    /**
     * Calculates the preferred width for the labels on the left side of the
     * status bar (which represent "pixel information" like mouse position) by
     * assuming very large 4-digit values.
     * 
     * @return
     */
    private int getPreferredWidthForPixelLabels() {
        Font font = mousePos.getFont();
        FontMetrics metrics = getFontMetrics(font);
        int fontWidth = metrics.stringWidth("9999, 9999px");
        int widthAndImageIconOffset = fontWidth + 40;
        return widthAndImageIconOffset;
    }

    @Override
    public void setInfo_MousePos(Point p) {
        String text = "";
        text = p.x + ", " + p.y + "px";
        this.mousePos.setText(text);
    }

    @Override
    public void setInfo_SelectionRangeSize(int width, int height) {
        String text = "";
        if (width > 0 && height > 0) {
            text = width + ", " + height + "px";
        }
        this.selectionRangeSize.setText(text);
    }

    @Override
    public void setInfo_DrawingAreaSize(int width, int height) {
        String text = "";
        if (width > -1 && height > -1) {
            text = width + ", " + height + "px";
        }
        this.drawingAreaSize.setText("" + text);
    }

    @Override
    public void setInfo_Status(String s, EStatusMessageLevel level) {
        switch (level) {
        case INFO:
            status.setForeground(Color.BLACK);
            // status.setBackground(this.getBackground());
            status.setBackground(Color.WHITE);
            this.status.setText(s);
            break;
        case WARNING:
            status.setForeground(Color.BLACK);
            status.setBackground(Color.YELLOW);
            this.status.setText(s);
            break;
        case ERROR:
            status.setForeground(Color.WHITE);
            status.setBackground(Color.RED);
            this.status.setText(s);
            break;
        default:
            status.setForeground(Color.BLACK);
            status.setBackground(this.getBackground()); // Panel background
        }
    }

}
