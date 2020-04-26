package dev.maxc.os.components.memory;

import dev.maxc.os.io.exceptions.memory.InvalidLogicalMemoryHandler;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Max Carter
 * @since 21/04/2020
 */
public class MemoryManagementUnit<T extends LogicalMemoryHandler> {
    private final RandomAccessMemory ram;
    private final Class<T> logicalHandlerClass;
    private final ArrayList<T> logicalHandlers = new ArrayList<>();
    private final LogicalMemoryHandlerUtils logicalMemoryHandlerUtils;
    private final AtomicInteger logicalHandlerCount = new AtomicInteger(-1);

    /**
     * Creates a Memory Management Unit which is responsible for managing
     * various operations in the main memory such as allocating memory to
     * new processes, allocating additional memory to processes and freeing
     * processes of their memory.
     *
     * @param ram                       Instance of the main memory to control.
     * @param logicalHandler            The class of the handler used to control the ram.
     * @param logicalMemoryHandlerUtils A util class for the controller's config.
     */
    public MemoryManagementUnit(RandomAccessMemory ram, Class<T> logicalHandler, LogicalMemoryHandlerUtils logicalMemoryHandlerUtils) {
        this.ram = ram;
        this.logicalHandlerClass = logicalHandler;
        this.logicalMemoryHandlerUtils = logicalMemoryHandlerUtils;
    }

    /**
     * Allocates a specified amount of memory addresses to a specified
     * process by creating a Logical Address handler (Paging, Segmentation etc...)
     */
    private void allocate(int processIdentifier, int space) throws InvalidLogicalMemoryHandler {
        T allocator = null;
        try {
            allocator = logicalHandlerClass.getConstructor().newInstance(ram, logicalHandlerCount.addAndGet(1), processIdentifier);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (allocator != null) {
            allocator.allocate(ram.getGroupedMemoryAddress(space));
            logicalHandlers.add(allocator);
        } else {
            Logger.log(Status.CRIT, this, "Unable to interpret the Logical Memory Handler for process [" + processIdentifier + "]");
            throw new InvalidLogicalMemoryHandler(logicalHandlerClass, processIdentifier);
        }
    }

    /**
     * Allocates memory to a new process. The amount of memory that
     * is allocated is dependent on whether the system allocates
     * memory with Segmentation or Paging, this can all be modified
     * in the config file.
     */
    public boolean allocateMemoryNewProcess(int processIdentifier) {
        if (ram.getFreeMemory() >= logicalMemoryHandlerUtils.getInitialSize()) {
            try {
                allocate(processIdentifier, logicalMemoryHandlerUtils.getInitialSize());
            } catch (InvalidLogicalMemoryHandler ex) {
                ex.printStackTrace();
                return false;
            }
            return true;
        }
        Logger.log(Status.ERROR, this, "Unable to allocate memory to process [" + processIdentifier + "] because the main memory is full.");
        return false;
    }

    /**
     * Allocates additional memory to a specified process. The amount
     * that is additionally allocated is variable whether the system
     * allocates memory with Segmentation or Paging, this can all be
     * modified in the config file.
     */
    public boolean allocateAdditionalMemory(int processIdentifier) {
        if (ram.getFreeMemory() >= logicalMemoryHandlerUtils.getIncrease()) {
            for (LogicalMemoryHandler handler : logicalHandlers) {
                if (handler.getParentProcessID() == processIdentifier) {
                    handler.allocate(ram.getGroupedMemoryAddress(logicalMemoryHandlerUtils.getIncrease()));
                }
            }
            return true;
        } else {
            Logger.log(Status.ERROR, this, "Unable to allocate memory to process [" + processIdentifier + "] because the main memory is full.");
            return false;
        }
    }

    public MemoryUnit getMemoryUnitFromProcess(int processIdentifier, int offset) {
        for (LogicalMemoryHandler handler : logicalHandlers) {
            if (handler.getParentProcessID() == processIdentifier) {
                return handler.getMemoryUnit(offset);
            }
        }
        Logger.log(Status.ERROR, this, "Unable to get a Memory Unit for process [" + processIdentifier + "] at offset [" + offset + "]");
        return null;
    }

    /**
     * Clears a process from memory by identifying the handler for the process,
     * then making the handler free it's pointers, and then the handler is
     * removed from the memory's list of handlers. This means that the handler
     * will no longer be able to be accessed.
     */
    public void clearProcessMemory(int processIdentifier) {
        for (LogicalMemoryHandler handler : logicalHandlers) {
            if (handler.getParentProcessID() == processIdentifier) {
                handler.free();
                logicalHandlers.remove(handler);
            }
        }
    }
}
