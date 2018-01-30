package de.lambeck.pned.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.*;

import de.lambeck.pned.gui.menuBar.MenuBar;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Tests {@link MouseLocationFinder} with nested components.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class MouseLocationFinderTest {

    /** Show debug messages? */
    private static boolean debug = false;

    /* Starting the test */

    /**
     * Create the GUI and show it.
     */
    private static void createAndShowGUI() {
        /* Create and set up the window. */
        JFrame frame = new JFrame("MouseLocationFinderTest");
        frame.setMinimumSize(minSize);
        frame.setLocation(100, 100);

        /*
         * Get a few objects (like the manager for localized strings) that are
         * needed for application controller, draw panel etc.
         */
        I18NManager i18n = new I18NManager(new Locale("de", "DE"));

        /* Add the application controller. */
        @SuppressWarnings("unused")
        MouseLocationFinderTest myTest = new MouseLocationFinderTest(frame, i18n);

        /* Display the window. */
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Starts the application
     * 
     * @param args
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /* Attributes for an application with menu, tool bar and tabbed pane */

    /** The minimum size of the main application window */
    private static Dimension minSize = new Dimension(400, 300);

    /** Reference to the main application window */
    private JFrame mainFrame = null;

    /** The manager for localized strings */
    @SuppressWarnings("unused")
    private I18NManager i18n = null;

    /** Reference to the applications menu bar */
    @SuppressWarnings("unused")
    private MenuBar menuBar = null;

    /**
     * Lists of Action objects for menu bar and tool bar
     */
    protected Map<String, AbstractAction> allActions = new HashMap<>();

    /** The content pane for the main application window */
    private JPanel contentPane;

    /** The {@link JTabbedPane} with a tab for each file */
    private JTabbedPane tabbedPane;

    /* Constructor */

    /**
     * Constructs the test with a reference to the top component (main frame) of
     * the application.
     * 
     * @param frame
     *            The main frame (window) of the application
     * @param i18n
     *            The manager for localized strings
     */
    @SuppressWarnings("hiding")
    public MouseLocationFinderTest(JFrame frame, I18NManager i18n) {
        super();
        this.mainFrame = frame;
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* Add nested components */
        this.tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        this.contentPane = new JPanel(new BorderLayout());
        this.contentPane.add(tabbedPane, BorderLayout.CENTER);

        mainFrame.setContentPane(contentPane);

        /* Add menu, tool and status bar. */
        MenuBar menuBar = new MenuBar(frame, i18n, allActions);
        mainFrame.setJMenuBar(menuBar);

        JToolBar toolBar = new JToolBar();
        mainFrame.add(toolBar, BorderLayout.PAGE_START);

        // mainFrame.getContentPane().add(statusBar, BorderLayout.SOUTH);

        doTest();
    }

    private void doTest() {
        System.out.println("doTest()");

        System.out.println();
        System.out.println("Test 1 (main frame)...");

        Point mouseLocOnScreen = new Point(200, 200);
        System.out.println("mouseLocationOnScreen: " + mouseLocOnScreen);
        Point convertedPoint = MouseLocationFinder.findLocationOverComponent(mouseLocOnScreen, mainFrame, mainFrame);
        System.out.println("convertedPoint: " + convertedPoint);

        System.out.println();
        System.out.println("Test 2 (nested JPanel)...");

        addNewEmptyFile(0);
    }

    /* Copied/adapted methods from ApplicationController */

    /**
     * Returns the applications main frame to other classes.
     * 
     * @return The main application window as {@link JFrame}
     */
    public JFrame getMainFrame() {
        return this.mainFrame;
    }

    /**
     * Adds a new (non-existing) file.
     */
    private void addNewEmptyFile(int currNewFileIndex) {
        String fileName = "FileNew" + currNewFileIndex;
        String displayName = fileName; // Only for a new file!
        addEmptyTab(fileName, displayName);
    }

    /**
     * Adds a new Tab.<BR>
     * <BR>
     * Note: Used by FileNew<BR>
     * 
     * @param fullName
     *            The full path name of the PNML file
     * @param displayName
     *            The title of the tab (= the file name)
     */
    private void addEmptyTab(String fullName, String displayName) {
        JPanel drawPanel = new JPanel();
        addTabForDrawPanel(drawPanel, fullName, displayName);
    }

    /**
     * Adds a new Tab for the specified {@link JPanel}.<BR>
     * <BR>
     * Note: The full path name us used as tool tip for the tab. (This can be
     * used later to determine the current active file.)
     * 
     * @param drawPanel
     *            The draw panel
     * @param fullName
     *            The full path name of the PNML file. (Intended to be the
     *            modelName of the draw panel!)
     * @param displayName
     *            The title of the tab (= the file name)
     */
    private void addTabForDrawPanel(JPanel drawPanel, String fullName, String displayName) {
        /* Add the draw panel to a scroll pane on a new tab. */
        JScrollPane scrollPanel = new JScrollPane();
        scrollPanel.setViewportView(drawPanel);

        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(scrollPanel, BorderLayout.CENTER);

        this.tabbedPane.addTab(displayName, null, jPanel, fullName);

        /*
         * Store the index of the added tab. (The command .indexOfTab(fileName)
         * cannot be used because it returns always the 1st tab if two files
         * have the same name!)
         */
        int index = tabbedPane.getTabCount() - 1;
        if (debug) {
            System.out.println("tabbedPane.getToolTipTextAt(index): " + tabbedPane.getToolTipTextAt(index));
        }

        /* Activate the new tab */
        this.tabbedPane.setSelectedIndex(index);
        this.tabbedPane.requestFocus();

        /* Test a specified Point */
        Point mouseLocOnScreen = new Point(200, 200);
        System.out.println("mouseLocOnScreen: " + mouseLocOnScreen);

        Point convertedPoint = MouseLocationFinder.findLocationOverComponent(mouseLocOnScreen, jPanel, mainFrame);
        System.out.println("convertedPoint: " + convertedPoint);
    }

}
