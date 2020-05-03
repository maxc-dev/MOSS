package dev.maxc.os.components.scheduler.disciplines;

import dev.maxc.os.components.instruction.Instruction;
import dev.maxc.os.components.scheduler.InstructionBatch;
import dev.maxc.os.structures.MutableQueue;
import dev.maxc.os.structures.Queue;

/**
 * @author Max Carter
 * @since 02/05/2020
 */
public abstract class SchedulingDiscipline {
    protected volatile Queue<Instruction> jobQueue = new Queue<>();

    /**
     * Uses an algorithm to decide what order the instruction sets
     * should be in when they enter the job queue.
     */
    public abstract void schedule(MutableQueue<InstructionBatch> instructionBatches);
}
