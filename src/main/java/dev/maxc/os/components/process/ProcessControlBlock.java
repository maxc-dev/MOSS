package dev.maxc.os.components.process;

import dev.maxc.os.components.cpu.ProgramCounter;
import dev.maxc.os.components.memory.model.MemoryAddress;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class ProcessControlBlock {
    private ProcessState processState = ProcessState.NEW;
    private final int processIdentifier;
    private final int parentProcessIdentifier;
    private final ProgramCounter programCounter = new ProgramCounter();

    public ProcessControlBlock(int processIdentifier, int parentProcessIdentifier) {
        this.processIdentifier = processIdentifier;
        this.parentProcessIdentifier = parentProcessIdentifier;
    }

    public void addToProgramCounter(int addressLocation) {
        programCounter.add(addressLocation);
    }

    public int getProcessID() {
        return processIdentifier;
    }

    public int getParentProcessID() {
        return parentProcessIdentifier;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }
}
