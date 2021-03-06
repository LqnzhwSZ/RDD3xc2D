package de.lambeck.pned.elements.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Test for the calculation of the arc anchors.
 * 
 * @author lambeck
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public class GuiTransitionArcAnchorTest extends JPanel {

    private static Dimension minSize = new Dimension(400, 300);

    @SuppressWarnings("javadoc")
    public static void main(String[] args) {
        GuiTransitionArcAnchorTest myTest = new GuiTransitionArcAnchorTest();
        myTest.doTest();
    }

    private void doTest() {
        JFrame frame = new JFrame("ArcAnchorTest");
        frame.setMinimumSize(minSize);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GuiTransitionArcAnchorTest panel = new GuiTransitionArcAnchorTest();
        panel.setPreferredSize(new Dimension(600, 600));

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString("ArcAnchorTest", 10, 20);

        // for (IGuiElement element : elements) {
        // element.paintElement(g);
        // }

        GuiTransition transition = new GuiTransition("1", "Transition 1", new Point(300, 300), 0);

        /*
         * Change size for testing!
         */
        // transition.setShapeSize(237);

        transition.paintElement(g);
        g.setColor(Color.BLACK);

        Point target;
        Point anchor;

        /*
         * The 4 corners
         */
        target = new Point(0, 0);
        anchor = transition.getArcAnchor(target);
        // System.out.println(anchor);
        g.drawLine(target.x, target.y, anchor.x, anchor.y);
        g.drawOval(anchor.x - 5, anchor.y - 5, 10, 10);

        target = new Point(600, 0);
        anchor = transition.getArcAnchor(target);
        // System.out.println(anchor);
        g.drawLine(target.x, target.y, anchor.x, anchor.y);
        g.drawOval(anchor.x - 5, anchor.y - 5, 10, 10);

        target = new Point(600, 600);
        anchor = transition.getArcAnchor(target);
        // System.out.println(anchor);
        g.drawLine(target.x, target.y, anchor.x, anchor.y);
        g.drawOval(anchor.x - 5, anchor.y - 5, 10, 10);

        target = new Point(0, 600);
        anchor = transition.getArcAnchor(target);
        // System.out.println(anchor);
        g.drawLine(target.x, target.y, anchor.x, anchor.y);
        g.drawOval(anchor.x - 5, anchor.y - 5, 10, 10);

        /*
         * Some random points
         */
        target = new Point(100, 200);
        anchor = transition.getArcAnchor(target);
        // System.out.println(anchor);
        g.drawLine(target.x, target.y, anchor.x, anchor.y);
        g.drawOval(anchor.x - 5, anchor.y - 5, 10, 10);

        target = new Point(250, 100);
        anchor = transition.getArcAnchor(target);
        // System.out.println(anchor);
        g.drawLine(target.x, target.y, anchor.x, anchor.y);
        g.drawOval(anchor.x - 5, anchor.y - 5, 10, 10);

        target = new Point(320, 270);
        anchor = transition.getArcAnchor(target);
        // System.out.println(anchor);
        g.drawLine(target.x, target.y, anchor.x, anchor.y);
        g.drawOval(anchor.x - 5, anchor.y - 5, 10, 10);

        target = new Point(500, 400);
        anchor = transition.getArcAnchor(target);
        // System.out.println(anchor);
        g.drawLine(target.x, target.y, anchor.x, anchor.y);
        g.drawOval(anchor.x - 5, anchor.y - 5, 10, 10);

        target = new Point(400, 500);
        anchor = transition.getArcAnchor(target);
        // System.out.println(anchor);
        g.drawLine(target.x, target.y, anchor.x, anchor.y);
        g.drawOval(anchor.x - 5, anchor.y - 5, 10, 10);

        target = new Point(50, 500);
        anchor = transition.getArcAnchor(target);
        // System.out.println(anchor);
        g.drawLine(target.x, target.y, anchor.x, anchor.y);
        g.drawOval(anchor.x - 5, anchor.y - 5, 10, 10);
    }

}
