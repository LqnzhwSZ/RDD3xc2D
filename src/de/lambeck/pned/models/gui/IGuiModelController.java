package de.lambeck.pned.models.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;

import de.lambeck.pned.application.*;
import de.lambeck.pned.application.actions.EditDeleteAction;
import de.lambeck.pned.application.actions.EditRenameAction;
import de.lambeck.pned.application.actions.NewPlaceAction;
import de.lambeck.pned.elements.ENodeType;
import de.lambeck.pned.elements.EPlaceToken;
import de.lambeck.pned.elements.data.IDataTransition;
import de.lambeck.pned.elements.gui.*;
import de.lambeck.pned.gui.menuBar.MenuBar;
import de.lambeck.pned.gui.settings.SizeSlider;
import de.lambeck.pned.models.data.IDataModelController;

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
     * Adds a GUI model for a non-existing file.
     * 
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
     * Resets the "modified" state of the specified model.
     * 
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
     */
    void setCurrentDrawPanel(IDrawPanel drawPanel);

    /**
     * Returns a list of GUI models which have been modified and need to be
     * saved.
     * 
     * @return List of modified GUI models
     */
    List<String> getModifiedGuiModels();

    /*
     * Methods for adding, modify and removal of elements (and callbacks for
     * updates between data and GUI model controller)...
     */

    /*
     * Add elements
     */

    /**
     * Adds a place to the current GUI model.
     * 
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
     * Adds a transition to the current GUI model.
     * 
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
     * Adds an arc to the current GUI model.
     * 
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

    /**
     * Sets the {@link IGuiNode} at the popup menu location as source for the
     * new {@link IGuiArc} in the current {@link IGuiModel}.
     */
    void setSourceNodeForNewArc();

    /**
     * Checks if this {@link IGuiModelController} is currently in the state to
     * add a new {@link IGuiArc}.
     * 
     * @return True if this GUI model controller is waiting for the second
     *         {@link IGuiNode} to finish the Arc; otherwise false.
     */
    boolean getStateAddingNewArc();

    /**
     * Returns the type of node currently set as source for the new
     * {@link IGuiArc} to be added by this {@link IGuiModelController}.
     * 
     * @return The {@link ENodeType} of the node
     */
    ENodeType getSourceForNewArcType();

    /**
     * Sets the {@link IGuiNode} at the popup menu location as target for the
     * new {@link IGuiArc} in the current {@link IGuiModel}.
     */
    void setTargetNodeForNewArc();

    /**
     * Resets the state of the {@link IGuiModelController} to add a new
     * {@link IGuiArc}.
     */
    void resetStateAddingNewArc();

    /*
     * Modify elements
     */

    /**
     * Returns the selectable element at the specified Point. Returns the one
     * with the highest z-value if there is more than 1 at this location.
     * 
     * Note: This means any element, not only nodes!
     * 
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
     * Callback for the {@link EditRenameAction} in the
     * {@link ApplicationController}.
     * 
     * Renames the selected element in the current GUI model.
     */
    void renameSelectedGuiElement();

    /**
     * Tells the GUI model controller that an area has changed and needs
     * repainting.
     * 
     * @param area
     *            The area to update; set to null for complete repaint
     */
    void updateDrawing(Rectangle area);

    /*
     * Remove elements
     */

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

    /**
     * Removes all elements from the current GUI model.
     */
    void clearCurrentGuiModel();

    // void clearCurrentGuiModel();

    /*
     * Mouse events
     */

    /**
     * Callback for the {@link DrawPanel}.
     * 
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
     * Callback for the {@link DrawPanel}.
     * 
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
     * Callback for the {@link DrawPanel}.
     * 
     * @param distance_x
     *            The distance in x direction
     * @param distance_y
     *            The distance in y direction
     */
    void mouseDragged(int distance_x, int distance_y);

    // /**
    // * Callback for the {@link DrawPanel}. Informs the
    // * {@link GuiModelController} that the dragging has finished and that the
    // * {@link DataModelController} needs an update for the position of all
    // * dragged nodes.
    // */
    // void mouseDragging_Finished();

    /**
     * Handles the {@link MyMouseAdapter} request to update the positions of the
     * nodes in the data model after mouse dragging.
     */
    void updateDataNodePositions();

    /*
     * Keyboard events
     */

    /**
     * Callback for the {@link DrawPanel}.
     */
    void keyEvent_Escape_Occurred();

    /**
     * Callback for the KeyBinding event in the {@link DrawPanel}.
     * 
     * Note: Invokes removeSelectedGuiElements()
     */
    void keyEvent_Delete_Occurred();

    /**
     * Callback for {@link EditRenameAction} and the KeyBindings in
     * {@link DrawPanel}.
     * 
     * Note: Invokes renameSelectedGuiElements()
     */
    void keyEvent_F2_Occurred();

    /**
     * Callback for the popup menus of the {@link IDrawPanel}. Selects the
     * element that contained the popup trigger.
     */
    void selectElementAtPopupMenu();

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
     * Callback for the {@link SizeSlider} to change the size of the elements on
     * the draw panels.
     * 
     * @param size
     *            The new size
     */
    void changeShapeSize(int size);

    /*
     * Validation events
     */

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
     * to provide the id of the desired {@link IDataTransition} to the
     * {@link IDataModelController} because the popup menu location is known
     * only to the GUI controller.
     */
    void fireGuiTransition();

    /**
     * @return the minimum z value from the current {@link IGuiModel}
     */
    int getCurrentMinZValue();

    /**
     * @return the maximum z value from the current {@link IGuiModel}
     */
    int getCurrentMaxZValue();

}
