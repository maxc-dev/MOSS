package dev.maxc.os.components.process;

import dev.maxc.os.components.process.thread.Thread;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class Process {
    private final ArrayList<Thread> threads = new ArrayList<>();
    private final ProcessControlBlock processControlBlock;
    private final ProcessAPI processAPI;

    public Process(ProcessControlBlock processControlBlock, ProcessAPI processAPI) {
        this.processControlBlock = processControlBlock;
        this.processAPI = processAPI;
    }

    /**
     * Exits the process.
     */
    public void exit() {
        processAPI.exitProcess(this);
    }

    /**
     * Forks a new process using the same parent Process ID
     */
    public void fork() {
        processAPI.getNewProcess(processControlBlock.getParentProcessID());
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
