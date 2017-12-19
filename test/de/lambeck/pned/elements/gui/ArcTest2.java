package de.lambeck.pned.elements.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.GeneralPath;

import javax.swing.JApplet;
import javax.swing.JFrame;

/*
 * Siehe: Tutorial - Working with geometry
 * 
 * https://docs.oracle.com/javase/tutorial/2d/geometry/index.html
 * 
 * https://docs.oracle.com/javase/tutorial/2d/geometry/examples/ShapesDemo2D.
 * java
 */
@SuppressWarnings({ "javadoc", "serial" })
public class ArcTest2 extends JApplet {

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // fill and stroke GeneralPath
//        int x3Points[] = { 0, 100, 50 };
//        int y3Points[] = { 0, 0, 100 };
        int x3Points[] = { 200, 100, 90 };
        int y3Points[] = { 160, 100, 140 };

        GeneralPath filledPolygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, x3Points.length);
//        filledPolygon.moveTo(x3Points[0], y3Points[0]);
        filledPolygon.moveTo(x3Points[0], y3Points[0]);
        for (int index = 1; index < x3Points.length; index++) {
            filledPolygon.lineTo(x3Points[index], y3Points[index]);
        }

        filledPolygon.closePath();
        g2.setPaint(Color.RED);
        g2.fill(filledPolygon);
        g2.setPaint(Color.BLACK);
        g2.draw(filledPolygon);
    }

    public static void main(String s[]) {
        JFrame f = new JFrame("ShapesDemo2D");
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        JApplet applet = new ArcTest2();
        f.getContentPane().add("Center", applet);
        applet.init();
        f.pack();
        f.setSize(new Dimension(600, 400));
        f.setVisible(true);
    }

}
