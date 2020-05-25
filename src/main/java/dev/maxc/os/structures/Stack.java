package dev.maxc.os.structures;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class Stack<T> {
    protected ArrayList<T> stack = new ArrayList<>();
    protected int pointer = -1;

    /**
     * Pushes an item to the stack
     */
    public void push(T item) {
        stack.add(item);
        pointer++;
    }

    /**
     * Returns the top of the stack.
     *
     * @return Returns null if the stack is empty
     */
    public T pop() {
        if (pointer >= 0) {
            T item = stack.get(pointer);
            stack.remove(item);
            pointer--;
            return item;
        } else {
            return null;
        }
    }
}
