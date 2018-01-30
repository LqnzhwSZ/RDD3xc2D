package de.lambeck.pned.elements.util;

import de.lambeck.pned.elements.INode;
import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.data.IDataTransition;
import de.lambeck.pned.elements.gui.IGuiPlace;
import de.lambeck.pned.elements.gui.IGuiTransition;

/**
 * Helper for messages with name and ID of nodes because the composition of the
 * Strings depend on whether the node has a name or not.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class NodeInfo {

    /**
     * Returns a uniform, comma separated {@link String} for messages containing
     * the name and the ID of the specified {@link INode}.
     * 
     * @param node
     *            The specified {@link INode}
     * @return Null if node == null; a comma separated {@link String} with the
     *         name (if not "") and the ID of the node
     */
    public static String getMessageStringNameAndId(INode node) {
        if (node == null)
            return null;

        String nameAndId = node.getName();

        /* Add comma as separator if name != "". */
        if (nameAndId != "")
            nameAndId = nameAndId + ", ";

        /* Add the ID (always != ""). */
        nameAndId = nameAndId + "ID: " + node.getId();

        return nameAndId;
    }

    /**
     * Wrapper for getMessageStringNameAndId(IDataNode node) for a
     * {@link DataPlace} as parameter.
     * 
     * @param place
     *            The specified {@link DataPlace}
     * @return The result of getMessageStringNameAndId(INode node)
     */
    public static String getMessageStringNameAndId(DataPlace place) {
        INode node = (INode) place;
        return getMessageStringNameAndId(node);
    }

    /**
     * Wrapper for getMessageStringNameAndId(IDataNode node) for a
     * {@link IDataTransition} as parameter.
     * 
     * @param transition
     *            The specified {@link IDataTransition}
     * @return The result of getMessageStringNameAndId(INode node)
     */
    public static String getMessageStringNameAndId(IDataTransition transition) {
        INode node = (INode) transition;
        return getMessageStringNameAndId(node);
    }

    /**
     * Wrapper for getMessageStringNameAndId(IDataNode node) for a
     * {@link IGuiPlace} as parameter.
     * 
     * @param place
     *            The specified {@link IGuiPlace}
     * @return The result of getMessageStringNameAndId(INode node)
     */
    public static String getMessageStringNameAndId(IGuiPlace place) {
        INode node = (INode) place;
        return getMessageStringNameAndId(node);
    }

    /**
     * Wrapper for getMessageStringNameAndId(IDataNode node) for a
     * {@link IGuiTransition} as parameter.
     * 
     * @param transition
     *            The specified {@link IGuiTransition}
     * @return The result of getMessageStringNameAndId(INode node)
     */
    public static String getMessageStringNameAndId(IGuiTransition transition) {
        INode node = (INode) transition;
        return getMessageStringNameAndId(node);
    }

}
