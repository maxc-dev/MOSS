package dev.maxc.os.components.memory;

/**
 * @author Max Carter
 * @since 25/04/2020
 */
public class Segment extends LogicalMemoryHandler {
    public Segment(RandomAccessMemory ram, int id, int parentProcessID) {
        super(ram, id, parentProcessID);
    }

    @Override
    protected void handle(GroupedMemoryAddress groupedMemoryAddress) {
        /*
            todo (
             memory is allocated by increasing the segment size
             the size is obtained by the pointers in the param
             )
         */
    }
}
