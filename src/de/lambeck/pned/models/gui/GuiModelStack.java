package de.lambeck.pned.models.gui;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EmptyStackException;

import de.lambeck.pned.util.ILIFOStack;

// Implements an {@link ILIFOStack} for items of type {@link
// UndoRedoElementBuffer}.

/**
 * Implements an {@link ILIFOStack} for items of type {@link IGuiModel}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class GuiModelStack implements IGuiModelStack {

    private final Deque<IGuiModel> deque = new ArrayDeque<IGuiModel>();

    @Override
    public void push(IGuiModel item) {
        deque.addFirst(item);
    }

    @Override
    public IGuiModel pop() throws EmptyStackException {
        return deque.removeFirst();
    }

    @Override
    public IGuiModel peek() throws EmptyStackException {
        return deque.peekFirst();
    }

    @Override
    public boolean empty() {
        return deque.isEmpty();
    }

}
