package de.lambeck.pned.models.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/*
 * Didn't work in the DrawPanel! Replaced by KeyBindings.
 */

/**
 * {@link KeyAdapter} for the {@link DrawPanel}. Holds a reference to the draw
 * panel to tell the draw panel which operations are allowed at the moment.
 * 
 * @formatter:off
 * Actions for keyboard events:
 * - ALT pressed:
 *   -> Allow dragging
 * - ALT released:
 *   -> Quit dragging
 * 
 * @formatter:on
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class MyKeyAdapter extends KeyAdapter {

    private static boolean debug = true;

    private DrawPanel myDrawPanel = null;

    /**
     * Constructs a keyboard adapter for the specified draw panel.
     * 
     * @param drawPanel
     *            The draw panel
     */
    public MyKeyAdapter(DrawPanel drawPanel) {
        this.myDrawPanel = drawPanel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        /*
         * VK_ALT?
         */
        if (e.getKeyCode() == KeyEvent.VK_ALT) {
            if (debug) {
                System.out.println("ALT pressed: We allow dragging...");
            }
            myDrawPanel.altKey_pressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        /*
         * VK_ALT?
         */
        if (e.getKeyCode() == KeyEvent.VK_ALT) {
            if (debug) {
                System.out.println("ALT released: We quit dragging.");
            }
            myDrawPanel.altKey_pressed = false;
        }
    }

}
