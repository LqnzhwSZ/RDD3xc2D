package de.lambeck.pned.elements.util;

import de.lambeck.pned.elements.INode;
import de.lambeck.pned.elements.IPlace;
import de.lambeck.pned.elements.ITransition;
import de.lambeck.pned.elements.data.IDataNode;
import de.lambeck.pned.elements.gui.IGuiNode;
import de.lambeck.pned.models.data.IDataModel;
import de.lambeck.pned.models.gui.IGuiModel;

/**
 * Implements checks for nodes.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class NodeCheck {

    /**
     * Checks if source and target are a valid connection of a place and a
     * transition for two {@link IDataNode} in a {@link IDataModel}.
     * 
     * @param source
     *            The source {@link IDataNode}
     * @param target
     *            The target {@link IDataNode}
     * @return true = valid combination, false = invalid combination
     */
    public static boolean isValidConnection(IDataNode source, IDataNode target) {
        return isValidCombination(source, target);
    }

    /**
     * Checks if source and target are a valid connection of a place and a
     * transition for two {@link IGuiNode} in a {@link IGuiModel}.
     * 
     * @param source
     *            The source {@link IGuiNode}
     * @param target
     *            The target {@link IGuiNode}
     * @return true = valid combination, false = invalid combination
     */
    public static boolean isValidConnection(IGuiNode source, IGuiNode target) {
        return isValidCombination(source, target);
    }

    /**
     * Checks if source and target are a valid combination of a place and a
     * transition.
     * 
     * @param source
     *            The source {@link INode}
     * @param target
     *            The target {@link INode}
     * @return true = valid combination, false = invalid combination
     */
    private static boolean isValidCombination(INode source, INode target) {
        // if (source instanceof IPlace && target instanceof ITransition)
        // return true;
        // if (source instanceof ITransition && target instanceof IPlace)
        // return true;
        // return false;

        boolean place = false;
        boolean transition = false;

        if (source instanceof IPlace || target instanceof IPlace)
            place = true;

        if (source instanceof ITransition || target instanceof ITransition)
            transition = true;

        return place & transition;
    }

}
