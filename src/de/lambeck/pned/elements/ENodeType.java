package de.lambeck.pned.elements;

import de.lambeck.pned.application.actions.NewArcFromHereAction;

/**
 * Enum for possible node types. Used in the popup menus for places and
 * transitions to enable {@link NewArcFromHereAction}.
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
