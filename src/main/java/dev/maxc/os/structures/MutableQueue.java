package dev.maxc.os.structures;

import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;

/**
 * @author Max Carter
 * @since 02/05/2020
 */
public class MutableQueue<T> extends Queue<T> {
    public final T get(int index) {
        if (size() <= index || index < 0) {
            Logger.log(Status.WARN, this, "Queue accessed at an invalid index [" + index + "] (the size is [" + size() + "]).");
            return null;
        }
        return super.remove(index);
    }
}
