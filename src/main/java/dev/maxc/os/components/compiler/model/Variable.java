package dev.maxc.os.components.compiler.model;

import dev.maxc.os.components.compiler.CompilerTranslator;
import dev.maxc.os.components.instruction.Operand;
import dev.maxc.os.io.exceptions.compiler.InvalidVariableNameException;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 03/05/2020
 */
public class Variable {
    private final String identifier;
    private Operand operand;

    public Variable(String identifier, Operand operand) throws InvalidVariableNameException {
        if (!identifier.matches(CompilerTranslator.VARIABLE_NAME)) {
            throw new InvalidVariableNameException(identifier);
        }
        this.identifier = identifier;
        this.operand = operand;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Operand getOperand() {
        return operand;
    }

    public void setOperand(Operand operand) {
        this.operand = operand;
    }

    public static boolean isValidIdentifier(String identifier, ArrayList<Variable> vars) {
        for (Variable var : vars) {
            if (var.getIdentifier().contains(identifier) || identifier.contains(var.getIdentifier())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "identifier='" + identifier + '\'' +
                ", operand=" + operand.toString() +
                '}';
    }
}
