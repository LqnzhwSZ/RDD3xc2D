package de.lambeck.pned.application;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("javadoc")
public class CombineAreasTest {

    /**
     * Combines the areas of 1 {@link Rectangle} and several {@link Rectangle}
     * to 1 area.
     * 
     * @param startArea
     *            The first {@link Rectangle}
     * @param areasToAdd
     *            The {@link List} of type {@link Rectangle} to add
     * @return A {@link Rectangle} that contains all specified areas
     */
    private Rectangle combineAreas(Rectangle startArea, List<Rectangle> areasToAdd) {
        Rectangle resultArea = startArea;

        for (Rectangle r : areasToAdd) {
            resultArea.add(r);
        }

        return resultArea;
    }

    public static void main(String[] args) {
        CombineAreasTest myTest = new CombineAreasTest();
        myTest.doTest();
    }

    void doTest() {
        Rectangle startArea = null;
        List<Rectangle> areasToAdd = new ArrayList<Rectangle>();

        Rectangle resultArea = combineAreas(startArea, areasToAdd);
        if (resultArea == null)
            System.out.println("OK, resultArea == null");
    }
}
