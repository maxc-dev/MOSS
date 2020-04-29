package dev.maxc.os.components.memory;

import dev.maxc.os.components.memory.allocation.LogicalMemoryHandler;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandlerUtils;
import dev.maxc.os.components.memory.allocation.Paging;
import dev.maxc.os.components.memory.allocation.Segmentation;
import dev.maxc.os.components.memory.model.MemoryUnit;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Max Carter
 * @since 21/04/2020
 */
public class MemoryManagementUnit {
    private final RandomAccessMemory ram;
    private final boolean useSegmentation;
    private final ArrayList<LogicalMemoryHandler> logicalHandlers = new ArrayList<>();
    private final LogicalMemoryHandlerUtils logicalMemoryHandlerUtils;
    private final AtomicInteger logicalHandlerCount = new AtomicInteger(-1);

    /**
     * Creates a Memory Management Unit which is responsible for managing
     * various operations in the main memory such as allocating memory to
     * new processes, allocating additional memory to processes and freeing
     * processes of their memory.
     *
     * @param ram                       Instance of the main memory to control.
     * @param useSegmentation           Whether to use segmentation or paging
     * @param logicalMemoryHandlerUtils A util class for the controller's config.
     */
    public MemoryManagementUnit(RandomAccessMemory ram, boolean useSegmentation, LogicalMemoryHandlerUtils logicalMemoryHandlerUtils) {
        this.ram = ram;
        this.useSegmentation = useSegmentation;
        this.logicalMemoryHandlerUtils = logicalMemoryHandlerUtils;
    }

    /**
     * Allocates memory to a new process. The amount of memory that
     * is allocated is dependent on whether the system allocates
     * memory with Segmentation or Paging, this can all be modified
     * in the config file.
     */
    public boolean allocateMemory(int processIdentifier) {
        for (LogicalMemoryHandler handler : logicalHandlers) {
            if (handler.getParentProcessID() == processIdentifier) {
                Logger.log(Status.ERROR, this, "Attempted to allocate new memory to a process [" + processIdentifier + "] which already has memory. Processes which already have memory must allocate additional memory instead since it saves memory space.");
                return false;
            }
        }
        if (ram.getFreeMemory() >= logicalMemoryHandlerUtils.getInitialSize()) {
            LogicalMemoryHandler allocator;
            if (useSegmentation) {
                allocator = new Segmentation(ram, logicalMemoryHandlerUtils, logicalHandlerCount.addAndGet(1), processIdentifier);
            } else {
                allocator = new Paging(ram, logicalMemoryHandlerUtils, logicalHandlerCount.addAndGet(1), processIdentifier);
            }
            allocator.allocate(ram.indexMemory(logicalMemoryHandlerUtils.getInitialSize()));
            logicalHandlers.add(allocator);
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
                    handler.allocate(ram.indexMemory(useSegmentation ? logicalMemoryHandlerUtils.getIncrease() : logicalMemoryHandlerUtils.getInitialSize()));
                    return true;
                }
            }
            Logger.log(Status.ERROR, this, "Unable to allocate memory to process [" + processIdentifier + "] because the process ID could not be linked to a memory handler.");
        } else {
            Logger.log(Status.ERROR, this, "Unable to allocate memory to process [" + processIdentifier + "] because the main memory is full.");
        }
        return false;
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
        for (int i = 0; i < logicalHandlers.size(); i++) {
            if (logicalHandlers.get(i).getParentProcessID() == processIdentifier) {
                logicalHandlers.get(i).free();
                logicalHandlers.remove(i);
                return;
            }
        }
    }
}
