package de.lambeck.pned.gui.settings;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalSliderUI;

import de.lambeck.pned.application.ApplicationController;

/**
 * Implements a Slider for the size of elements on the draw panels. This slider
 * is intended to be added to the tool bar.
 * 
 * Note: Holds a reference to the application controller which has to pass
 * changes to the GUI model controller.
 * 
 * (Based on Oracles "SliderDemo".)
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class SizeSlider extends JPanel implements ChangeListener {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 9037749792815202561L;

    /** Reference to the {@link ApplicationController} */
    protected ApplicationController appController = null;

    /** Minimum value of the slider */
    private static final int SIZE_MIN = 30;

    /** Maximum value of the slider */
    private static final int SIZE_MAX = 100;

    /** Initial value of the slider */
    private static final int SIZE_INIT = 50; // (Initial shape size)

    /** Font for the slider tick labels (and size label) */
    private static final Font LABEL_FONT = new Font("Dialog", Font.PLAIN, 10);

    /** The preferred height of the slider component. */
    private static final int WANTED_HEIGHT = 41;

    /** The slider for size adjustment. */
    private JSlider shapeSizeSlider;

    /** The label to display the current value (when testing). */
    private JLabel sizeLabel;

    /**
     * Constructs the slider with a specified title and a reference to the
     * application controller.
     * 
     * @param title
     *            The title of the slider
     * @param controller
     *            The application controller
     */
    public SizeSlider(String title, ApplicationController controller) {
        this.appController = controller;

        // setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        // setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        // setLayout(new FlowLayout(FlowLayout.LEFT));
        if (appController == null) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 7, 0)); // 7 for self test
        } else {
            setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        }

        /* Create the label. */
        JLabel sliderLabel = new JLabel(title, JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        /* Create the slider. */
        shapeSizeSlider = new JSlider(JSlider.HORIZONTAL, SIZE_MIN, SIZE_MAX, SIZE_INIT);
        shapeSizeSlider.addChangeListener(this);

        /* Turn on labels at major tick marks. */
        shapeSizeSlider.setMajorTickSpacing(10);
        shapeSizeSlider.setMinorTickSpacing(5);
        shapeSizeSlider.setPaintTicks(true);
        shapeSizeSlider.setPaintLabels(true);
        shapeSizeSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        // Font font = new Font("Serif", Font.ITALIC, 12);
        // Font font = new Font("SansSerif", Font.ITALIC, 10);
        // Font font = new Font("Dialog", Font.PLAIN, 10);
        shapeSizeSlider.setFont(LABEL_FONT);

        if (appController == null) {
            /* Create the label that displays the size. */
            sizeLabel = new JLabel();
            sizeLabel.setHorizontalAlignment(JLabel.CENTER);
            sizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            sizeLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));

            /* Display the initial size. */
            updateSize();
        }

        /* Put everything together. */
        add(sliderLabel);
        add(shapeSizeSlider);
        if (appController == null) {
            add(sizeLabel);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            // setBorder(BorderFactory.createEtchedBorder());
        }

        /* Additional settings */

        // Preferred size?
        int labelWidth = shapeSizeSlider.getFontMetrics(LABEL_FONT).stringWidth(String.valueOf(SIZE_MAX));
        int sliderBaseWidth = shapeSizeSlider.getPreferredSize().width;
        Dimension dim = new Dimension(sliderBaseWidth + labelWidth, WANTED_HEIGHT);
        shapeSizeSlider.setPreferredSize(dim);

        /*
         * Allow direct left mouse click at a value.
         * (https://stackoverflow.com/a/518672/5944475)
         */
        shapeSizeSlider.setUI(new MetalSliderUI() {
            @Override
            protected void scrollDueToClickInTrack(int direction) {
                // Default behavior, commented out
                // scrollByBlock(direction);

                int value = shapeSizeSlider.getValue();

                if (shapeSizeSlider.getOrientation() == JSlider.HORIZONTAL) {
                    value = this.valueForXPosition(shapeSizeSlider.getMousePosition().x);
                } else if (shapeSizeSlider.getOrientation() == JSlider.VERTICAL) {
                    value = this.valueForYPosition(shapeSizeSlider.getMousePosition().y);
                }
                shapeSizeSlider.setValue(value);
            }
        });
    }

    /** Listen to the slider. */
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        // if (source.getValueIsAdjusting())
        // return;

        int size = (int) source.getValue();
        if (appController == null) {
            updateSize(size); // Only self test
        } else {
            appController.changeShapeSize(size);
        }
    }

    /** Update the label to display the current size. */
    protected void updateSize() {
        int size = shapeSizeSlider.getValue();
        sizeLabel.setText("" + size);
    }

    /**
     * Update the label to display the current size.
     * 
     * @param size
     *            the new size
     */
    protected void updateSize(int size) {
        sizeLabel.setText("" + size);
    }

}
