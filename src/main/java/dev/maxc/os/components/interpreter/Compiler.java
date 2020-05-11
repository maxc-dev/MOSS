package dev.maxc.os.components.interpreter;

import dev.maxc.App;
import dev.maxc.os.components.instruction.Operand;
import dev.maxc.os.components.interpreter.model.CompilerTranslator;
import dev.maxc.os.components.interpreter.model.Variable;
import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.process.Process;
import dev.maxc.os.components.process.ProcessAPI;
import dev.maxc.os.components.scheduler.AdmissionScheduler;
import dev.maxc.os.io.exceptions.compiler.InvalidVariableNameException;
import dev.maxc.os.io.exceptions.compiler.UnparsableDataException;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Max Carter
 * @since 03/05/2020
 */
public class Compiler {
    private static final String COMMENT_CHARACTER = "#";
    private static final String FILE_EXTENSION = ".moss";

    private final AdmissionScheduler admissionScheduler;
    private final ArrayList<Variable> globalVariables = new ArrayList<>();
    private final MemoryManagementUnit mmu;
    private final ProcessAPI processAPI;


    public Compiler(AdmissionScheduler admissionScheduler, MemoryManagementUnit mmu, ProcessAPI processAPI) {
        this.admissionScheduler = admissionScheduler;
        this.mmu = mmu;
        this.processAPI = processAPI;
    }

    /**
     * Runs the interpreter on a file.
     */
    public void compile(String fileName) {
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
        CompilerTranslator utils = new CompilerTranslator(globalVariables, mmu, mainProcess.getProcessControlBlock());
        Logger.log(this, "Successfully gathered process file [" + fileName + FILE_EXTENSION + "] interpreting...");
        //compile file
        try {
            int lineNumber = 1;
            String line;
            while ((line = Objects.requireNonNull(re).readLine()) != null) {
                line = line.replaceAll(" ", "");
                if (!line.startsWith(COMMENT_CHARACTER) && !line.isBlank()) {

                    //if variable declaration is inbound
                    if (line.contains(CompilerTranslator.DECLARE)) {
                        int declareIndex = line.indexOf(CompilerTranslator.DECLARE);
                        Variable var = new Variable(line.substring(0, declareIndex), utils.getOperand(line.substring(declareIndex+1)));
                        globalVariables.add(var);
                        Logger.log(this, "New variable initialized [" + var.toString() + "]");

                    } else if (line.startsWith("print(") && line.endsWith(")")) {
                        Operand output = utils.getOperand(line.substring(line.indexOf("(")+1, line.indexOf(")")));
                        utils.outputOperand(output);
                    } else {
                        throw new UnparsableDataException(lineNumber, line);
                    }
                    lineNumber++;
                }
            }
            admissionScheduler.schedulePCB(mainProcess.getProcessControlBlock());
        } catch (IOException | InvalidVariableNameException | UnparsableDataException ex) {
            ex.printStackTrace();
        }
    }
}
