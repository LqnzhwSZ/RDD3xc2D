package de.lambeck.pned.models.data;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EmptyStackException;

import de.lambeck.pned.util.ILIFOStack;

// Implements an {@link ILIFOStack} for items of type {@link
// UndoRedoElementBuffer}.

/**
 * Implements an {@link ILIFOStack} for items of type {@link IDataModel}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class DataModelStack implements IDataModelStack {

    private final Deque<IDataModel> deque = new ArrayDeque<IDataModel>();

    @Override
    public void push(IDataModel item) {
        deque.addFirst(item);
    }

    @Override
    public IDataModel pop() throws EmptyStackException {
        return deque.removeFirst();
    }

    @Override
    public IDataModel peek() throws EmptyStackException {
        return deque.peekFirst();
    }

    @Override
    public boolean empty() {
        return deque.isEmpty();
    }

}
