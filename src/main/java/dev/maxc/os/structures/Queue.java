package dev.maxc.os.structures;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class Queue<T> extends ArrayList<T> {
    public T get() {
        if (isEmpty()) {
            return null;
        } else {
            return remove(0);
        }
    }
}
