package de.lambeck.pned.models.data;

import de.lambeck.pned.util.ILIFOStack;

// Restricts {@link ILIFOStack} to items of type {@link UndoRedoElementBuffer}.

/**
 * Restricts {@link ILIFOStack} to items of type {@link IDataModel}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
// public interface IDataModelStack extends ILIFOStack<UndoRedoElementBuffer> {
public interface IDataModelStack extends ILIFOStack<IDataModel> {

    // @Override
    // void push(IDataModel item);

    // @Override
    // IDataModel pop() throws EmptyStackException;

    // @Override
    // IDataModel peek() throws EmptyStackException;

    // @Override
    // boolean empty();

}
