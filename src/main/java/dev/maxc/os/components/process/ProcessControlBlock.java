package dev.maxc.os.components.process;

import dev.maxc.os.components.cpu.ProgramCounter;
import dev.maxc.os.io.log.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    private final Date processStartTime;
    private Date processEndTime;

    public ProcessControlBlock(int processIdentifier, int parentProcessIdentifier, ProcessAPI processAPI) {
        this.processIdentifier = processIdentifier;
        this.parentProcessIdentifier = parentProcessIdentifier;
        this.processAPI = processAPI;
        processStartTime = Calendar.getInstance().getTime();
    }

    public ProgramCounter getProgramCounter() {
        return programCounter;
    }

    public int getCPUBursts() {
        return programCounter.size();
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
        if (this.processState != processState) {
            this.processState = processState;
            if (processState == ProcessState.TERMINATED) {
                processEndTime = Calendar.getInstance().getTime();
                long diffInMillies = processEndTime.getTime() - processStartTime.getTime();
                Logger.log(this, "Process [" + processIdentifier + "] terminated. Alive time: [" + TimeUnit.MILLISECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS) + "ms]");
                processAPI.exitProcess(processIdentifier);
            }
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
