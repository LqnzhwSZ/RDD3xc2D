package de.lambeck.pned.elements.gui;

@SuppressWarnings("javadoc")
public class GradientTest {

    public static void main(String[] args) {
        double target_x = 100;
        double target_y = 100;

        double shape_center_x = 200;
        double shape_center_y = 200;

        double x_dist = target_x - shape_center_x;
        System.out.println("x_dist: " + x_dist);
        double y_dist = target_y - shape_center_y;
        System.out.println("y_dist: " + y_dist);

        double gradient = x_dist / y_dist;
        System.out.println("gradient: " + gradient);
    }

}
