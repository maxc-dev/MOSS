package dev.maxc.os.components.interpreter;

import dev.maxc.App;
import dev.maxc.os.components.instruction.Instruction;
import dev.maxc.os.components.instruction.Operand;
import dev.maxc.os.components.interpreter.model.InterpreterUtils;
import dev.maxc.os.components.interpreter.model.object.variable.Variable;
import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.process.Process;
import dev.maxc.os.components.process.ProcessAPI;
import dev.maxc.os.io.exceptions.interpreter.InvalidVariableNameException;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;
import dev.maxc.os.structures.Stack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Max Carter
 * @since 03/05/2020
 */
public class Interpreter {
    private static final String COMMENT_CHARACTER = "#";
    private static final String FILE_EXTENSION = ".moss";

    private final ArrayList<Variable> globalVariables = new ArrayList<>();
    private final MemoryManagementUnit mmu;
    private final ProcessAPI processAPI;


    public Interpreter(MemoryManagementUnit mmu, ProcessAPI processAPI) {
        this.mmu = mmu;
        this.processAPI = processAPI;
    }

    /**
     * Runs the interpreter on a file.
     */
    public void interpret(String fileName) {
        //read file
        Logger.log(this, "Gathering process file [" + fileName + "]");
        BufferedReader re = null;
        try {
            String file = App.class.getResource(fileName + FILE_EXTENSION).getFile();
            re = new BufferedReader(new FileReader(file));
        } catch (IOException ex) {
            Logger.log(Status.ERROR, this, "Could not recognise file: [" + fileName + FILE_EXTENSION + "].");
            ex.printStackTrace();
        }

        Process mainProcess = processAPI.getNewProcess(ProcessAPI.NO_PARENT_PROCESS);
        InterpreterUtils utils = new InterpreterUtils(globalVariables, mmu, mainProcess.getProcessControlBlock());
        Logger.log(this, "Created new interpreter process [" + mainProcess.toString() + "]");

        Logger.log(this, "Successfully gathered process file [" + fileName + FILE_EXTENSION + "] interpreting...");
        //iterate file
        try {
            int lineNumber = 1;
            String line;
            while ((line = Objects.requireNonNull(re).readLine()) != null) {
                line = line.replaceAll(" ", "");
                if (!line.startsWith(COMMENT_CHARACTER) && !line.isBlank()) {
                    Stack<Instruction> instructionStack = new Stack<>();

                    //if variable declaration is inbound
                    if (line.contains(InterpreterUtils.DECLARE)) {
                        int declareIndex = line.indexOf(InterpreterUtils.DECLARE);
                        Variable var = new Variable(line.substring(0, declareIndex), utils.getVariable(line.substring(declareIndex+1)));
                        globalVariables.add(var);
                        Logger.log(this, "New variable initialized [" + var.toString() + "]");

                    } else if (line.startsWith("print(") && line.endsWith(")")) {
                        Operand output = utils.getVariable(line.substring(line.indexOf("(")+1, line.indexOf(")")));
                        utils.outputOperand(output);
                    }

                    lineNumber++;
                }
            }
        } catch (IOException | InvalidVariableNameException ex) {
            ex.printStackTrace();
        }
    }
}
