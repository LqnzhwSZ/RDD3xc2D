package de.lambeck.pned.elements.gui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings({ "javadoc", "serial" })
public class Arrow_Test extends JPanel implements ChangeListener {

    double theta = 0;
    Path2D arrow = new Arrow(36, 10);

    @Override
    public void stateChanged(ChangeEvent e) {
        int value = ((JSlider) e.getSource()).getValue();
        theta = Math.toRadians(value);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2d.setStroke(new BasicStroke(4));

        double x = (getWidth() - arrow.getBounds().getWidth()) / 2d;
        double y = (getHeight() - arrow.getBounds().getHeight()) / 2d;

        AffineTransform at = new AffineTransform();
        at.translate(x, y);
        at.rotate(theta, arrow.getBounds().getWidth() / 2d, arrow.getBounds().getHeight() / 2d);
        g2d.setTransform(at);

        g2d.draw(arrow);
        g2d.drawLine(-100, 0, 100, 0);
        g2d.dispose();
    }

    private JSlider getSlider() {
        JSlider slider = new JSlider(-180, 180, 0);
        slider.addChangeListener(this);
        return slider;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                        | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                Arrow_Test test = new Arrow_Test();
                JFrame f = new JFrame();
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.add(test);
                f.add(test.getSlider(), "Last");
                f.setSize(400, 400);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }

    public class Arrow extends Path2D.Double {

        public Arrow(double length, double width) {
            // moveTo(0, 10);
            // lineTo(36, 10);
            // moveTo(36 - 16, 0);
            // lineTo(36, 10);
            // moveTo(36 - 16, 20);
            // lineTo(36, 10);

//            moveTo(0, width);
//            lineTo(length, width);
//            moveTo(length / 2, 0);
//            lineTo(length, width);
//            moveTo(length / 2, 2 * width);
//            lineTo(length, width);

            moveTo(length, 0);
            lineTo(0, width);
            lineTo(0, -width);
            lineTo(length, 0);
        }

    }
}
