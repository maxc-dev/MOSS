package dev.maxc.os.components.scheduler;

import dev.maxc.os.components.instruction.Instruction;
import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.io.exceptions.deadlock.AccessingLockedUnitException;
import dev.maxc.os.io.exceptions.structures.EmptyQueueException;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;
import dev.maxc.os.structures.Queue;

/**
 * @author Max Carter
 * @since 02/05/2020
 */
public class InstructionBatch {
    private final Queue<Instruction> instructions = new Queue<>();
    private boolean locked = false;
    private final ProcessControlBlock pcb;

    public InstructionBatch(ProcessControlBlock pcb) {
        this.pcb = pcb;
    }

    public ProcessControlBlock getProcessControlBlock() {
        return pcb;
    }

    /**
     * Adds an instruction set to a batch which will be executed together.
     */
    public boolean addInstructionSet(Instruction instructionSet) {
        if (locked) {
            Logger.log(Status.WARN, this, "Instruction batch is already locked and cannot have anymore instruction sets added.");
            return false;
        }
        instructions.add(instructionSet);
        return true;
    }

    /**
     * Locks the batch so no more instructions can be added.
     */
    public void lock() {
        locked = true;
    }

    /**
     * Gets the amount of instructions to be executed.
     */
    public int getBursts() {
        return instructions.size();
    }

    /**
     * Gets the next instruction set to execute, although the batch must be locked.
     */
    public Instruction get() throws AccessingLockedUnitException, EmptyQueueException {
        if (locked) {
            if (instructions.isEmpty()) {
                Logger.log(Status.WARN, this, "Attempting to retrieve an instruction set from an empty queue.");
                throw new EmptyQueueException();
            }
            return instructions.get();
        }
        Logger.log(Status.ERROR, this, "Attempting to retrieve an instruction set when the batch is not locked.");
        throw new AccessingLockedUnitException();
    }

    public boolean hasNext() {
        return instructions.hasNext();
    }
}
