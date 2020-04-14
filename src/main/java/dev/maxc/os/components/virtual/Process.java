package dev.maxc.os.components.virtual;

import java.util.ArrayList;

import dev.maxc.os.system.SystemAPI;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class Process {
    private final ArrayList<Thread> threads = new ArrayList<>();
    private final ProcessControlBlock processControlBlock;

    public Process(ProcessControlBlock processControlBlock) {
        this.processControlBlock = processControlBlock;
    }

    /**
     * Exits the process.
     */
    public void exit() {
        SystemAPI.processAPI.exitProcess(this);
    }

    /**
     * Forks a new process using the same parent Process ID
     */
    public void fork() {
        SystemAPI.processAPI.getNewProcess(processControlBlock.getParentProcessID());
    }

    public ArrayList<Thread> getThreads() {
        return threads;
    }

    public ProcessControlBlock getProcessControlBlock() {
        return processControlBlock;
    }

    @Override
    public String toString() {
        return "Thread-" + getProcessControlBlock().getProcessID();
    }
}
