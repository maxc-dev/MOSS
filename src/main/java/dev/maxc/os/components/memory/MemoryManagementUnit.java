package dev.maxc.os.components.memory;

import dev.maxc.os.components.memory.model.Cache;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandler;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandlerUtils;
import dev.maxc.os.components.memory.allocation.Paging;
import dev.maxc.os.components.memory.allocation.Segmentation;
import dev.maxc.os.components.memory.model.CacheMemoryNode;
import dev.maxc.os.components.memory.model.MemoryUnit;
import dev.maxc.os.io.exceptions.memory.MemoryLogicalHandlerFullException;
import dev.maxc.os.io.exceptions.memory.MemoryUnitNotFoundException;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;
import dev.maxc.os.system.sync.SystemClock;
import dev.maxc.ui.anchors.TaskManagerController;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Max Carter
 * @since 21/04/2020
 */
public class MemoryManagementUnit implements SystemClock {
    private final RandomAccessMemory ram;
    private final boolean useSegmentation;
    private final ArrayList<LogicalMemoryHandler> logicalHandlers = new ArrayList<>();
    private final LogicalMemoryHandlerUtils logicalMemoryHandlerUtils;
    private final AtomicInteger logicalHandlerCount = new AtomicInteger(-1);
    private volatile Cache cache;
    private volatile int readRequests = 0;
    private volatile int writeRequests = 0;

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
    public MemoryManagementUnit(RandomAccessMemory ram, boolean useSegmentation, LogicalMemoryHandlerUtils logicalMemoryHandlerUtils, int cacheSize) {
        this.ram = ram;
        this.useSegmentation = useSegmentation;
        this.logicalMemoryHandlerUtils = logicalMemoryHandlerUtils;

        //dummy values for cache declaration.
        cache = new Cache(cacheSize);
    }

    /**
     * Allocates memory to a new process. The amount of memory that
     * is allocated is dependent on whether the system allocates
     * memory with Segmentation or Paging, this can all be modified
     * in the config file.
     */
    public synchronized boolean allocateMemory(int processIdentifier) {
        writeRequests++;
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
    public synchronized boolean allocateAdditionalMemory(int processIdentifier) {
        writeRequests++;
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

    public synchronized int getNextUnitOffset(int processIdentifier) {
        readRequests++;
        //search the logical handlers in the ram
        for (LogicalMemoryHandler handler : logicalHandlers) {
            if (handler.getParentProcessID() == processIdentifier) {
                try {
                    return handler.getNextUnitOffset();
                } catch (MemoryLogicalHandlerFullException e) {
                    allocateAdditionalMemory(processIdentifier);
                    return getNextUnitOffset(processIdentifier);
                }
            }
        }
        throw new OutOfMemoryError();
    }

    /**
     * Gets the memory unit for a process at a specific offset.
     */
    public synchronized MemoryUnit getMemoryUnit(int processIdentifier, int offset) {
        readRequests++;
        //checks the cache before searching the ram
        for (CacheMemoryNode cacheMemoryNode : cache) {
            if (cacheMemoryNode.getParentProcessID() == processIdentifier && cacheMemoryNode.getOffset() == offset) {
                //Logger.log(this, "Fetched memory unit from cache instead of memory for process [" + processIdentifier + "] at offset [" + offset + "]");
                return cacheMemoryNode.getMemoryUnit();
            }
        }

        //search the logical handlers in the ram
        for (LogicalMemoryHandler handler : logicalHandlers) {
            if (handler.getParentProcessID() == processIdentifier) {
                MemoryUnit unit = null;
                try {
                    unit = handler.getMemoryUnit(offset);
                } catch (MemoryUnitNotFoundException e) {
                    e.printStackTrace();
                }
                cache.add(new CacheMemoryNode(processIdentifier, offset, unit));
                return unit;
            }
        }
        Logger.log(Status.WARN, this, "Unable to get a Memory Unit for process [" + processIdentifier + "] allocating more memory and trying again...");
        return null;
    }

    /**
     * Clears a process from memory by identifying the handler for the process,
     * then making the handler free it's pointers, and then the handler is
     * removed from the memory's list of handlers. This means that the handler
     * will no longer be able to be accessed.
     */
    public synchronized void clearProcessMemory(int processIdentifier) {
        writeRequests++;
        for (int i = 0; i < logicalHandlers.size(); i++) {
            if (logicalHandlers.get(i).getParentProcessID() == processIdentifier) {
                logicalHandlers.get(i).free();
                logicalHandlers.remove(i);
                return;
            }
        }
    }

    @Override
    public void onSecondTick(TaskManagerController taskManagerController) {
        taskManagerController.addMemoryUsageData(readRequests, writeRequests);
        writeRequests = 0;
        readRequests = 0;
    }
}
