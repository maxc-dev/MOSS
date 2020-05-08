package dev.maxc.os.components.interpreter.model;

import dev.maxc.os.components.instruction.Instruction;
import dev.maxc.os.components.instruction.Opcode;
import dev.maxc.os.components.instruction.Operand;
import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.memory.model.MemoryUnit;
import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.components.scheduler.AdmissionScheduler;
import dev.maxc.os.io.exceptions.deadlock.MutatingLockedUnitException;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Max Carter
 * @since 03/05/2020
 */
public class InterpreterUtils {
    public static final String DECLARE = "=";
    public static final String VARIABLE_NAME = "[A-Za-z]+";
    public static final String OPERATOR = "[+|\\-|*|/]";
    public static final String WILDCARD = ".*";

    private final ArrayList<Variable> globalVariables;
    private final MemoryManagementUnit mmu;
    private final ProcessControlBlock pcb;

    public InterpreterUtils(ArrayList<Variable> globalVariables, MemoryManagementUnit mmu, ProcessControlBlock pcb) {
        this.globalVariables = globalVariables;
        this.mmu = mmu;
        this.pcb = pcb;
    }

    public void outputOperand(Operand operand) {
        int address = submitInstruction(new Instruction(Opcode.OUT, operand, null));
        pcb.getProgramCounter().add(address);
    }

    public Operand getVariable(String text) {
        return translateVariable(text);
    }

    public Operand translateVariable(String text) {
        if (text.matches("[0-9]+")) {
            return new Operand(false, Integer.parseInt(text.replace("#", "")));
        } else if (text.matches(WILDCARD + OPERATOR + WILDCARD)) {
            String[] vars = text.split(OPERATOR);
            for (String var : vars) {
                text = text.replace(var, translateVariable(var) + "");
            }
        }

        for (Variable variable : globalVariables) {
            if (text.matches(variable.getIdentifier())) {
                return variable.getOperand();
            }
        }

        return splitOperandList(text.split("(?<=" + OPERATOR + ")|(?=" + OPERATOR + ")"));
    }

    private int getNextBestOperatorIndex(String[] args) {
        for (String op : new String[]{"-", "+", "/", "*"}) {
            for (int i = 0; i < args.length; i++) {
                if (op.equals(args[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    private Operand splitOperandList(String[] list) {
        if (list.length == 1) {
            return new Operand(false, Integer.parseInt(list[0].replace("#", "") + ""));
        } else {
            int opIndex = getNextBestOperatorIndex(list);
            String[] beforeNew = Arrays.copyOfRange(list, 0, opIndex);
            String[] afterNew = Arrays.copyOfRange(list, opIndex + 1, list.length);
            return operate(beforeNew, list[opIndex], afterNew);
        }
    }

    private Operand operate(String[] before, String operator, String[] after) {
        Operand afterInt = splitOperandList(after);
        Operand beforeInt = splitOperandList(before);

        Opcode code = null;
        switch (operator) {
            case "*":
                code = Opcode.MUL;
                break;
            case "/":
                code = Opcode.DIV;
                break;
            case "+":
                code = Opcode.ADD;
                break;
            case "-":
                code = Opcode.SUB;
                break;
        }

        int address = submitInstruction(new Instruction(code, beforeInt, afterInt));
        pcb.getProgramCounter().add(address);
        return new Operand(true, address);
    }

    /**
     * Writes an instruction to memory by checking for the next available
     * memory unit in the page/segment. Will keep checking for a next
     * available instruction until one isn't locked
     */
    private int submitInstruction(Instruction instruction) {
        MemoryUnit unit = null;
        int offset = -1;
        boolean sent = false;
        while (!sent) {
            offset = mmu.getNextUnitOffset(pcb.getProcessID());
            unit = mmu.getMemoryUnit(pcb.getProcessID(), offset);
            unit.lock(pcb.getProcessID());
            try {
                unit.mutate(pcb.getProcessID(), instruction);
                sent = true;
            } catch (MutatingLockedUnitException e) {
                Logger.log(this, "Attempted to access a locked unit, retrying in 1ms.");
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        unit.unlock();
        return offset;
    }
}
