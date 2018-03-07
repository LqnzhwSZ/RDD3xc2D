package de.lambeck.pned.models.gui;

import de.lambeck.pned.util.ILIFOStack;

// Restricts {@link ILIFOStack} to items of type {@link UndoRedoElementBuffer}.

/**
 * Restricts {@link ILIFOStack} to items of type {@link IGuiModel}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
// public interface IGuiModelStack extends ILIFOStack<UndoRedoElementBuffer> {
public interface IGuiModelStack extends ILIFOStack<IGuiModel> {

    // @Override
    // void push(IGuiModel item);

    // @Override
    // IGuiModel pop() throws EmptyStackException;

    // @Override
    // IGuiModel peek() throws EmptyStackException;

    // @Override
    // boolean empty();

}
