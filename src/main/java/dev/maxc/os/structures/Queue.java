package dev.maxc.os.structures;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class Queue<T> {
    protected ArrayList<T> queue = new ArrayList<>();

    public void add(T item) {
        queue.add(item);
    }

    public T get() {
        if (queue.isEmpty()) {
            return null;
        } else {
            T item = queue.get(0);
            queue.remove(item);
            return item;
        }
    }
}
