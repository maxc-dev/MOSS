package dev.maxc.os.components.process;

import dev.maxc.os.components.cpu.ProgramCounter;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class ProcessControlBlock {
    private ProcessState processState = ProcessState.NEW;
    private final int processIdentifier;
    private final int parentProcessIdentifier;
    private final ProgramCounter programCounter = new ProgramCounter();
    private final ProcessAPI processAPI;

    public ProcessControlBlock(int processIdentifier, int parentProcessIdentifier, ProcessAPI processAPI) {
        this.processIdentifier = processIdentifier;
        this.parentProcessIdentifier = parentProcessIdentifier;
        this.processAPI = processAPI;
    }

    public ProgramCounter getProgramCounter() {
        return programCounter;
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
        if (processState == ProcessState.TERMINATED) {
            processAPI.exitProcess(processIdentifier);
        }
    }

    @Override
    public String toString() {
        return "ProcessControlBlock{" +
                "PID=" + processIdentifier +
                ", PPID=" + parentProcessIdentifier +
                ", State=" + processState +
                ", PC=" + programCounter.toString() +
                '}';
    }
}
