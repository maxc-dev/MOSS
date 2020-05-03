package dev.maxc.os.components.interpreter.model;

import dev.maxc.os.components.interpreter.model.object.variable.Variable;
import dev.maxc.os.io.log.Status;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Max Carter
 * @since 03/05/2020
 */
public class InterpreterUtils {
    public static final String OPEN = "(";
    public static final String CLOSE = ")";
    public static final String DECLARE = "=";
    public static final String VARIABLE_NAME = "[A-Za-z]+";
    public static final String OPERATOR = "[+|\\-|*|/]";
    public static final String WILDCARD = ".*";

    private final ArrayList<Variable> globalVariables;

    public InterpreterUtils(ArrayList<Variable> globalVariables) {
        this.globalVariables = globalVariables;
    }

    public int getIntReturn(String text) {
        return getInt(text);
    }

    public int getInt(String text) {
        if (text.matches("[0-9]+")) {
            return Integer.parseInt(text);
        } else if (text.matches(WILDCARD + OPERATOR + WILDCARD)) {
            String[] vars = text.split(OPERATOR);
            for (String var : vars) {
                text = text.replace(var, getInt(var) + "");
            }
        }

        for (Variable variable : globalVariables) {
            if (text.matches(variable.getName())) {
                return variable.getValue();
            }
        }

        String[] values = text.split("(?<=" + OPERATOR + ")|(?=" + OPERATOR + ")");
        return indexLists(values);
    }

    private int getNextBestOperatorIndex(String[] args) {
        for(String op : new String[]{ "-", "+", "/", "*" }) {
            for (int i = 0; i < args.length; i++) {
                if (op.equals(args[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    private int indexLists(String[] list) {
        if (list.length == 1) {
            return Integer.parseInt(list[0] + "");
        } else {
            int opIndex = getNextBestOperatorIndex(list);
            String[] beforeNew = Arrays.copyOfRange(list, 0, opIndex);
            String[] afterNew = Arrays.copyOfRange(list, opIndex+1, list.length);
            return operate(beforeNew, list[opIndex], afterNew);
        }
    }

    private int operate(String[] before, String operator, String[] after) {
        int afterInt = indexLists(after);
        int beforeInt = indexLists(before);

       switch (operator) {
           case "*":
               return beforeInt * afterInt;
           case "/":
               return beforeInt / afterInt;
           case "+":
               return beforeInt + afterInt;
           default:
               return beforeInt - afterInt;
       }
    }
}
