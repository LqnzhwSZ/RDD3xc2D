package de.lambeck.pned.util;

// TODO Not necessary, better: SwingUtilities.convertPointFromScreen(p, c);

import java.awt.Component;
import java.awt.Point;

import javax.swing.SwingUtilities;

@SuppressWarnings("javadoc")
public class MouseLocationFinder {

    // public static Point findLocationOverComponent(Point mouseLocation,
    // Component currComp, Component topComp) {
    // System.out.println("currComp.getClass(): " + currComp.getClass());
    //
    // Point convertedCoordinate = mouseLocation;
    //
    // /* Quit recursion? */
    // if (currComp == topComp) {
    // System.out.println("Finish recursion.");
    //
    // /* At last: subtract the location of the main frame */
    // int dx = topComp.getX();
    // int dy = topComp.getY();
    // convertedCoordinate.translate(-dx, -dy);
    //
    // } else {
    // System.out.println("Next recursion level...");
    //
    // /* Convert to parent's coordinate system */
    // Component parent = currComp.getParent();
    // System.out.println("parent.getClass(): " + parent.getClass());
    // convertedCoordinate = SwingUtilities.convertPoint(currComp,
    // mouseLocation, parent);
    //
    // /* Recursive call for the parent component */
    // convertedCoordinate = findLocationOverComponent(mouseLocation, parent,
    // topComp);
    //
    // }
    //
    // return convertedCoordinate;
    // }

    public static Point findLocationOverComponent(Point mouseLocation, Component currComp, Component topComp) {
        System.out.println("currComp.getClass(): " + currComp.getClass());

        Point convertedCoordinate = mouseLocation;

        /* Subtract the location of this component. */
        int dx = currComp.getX();
        int dy = currComp.getY();
        convertedCoordinate.translate(-dx, -dy);
        System.out.println("convertedCoordinate: " + convertedCoordinate);

        /* Quit recursion? */
        if (currComp == topComp) {
            System.out.println("Finish recursion.");

        } else {
            System.out.println("Next recursion level...");

            /* Convert to parent's coordinate system */
            Component parent = currComp.getParent();
            System.out.println("parent.getClass(): " + parent.getClass());
            convertedCoordinate = SwingUtilities.convertPoint(currComp, mouseLocation, parent);

            /* Recursive call for the parent component */
            convertedCoordinate = findLocationOverComponent(mouseLocation, parent, topComp);

        }

        return convertedCoordinate;
    }

}
