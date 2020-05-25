package dev.maxc.os.components.compiler;

import dev.maxc.os.components.instruction.Instruction;
import dev.maxc.os.components.instruction.Opcode;
import dev.maxc.os.components.instruction.Operand;
import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandlerUtils;
import dev.maxc.os.components.memory.indexer.FirstFit;
import dev.maxc.os.components.memory.model.MemoryUnit;
import dev.maxc.os.components.process.ProcessAPI;
import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.components.process.thread.ThreadAPI;
import dev.maxc.os.components.scheduler.AdmissionScheduler;
import dev.maxc.os.components.scheduler.CPUScheduler;
import dev.maxc.os.components.scheduler.disciplines.FirstInFirstOut;
import dev.maxc.os.io.exceptions.deadlock.AccessingLockedUnitException;
import dev.maxc.os.structures.Queue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerTest {
    public LogicalMemoryHandlerUtils getTestUtils() {
        return new LogicalMemoryHandlerUtils(4, 2);
    }

    public RandomAccessMemory getTestRAM() {
        return new RandomAccessMemory(16, FirstFit.class);
    }

    public MemoryManagementUnit getTestMMU(RandomAccessMemory ram, LogicalMemoryHandlerUtils utils, boolean useSegmentation) {
        return new MemoryManagementUnit(ram, useSegmentation, utils, 5);
    }

    public Compiler getTestInterpreter(MemoryManagementUnit mmu, ProcessAPI processAPI) {
        Queue<ProcessControlBlock> jobQueue = new Queue<>();
        return new Compiler(new AdmissionScheduler(new CPUScheduler(FirstInFirstOut.class, jobQueue)), mmu, processAPI);
    }

    /*
        This test will only pass for as long as sample.moss is NOT modified
        by the end user. Editing `sample.moss` could impact the test because it
        relies on an instruction at a specific memory unit location which
        could change if the file is edited.
     */
    @Test
    public void testCompiler() {
        RandomAccessMemory ram = getTestRAM();
        LogicalMemoryHandlerUtils utils = getTestUtils();
        MemoryManagementUnit mmu = getTestMMU(ram, utils, true);
        ThreadAPI threadAPI = new ThreadAPI();
        ProcessAPI processAPI = new ProcessAPI(threadAPI, mmu, true);
        Compiler compiler = getTestInterpreter(mmu, processAPI);
        compiler.compile("sample");

        try {
            MemoryUnit unit1 = mmu.getMemoryUnit(1, 0);
            unit1.lock(1);
            assertEquals(new Instruction(Opcode.MUL, new Operand(false, 3), new Operand(false, 4)).toString(), unit1.access(1).toString());
            unit1.unlock();
        } catch (AccessingLockedUnitException e) {
            e.printStackTrace();
        }
    }
}