package dev.maxc.os.components.virtual;

import dev.maxc.os.components.cpu.ProgramCounter;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class ProcessControlBlock {
    private ProcessState processState = ProcessState.NEW;
    private final int processIdentifier;
    private final int parentProcessIdentifier;
    private final ProgramCounter programCounter;

    public ProcessControlBlock(int processIdentifier, int parentProcessIdentifier, ProgramCounter programCounter) {
        this.processIdentifier = processIdentifier;
        this.parentProcessIdentifier = parentProcessIdentifier;
        this.programCounter = programCounter;
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
