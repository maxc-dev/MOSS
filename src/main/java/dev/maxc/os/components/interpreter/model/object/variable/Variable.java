package dev.maxc.os.components.interpreter.model.object.variable;

import dev.maxc.os.components.instruction.Operand;
import dev.maxc.os.components.interpreter.model.InterpreterUtils;
import dev.maxc.os.io.exceptions.interpreter.InvalidVariableNameException;

/**
 * @author Max Carter
 * @since 03/05/2020
 */
public class Variable {
    private final String identifier;
    private Operand operand;

    public Variable(String identifier, Operand operand) throws InvalidVariableNameException {
        if (!identifier.matches(InterpreterUtils.VARIABLE_NAME)) {
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

    @Override
    public String toString() {
        return "Variable{" +
                "identifier='" + identifier + '\'' +
                ", operand=" + operand.toString() +
                '}';
    }
}
