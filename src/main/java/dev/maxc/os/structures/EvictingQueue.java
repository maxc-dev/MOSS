package dev.maxc.os.structures;

/**
 * @author Max Carter
 * @since 01/05/2020
 */
public class EvictingQueue<T> extends Queue<T> {
    private final int size;

    public EvictingQueue(int size) {
        this.size = size;
    }

    @Override
    public boolean add(T item) {
        if (size() + 1 > size) {
            get();
        }
        super.add(item);
        return true;
    }
}
