package dev.maxc.os.components.cpu;

import dev.maxc.os.components.instruction.Instruction;
import dev.maxc.os.components.instruction.Opcode;
import dev.maxc.os.components.instruction.Operand;
import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.memory.model.MemoryUnit;
import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.components.process.ProcessState;
import dev.maxc.os.io.exceptions.deadlock.AccessingLockedUnitException;
import dev.maxc.os.io.exceptions.deadlock.MutatingLockedUnitException;
import dev.maxc.os.io.exceptions.instruction.UnknownOpcodeException;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class ProcessorCore {
    private final int index;
    private final MemoryManagementUnit mmu;
    private final ControlUnit cu;
    private final CoreThread coreThread;

    public ProcessorCore(int index, ControlUnit cu, MemoryManagementUnit mmu, int frequency) {
        Logger.log(this, "Processor Core [" + index + "] initialised.");
        this.index = index;
        this.cu = cu;
        this.mmu = mmu;
        coreThread = new CoreThread(this, frequency);
/*        coreThread = new Thread(() -> {
            while (true) {
                requestInstruction();
                try {
                    Thread.sleep(1000/frequency);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });*/
        coreThread.start();
    }

    /**
     * Continuously requests instructions to decode, triggered
     * in the core's thread.
     */
    protected synchronized void requestInstruction() {
        try {
            ProcessControlBlock pcb = cu.getJobQueuePCB();
            if (pcb != null) {
                coreThread.setBusy(true);
                decodeInstruction(pcb);
            }
        } catch (JobQueueEmpty ignored) {
        }
    }

    /**
     * Reads the program counter and fetches & decodes the instructions
     * then passes it to the executor.
     */
    public synchronized void decodeInstruction(ProcessControlBlock pcb) {
        while (pcb.getProgramCounter().hasNext()) {
            int nextAddress = pcb.getProgramCounter().getNextInstructionLocation();
            MemoryUnit unit = mmu.getMemoryUnit(pcb.getProcessID(), nextAddress);
            if (unit.isLocked()) {
                Logger.log(Status.WARN, this, "Process [" + pcb.getProcessID() + "] Unit [" + unit.toString() + "] is locked and cannot be accessed.");
            } else {
                try {
                    pcb.setProcessState(ProcessState.RUNNING);
                    unit.lock(pcb.getProcessID());
                    Instruction instruction = unit.access(pcb.getProcessID());
                    int val1 = getValueFromOperand(pcb, instruction.getOperand1());
                    if (instruction.getOpcode().needsSecondOperand()) {
                        int val2 = getValueFromOperand(pcb, instruction.getOperand2());
                        executeInstruction(pcb, instruction.getOpcode(), val1, val2, unit);
                    } else {
                        if (instruction.getOpcode() == Opcode.STR) {
                            saveExecution(pcb, unit, instruction.getOperand1().get());
                        } else {
                            executeInstruction(instruction.getOpcode(), val1);
                        }
                    }
                    unit.unlock();
                    coreThread.setBusy(false);

                } catch (AccessingLockedUnitException | UnknownOpcodeException | MutatingLockedUnitException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private int getValueFromOperand(ProcessControlBlock pcb, Operand operand) throws AccessingLockedUnitException {
        if (operand.isDirect()) {
            return operand.get();
        }
        MemoryUnit unit = mmu.getMemoryUnit(pcb.getProcessID(), operand.get());
        unit.lock(pcb.getProcessID());
        return unit.access(pcb.getProcessID()).getOperand1().get(); //the ret value should have opcode "STR" implying it is direct
    }

    private void executeInstruction(ProcessControlBlock pcb, Opcode opcode, int val1, int val2, MemoryUnit unit) throws UnknownOpcodeException, MutatingLockedUnitException {
        int val3;
        switch (opcode) {
            case MUL:
                val3 = val1 * val2;
                break;
            case DIV:
                val3 = val1 / val2;
                break;
            case ADD:
                val3 = val1 + val2;
                break;
            case SUB:
                val3 = val1 - val2;
                break;
            default:
                Logger.log(Status.CRIT, this, "Opcode not recognised [" + opcode.toString() + "] for process [" + pcb.getProcessID() + "]");
                throw new UnknownOpcodeException(opcode.toString());
        }
        saveExecution(pcb, unit, val3);
    }

    private void executeInstruction(Opcode opcode, int val) {
        if (opcode == Opcode.OUT) {
            Logger.log(toString() + ":OUT", "" + val);
        }
    }

    private void saveExecution(ProcessControlBlock pcb, MemoryUnit unit, int val) throws MutatingLockedUnitException {
        unit.mutate(pcb.getProcessID(), new Instruction(Opcode.STR, new Operand(false, val)));
    }

    @Override
    public String toString() {
        return "Core-" + index;
    }
}
