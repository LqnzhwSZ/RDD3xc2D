package de.lambeck.pned.util;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.Vector;

/**
 * This interface allows hiding the implementation of {@link Stack} and exposing
 * only the intended role (LIFO).<BR>
 * <BR>
 * Note: {@link java.util.Stack} has two problems:<BR>
 * 1. It is <B>NOT an interface</B> but a concrete class which means that we are
 * bound to its implementation and not flexible.<BR>
 * <BR>
 * 2. It <B>extends</B> {@link Vector} and this exposes methods which can insert
 * elements to and get elements from specific positions. This means that a
 * {@link Stack} can do more than a (LIFO) stack should be able to do.<BR>
 * <BR>
 * http://openbook.rheinwerk-verlag.de/javainsel9/javainsel_13_006.htm#mj0c9a4bc09c3ca7bb4407a33d4270d2b4
 * 
 * @author Thomas Lambeck, 4128320
 * @param <T>
 *            the Type of the elements
 *
 */
public interface ILIFOStack<T> {

    /**
     * Pushes an item onto the top of this stack.
     * 
     * @param item
     *            the item to be pushed onto this stack.
     */
    void push(T item);

    /**
     * Removes the object at the top of this stack and returns that object as
     * the value of this function.
     * 
     * @return The object at the top of this stack.
     * @throws EmptyStackException
     *             if this stack is empty.
     */
    T pop() throws EmptyStackException;

    /**
     * Looks at the object at the top of this stack without removing it from the
     * stack.
     * 
     * @return the object at the top of this stack.
     * @throws EmptyStackException
     *             if this stack is empty.
     */
    T peek() throws EmptyStackException;

    /**
     * Tests if this stack is empty.
     * 
     * @return true if and only if this stack contains no items; false
     *         otherwise.
     */
    boolean empty();

    /* We do not need "search()" from the class Stack<E>. */
    // int search(Object o);

    /**
     * Removes all of the elements from this collection (optional operation).
     * The collection will be empty after this method returns.
     */
    void clear();

}
