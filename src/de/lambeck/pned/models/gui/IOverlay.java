package de.lambeck.pned.models.gui;

import java.util.List;

import de.lambeck.pned.elements.gui.IPaintable;

/**
 * Interface for overlays for the {@link IDrawPanel} which contain elements with
 * a paint method.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IOverlay {

    /* Getter and Setter */

    /**
     * Returns a list with all elements in this overlay.
     * 
     * @return {@link List} of type {@link IPaintable} with all paintable
     *         elements of this overlay
     */
    List<IPaintable> getPaintableElements();

}
