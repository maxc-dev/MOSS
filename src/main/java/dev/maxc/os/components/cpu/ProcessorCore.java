package dev.maxc.os.components.cpu;

import dev.maxc.os.components.instruction.Instruction;
import dev.maxc.os.components.instruction.Opcode;
import dev.maxc.os.components.instruction.Operand;
import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.memory.model.MemoryUnit;
import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.components.process.ProcessState;
import dev.maxc.os.io.exceptions.cpu.DirectAddressingException;
import dev.maxc.os.io.exceptions.deadlock.AccessingLockedUnitException;
import dev.maxc.os.io.exceptions.deadlock.MutatingLockedUnitException;
import dev.maxc.os.io.exceptions.instruction.UnknownOpcodeException;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;
import dev.maxc.ui.anchors.TaskManagerController;
import javafx.application.Platform;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class ProcessorCore {
    private final int index;
    private final MemoryManagementUnit mmu;
    private volatile CoreThread coreThread;
    private TaskManagerController taskManager;
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
            try {
                MemoryUnit unit = mmu.getMemoryUnit(pcb.getProcessID(), pcb.getProgramCounter().getNextInstructionLocation());
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
                        executeInstruction(pcb, instruction.getOpcode(), val1);
                    }
                }
                //finishes by unlocking the unit
                unit.unlock();
            } catch (AccessingLockedUnitException | UnknownOpcodeException | MutatingLockedUnitException | DirectAddressingException ex) {
                Logger.log(Status.ERROR, toString(), "Unable to decode & execute instruction so the process has been terminated, see stack trace for more details.");
                ex.printStackTrace();
                //^ critical error if the cpu cannot decode an instruction successfully
            }
        }
        pcb.setProcessState(ProcessState.TERMINATED);
        socketPCB = null;
        coreThread.setBusy(false);
    }

    /**
     * Extracts the value of an operand. If the operand uses immediate
     * addressing it will unlock the memory unit it belongs to and assume
     * that the operand uses direct addressing in a stored instruction.
     * If this isn't the case there has been an undetectable error with the
     * program counter where it has incorrectly queued the instructions.
     */
    private int getValueFromOperand(ProcessControlBlock pcb, Operand operand) throws AccessingLockedUnitException, DirectAddressingException {
        if (operand.isDirect()) {
            return operand.get();
        }
        MemoryUnit unit = mmu.getMemoryUnit(pcb.getProcessID(), operand.get());
        unit.lock(pcb.getProcessID());
        Instruction requiredInstruction = unit.access(pcb.getProcessID());
        unit.unlock();
        //the ret value should have opcode "STR" implying it is direct
        if (requiredInstruction.getOpcode() == Opcode.STR) {
            return requiredInstruction.getOperand1().get();
        }
        Logger.log(Status.ERROR, toString(), "Tried to get a direct value which is incorrectly stored.");
        throw new DirectAddressingException(requiredInstruction.getOperand1().get());
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
    private void executeInstruction(ProcessControlBlock pcb, Opcode opcode, int val) {
        if (opcode == Opcode.OUT) {
            Logger.log(Status.OUT, toString(), "[Process-" + pcb.getProcessID() + "] " + val);
            if (taskManager != null) {
                Platform.runLater(() -> taskManager.output("[" + toString() + "][Process-" + pcb.getProcessID() + "] " + val));
            }
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

    protected void setTaskManager(TaskManagerController taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public String toString() {
        return "Core-" + index;
    }
}
