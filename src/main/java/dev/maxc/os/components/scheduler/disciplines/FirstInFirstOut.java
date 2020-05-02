package dev.maxc.os.components.scheduler.disciplines;

import dev.maxc.os.components.scheduler.InstructionBatch;
import dev.maxc.os.components.scheduler.InstructionSet;
import dev.maxc.os.io.exceptions.deadlock.AccessingLockedUnitException;
import dev.maxc.os.io.exceptions.structures.EmptyQueueException;
import dev.maxc.os.structures.MutableQueue;

/**
 * @author Max Carter
 * @since 02/05/2020
 */
public class FirstInFirstOut extends SchedulingDiscipline {
    @Override
    public void schedule(MutableQueue<InstructionBatch> instructionBatches) {
        //loop the batch
        while (instructionBatches.hasNext()) {
            InstructionBatch batch = instructionBatches.get();
            try {
                //loop each instruction set in the batch
                while (batch.hasNext()) {
                    InstructionSet set = batch.get();
                    jobQueue.add(set);
                }
            } catch (AccessingLockedUnitException | EmptyQueueException ex) {
                ex.printStackTrace();
            }
        }
    }
}
