package dev.maxc.os.components.memory;

/**
 * @author Max Carter
 * @since 21/04/2020
 */
public class Page extends LogicalMemoryHandler {
    public Page(RandomAccessMemory ram, int id, int parentProcessID) {
        super(ram, id, parentProcessID);
    }

    @Override
    protected void handle(GroupedMemoryAddress groupedMemoryAddress) {
        /*
            todo(
             memory is allocated by creating a new page
             )
         */
    }
}
