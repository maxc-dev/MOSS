package dev.maxc.os.components.compiler;

import dev.maxc.App;
import dev.maxc.os.components.compiler.model.Variable;
import dev.maxc.os.components.instruction.Operand;
import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.process.Process;
import dev.maxc.os.components.process.ProcessAPI;
import dev.maxc.os.components.process.ProcessState;
import dev.maxc.os.components.scheduler.AdmissionScheduler;
import dev.maxc.os.io.exceptions.compiler.InUseVariableNameException;
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
     * Runs the compiler on a file.
     */
    public void compile(String fileName) {
        BufferedReader re = null;
        try {
            String file = App.class.getResource("processes/" + fileName + FILE_EXTENSION).getFile();
            re = new BufferedReader(new FileReader(file));
        } catch (IOException ex) {
            Logger.log(Status.ERROR, this, "Could not recognise file: [" + fileName + FILE_EXTENSION + "].");
            ex.printStackTrace();
        }

        Process mainProcess = processAPI.getNewProcess(ProcessAPI.NO_PARENT_PROCESS);
        CompilerTranslator utils = new CompilerTranslator(globalVariables, mmu, mainProcess.getProcessControlBlock());
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
                        String identifier = line.substring(0, declareIndex);
                        if (Variable.isValidIdentifier(identifier, globalVariables)) {
                            Variable var = new Variable(identifier, utils.getOperand(line.substring(declareIndex + 1)));
                            globalVariables.add(var);
                        } else {
                            throw new InUseVariableNameException(identifier);
                        }

                    } else if (line.startsWith("print(") && line.endsWith(")")) {
                        Operand output = utils.getOperand(line.substring(line.indexOf("(") + 1, line.indexOf(")")));
                        utils.outputOperand(output);
                    } else {
                        throw new UnparsableDataException(lineNumber, line);
                    }
                    lineNumber++;
                }
            }
            new Thread(() -> admissionScheduler.schedulePCB(mainProcess.getProcessControlBlock())).start();
        } catch (IOException | InvalidVariableNameException | UnparsableDataException | InUseVariableNameException ex) {
            ex.printStackTrace();
            Logger.log(Status.ERROR, this, "Cannot parse process file, terminating process.");
            mainProcess.getProcessControlBlock().setProcessState(ProcessState.TERMINATED);
        }
    }
}
