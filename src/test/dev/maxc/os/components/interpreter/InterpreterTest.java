package dev.maxc.os.components.interpreter;

import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandlerUtils;
import dev.maxc.os.components.memory.indexer.FirstFit;
import dev.maxc.os.components.process.ProcessAPI;
import dev.maxc.os.components.process.thread.ThreadAPI;
import org.junit.jupiter.api.Test;

class InterpreterTest {
    public LogicalMemoryHandlerUtils getTestUtils() {
        return new LogicalMemoryHandlerUtils(2, 4, 2);
    }

    public RandomAccessMemory getTestRAM() {
        return new RandomAccessMemory(2, 16, FirstFit.class, true);
    }

    public MemoryManagementUnit getTestMMU(RandomAccessMemory ram, LogicalMemoryHandlerUtils utils, boolean useSegmentation) {
        return new MemoryManagementUnit(ram, useSegmentation, utils, 5);
    }

    public Interpreter getTestInterpreter() {
        RandomAccessMemory ram = getTestRAM();
        LogicalMemoryHandlerUtils utils = getTestUtils();
        MemoryManagementUnit mmu = getTestMMU(ram, utils, true);
        ThreadAPI threadAPI = new ThreadAPI();
        ProcessAPI processAPI = new ProcessAPI(threadAPI, mmu);
        return new Interpreter(mmu, processAPI);
    }

    @Test
    public void testInterpreter() {
        Interpreter interpreter = getTestInterpreter();
        interpreter.interpret("pf1");
    }

}