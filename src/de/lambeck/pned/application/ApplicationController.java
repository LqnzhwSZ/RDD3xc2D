package de.lambeck.pned.application;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.*;

import de.lambeck.pned.application.actions.*;
import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.elements.data.DataArc;
import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.data.DataTransition;
import de.lambeck.pned.elements.data.IDataElement;
import de.lambeck.pned.elements.gui.*;
import de.lambeck.pned.filesystem.FSInfo;
import de.lambeck.pned.filesystem.pnml.PNMLWriter;
import de.lambeck.pned.gui.menuBar.MenuBar;
import de.lambeck.pned.gui.settings.SizeSlider;
import de.lambeck.pned.gui.statusBar.StatusBar;
import de.lambeck.pned.gui.toolBar.PnedToolBar;
import de.lambeck.pned.i18n.I18NManager;
import de.lambeck.pned.models.data.DataModelController;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;
import de.lambeck.pned.models.data.validation.*;
import de.lambeck.pned.models.gui.*;

/**
 * Extends the abstract application controller. Observes the application (e.g.
 * attempt to close with unsaved changes). Holds a reference to the status bar
 * so that the application controller can show status messages etc.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ApplicationController extends AbstractApplicationController {

    private static boolean debug = false;

    /** The application title to begin with */
    private static String initialTitle = "Petri net Editor - Thomas Lambeck, MatrNr. 4128320";

    /**
     * This attribute defines the preferred width and height of the application
     * window in comparison to the width and height of the screen.
     */
    private double useScreenPercentage = 66.7;

    /** The content pane for the main application window */
    private JPanel contentPane;

    /** The {@link JTabbedPane} with a tab for each file */
    private JTabbedPane tabbedPane;

    /** Reference to the {@link IDataModelController} */
    private IDataModelController dataModelController;

    /** Reference to the {@link IGuiModelController}. */
    private IGuiModelController guiModelController;

    /**
     * This is the list of open files. (The full path name of the file or
     * "New1", "New2"... for new models)
     */
    private List<String> fileList = new ArrayList<String>();

    /**
     * Counter for new models (e.g. "Untitled1", "Untitled2"... or "New1",
     * "New2"...); counts continuously upwards.
     */
    private int currNewFileIndex = -1;

    /**
     * Indicates whether we are importing data from a PNML file or not. This is
     * important to avoid infinite loops when adding elements.
     * 
     * (If true: changes to a data model need to be passed to the GUI model. If
     * false: changes to a GUI model need to be passed to the data model.)
     */
    private boolean importingFromPnml = false;

    /**
     * List of data models which need to be saved.
     */
    private List<String> modifiedDataModels = new ArrayList<String>();

    /** Reference to the {@link ValidationController} */
    private ValidationController validationController;

    /**
     * Constructs the application controller for the specified main frame
     * (application window).
     * 
     * @param frame
     *            The main frame (window) of the application
     * @param i18n
     *            The source object for I18N strings
     * @param stBar
     *            The status bar (of this application)
     */
    @SuppressWarnings("hiding")
    public ApplicationController(JFrame frame, I18NManager i18n, StatusBar stBar) {
        super(frame, i18n, stBar);
        mainFrame.addComponentListener(new ComponentResizeListener(this));

        /*
         * Replace the DefaultCloseOperation with the application controller's
         * windowClosing() method to observe the state of the application.
         */
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(this);
        mainFrame.addWindowStateListener(this);

        /*
         * Add controllers for data models, GUI models and validation
         */
        addControllers(i18n);

        /*
         * Add validators to the validation controller.
         */
        addValidators(i18n);

        /*
         * Create and set up the content pane (BEFORE adding menu, tool and
         * status bars!) add the content to it.
         */
        this.tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        this.tabbedPane.addChangeListener(new TabListener(this));

        this.contentPane = new JPanel(new BorderLayout());
        this.contentPane.add(tabbedPane, BorderLayout.CENTER);

        mainFrame.setContentPane(contentPane);

        /*
         * Add menu, tool and status bar.
         */
        MenuBar menuBar = new MenuBar(frame, this, i18n);
        JToolBar toolBarElements = new PnedToolBar(this, i18n);

        toolBarElements.addSeparator();
        String sizeSliderName = i18n.getNameOnly("ElementsDisplaySize");
        toolBarElements.add(new SizeSlider(sizeSliderName, this));

        mainFrame.setJMenuBar(menuBar);
        mainFrame.add(toolBarElements, BorderLayout.PAGE_START);
        mainFrame.getContentPane().add(statusBar, BorderLayout.SOUTH);

        /*
         * Additional settings
         */
        Dimension preferredSize = getScreenDependingMinimumSize();
        mainFrame.setPreferredSize(preferredSize);

        // Ctrl-Tab switch tabs in a JTabbedPane
        setupTabTraversalKeys(tabbedPane);

        /*
         * Set focus to the TabbedPane to use the keyboard to cycle through the
         * tabs. Works only for cursors before calling setupTabTraversalKeys().
         */
        this.tabbedPane.requestFocus();

        // TODO Create a HashMap with all names and messages in the current
        // language and pass only needed (already localized) Strings to
        // methods/other classes? (So that static methods as in FSInfo.java can
        // use localized messages.)

        /*
         * Start the validation controller (thread).
         */
        this.validationController.start();
    }

    /**
     * Adds all necessary controllers.
     * 
     * @param i18n
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    private void addControllers(I18NManager i18n) {
        this.dataModelController = new DataModelController(this, i18n);
        this.guiModelController = new GuiModelController(this, i18n, this.popupActions);
        this.validationController = new ValidationController(this.dataModelController, i18n);
    }

    /**
     * Adds all necessary {@link IValidator} to the
     * {@link ValidationController}.
     * 
     * @param i18n
     *            The source object for I18N strings
     */
    @SuppressWarnings("hiding")
    private void addValidators(I18NManager i18n) {
        IValidator startPlacesValidator = new StartPlacesValidator(1, validationController, dataModelController, i18n);
        this.validationController.addValidator(startPlacesValidator);

        IValidator endPlacesValidator = new EndPlacesValidator(2, validationController, dataModelController, i18n);
        this.validationController.addValidator(endPlacesValidator);

        IValidator allNodesOnPathsValidator = new AllNodesOnPathsValidator(3, validationController, dataModelController,
                i18n);
        this.validationController.addValidator(allNodesOnPathsValidator);

        IValidator initialMarkingValidator = new InitialMarkingValidator(4, validationController, dataModelController,
                i18n);
        this.validationController.addValidator(initialMarkingValidator);
    }

    /*
     * Helper methods
     */

    /**
     * Returns a size for the main frame depending on the size of the screen and
     * the scale factor.
     * 
     * @return The minimum size
     */
    private Dimension getScreenDependingMinimumSize() {
        if (useScreenPercentage < 25)
            useScreenPercentage = 25;
        if (useScreenPercentage > 100)
            useScreenPercentage = 100;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        int appWidth = (int) (screenWidth * useScreenPercentage / 100);
        int appHeight = (int) (screenHeight * useScreenPercentage / 100);
        Dimension appSize = new Dimension(appWidth, appHeight);

        return appSize;
    }

    /**
     * Makes Ctrl-Tab switch tabs in a JTabbedPane. See:
     * http://www.davidc.net/programming/java/how-make-ctrl-tab-switch-tabs-jtabbedpane
     * 
     * @param tabbedPane
     *            The TabbedPane to change
     */
    private static void setupTabTraversalKeys(JTabbedPane tabbedPane) {
        KeyStroke ctrlTab = KeyStroke.getKeyStroke("ctrl TAB");
        KeyStroke ctrlShiftTab = KeyStroke.getKeyStroke("ctrl shift TAB");

        // Remove ctrl-tab from normal focus traversal
        Set<AWTKeyStroke> forwardKeys = new HashSet<AWTKeyStroke>(
                tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forwardKeys.remove(ctrlTab);
        tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

        // Remove ctrl-shift-tab from normal focus traversal
        Set<AWTKeyStroke> backwardKeys = new HashSet<AWTKeyStroke>(
                tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backwardKeys.remove(ctrlShiftTab);
        tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

        // Add keys to the tab's input map
        InputMap inputMap = tabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(ctrlTab, "navigateNext");
        inputMap.put(ctrlShiftTab, "navigatePrevious");
    }

    /*
     * Methods for super class AbstractApplicationController
     */

    @Override
    protected void addAllActionsToHashMaps() {
        // Menu "File"
        allActions.put("FileNew", new FileNewAction(this, i18n));
        allActions.put("FileOpen...", new FileNewAction(this, i18n));
        allActions.put("FileClose", new FileNewAction(this, i18n));
        allActions.put("FileSave", new FileNewAction(this, i18n));
        allActions.put("FileSaveAs...", new FileNewAction(this, i18n));
        allActions.put("AppExit", new FileNewAction(this, i18n));

        // Menu "Edit"
        allActions.put("EditRename...", new FileNewAction(this, i18n));
        allActions.put("EditDelete", new FileNewAction(this, i18n));
        allActions.put("EditSettings...", new FileNewAction(this, i18n));

        // Tool bar "Elements"
        allActions.put("ElementToTheForeground", new FileNewAction(this, i18n));
        allActions.put("ElementOneLayerUp", new FileNewAction(this, i18n));
        allActions.put("ElementOneLayerDown", new FileNewAction(this, i18n));
        allActions.put("ElementToTheBackground", new FileNewAction(this, i18n));

        // Tool bar "Testrun"
        allActions.put("TestingStep", new FileNewAction(this, i18n));
        allActions.put("TestingComplete", new FileNewAction(this, i18n));
        allActions.put("TestingQuit", new FileNewAction(this, i18n));

        // Popup menu "Elements"
        popupActions.put("ElementSelect", new ElementSelectAction(this, i18n));
        popupActions.put("ElementToTheForeground", new ElementToTheForegroundAction(this, i18n));
        popupActions.put("ElementOneLayerUp", new ElementOneLayerUpAction(this, i18n));
        popupActions.put("ElementOneLayerDown", new ElementOneLayerDownAction(this, i18n));
        popupActions.put("ElementToTheBackground", new ElementToTheBackgroundAction(this, i18n));

        popupActions.put("NewArcFromHere", new NewArcFromHereAction(this, i18n));
        popupActions.put("NewArcToHere", new NewArcToHereAction(this, i18n));

        // Popup menu "Empty area"
        popupActions.put("NewPlace", new NewPlaceAction(this, i18n));
        popupActions.put("NewTransition", new NewTransitionAction(this, i18n));
    }

    /**
     * Asks the data model controller for it's list of models that have been
     * changed and need to be saved.
     */
    private void updateModifiedDataModelsList() {
        this.modifiedDataModels = dataModelController.getModifiedDataModels();
    }

    @Override
    public boolean getAllowedToClose() {
        return this.allowedToClose;
    }

    /**
     * Method for WindowAdapter. Intercepts the attempt to close the main window
     * of the application if the user has to save open files.
     */
    @Override
    public void windowClosing(WindowEvent e) {
        /*
         * Close all unmodified files immediately.
         */
        closeUnmodifiedFiles();

        /*
         * Note: Update allowedToClose state here because the menu command
         * AppExit is called only if the user closes the application via the
         * menu bar. But we also have to catch if the user is trying to close
         * the main window directly!
         */
        boolean close;
        updateAllowedToClose();
        close = getAllowedToClose();
        if (debug) {
            System.out.println("close = getAllowedToClose(); close = " + close);
        }
        if (close)
            closeApplication();

        /*
         * Ask the user to save all modified models to a file.
         */
        int answer = askSaveModifiedModelsBeforeExit();
        if (debug) {
            System.out.println("AppController.windowClosing(), askSaveModifiedModelsBeforeExit(), answer: " + answer);
        }

        if (answer == ExitCode.OPERATION_CANCELLED)
            return;
        if (answer == ExitCode.OPERATION_FAILED)
            return;

        /*
         * Check for modified models again and close the application if the user
         * has saved all changes.
         */
        updateAllowedToClose();
        close = getAllowedToClose();
        if (debug) {
            System.out.println("close = getAllowedToClose(); close = " + close);
        }
        if (close)
            closeApplication();
        System.out.println("Application not closed!");
    }

    /**
     * Updates the attribute allowedToClose.
     */
    private void updateAllowedToClose() {
        /*
         * Check if it's safe to close the application.
         */
        updateModifiedDataModelsList();
        int allModifiedDataModelsCount = modifiedDataModels.size();
        this.allowedToClose = (allModifiedDataModelsCount == 0);
    }

    /**
     * Closes the application (without questions!)
     */
    private void closeApplication() {
        this.validationController.interrupt();
        mainFrame.dispose();
        System.exit(0);
    }

    /*
     * Method for the ComponentResizeListener
     */

    /**
     * Callback for the ComponentResizeListener
     * 
     * @param arg0
     *            The ComponentEvent
     */
    void componentResized(ComponentEvent arg0) {
        updateDrawPanelSizeInfo();
    }

    /**
     * Updates the draw panels size on the status bar.
     */
    private void updateDrawPanelSizeInfo() {
        IDrawPanel activeDrawPanel = guiModelController.getCurrentDrawPanel();
        if (activeDrawPanel == null) {
            setInfo_DrawingAreaSize(-1, -1);
        } else {
            int drawPanelWidth = activeDrawPanel.getPreferredSize().width;
            int drawPanelHeight = activeDrawPanel.getPreferredSize().height;
            setInfo_DrawingAreaSize(drawPanelWidth, drawPanelHeight);
        }
    }

    /*
     * Methods for the TabListener
     */

    /**
     * Adds the file name to the title of the main frame.
     * 
     * Note: This method is visible in this package because the TabListener uses
     * this method!
     * 
     * @param filename
     *            The name of the active file
     */
    void setFilenameOnTitle(String filename) {
        String newTitle = "";

        if (filename.equals("")) {
            newTitle = initialTitle;
        } else {
            newTitle = filename + "   -   " + initialTitle;
        }
        mainFrame.setTitle(newTitle);
    }

    /**
     * Callback for the TabListener
     * 
     * Note: This method is visible in this package because the TabListener uses
     * this method!
     * 
     * @param tabIndex
     *            The index of the active tab
     */
    void setActiveFile(int tabIndex) {
        // IDrawPanel drawPanel = null;
        // Component component = tabbedPane.getSelectedComponent();
        // if (component instanceof IDrawPanel) {
        // drawPanel = (IDrawPanel) component;
        // }
        //
        // if (drawPanel == null) {
        // System.err.println("Could not determine the draw panel on tab " +
        // tabIndex + "!");
        // return;
        // }
        //
        // this.activeFile = drawPanel.getModelName();

        /*
         * Easier with the full path name as tool tip on the tabs!
         */
        if (tabIndex < 0) {
            this.activeFile = null;
        } else {
            this.activeFile = tabbedPane.getToolTipTextAt(tabIndex);
        }

        /*
         * Update the current models/draw panels of data and GUI controller.
         */
        if (activeFile != null) {
            IDataModel newActiveDataModel = dataModelController.getDataModel(activeFile);
            if (newActiveDataModel == null) {
                System.err.println("setActiveFile, DataModel for '" + activeFile + "' does not exist!");
            }
            dataModelController.setCurrentModel(newActiveDataModel);

            IGuiModel newActiveGuiModel = guiModelController.getGuiModel(activeFile);
            if (newActiveGuiModel == null) {
                System.err.println("setActiveFile, GuiModel for '" + activeFile + "' does not exist!");
            }
            guiModelController.setCurrentModel(newActiveGuiModel);

            IDrawPanel newActiveDrawPanel = guiModelController.getDrawPanel(activeFile);
            if (newActiveDrawPanel == null) {
                System.err.println("setActiveFile, DrawPanel for '" + activeFile + "' does not exist!");
            }
            guiModelController.setCurrentDrawPanel(newActiveDrawPanel);
        }

        /*
         * Update the status bar
         */
        updateDrawPanelSizeInfo();

        if (debug) {
            System.out.println("ApplicationController.setActiveFile, new active file: " + this.activeFile);
        }
    }

    /**
     * Determines the active tab "manually" to call setActiveFile(tabIndex).
     * 
     * Note: This might be necessary in case there is no tab stateChanged event
     * in the {@link TabListener} even if "current file" is no longer valid.
     * 
     * Example: If the {@link IDataModelController} discards a corrupted file,
     * all "current models" etc. that were prepared for this file will be reset
     * to null. And because the tab for this file was not displayed yet, the
     * {@link TabListener} cannot notice that another file is now active.
     */
    private void refreshActiveFile() {
        int tabIndex = tabbedPane.getSelectedIndex();
        setActiveFile(tabIndex);
    }

    /*
     * Implemented menu commands for the Actions
     */

    /**
     * Callback for {@link FileNewAction}, creates a new file.
     */
    public void menuCmd_FileNew() {
        if (debug) {
            String testMsg = "Menu command: FileNew";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        addNewEmptyFile();
    }

    /**
     * Callback for {@link FileOpenAction}, opens an existing file.
     * 
     * Note: This method should be called by the FileOpenAction after getting a
     * file.
     * 
     * @param pnmlFile
     *            The file chosen by the user
     */
    public void menuCmd_FileOpen(File pnmlFile) {
        if (debug) {
            String testMsg = "Menu command: FileOpen...";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        if (isFileAlreadyOpen(pnmlFile))
            return;

        addNewModelFromFile(pnmlFile);
    }

    /**
     * Callback for {@link FileCloseAction}, closes the active file.
     */
    public void menuCmd_FileClose() {
        if (debug) {
            String testMsg = "Menu command: FileClose";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        closeActiveFile();
    }

    /**
     * Callback for {@link FileSaveAction}, saves the active file.
     */
    public void menuCmd_FileSave() {
        if (debug) {
            String testMsg = "Menu command: FileSave";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        saveActiveFile();
    }

    /**
     * Callback for {@link FileSaveAsAction}, saves the model as the specified
     * file.
     * 
     * Note: This method should be called by the FileSaveAsAction after getting
     * a file.
     * 
     * @param pnmlFile
     *            The file chosen by the user
     */
    public void menuCmd_FileSaveAs(File pnmlFile) {
        if (debug) {
            String testMsg = "Menu command: FileSaveAs...";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        saveActiveFileAs(pnmlFile);
    }

    /**
     * Callback for {@link AppExitAction}, closes the application
     */
    public void menuCmd_AppExit() {
        if (debug) {
            String testMsg = "Menu command: AppExit";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        /*
         * Try to close the application using the windowClosing method, which
         * will check the attribute allowedToClose.
         */
        windowClosing(null);
    }

    /**
     * Callback for {@link EditRenameAction}, renames a node in the current
     * Petri net.
     */
    public void menuCmd_EditRename() {
        if (debug) {
            String testMsg = "Menu command: EditRename...";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        guiModelController.keyEvent_F2_Occurred();
    }

    /**
     * Callback for {@link EditDeleteAction}, deletes an element in the current
     * Petri net.
     */
    public void menuCmd_EditDelete() {
        if (debug) {
            String testMsg = "Menu command: EditDelete";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        removeSelectedGuiElements();
    }

    /**
     * Callback for {@link EditSettingsAction}, shows an input dialog.
     */
    public void menuCmd_EditSettings() {
        if (debug) {
            String testMsg = "Menu command: EditSettings...";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        // TODO Implement a SettingsManager
    }

    /**
     * Callback for {@link ElementSelectAction}, selects the current element.
     */
    public void menuCmd_ElementSelect() {
        if (debug) {
            String testMsg = "Menu command: ElementSelect";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        guiModelController.selectElementAtPopupMenu();
    }

    /**
     * Callback for {@link ElementToTheForegroundAction}, moves the current
     * {@link IGuiElement} to the foreground.
     */
    public void menuCmd_ElementToTheForeground() {
        if (debug) {
            String testMsg = "Menu command: ElementToTheForeground";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        guiModelController.moveElementToForeground();
    }

    /**
     * Callback for {@link ElementToTheBackgroundAction}, moves the current
     * {@link IGuiElement} to the foreground.
     */
    public void menuCmd_ElementToTheBackground() {
        if (debug) {
            String testMsg = "Menu command: ElementToTheBackground";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        guiModelController.moveElementToBackground();
    }

    /**
     * Callback for {@link ElementOneLayerUpAction}, moves the current
     * {@link IGuiElement} 1 layer up.
     */
    public void menuCmd_ElementOneLayerUp() {
        if (debug) {
            String testMsg = "Menu command: ElementOneLayerUp";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        guiModelController.moveElementOneLayerUp();
    }

    /**
     * Callback for {@link ElementOneLayerDownAction}, moves the current
     * {@link IGuiElement} 1 layer down.
     */
    public void menuCmd_ElementOneLayerDown() {
        if (debug) {
            String testMsg = "Menu command: ElementOneLayerDown";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        guiModelController.moveElementOneLayerDown();
    }

    /**
     * Callback for {@link NewPlaceAction}, creates a new {@link IGuiPlace} in
     * the current {@link IGuiModel}.
     */
    public void menuCmd_NewPlace() {
        if (debug) {
            String testMsg = "Menu command: NewPlace";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        guiModelController.createNewPlaceInCurrentGuiModel();
    }

    /**
     * Callback for {@link NewTransitionAction}, creates a new
     * {@link GuiTransition} in the current {@link IGuiModel}.
     */
    public void menuCmd_NewTransition() {
        if (debug) {
            String testMsg = "Menu command: NewTransition";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        guiModelController.createNewTransitionInCurrentGuiModel();
    }

    /**
     * Callback for {@link NewArcFromHereAction}, sets the location for the
     * source of the new {@link IGuiArc} in the current {@link IGuiModel}.
     */
    public void menuCmd_NewArcFromHere() {
        guiModelController.setSourceNodeForNewArc();
    }

    /**
     * Callback for {@link NewArcToHereAction}, sets the location for the target
     * of the new {@link IGuiArc} in the current {@link IGuiModel}.
     */
    public void menuCmd_NewArcToHere() {
        guiModelController.setTargetNodeForNewArc();
    }

    /**
     * Callback for ActionTestingStep, test 1 step forward
     */
    public void menuCmd_TestingStep() {
        if (debug) {
            String testMsg = "Menu command: TestingStep";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        // TODO Implement
    }

    /**
     * Callback for TestingCompleteAction, complete test
     */
    public void menuCmd_TestingComplete() {
        if (debug) {
            String testMsg = "Menu command: TestingComplete";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        // TODO Implement
    }

    /**
     * Callback for TestingQuitAction, stop test
     */
    public void menuCmd_TestingQuit() {
        if (debug) {
            String testMsg = "Menu command: TestingQuit";
            setInfo_Status(testMsg, EStatusMessageLevel.INFO);
            System.out.println(testMsg);
        }

        // TODO Implement
    }

    /**
     * Callback for the {@link SizeSlider} to change the size of the elements on
     * the draw panels.
     * 
     * @param size
     *            The new size
     */
    public void changeShapeSize(int size) {
        guiModelController.changeShapeSize(size);
    }

    /*
     * End of menu commands for the Actions
     */

    /**
     * Adds a new (non-existing) file.
     */
    private void addNewEmptyFile() {
        /*
         * Get the next new file name
         */
        currNewFileIndex++;
        String fileName = i18n.getNameOnly("FileNew") + currNewFileIndex;

        /*
         * Add a tab with empty models and empty draw panel.
         */
        String displayName = fileName; // Only for a new file!
        addEmptyTab(fileName, displayName);
    }

    /**
     * Adds a new Tab with empty data and GUI models.
     * 
     * Note: Used by FileNew
     * 
     * Note: The full path name us used as tool tip for the tab. (This can be
     * used later to determine the current active file.)
     * 
     * @param fullName
     *            The full path name of the PNML file
     * @param displayName
     *            The title of the tab (= the file name)
     */
    private void addEmptyTab(String fullName, String displayName) {
        /*
         * Create empty models.
         */
        dataModelController.addDataModel(fullName, displayName);
        guiModelController.addGuiModel(fullName, displayName);

        /*
         * Get the draw panel for this tab from the GUI controller, which has
         * created it in his addGuiModel() method.
         */
        DrawPanel drawPanel = (DrawPanel) guiModelController.getDrawPanel(fullName);

        /*
         * Get the validation messages panel for this tab from the data
         * controller, which has created it in his addDataModel() method.
         */
        IValidationMsgPanel validationMessagesPanel = dataModelController.getValidationMessagePanel(fullName);

        /*
         * Add the draw panel to a scroll pane on a new tab.
         */
        addTabForDrawPanel(drawPanel, validationMessagesPanel, fullName, displayName);
    }

    /**
     * Adds the specified existing file.
     * 
     * @param pnmlFile
     *            The {@link File} chosen by the user
     * @see {@link FileOpenAction}
     */
    private void addNewModelFromFile(File pnmlFile) {
        /*
         * Get the (unique) canonical path name of the specified file.
         */
        String canonicalPath = FSInfo.getCanonicalPath(pnmlFile);
        if (canonicalPath == null) {
            String errMessage = i18n.getMessage("errFileOpen");
            errMessage = errMessage.replace("%fullName%", canonicalPath);
            System.err.println(errMessage);
            setInfo_Status(errMessage, EStatusMessageLevel.ERROR);
            return;
        }

        /*
         * Get the file name of the specified file.
         */
        String displayName = FSInfo.getFileName(pnmlFile);

        /*
         * Create a GUI model first because importing elements into the data
         * model will cause updates on the GUI model!
         */
        guiModelController.addGuiModel(canonicalPath, displayName);

        /*
         * Add a data model from the PNML file.
         * 
         * Note: -> The method with a File parameter
         */
        importingFromPnml = true;
        int returnValue = dataModelController.addDataModel(pnmlFile);
        importingFromPnml = false;

        if (returnValue != ExitCode.OPERATION_SUCCESSFUL) {
            /*
             * Show an error message
             */
            String title = displayName;
            String errorMessage = i18n.getMessage("errFileNotAccepted") + canonicalPath;
            System.err.println(errorMessage);
            JOptionPane.showMessageDialog(mainFrame, errorMessage, title, JOptionPane.WARNING_MESSAGE);

            /*
             * Try to update/reset the current file now! (the last open file)!
             * 
             * Otherwise: disposeFile() would reset current model, current draw
             * panel etc. and we would just be forced to refresh all those
             * references anyways to continue working with another file.
             */
            refreshActiveFile();

            /* Dispose this file after we "returned" to the previous. */
            disposeFile(canonicalPath);

            return;
        }

        /*
         * Reset the "modified" attributes of the models since the data model
         * controller has accepted the imported data and all changes came from
         * the import. (The user could not change anything until now.)
         */
        dataModelController.resetModifiedDataModel(canonicalPath);
        guiModelController.resetModifiedGuiModel(canonicalPath);

        /*
         * Add a tab for this file (with the draw panel)
         */
        DrawPanel drawPanel = (DrawPanel) guiModelController.getDrawPanel(canonicalPath);

        /*
         * Get the validation messages panel for this tab from the data
         * controller, which has created it in his addDataModel() method.
         */
        IValidationMsgPanel validationMessagesPanel = dataModelController.getValidationMessagePanel(canonicalPath);

        addTabForDrawPanel(drawPanel, validationMessagesPanel, canonicalPath, displayName);
    }

    /**
     * Adds a new Tab for the specified {@link DrawPanel}.
     * 
     * Note: The full path name us used as tool tip for the tab. (This can be
     * used later to determine the current active file.)
     * 
     * @param drawPanel
     *            The draw panel
     * @param validationMsgPanel
     *            The {@link IValidationMsgPanel} for the validation messages
     * @param fullName
     *            The full path name of the PNML file. (Intended to be the
     *            modelName of the draw panel!)
     * @param displayName
     *            The title of the tab (= the file name)
     */
    private void addTabForDrawPanel(DrawPanel drawPanel, IValidationMsgPanel validationMessagesPanel, String fullName,
            String displayName) {
        /*
         * Add the path to the list of files. Use the (unique) canonical path
         * name if the file is a file on the file system.
         */
        boolean isFSFile = FSInfo.isFileSystemFile(fullName);
        if (isFSFile) {
            String canonicalPath = FSInfo.getCanonicalPath(fullName);
            this.fileList.add(canonicalPath);
        } else {
            this.fileList.add(fullName);
        }

        /*
         * Add the draw panel to a scroll pane on a new tab.
         */
        JScrollPane scrollPanel = new JScrollPane();
        scrollPanel.setViewportView(drawPanel);

        // TODO Add infoScrollPane?

        JPanel documentPanel = new JPanel(new BorderLayout());
        documentPanel.add(scrollPanel, BorderLayout.CENTER);
        documentPanel.add((Component) validationMessagesPanel, BorderLayout.EAST);

        // this.tabbedPane.addTab(displayName, null, scrollPanel, fullName);
        this.tabbedPane.addTab(displayName, null, documentPanel, fullName);

        /*
         * Store the index of the added tab. (The command .indexOfTab(fileName)
         * cannot be used because it returns always the 1st tab if two files
         * have the same name!)
         */
        int index = tabbedPane.getTabCount() - 1;
        if (debug) {
            System.out.println("tabbedPane.getToolTipTextAt(index): " + tabbedPane.getToolTipTextAt(index));
        }

        /*
         * Activate the new tab
         */
        this.tabbedPane.setSelectedIndex(index);
        this.tabbedPane.requestFocus();

        /*
         * Additional settings
         */

        // Speedup vertical scrolling
        scrollPanel.getVerticalScrollBar().setUnitIncrement(5);
        // ...horizontal scrolling too?
        scrollPanel.getHorizontalScrollBar().setUnitIncrement(20);

        // Tab colors?
        // initTabColor(index);
    }

    /*
     * Closing file(s)
     */

    /**
     * Invokes closeFile(modelName) with the active file.
     * 
     * @return The result of {@link closeFile}
     */
    private int closeActiveFile() {
        if (this.activeFile == null || this.activeFile == "") {
            if (debug) {
                System.out.println("No active file to close.");
            }
            return 0;
        }

        String modelName = this.activeFile;
        int result = closeFile(modelName);
        if (debug) {
            System.out.println("closeActiveFile, result: " + result);
        }
        return result;
    }

    /**
     * Closes the specified file.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return OPERATION_SUCCESSFUL if an unmodified file was closed without
     *         saving, exit code depending on {@link askSaveChanges} if the file
     *         was modified and {@link closeFile} if the user selected "Yes";
     *         otherwise UNEXPECTED_ERROR
     */
    private int closeFile(String modelName) {
        if (isParamUndefined(modelName, "closeFile", "modelName"))
            return ExitCode.UNEXPECTED_ERROR;

        /*
         * Close the file immediately, if not modified
         */
        boolean modified = isFileModified(modelName);

        if (!modified) {
            disposeFile(modelName);
            return ExitCode.OPERATION_SUCCESSFUL;
        }

        /*
         * Ask the user if he wants to save the file.
         * 
         * 0 = YES_OPTION, 1 = NO_OPTION, 2 = CANCEL_OPTION
         */
        int answer = askSaveChanges(modelName);

        switch (answer) {
        case JOptionPane.CANCEL_OPTION:
            return ExitCode.OPERATION_CANCELLED;

        case JOptionPane.NO_OPTION:
            disposeFile(modelName);
            return ExitCode.OPERATION_SUCCESSFUL;

        case JOptionPane.YES_OPTION:
            int result = closeFile(modelName, true);
            if (result == 0)
                disposeFile(modelName);
            return result;

        default:
            System.err.println("Unexpected return value from askSaveChanges(modelName): " + answer);
            return ExitCode.UNEXPECTED_ERROR;
        }
    }

    /**
     * Closes the specified file.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param saveChanges
     *            Save changes before closing?
     * @return OPERATION_SUCCESSFUL if an unmodified file was closed without
     *         saving, exit code of {@link saveFile} if file was modified;
     *         otherwise UNEXPECTED_ERROR
     */
    private int closeFile(String modelName, boolean saveChanges) {
        if (isParamUndefined(modelName, "closeFile", "modelName"))
            return ExitCode.UNEXPECTED_ERROR;

        /*
         * Close the file immediately, if saveChanges is false.
         */
        if (!saveChanges) {
            disposeFile(modelName);
            return ExitCode.OPERATION_SUCCESSFUL;
        }

        /*
         * Close the file immediately, if not modified.
         */
        boolean modified = isFileModified(modelName);

        if (!modified) {
            disposeFile(modelName);
            return ExitCode.OPERATION_SUCCESSFUL;
        }

        int result = saveFile(modelName);
        return result;
    }

    /**
     * Closes all unmodified files in {@link fileList}.
     */
    private void closeUnmodifiedFiles() {
        /*
         * Add to a separate list first to avoid ConcurrentModificationException
         * when closeFile() removes the file name from the fileList!
         */
        List<String> unmodified = new ArrayList<String>();

        for (String file : fileList) {
            if (!isFileModified(file)) {
                unmodified.add(file);
            }
        }

        for (String file : unmodified) {
            if (debug) {
                System.out.println("Closing unmodified file: " + file);
            }
            closeFile(file, false);
        }
    }

    /**
     * Checks if a file was modified.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return True if the file was modified; otherwise false
     */
    private boolean isFileModified(String modelName) {
        if (isParamUndefined(modelName, "isFileModified", "modelName"))
            return false;

        boolean dataModelModified = dataModelController.isModifiedDataModel(modelName);
        /*
         * Check only the data model. It should hold all persistent info.
         */
        // boolean guiModelModified =
        // guiModelController.isModifiedGuiModel(modelName);

        // boolean result = (dataModelModified || guiModelModified);
        boolean result = dataModelModified;
        return result;
    }

    /**
     * Disposes the specified file (tab, draw panel, data and GUI model) without
     * further questions.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    private void disposeFile(String modelName) {
        if (isParamUndefined(modelName, "disposeFile", "modelName"))
            return;
        /*
         * Remove the specified tab.
         */
        int index = getTabIndexForFile(modelName);
        // if (index == -1)
        // return;

        /*
         * Do not leave this method here if there is no tab to close!
         * 
         * -> There is no tab if we have opened a new file with errors. (In this
         * case: The data model controller has rejected the file during import,
         * but the models were created before reading the PNML file!
         * 
         * -> So we still have to remove the models!
         */
        if (index != -1)
            tabbedPane.removeTabAt(index);

        /*
         * Note: Removing the tab invokes tabstateChanged() of the TabListener
         * which should then update the attribute "activeFile" by calling
         * appController.setActiveFile(tabIndex).
         */

        /*
         * Dispose data model and GUI model (+ draw panel)
         */
        dataModelController.removeDataModel(modelName);
        guiModelController.removeGuiModel(modelName);

        /*
         * Remove the path name from the list of open files
         */
        fileList.remove(modelName);

        /*
         * Update the status bar
         */
        updateDrawPanelSizeInfo();
    }

    /**
     * Asks the user if he wants the file to be saved.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return 0 = YES_OPTION, 1 = NO_OPTION, 2 = CANCEL_OPTION; otherwise
     *         UNEXPECTED_ERROR
     */
    private int askSaveChanges(String modelName) {
        if (isParamUndefined(modelName, "askSaveChanges", "modelName"))
            return ExitCode.UNEXPECTED_ERROR;

        /*
         * Ask the user if he wants to save the file.
         * 
         * 0: YES_OPTION, 1: NO_OPTION, 2: CANCEL_OPTION, -1: DEFAULT_OPTION
         * 
         * https://docs.oracle.com/javase/8/docs/api/constant-values.html#javax.
         * swing
         */
        String title = i18n.getNameOnly("SaveChanges");
        String question = i18n.getMessage("questionSaveChanges").replace("%modelName%", modelName);
        int messageType = JOptionPane.YES_NO_CANCEL_OPTION;

        Toolkit.getDefaultToolkit().beep();
        int answer = JOptionPane.showConfirmDialog(mainFrame, question, title, messageType);
        if (answer == -1)
            return ExitCode.OPERATION_CANCELLED;
        return answer;
    }

    /*
     * Saving file(s)
     */

    /**
     * Invokes saveFile(modelName) with the active file.
     * 
     * @return The result of {@link saveFile}
     */
    private int saveActiveFile() {
        if (this.activeFile == null) {
            if (debug) {
                System.out.println("No active file to save.");
                return 0;
            }
        }

        String modelName = this.activeFile;
        int result = saveFile(modelName);
        if (debug) {
            System.out.println("saveActiveFile, result: " + result);
        }
        return result;
    }

    /**
     * Saves the specified file.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return Exit code of {@link saveToExistingFile}, OPERATION_CANCELLED if
     *         the user didn't chose a file name for a new file; otherwise
     *         UNEXPECTED_ERROR
     */
    private int saveFile(String modelName) {
        if (isParamUndefined(modelName, "saveFile", "modelName"))
            return ExitCode.UNEXPECTED_ERROR;

        /*
         * Save the file immediately, if it is an existing file.
         */
        boolean isFSFile = FSInfo.isFileSystemFile(modelName);

        if (isFSFile) {
            int result = saveToFile(modelName, modelName, false);
            return result;
        }

        /*
         * Ask for a file name.
         */
        String saveAsFullName = FSInfo.getSaveAsFullName(mainFrame);
        if (saveAsFullName == null)
            return ExitCode.OPERATION_CANCELLED;

        int result = saveToFile(modelName, saveAsFullName, true);
        return result;
    }

    /**
     * Saves to the specified file.
     * 
     * Known limitation: modelName.equals(saveAsFullName) should be misleading
     * on Linux because this OS can handle case-sensitive file names. But
     * modelName.equalsIgnoreCase(saveAsFullName) cannot be used because Windows
     * does not handle case-sensitive file names.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param saveAsFullName
     *            The full path name of the file to be written
     * @param displayAlerts
     *            Show an alert if a file already exists?
     * @return Exit code OPERATION_SUCCESSFUL if the file was saved,
     *         OPERATION_FAILED if the file was not saved, OPERATION_CANCELLED
     *         if the user cancelled the operation; otherwise UNEXPECTED_ERROR
     */
    private int saveToFile(String modelName, String saveAsFullName, boolean displayAlerts) {
        if (isParamUndefined(modelName, "saveToExistingFile", "modelName"))
            return ExitCode.UNEXPECTED_ERROR;
        if (isParamUndefined(saveAsFullName, "saveToExistingFile", "saveAsFullName"))
            return ExitCode.UNEXPECTED_ERROR;

        int result = -1;

        /*
         * Do we have to display an overwrite warning?
         */
        if (modelName.equals(saveAsFullName))
            displayAlerts = false; // We just save the open file.
        if (!FSInfo.isFileSystemFile(saveAsFullName))
            displayAlerts = false; // We don't overwrite an existing file.

        if (displayAlerts) {
            String title = i18n.getNameOnly("OverwriteFile");
            // String title = modelName;
            String question = i18n.getMessage("questionOverwriteFile").replace("%fullName%", saveAsFullName);
            int messageType = JOptionPane.YES_NO_CANCEL_OPTION;

            int answer = JOptionPane.showConfirmDialog(mainFrame, question, title, messageType);

            if (answer == JOptionPane.CANCEL_OPTION)
                return ExitCode.OPERATION_CANCELLED;
            if (answer == JOptionPane.NO_OPTION)
                return ExitCode.OPERATION_FAILED;
        }

        /*
         * Do we have to ask for a file name?
         */
        if (saveAsFullName == null || saveAsFullName == "") {
            saveAsFullName = FSInfo.getSaveAsFullName(mainFrame);
            if (saveAsFullName == null)
                return ExitCode.OPERATION_CANCELLED;
        }

        /*
         * Is the target file write-protected?
         */
        boolean writeProtected = FSInfo.isWriteProtectedFile(saveAsFullName);
        if (writeProtected) {
            String title = modelName;
            String message = i18n.getMessage("errFileWriteProtected").replace("%fullName%", saveAsFullName);
            int messageType = JOptionPane.OK_OPTION;

            JOptionPane.showMessageDialog(mainFrame, message, title, messageType);

            return ExitCode.OPERATION_FAILED;
        }

        /*
         * We save the data model
         */
        IDataModel modifiedDataModel = dataModelController.getDataModel(modelName);
        if (debug) {
            System.out.println("modifiedDataModel.getModelName(): " + modifiedDataModel.getModelName());
        }
        result = writeToPnmlFile(modifiedDataModel, saveAsFullName);

        /*
         * "Convert" the exit codes from the PNML writer to what we need here.
         */
        switch (result) {
        case ExitCode.OPERATION_SUCCESSFUL:
            return ExitCode.OPERATION_SUCCESSFUL;
        case ExitCode.OPERATION_FAILED:
        case ExitCode.OPERATION_CANCELLED:
            return ExitCode.OPERATION_FAILED;
        default:
            System.err.println(
                    "Unexpected return value from writeToPnmlFile(modifiedDataModel, saveAsFullName): " + result);
            return ExitCode.OPERATION_CANCELLED;
        }
    }

    /**
     * Sends the content of the specified {@link IDataModel} to the
     * {@link PNMLWriter}.
     * 
     * @param model
     *            The specified model
     * @param saveAsFullName
     *            The full path name of the file to be written
     * @return Exit code OPERATION_SUCCESSFUL if the file was written,
     *         OPERATION_FAILED if the file was not written; otherwise
     *         UNEXPECTED_ERROR
     */
    private int writeToPnmlFile(IDataModel model, String saveAsFullName) {
        if (isParamUndefined(model, "writeToPnmlFile", "model"))
            return ExitCode.UNEXPECTED_ERROR;
        if (isParamUndefined(saveAsFullName, "writeToPnmlFile", "saveAsFullName"))
            return ExitCode.UNEXPECTED_ERROR;

        PNMLWriter writer = getPnmlWriter(saveAsFullName);
        if (isParamUndefined(writer, "writeToPnmlFile", "writer"))
            return ExitCode.UNEXPECTED_ERROR;

        /*
         * Start the document
         * 
         * Exit codes of the writer: 0 if completed without errors; 1 on IO
         * errors; 2 on XML errors
         */
        int returnValue = writer.startXMLDocument();
        if (returnValue > 0)
            return ExitCode.OPERATION_FAILED;

        /*
         * Write all places in this model
         */
        for (IDataElement element : model.getElements()) {
            if (element instanceof DataPlace) {
                String id = element.getId();
                String label = ((DataPlace) element).getName();
                String xPosition = Integer.toString(((DataPlace) element).getPosition().x);
                String yPosition = Integer.toString(((DataPlace) element).getPosition().y);
                String initialTokens = ((DataPlace) element).getTokensCount().toPnedString();

                returnValue = writer.addPlace(id, label, xPosition, yPosition, initialTokens);
                if (returnValue > 0)
                    return ExitCode.OPERATION_FAILED;
            }
        }

        /*
         * Write all transitions in this model
         */
        for (IDataElement element : model.getElements()) {
            if (element instanceof DataTransition) {
                String id = element.getId();
                String label = ((DataTransition) element).getName();
                String xPosition = Integer.toString(((DataTransition) element).getPosition().x);
                String yPosition = Integer.toString(((DataTransition) element).getPosition().y);

                returnValue = writer.addTransition(id, label, xPosition, yPosition);
                if (returnValue > 0)
                    return ExitCode.OPERATION_FAILED;
            }
        }

        /*
         * Write all arcs in this model
         */
        for (IDataElement element : model.getElements()) {
            if (element instanceof DataArc) {
                String id = element.getId();
                String source = ((DataArc) element).getSourceId();
                String target = ((DataArc) element).getTargetId();

                returnValue = writer.addArc(id, source, target);
                if (returnValue > 0)
                    return ExitCode.OPERATION_FAILED;
            }
        }

        /*
         * Finish the document
         */
        int result = writer.finishXMLDocument();
        if (result > 0)
            return ExitCode.OPERATION_FAILED;

        /*
         * Reset the modified state of this data model!
         */
        model.setModified(false, false);

        return ExitCode.OPERATION_SUCCESSFUL;
    }

    /**
     * Returns a writer for PNML files.
     * 
     * @param saveAsFullName
     *            The full path name of the file to be written
     * @return {@link PNMLWriter} initialized with the specified file.
     */
    private PNMLWriter getPnmlWriter(String saveAsFullName) {
        File pnml = new File(saveAsFullName);
        PNMLWriter writer = new PNMLWriter(pnml);
        return writer;
    }

    /**
     * Invokes saveFileAs(modelName, pnmlFile) with the active file.
     * 
     * @return Exit code is the exit code of {@link saveFileAs}; otherwise
     *         UNEXPECTED_ERROR
     */
    private int saveActiveFileAs(File pnmlFile) {
        if (isParamUndefined(pnmlFile, "saveActiveFileAs", "pnmlFile"))
            return ExitCode.UNEXPECTED_ERROR;

        String saveAsFullName;
        try {
            saveAsFullName = pnmlFile.getCanonicalPath();
        } catch (IOException e) {
            String title = i18n.getNameOnly("FileSaveAs...");
            String message = i18n.getMessage("errNoCanonicalPathname").replace("%fullName%", pnmlFile.getName());
            int messageType = JOptionPane.OK_OPTION;

            JOptionPane.showMessageDialog(mainFrame, message, title, messageType);

            return ExitCode.UNEXPECTED_ERROR;
        }

        String modelName = this.activeFile;
        int result = saveFileAs(modelName, saveAsFullName);
        if (debug) {
            System.out.println("saveActiveFileAs, result: " + result);
        }
        return result;
    }

    /**
     * Saves the specified file as the specified {@link File}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param saveAsFullName
     *            The full path name of the file to be written
     * @return Exit code is the exit code of {@link saveToExistingFile};
     *         otherwise UNEXPECTED_ERROR
     */
    private int saveFileAs(String modelName, String saveAsFullName) {
        if (isParamUndefined(modelName, "saveFileAs", "modelName"))
            return ExitCode.UNEXPECTED_ERROR;

        int result = saveToFile(modelName, saveAsFullName, true);

        /*
         * Update the name of the models etc.
         */
        if (result == 0)
            renameModels(modelName, saveAsFullName);

        return result;
    }

    /**
     * Renames the models according to the new file name after SaveAs.
     * 
     * @param modelName
     *            The old name of the model (This is intended to be the full
     *            path name of the PNML file represented by this model.)
     * @param saveAsFullName
     *            The new full path name
     */
    private void renameModels(String modelName, String saveAsFullName) {
        String canonicalPath = FSInfo.getCanonicalPath(saveAsFullName);
        String displayName = FSInfo.getFileName(saveAsFullName);

        IDataModel dataModel = dataModelController.getDataModel(modelName);
        dataModelController.renameDataModel(dataModel, canonicalPath, displayName);

        IGuiModel guiModel = guiModelController.getGuiModel(modelName);
        guiModelController.renameGuiModel(guiModel, canonicalPath, displayName);
        /*
         * Note: The GUI controller is responsible to update the draw panel too.
         */

        updateTabInfo(modelName, canonicalPath, displayName);
    }

    /**
     * Renames the tab after SaveAs and updates all references to it.
     * 
     * @param oldModelName
     *            The old name of the model (This is intended to be the full
     *            path name of the PNML file represented by this model.)
     * @param newModelName
     *            The new name of the model (Use the canonical path name!)
     * @param displayName
     *            The tab title
     */
    private void updateTabInfo(String oldModelName, String newModelName, String displayName) {
        int tabIndex = getTabIndexForFile(oldModelName);
        if (tabIndex == -1) {
            /*
             * tabIndex should never be -1 because we renamed an open file!
             */
            System.err.println("Could not find the tab to rename after SaveAs.");
            return;
        }

        /*
         * Rename tab (and tool tip).
         */
        // tabbedPane.getTabComponentAt(tabIndex).setName(displayName);
        tabbedPane.setTitleAt(tabIndex, displayName);
        tabbedPane.setToolTipTextAt(tabIndex, newModelName);

        /*
         * Update references
         */
        setActiveFile(tabIndex);
        setFilenameOnTitle(displayName);
    }

    /*
     * Helpers
     */

    /**
     * Checks if the specified file is already open.
     * 
     * @param pnmlFile
     *            The {@link File} to be opened
     * @return True if the file is already open; otherwise false
     */
    private boolean isFileAlreadyOpen(File pnmlFile) {
        String fullName = FSInfo.getCanonicalPath(pnmlFile);
        if (!fileList.contains(fullName))
            return false;

        /* Show a warning message before returning true. */
        String displayName = FSInfo.getFileName(pnmlFile);
        String title = displayName;

        String message = i18n.getMessage("errFileAlreadyOpen").replace("%fullName%", fullName);

        int messageType = JOptionPane.OK_OPTION + JOptionPane.WARNING_MESSAGE;
        JOptionPane.showMessageDialog(mainFrame, message, title, messageType);

        return true;
    }

    /**
     * Activates the tab which is representing the specified file.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    private void activateTabForFile(String modelName) {
        int tabIndex = getTabIndexForFile(modelName);
        if (tabIndex == -1)
            return;

        tabbedPane.setSelectedIndex(tabIndex);
    }

    /**
     * Returns the tab index for the specified file.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return The tab index if found; otherwise -1
     */
    private int getTabIndexForFile(String modelName) {
        int tabIndex = -1;

        // /*
        // * Search for the component (draw panel) with the full name.
        // */
        // for (int i = 0; i < tabbedPane.getTabCount(); i++) {
        // Component currComponent = tabbedPane.getComponentAt(i);
        // if (currComponent instanceof IDrawPanel) {
        // IDrawPanel currDrawPanel = (IDrawPanel) currComponent;
        // String currModelName = currDrawPanel.getModelName();
        //
        // if (currModelName == modelName) {
        // tabIndex = i;
        // break;
        // }
        // }
        // }
        //
        // if (tabIndex == -1) {
        // System.err.println("Could not determine the index for the file " +
        // modelName + "!");
        // }

        /*
         * Easier with the full path name as tool tip on the tabs!
         */
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            String nextToolTip = tabbedPane.getToolTipTextAt(i);
            if (nextToolTip.equals(modelName)) {
                tabIndex = i;
                break;
            }
        }

        return tabIndex;
    }

    /**
     * Returns true if a parameter is null or empty.
     * 
     * @param param
     *            The parameter to check
     * @param methodName
     *            The name of the method
     * @param paramName
     *            The name of the parameter
     * @return True if parameter is null or empty; otherwise false
     */
    private boolean isParamUndefined(Object param, String methodName, String paramName) {
        if (param == null) {
            System.err.println("Undefined parameter " + paramName + " in method " + methodName + "!");
            return true;
        }
        if (param == "") {
            System.err.println("Empty parameter " + paramName + " in method " + methodName + "!");
            return true;
        }
        return false;
    }

    /**
     * Asks the user to save all modified models before the application gets
     * closed.
     * 
     * @return Exit code OPERATION_SUCCESSFUL if all modified file were saved,
     *         OPERATION_FAILED if at least one file was not saved,
     *         OPERATION_CANCELLED if the user cancelled the operation;
     *         otherwise UNEXPECTED_ERROR
     */
    private int askSaveModifiedModelsBeforeExit() {
        int result = ExitCode.UNEXPECTED_ERROR;

        if (modifiedDataModels.size() == 0)
            return ExitCode.OPERATION_SUCCESSFUL;

        int answer;
        for (String file : modifiedDataModels) {
            if (debug) {
                System.out.println("AppController.askSaveModifiedModelsBeforeExit: " + file);
            }

            /*
             * Activate the according tab before asking the user to save!
             */
            activateTabForFile(file);

            /*
             * Ask the user if he wants to save the file.
             * 
             * 0: YES_OPTION, 1: NO_OPTION, 2: CANCEL_OPTION, -1: DEFAULT_OPTION
             * 
             * https://docs.oracle.com/javase/8/docs/api/constant-values.html#
             * javax.swing
             */
            answer = askSaveChanges(file);
            if (debug) {
                System.out.println("answer: " + answer);
            }

            if (answer == JOptionPane.CANCEL_OPTION)
                return ExitCode.OPERATION_CANCELLED;

            if (answer == JOptionPane.NO_OPTION) {
                disposeFile(file);
                // return ExitCode.OPERATION_FAILED;
                /*
                 * No problem, continue with the next file.
                 */
            }

            if (answer == JOptionPane.YES_OPTION) {
                /*
                 * Try to save the file.
                 */
                int savedReturnValue = saveFile(file);
                if (savedReturnValue != ExitCode.OPERATION_SUCCESSFUL)
                    return savedReturnValue;

                /*
                 * OK, continue with the next file.
                 */
            }
        }

        return result;
    }

    /*
     * Callbacks for updates between data model controller and GUI controller
     */

    /**
     * Callback for the data model controller to get the GUI controller up to
     * date.
     * 
     * @param id
     * @param name
     * @param initialTokens
     * @param position
     */
    public void placeAddedToCurrentDataModel(String id, String name, EPlaceToken initialTokens, Point position) {
        if (!importingFromPnml)
            return;

        guiModelController.addPlaceToCurrentGuiModel(id, name, initialTokens, position);
    }

    /**
     * Callback for the GUI controller to get the data model controller up to
     * date.
     * 
     * @param id
     * @param name
     * @param initialTokens
     * @param position
     */
    public void placeAddedToCurrentGuiModel(String id, String name, EPlaceToken initialTokens, Point position) {
        if (importingFromPnml)
            return;

        dataModelController.addPlaceToCurrentDataModel(id, name, initialTokens, position);
    }

    /**
     * Callback for the data model controller to get the GUI controller up to
     * date.
     * 
     * @param id
     * @param name
     * @param position
     */
    public void transitionAddedToCurrentDataModel(String id, String name, Point position) {
        if (!importingFromPnml)
            return;

        guiModelController.addTransitionToCurrentGuiModel(id, name, position);
    }

    /**
     * Callback for the GUI controller to get the data model controller up to
     * date.
     * 
     * @param id
     * @param name
     * @param position
     */
    public void transitionAddedToCurrentGuiModel(String id, String name, Point position) {
        if (importingFromPnml)
            return;

        dataModelController.addTransitionToCurrentDataModel(id, name, position);
    }

    /**
     * Callback for the data model controller to get the GUI controller up to
     * date.
     * 
     * @param id
     * @param sourceId
     * @param targetId
     */
    public void arcAddedToCurrentDataModel(String id, String sourceId, String targetId) {
        if (!importingFromPnml)
            return;

        guiModelController.addArcToCurrentGuiModel(id, sourceId, targetId);
    }

    /**
     * Callback for the GUI controller to get the data model controller up to
     * date.
     * 
     * @param id
     * @param sourceId
     * @param targetId
     */
    public void arcAddedToCurrentGuiModel(String id, String sourceId, String targetId) {
        if (importingFromPnml)
            return;

        dataModelController.addArcToCurrentDataModel(id, sourceId, targetId);
    }

    /*
     * Modify methods for elements
     */

    /*
     * Remove methods for elements
     */

    /**
     * Removes all selected elements from the GUI model.
     * 
     * Note: Callback for menuCmd_EditDelete (EditDeleteAction)
     */
    public void removeSelectedGuiElements() {
        if (debug) {
            System.out.println("ApplicationController.removeSelectedGuiElements()");
        }

        guiModelController.removeSelectedGuiElements();
    }

    /**
     * Handles the GUI controllers info to remove an element from the data
     * model.
     * 
     * @param elementId
     *            The id of the element
     */
    public void guiElementRemoved(String elementId) {
        dataModelController.removeDataElement(elementId);
    }

    /**
     * Handles the data controllers info to remove an arc from the GUI model.
     *
     * @param arcId
     *            The id of the arc
     */
    public void dataArcRemoved(String arcId) {
        guiModelController.removeGuiArc(arcId);
    }

    /**
     * Handles the GUI controllers request to rename a node in the data model.
     *
     * @param nodeId
     *            The id of the node
     * @param newName
     *            The new name
     */
    public void guiNodeRenamed(String nodeId, String newName) {
        dataModelController.renameNode(nodeId, newName);
    }

    /**
     * Handles the GUI controllers request to update the position of a node in
     * the data model.
     *
     * @param nodeId
     *            The id of the node
     * @param newPosition
     *            The new position
     */
    public void guiNodeDragged(String nodeId, Point newPosition) {
        dataModelController.moveNode(nodeId, newPosition);
    }

    /*
     * Validation events
     */

    /**
     * Handles the {@link IDataModelController} request to reset all start
     * places on the draw panel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    public void resetAllGuiStartPlaces(String modelName) {
        guiModelController.resetAllGuiStartPlaces(modelName);
    }

    /**
     * Handles the {@link IDataModelController} request to reset all end places
     * on the draw panel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    public void resetAllGuiEndPlaces(String modelName) {
        guiModelController.resetAllGuiEndPlaces(modelName);
    }

    /**
     * Handles the {@link IDataModelController} request to update the start
     * place on the draw panel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placeId
     *            The id of the {@link IGuiPlace}
     * @param b
     *            True to set as start place; otherwise false
     */
    public void setGuiStartPlace(String modelName, String placeId, boolean b) {
        guiModelController.setGuiStartPlace(modelName, placeId, b);
    }

    /**
     * Handles the {@link IDataModelController} request to update the end place
     * on the draw panel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placeId
     *            The id of the {@link IGuiPlace}
     * @param b
     *            True to set as end place; otherwise false
     */
    public void setGuiEndPlace(String modelName, String placeId, boolean b) {
        guiModelController.setGuiEndPlace(modelName, placeId, b);
    }

    /**
     * Handles the {@link IDataModelController} request to update the status of
     * the specified GUI node.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param nodeId
     *            The id of the {@link IGuiNode}
     * @param b
     *            True = unreachable; False = can be reached from the start
     *            place and can reach the end place
     */
    public void highlightUnreachableGuiNode(String modelName, String nodeId, boolean b) {
        guiModelController.highlightUnreachableGuiNode(modelName, nodeId, b);
    }

    /**
     * Handles the {@link IDataModelController} request to remove the token from
     * all GUI places in the specified GUI model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    public void removeAllGuiTokens(String modelName) {
        guiModelController.removeAllGuiTokens(modelName);
    }

    /**
     * Handles the {@link IDataModelController} request to add a token to all
     * specified GUI places in the specified GUI model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placesWithToken
     *            A {@link List} of type {@link String} with the IDs of the
     *            specified places
     */
    public void addGuiToken(String modelName, List<String> placesWithToken) {
        guiModelController.addGuiToken(modelName, placesWithToken);
    }

}
