package dev.maxc.os.components.compiler;

import dev.maxc.os.components.compiler.model.Variable;
import dev.maxc.os.components.instruction.Instruction;
import dev.maxc.os.components.instruction.Opcode;
import dev.maxc.os.components.instruction.Operand;
import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.memory.model.MemoryUnit;
import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.io.exceptions.deadlock.MutatingLockedUnitException;
import dev.maxc.os.io.log.Logger;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Max Carter
 * @since 03/05/2020
 */
public class CompilerTranslator {
    public static final String DECLARE = "=";
    public static final String VARIABLE_NAME = "[A-Za-z]+";
    public static final String OPERATOR = "[+|\\-|*|/]";
    public static final String WILDCARD = ".*";

    private final ArrayList<Variable> globalVariables;
    private final MemoryManagementUnit mmu;
    private final ProcessControlBlock pcb;

    public CompilerTranslator(ArrayList<Variable> globalVariables, MemoryManagementUnit mmu, ProcessControlBlock pcb) {
        this.globalVariables = globalVariables;
        this.mmu = mmu;
        this.pcb = pcb;
    }

    /**
     * Writes an output instruction directly.
     */
    public void outputOperand(Operand operand) {
        int address = submitInstruction(new Instruction(Opcode.OUT, operand, null));
        pcb.getProgramCounter().add(address);
    }

    /**
     * Extracts the operand from some text
     */
    public Operand getOperand(String text) {
        return translateVariable(text);
    }

    /**
     * Translates text into an operand which can be understood by the CPU.
     */
    public Operand translateVariable(String text) {
        /*
            If the text is a number it is returned as an operand
            with a direct addressing mode.
         */
        if (text.matches("[0-9]+")) {
            return new Operand(false, Integer.parseInt(text.replace("#", "")));

        /*
            If the text is not an integer it references a variable or is an equation.
            The text is then split at the operators (*,/,+,-) to obtain a list of
            new values (variables or integers) and the same method is called again
            to understand these individual values.
         */
        } else if (text.matches(WILDCARD + OPERATOR + WILDCARD)) {
            String[] vars = text.split(OPERATOR);
            for (String var : vars) {
                String replacement = translateVariable(var).toString();
                text = text.replace(var, replacement);
            }
        }

        /*
            If the text is that of a variable identifier, the variable's operand
            can be directly returned since it has already been compiled.
         */
        for (Variable variable : globalVariables) {
            if (text.equals(variable.getIdentifier())) {
                return variable.getOperand();
            }
        }

        /*
            If the text has not yet been returned as an operand, it is an equation so
            the text is split into a list of values and operations which can be converted
            to instructions for the CPU to decode and execute.
         */
        return splitOperandList(text.split("(?<=" + OPERATOR + ")|(?=" + OPERATOR + ")"));
    }

    /**
     * Given a list of integers, variables and operators, this will
     * use BIDMAS logic to determine the next best operator to focus on.
     */
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

    /**
     * Converts a list of operations, variables and integers into
     * an operand.
     */
    private Operand splitOperandList(String[] list) {
        if (list.length == 1) {
            return new Operand(!list[0].contains("#"), Integer.parseInt(list[0].replace("#", "") + ""));
        } else {
            int opIndex = getNextBestOperatorIndex(list);
            String[] beforeNew = Arrays.copyOfRange(list, 0, opIndex);
            String[] afterNew = Arrays.copyOfRange(list, opIndex + 1, list.length);
            return operate(beforeNew, list[opIndex], afterNew);
        }
    }

    /**
     * Read the operation in the equation and requests an instruction
     * to be written into memory.
     */
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

        //gets the next available address in memory
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
