package dev.maxc.os.components.interpreter.model.object.variable;

import dev.maxc.os.components.interpreter.model.InterpreterUtils;
import dev.maxc.os.io.exceptions.interpreter.InvalidVariableNameException;

/**
 * @author Max Carter
 * @since 03/05/2020
 */
public class Variable {
    private final String name;
    private int value;

    public Variable(String name, int value) throws InvalidVariableNameException {
        if (!name.matches(InterpreterUtils.VARIABLE_NAME)) {
            throw new InvalidVariableNameException(name);
        }
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
