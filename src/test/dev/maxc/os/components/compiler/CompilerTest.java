package dev.maxc.os.components.compiler;

import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandlerUtils;
import dev.maxc.os.components.memory.indexer.FirstFit;
import dev.maxc.os.components.process.ProcessAPI;
import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.components.process.thread.ThreadAPI;
import dev.maxc.os.components.scheduler.AdmissionScheduler;
import dev.maxc.os.components.scheduler.CPUScheduler;
import dev.maxc.os.components.scheduler.disciplines.FirstInFirstOut;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.structures.Queue;
import org.junit.jupiter.api.Test;

class CompilerTest {
    public LogicalMemoryHandlerUtils getTestUtils() {
        return new LogicalMemoryHandlerUtils(2, 4, 2);
    }

    public RandomAccessMemory getTestRAM() {
        return new RandomAccessMemory(2, 16, FirstFit.class);
    }

    public MemoryManagementUnit getTestMMU(RandomAccessMemory ram, LogicalMemoryHandlerUtils utils, boolean useSegmentation) {
        return new MemoryManagementUnit(ram, useSegmentation, utils, 5);
    }

    public Compiler getTestInterpreter(MemoryManagementUnit mmu, ProcessAPI processAPI) {
        Queue<ProcessControlBlock> jobQueue = new Queue<>();
        return new Compiler(new AdmissionScheduler(new CPUScheduler(FirstInFirstOut.class, jobQueue)), mmu, processAPI);
    }

    @Test
    public void testInterpreter() {
        RandomAccessMemory ram = getTestRAM();
        LogicalMemoryHandlerUtils utils = getTestUtils();
        MemoryManagementUnit mmu = getTestMMU(ram, utils, true);
        ThreadAPI threadAPI = new ThreadAPI();
        ProcessAPI processAPI = new ProcessAPI(threadAPI, mmu);
        Compiler compiler = getTestInterpreter(mmu, processAPI);
        compiler.compile("pf1");
        Logger.log(this, mmu.getMemoryUnit(1, 0).toString());
        Logger.log(this, mmu.getMemoryUnit(1, 1).toString());
        Logger.log(this, mmu.getMemoryUnit(1, 2).toString());
        Logger.log(this, mmu.getMemoryUnit(1, 3).toString());

    }

}