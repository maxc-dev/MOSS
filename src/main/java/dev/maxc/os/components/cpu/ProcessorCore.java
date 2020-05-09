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
    private volatile CoreThread coreThread;

    private volatile ProcessControlBlock socketPCB = null;

    public ProcessorCore(int index, MemoryManagementUnit mmu) {
        Logger.log(this, "Processor Core [" + index + "] initialised.");
        this.index = index;
        this.mmu = mmu;
    }

    /**
     * Initialises the core thread at a specified frequency.
     */
    protected void initCoreThread(int frequency) {
        coreThread = new CoreThread(this, frequency);
        coreThread.start();
    }

    /**
     * Writes the process control block to the core's socket
     * for it to be decoded.
     *
     * @return Returns false if the socket is occupied by another process
     */
    protected synchronized boolean writePCBSocket(ProcessControlBlock pcb) {
        if (socketPCB == null) {
            this.socketPCB = pcb;
            return true;
        }
        return false;
    }

    /**
     * Reads the process control block socket to see
     * if the control unit requires another process to
     * be fetched decoded and executed.
     */
    protected synchronized void readPCBSocket() {
        if (socketPCB != null) {
            coreThread.setBusy(true);
            decodeInstruction(socketPCB);
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
                    //locks the memory unit to the process and requests access
                    pcb.setProcessState(ProcessState.RUNNING);
                    unit.lock(pcb.getProcessID());
                    Instruction instruction = unit.access(pcb.getProcessID());

                    //extracts the values of the operand1 (and operand2 if it's needed)
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
                    //finishes by unlocking the unit, opens the socket and continues the thread
                    unit.unlock();
                    coreThread.setBusy(false);

                } catch (AccessingLockedUnitException | UnknownOpcodeException | MutatingLockedUnitException ex) {
                    ex.printStackTrace();
                    //^ critical error if the cpu cannot decode an instruction successfully
                }
            }
        }
        pcb.setProcessState(ProcessState.TERMINATED);

        socketPCB = null;
    }

    /**
     * Extracts the value of an operand. If the operand uses immediate
     * addressing it will unlock the memory unit it belongs to and assume
     * that the operand uses direct addressing in a stored instruction.
     * If this isn't the case there has been an undetectable error with the
     * program counter where it has incorrectly queued the instructions.
     */
    private int getValueFromOperand(ProcessControlBlock pcb, Operand operand) throws AccessingLockedUnitException {
        if (operand.isDirect()) {
            return operand.get();
        }
        MemoryUnit unit = mmu.getMemoryUnit(pcb.getProcessID(), operand.get());
        unit.lock(pcb.getProcessID());
        int val = unit.access(pcb.getProcessID()).getOperand1().get();
        unit.unlock();
        return val; //the ret value should have opcode "STR" implying it is direct
    }

    /**
     * Performs the execution of the instruction with two operands
     */
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

    /**
     * Executes an instruction with only one operand
     */
    private void executeInstruction(Opcode opcode, int val) {
        if (opcode == Opcode.OUT) {
            Logger.log(toString() + ":OUT", "" + val);
        }
    }

    /**
     * Saves the result of the execution into the same memory address
     */
    private void saveExecution(ProcessControlBlock pcb, MemoryUnit unit, int val) throws MutatingLockedUnitException {
        unit.mutate(pcb.getProcessID(), new Instruction(Opcode.STR, new Operand(false, val)));
    }

    /**
     * Returns true if the core is available to socket writes
     * of the pcb
     */
    public synchronized boolean isAvailable() {
        return socketPCB == null;
    }

    @Override
    public String toString() {
        return "Core-" + index;
    }
}
