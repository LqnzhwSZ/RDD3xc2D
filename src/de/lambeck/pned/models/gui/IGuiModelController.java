package de.lambeck.pned.models.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import de.lambeck.pned.application.*;
import de.lambeck.pned.application.actions.*;
import de.lambeck.pned.elements.ENodeType;
import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.elements.data.IDataNode;
import de.lambeck.pned.elements.data.IDataTransition;
import de.lambeck.pned.elements.gui.*;
import de.lambeck.pned.gui.menuBar.MenuBar;
import de.lambeck.pned.gui.settings.SizeSlider;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.data.IDataModelController;
import de.lambeck.pned.models.gui.overlay.IDrawArcOverlay;

/**
 * Interface for controllers for GUI models representing a Petri net. This means
 * models with all the graphical information (from a data model or drawn by the
 * user).
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IGuiModelController
        extends IInfo_MousePos, IInfo_SelectionRangeSize, IInfo_DrawingAreaSize, IInfo_Status {

    /**
     * Adds a GUI model for a non-existing file.<BR>
     * <BR>
     * Note: This is intended to be used to add new models which are not coming
     * from a PNML file (e.g. "Untitled1", "Untitled2"... or "New1", "New2"...)
     * 
     * @param modelName
     *            The full path name of the PNML file
     * @param displayName
     *            The title of the tab (= the file name)
     */
    void addGuiModel(String modelName, String displayName);

    /**
     * Checks if the specified GUI model has been modified.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return True if the model has been modified; otherwise false
     */
    boolean isModifiedGuiModel(String modelName);

    /**
     * Resets the "modified" state of the specified model.<BR>
     * <BR>
     * Note: Use this method after loading a PNML file because the model has not
     * been changed by the user after adding elements from the PNML file only.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void resetModifiedGuiModel(String modelName);

    /**
     * Removes the specified GUI model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void removeGuiModel(String modelName);

    /**
     * Renames the specified {@link IGuiModel}. (Use this in case the user has
     * saved the file under a new file name.)
     * 
     * @param model
     *            The {@link IGuiModel}
     * @param newModelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param newDisplayName
     *            The title of the tab (= the file name)
     */
    void renameGuiModel(IGuiModel model, String newModelName, String newDisplayName);

    /**
     * Returns the specified {@link IGuiModel}.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return The specified GUI model
     */
    IGuiModel getGuiModel(String modelName);

    /**
     * Returns the current (active) {@link IGuiModel} of the
     * {@link GuiModelController}.
     * 
     * @return The {@link IGuiModel}
     */
    IGuiModel getCurrentModel();

    /**
     * Sets the specified {@link IGuiModel} as current (active) model of the
     * {@link GuiModelController}.
     * 
     * @param model
     *            The specified {@link IGuiModel}
     */
    void setCurrentModel(IGuiModel model);

    /**
     * Returns the draw panel for a file name.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @return The draw panel
     */
    IDrawPanel getDrawPanel(String modelName);

    /**
     * Returns the current (active) {@link IDrawPanel} of the
     * {@link GuiModelController}.
     * 
     * @return The {@link IDrawPanel}
     */
    IDrawPanel getCurrentDrawPanel();

    /**
     * Sets the specified {@link IDrawPanel} as current (active) draw panel of
     * the {@link GuiModelController}.
     * 
     * @param drawPanel
     *            The specified {@link IDrawPanel}
     */
    void setCurrentDrawPanel(IDrawPanel drawPanel);

    /**
     * Returns a list of GUI models which have been modified and need to be
     * saved.
     * 
     * @return List of modified GUI models
     */
    List<String> getModifiedGuiModels();

    /**
     * Returns the applications main frame so that other classes can position
     * messages or input dialogs properly instead on the center of the screen.
     * 
     * @return The main application window as {@link JFrame}
     */
    JFrame getMainFrame();

    /*
     * Methods for adding, modify and removal of elements (and callbacks for
     * updates between data and GUI model controller)...
     */

    /* Add elements */

    /**
     * Adds a place to the current GUI model.<BR>
     * <BR>
     * Intended use: adding a place after reading from a PNML file because these
     * places may have a name.
     * 
     * @param id
     *            The ID of the place
     * @param name
     *            The name of the place
     * @param initialTokens
     *            The initial tokens count of the place
     * @param position
     *            The position (center) of the place
     */
    void addPlaceToCurrentGuiModel(String id, String name, EPlaceToken initialTokens, Point position);

    /**
     * Adds a transition to the current GUI model.<BR>
     * <BR>
     * Intended use: adding a transition after reading from a PNML file because
     * these transitions may have a name.
     * 
     * @param id
     *            The ID of the transition
     * @param name
     *            The name of the transition
     * @param position
     *            The position (center) of the transition
     */
    void addTransitionToCurrentGuiModel(String id, String name, Point position);

    /**
     * Adds an arc to the current GUI model.<BR>
     * <BR>
     * Note: This method should be the same for GUI events and reading from a
     * PNML file because arcs will have all 3 attributes in either cases.
     * 
     * @param id
     *            The id of the arc
     * @param sourceId
     *            The id of the source (Place or Transition)
     * @param targetId
     *            The id of the target (Place or Transition)
     */
    void addArcToCurrentGuiModel(String id, String sourceId, String targetId);

    /**
     * Callback for {@link NewPlaceAction}, creates a new {@link IGuiPlace} in
     * the current {@link IGuiModel}.
     */
    void createNewPlaceInCurrentGuiModel();

    /**
     * Callback for {@link NewPlaceAction}, creates a new {@link GuiTransition}
     * in the current {@link IGuiModel}.
     */
    void createNewTransitionInCurrentGuiModel();

    /* For the "draw new arc" overlay */

    /**
     * Checks preconditions and activates this GUI model controllers "draw new
     * arc" mode. The {@link IGuiNode} at the popup menu location is set as
     * source for the new {@link IGuiArc} in the current {@link IGuiModel}.<BR>
     * <BR>
     * Note: <B>No direct public "activate" method</B> because only this GUI
     * model controller should decide whether the preconditions, e.g.
     * successfully finishing setSourceNodeForNewArc(), to activate this mode
     * are fulfilled.
     */
    void checkActivateDrawArcMode();

    /**
     * Checks if this {@link IGuiModelController} is currently in the state to
     * add a new {@link IGuiArc}.
     * 
     * @return True if this GUI model controller is waiting for the second
     *         {@link IGuiNode} to finish the Arc; otherwise false.
     */
    boolean getDrawArcModeState();

    /**
     * Returns the type of node currently set as source for the new
     * {@link IGuiArc} to be added by this {@link IGuiModelController}.
     * 
     * @return The {@link ENodeType} of the node
     */
    ENodeType getSourceForNewArcType();

    /**
     * Sets a new location for the end of the {@link IOverlayGuiArc} at the
     * (temporary) {@link IDrawArcOverlay}
     * 
     * @param p
     *            The location as {@link Point}
     */
    void updateDrawArcCurrentEndLocation(Point p);

    /**
     * Checks the final location for the end of the {@link IOverlayGuiArc} at
     * the (temporary) {@link IDrawArcOverlay} when the user has made the 2nd
     * mouse click in "draw new arc" mode.
     * 
     * @param p
     *            The location as {@link Point}
     */
    void checkDrawArcFinalEndLocation(Point p);

    /**
     * Deactivates this GUI model controllers "draw new arc" mode.<BR>
     * <BR>
     * Note: <B>No public "activate" method</B> because only this GUI model
     * controller should decide whether the preconditions, e.g. successfully
     * finishing setSourceNodeForNewArc(), to activate this mode are fulfilled.
     */
    void deactivateDrawArcMode();

    /* Modify elements */

    /**
     * Returns the selectable element at the specified Point. Returns the one
     * with the highest z-value if there is more than 1 at this location.<BR>
     * <BR>
     * Note: This means any element, not only nodes!<BR>
     * <BR>
     * Note: Not private to grant access to class PopupMenuManager
     * 
     * @param p
     *            The specified Point
     * @return A GUI element
     */
    IGuiElement getSelectableElementAtLocation(Point p);

    /**
     * Checks if an element can be selected. (squares, circles and arrows)
     * 
     * @param element
     *            The element to check
     * @return True if the element can be selected; otherwise false
     */
    boolean isSelectableElement(IGuiElement element);

    /**
     * Returns the {@link IGuiNode} (place or transition, not other elements) at
     * the specified Point. Returns the one with the highest z-value if there is
     * more than 1 at this location.
     * 
     * @param p
     *            The specified Point
     * @return The {@link IGuiNode}; null if none exists
     */
    IGuiNode getNodeAtLocation(Point p);

    /**
     * Callback for the {@link EditRenameAction} in the
     * {@link ApplicationController}.
     * 
     * Renames the selected element in the current GUI model.
     */
    void renameSelectedGuiNode();

    /**
     * Tells the GUI model controller that an area has changed and needs
     * repainting.
     * 
     * @param area
     *            The area to update; set to null for complete repaint
     */
    void updateDrawing(Rectangle area);

    /* Remove elements */

    /**
     * Callback for the {@link EditDeleteAction} in the
     * {@link ApplicationController}.
     * 
     * Removes all selected elements from the current GUI model.
     */
    void removeSelectedGuiElements();

    /**
     * Handles the application controllers info to remove an arc from the GUI
     * model.
     *
     * @param arcId
     *            The id of the arc
     */
    void removeGuiArc(String arcId);

    // /**
    // * Removes all elements from the current GUI model.
    // */
    // void clearCurrentGuiModel();

    /* Mouse and selection events */

    /**
     * Callback for the {@link DrawPanel}.<BR>
     * <BR>
     * Note: Rejects a mouseClicked event (as unintended) if mousePressed was on
     * a different element than mouseReleased.
     * 
     * @param mousePressedLocation
     *            The location of the mousePressed event
     * @param e
     *            The current location (mouseReleased)
     */
    void mouseClick_Occurred(Point mousePressedLocation, MouseEvent e);

    /**
     * Callback for the {@link DrawPanel}.<BR>
     * <BR>
     * Note: Rejects a mouseClicked event (as unintended) if mousePressed was on
     * a different element than mouseReleased.
     * 
     * @param mousePressedLocation
     *            The location of the mousePressed event
     * @param e
     *            The current location (mouseReleased)
     */
    void mouseClick_WithCtrl_Occurred(Point mousePressedLocation, MouseEvent e);

    /**
     * Callback for the {@link SelectAllAction} in the
     * {@link ApplicationController}.<BR>
     * <BR>
     * Selects all {@link IGuiElement} in the current
     * {@link IGuiModel}/{@link IDrawPanel}.
     */
    void selectAllGuiElements();

    /**
     * Callback for the {@link DrawPanel}.
     * 
     * @param distance_x
     *            The distance in x direction
     * @param distance_y
     *            The distance in y direction
     */
    void mouseDragged(int distance_x, int distance_y);

    /**
     * Handles the {@link MyMouseAdapter} request to update the positions of the
     * {@link IDataNode} in the {@link IDataModel} when <B>mouse dragging has
     * been finished</B>.
     */
    void updateDataNodePositions();

    /* Keyboard events */

    // /**
    // * Callback for the {@link DrawPanel}.
    // */
    // void keyEvent_Escape_Occurred();

    /**
     * Callback for the KeyBinding event in the {@link DrawPanel}.<BR>
     * <BR>
     * Note: Invokes removeSelectedGuiElements()
     */
    void keyEvent_Delete_Occurred();

    /**
     * Callback for {@link EditRenameAction} and the KeyBindings in
     * {@link DrawPanel}.<BR>
     * <BR>
     * Note: Invokes renameSelectedGuiElements()
     */
    void keyEvent_F2_Occurred();

    /* ZValue Actions */

    /**
     * Callback for {@link MenuBar} and popup menus of the {@link IDrawPanel}.
     * Assigns the specified element to the foreground. (1 level higher in z
     * direction than the current top element)
     */
    void moveElementToForeground();

    /**
     * Callback for {@link MenuBar} and popup menus of the {@link IDrawPanel}.
     * Assigns the specified element to the background. (1 level higher in z
     * direction than the current top element)
     */
    void moveElementToBackground();

    /**
     * Callback for {@link MenuBar} and popup menus of the {@link IDrawPanel}.
     * Swaps the layer of this element with the next higher element.
     */
    void moveElementOneLayerUp();

    /**
     * Callback for {@link MenuBar} and popup menus of the {@link IDrawPanel}.
     * Swaps the layer of this element with the next lower element.
     */
    void moveElementOneLayerDown();

    /**
     * Returns the minimum Z value (height level) over all elements in the
     * current {@link IGuiModel}.
     * 
     * @return the minimum Z value
     */
    int getCurrentMinZValue();

    /**
     * Returns the maximum Z value (height level) over all elements in the
     * current {@link IGuiModel}.
     * 
     * @return the maximum Z value
     */
    int getCurrentMaxZValue();

    /* Change shape size */

    /**
     * Callback for the {@link SizeSlider} to change the size of the elements on
     * the draw panels.
     * 
     * @param size
     *            The new size
     */
    void changeShapeSize(int size);

    /* Validation events */

    /**
     * Handles the {@link ApplicationController} request to reset all start
     * places on the draw panel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void resetAllGuiStartPlaces(String modelName);

    /**
     * Handles the {@link ApplicationController} request to reset all end places
     * on the draw panel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void resetAllGuiEndPlaces(String modelName);

    /**
     * Handles the {@link ApplicationController} request to update the start
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
    void setGuiStartPlace(String modelName, String placeId, boolean b);

    /**
     * Handles the {@link ApplicationController} request to update the start
     * place candidate on the draw panel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placeId
     *            The id of the {@link IGuiPlace}
     * @param b
     *            True to set as start place candidate; otherwise false
     */
    void setGuiStartPlaceCandidate(String modelName, String placeId, boolean b);

    /**
     * Handles the {@link ApplicationController} request to update the end place
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
    void setGuiEndPlace(String modelName, String placeId, boolean b);

    /**
     * Handles the {@link ApplicationController} request to update the end place
     * candidate on the draw panel.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placeId
     *            The id of the {@link IGuiPlace}
     * @param b
     *            True to set as end place candidate; otherwise false
     */
    void setGuiEndPlaceCandidate(String modelName, String placeId, boolean b);

    /**
     * Handles the {@link ApplicationController} request to update the status of
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
    void highlightUnreachableGuiNode(String modelName, String nodeId, boolean b);

    /**
     * Handles the {@link ApplicationController} request to remove the token
     * from all GUI places in the specified model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void removeAllGuiTokens(String modelName);

    /**
     * Handles the {@link ApplicationController} request to remove the token
     * from all specified GUI places in the specified GUI model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placesWithToken
     *            A {@link List} of type {@link String} with the IDs of the
     *            specified places
     */
    void removeGuiToken(String modelName, List<String> placesWithToken);

    /**
     * Handles the {@link ApplicationController} request to add a token to all
     * specified GUI places in the specified GUI model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param placesWithToken
     *            A {@link List} of type {@link String} with the IDs of the
     *            specified places
     */
    void addGuiToken(String modelName, List<String> placesWithToken);

    /**
     * Handles the {@link ApplicationController} request to reset the "enabled"
     * state on all transitions in the specified GUI model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void resetAllGuiTransitionsEnabledState(String modelName);

    /**
     * Handles the {@link ApplicationController} request to reset the "safe"
     * state on all transitions in the specified GUI model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     */
    void resetAllGuiTransitionsSafeState(String modelName);

    /**
     * Handles the {@link ApplicationController} request to set the "safe" state
     * on the specified transition in the specified GUI model to false.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param transitionId
     *            The id of the {@link IGuiTransition}
     */
    void setGuiTransitionUnsafe(String modelName, String transitionId);

    /**
     * Handles the {@link ApplicationController} request to set the "enabled"
     * state on the specified transition in the specified GUI model.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the PNML file represented by this model.)
     * @param transitionId
     *            The id of the {@link IGuiTransition}
     */
    void setGuiTransitionEnabled(String modelName, String transitionId);

    /**
     * Callback to fire the transition at the popup menu location.<BR>
     * <BR>
     * Note: The purpose of this method in {@link IGuiModelController} is mainly
     * to provide the ID of the desired {@link IDataTransition} to the
     * {@link IDataModelController} because the popup menu location is known
     * only to the GUI controller.
     */
    void fireGuiTransition();

    /**
     * Returns the area of the start place in the current {@link IGuiModel}.
     * 
     * @return A {@link Rectangle}; null if the current {@link IGuiModel} is
     *         null or has no real (unambiguous) start place
     */
    Rectangle getCurrentGuiModelStartPlaceArea();

    /**
     * Returns a {@link List} with the areas of all enabled
     * {@link IGuiTransition} in the current {@link IGuiModel}.
     * 
     * @return A {@link List} of type {@link Rectangle}; null if the current
     *         {@link IGuiModel} is null; empty List if it has no enabled
     *         {@link IGuiTransition}
     */
    List<Rectangle> getCurrentGuiModelEnabledTransitionsAreas();

    /* Undo + Redo */

    /**
     * Indicates whether an Undo operation for the current {@link IGuiModel} is
     * legal. (Undo stack is not empty.)<BR>
     * <BR>
     * Note: This is intended to be used to enable the
     * {@link EditUndoAction}.<BR>
     * <BR>
     * Note: This refers to the current {@link IGuiModel}.
     * 
     * @return true = at least 1 edit can be undone; false = no edit can be
     *         undone
     */
    boolean canUndo();

    /**
     * Indicates whether a Redo operation for the current {@link IGuiModel} is
     * legal. (Redo stack is not empty.)<BR>
     * <BR>
     * Note: This is intended to be used to enable the
     * {@link EditRedoAction}.<BR>
     * <BR>
     * Note: This refers to the current {@link IGuiModel}.
     * 
     * @return true = at least 1 edit can be redone; false = no edit can be
     *         redone
     */
    boolean canRedo();

    /**
     * Undoes the last edit in the current {@link IGuiModel} and invokes Undo on
     * the {@link IDataModelController}.
     * 
     * @throws CannotUndoException
     *             if there are no edits to be undone
     */
    void Undo() throws CannotUndoException;

    /**
     * Redoes the following edit in the current {@link IGuiModel} and invokes
     * Redo on the {@link IDataModelController}.
     * 
     * @throws CannotRedoException
     *             if there are no edits to be redone
     */
    void Redo() throws CannotRedoException;

}
