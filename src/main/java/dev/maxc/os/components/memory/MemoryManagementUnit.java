package dev.maxc.os.components.memory;

import dev.maxc.os.components.memory.allocation.LogicalMemoryHandler;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandlerUtils;
import dev.maxc.os.components.memory.allocation.Paging;
import dev.maxc.os.components.memory.allocation.Segmentation;
import dev.maxc.os.components.memory.model.Cache;
import dev.maxc.os.components.memory.model.CacheMemoryNode;
import dev.maxc.os.components.memory.model.MemoryUnit;
import dev.maxc.os.components.memory.virtual.VirtualMemoryDiskNode;
import dev.maxc.os.components.memory.virtual.VirtualMemoryInterface;
import dev.maxc.os.io.exceptions.disk.MemoryHandlerNotFoundException;
import dev.maxc.os.io.exceptions.memory.InvalidIndexSizeError;
import dev.maxc.os.io.exceptions.memory.MemoryLogicalHandlerFullException;
import dev.maxc.os.io.exceptions.memory.MemoryUnitNotFoundException;
import dev.maxc.os.io.exceptions.memory.virtual.OutOfHandlersException;
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
    private VirtualMemoryInterface vmi = null;
    private final AtomicInteger logicalHandlerCount = new AtomicInteger(-1);
    private final Cache translationLookasideBuffer;
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
        translationLookasideBuffer = new Cache(cacheSize);
    }

    public void initVirtualMemoryInterface(VirtualMemoryInterface vmi) {
        this.vmi = vmi;
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
        if (ram.getAvailableMemory() >= logicalMemoryHandlerUtils.getInitialSize() || vmi != null) {
            LogicalMemoryHandler allocator;
            if (useSegmentation) {
                allocator = new Segmentation(ram, logicalMemoryHandlerUtils, logicalHandlerCount.addAndGet(1), processIdentifier);
            } else {
                allocator = new Paging(ram, logicalMemoryHandlerUtils, logicalHandlerCount.addAndGet(1), processIdentifier);
            }
            try {
                allocator.allocate(ram.indexMemory(logicalMemoryHandlerUtils.getInitialSize()));
            } catch (OutOfMemoryError | InvalidIndexSizeError er) {
                er.printStackTrace();
                return false;
            }
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
        for (LogicalMemoryHandler handler : logicalHandlers) {
            if (handler.getParentProcessID() == processIdentifier) {
                if (handler.isInVirtualMemory() && vmi != null) {
                    try {
                        vmi.pushToDisk(getLeastUsedHandler(processIdentifier));
                        vmi.pullFromDisk(processIdentifier);
                    } catch (MemoryHandlerNotFoundException | OutOfHandlersException e) {
                        Logger.log(Status.CRIT, this, "Process [" + processIdentifier + "] was lost during paging swap.");
                        e.printStackTrace();
                        return false;
                    }
                }
                try {
                    handler.allocate(ram.indexMemory(useSegmentation ? logicalMemoryHandlerUtils.getIncrease() : logicalMemoryHandlerUtils.getInitialSize()));
                } catch (OutOfMemoryError | InvalidIndexSizeError er) {
                    er.printStackTrace();
                    return false;
                }
                return true;
            }
        }
        Logger.log(Status.ERROR, this, "Unable to allocate memory to process [" + processIdentifier + "] because the process ID could not be linked to a memory handler.");
        return false;
    }

    public synchronized void allocateMemoryHandler(VirtualMemoryDiskNode diskNode) {
        int allocationSize = diskNode.getInstructions().size();
        if (allocationSize < logicalMemoryHandlerUtils.getInitialSize()) {
            allocationSize = logicalMemoryHandlerUtils.getInitialSize();
        }
        try {
            diskNode.getHandler().allocate(ram.indexMemory(allocationSize));
        } catch (OutOfMemoryError er) {
            er.printStackTrace();
        } catch (InvalidIndexSizeError ex) {
            Logger.log(Status.CRIT, this, "Unable to pull process from disk, check stack trace for more details.");
            ex.printStackTrace();
        }
    }

    /**
     * Finds the most appropriate handler to be sent to virtual memory.
     */
    public synchronized LogicalMemoryHandler getLeastUsedHandler() throws OutOfHandlersException {
        for (LogicalMemoryHandler handler : logicalHandlers) {
            if (handler.isInMainMemory()) {
                return handler;
            }
        }
        Logger.log(Status.WARN, this, "Could not find a handler to sent to virtual memory.");
        throw new OutOfHandlersException();
    }

    /**
     * Finds the most appropriate handler to be sent to virtual memory.
     */
    public synchronized LogicalMemoryHandler getLeastUsedHandler(int ignoreProcess) throws OutOfHandlersException {
        for (LogicalMemoryHandler handler : logicalHandlers) {
            if (handler.isInMainMemory() && ignoreProcess != handler.getId()) {
                return handler;
            }
        }
        Logger.log(Status.WARN, this, "Could not find a handler to sent to virtual memory.");
        throw new OutOfHandlersException();
    }

    public synchronized int getNextUnitOffset(int processIdentifier) {
        readRequests++;
        //search the logical handlers in the ram
        for (LogicalMemoryHandler handler : logicalHandlers) {
            if (handler.getParentProcessID() == processIdentifier) {
                if (handler.isInVirtualMemory() && vmi != null) {
                    try {
                        vmi.pushToDisk(getLeastUsedHandler(processIdentifier));
                        vmi.pullFromDisk(processIdentifier);
                    } catch (MemoryHandlerNotFoundException e) {
                        Logger.log(Status.CRIT, this, "Process [" + processIdentifier + "] was lost during paging swap.");
                        e.printStackTrace();
                    } catch (OutOfHandlersException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    return handler.getNextUnitOffset();
                } catch (MemoryLogicalHandlerFullException e) {
                    if (allocateAdditionalMemory(processIdentifier)) {
                        return getNextUnitOffset(processIdentifier);
                    }
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
        for (CacheMemoryNode cacheMemoryNode : translationLookasideBuffer) {
            if (cacheMemoryNode.getParentProcessID() == processIdentifier && cacheMemoryNode.getOffset() == offset) {
                //Logger.log(this, "Fetched memory unit from cache instead of memory for process [" + processIdentifier + "] at offset [" + offset + "]");
                return cacheMemoryNode.getMemoryUnit();
            }
        }

        //search the logical handlers in the ram
        for (LogicalMemoryHandler handler : logicalHandlers) {
            if (handler.getParentProcessID() == processIdentifier) {
                if (handler.isInVirtualMemory() && vmi != null) {
                    try {
                        vmi.pullFromDisk(processIdentifier);
                    } catch (MemoryHandlerNotFoundException e) {
                        Logger.log(Status.CRIT, this, "Process [" + processIdentifier + "] was lost during paging swap.");
                        e.printStackTrace();
                    }
                }
                MemoryUnit unit = null;
                try {
                    unit = handler.getMemoryUnit(offset);
                } catch (MemoryUnitNotFoundException e) {
                    e.printStackTrace();
                }
                translationLookasideBuffer.add(new CacheMemoryNode(processIdentifier, offset, unit));
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
                if (logicalHandlers.get(i).isInVirtualMemory()) {
                    vmi.requestDiskCleanUp(processIdentifier);
                }
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
