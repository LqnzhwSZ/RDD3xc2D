package de.lambeck.pned.elements;

import de.lambeck.pned.application.actions.NewArcFromHereAction;
import de.lambeck.pned.application.actions.NewArcToHereAction;

/**
 * Enum for possible node types. Used in the popup menus for places and
 * transitions to enable {@link NewArcFromHereAction} or
 * {@link NewArcToHereAction}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public enum ENodeType {

    /**
     * Place (circle)
     */
    PLACE,
    /**
     * Transition (square)
     */
    TRANSITION;

}
