package dev.maxc.os.components.compiler;

import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.process.ProcessAPI;
import dev.maxc.os.components.scheduler.AdmissionScheduler;
import dev.maxc.os.io.log.Logger;

/**
 * @author Max Carter
 * @since 13/05/2020
 */
public class CompilerAPI {
    private final AdmissionScheduler admissionScheduler;
    private final MemoryManagementUnit mmu;
    private final ProcessAPI processAPI;

    public CompilerAPI(AdmissionScheduler admissionScheduler, MemoryManagementUnit mmu, ProcessAPI processAPI) {
        this.admissionScheduler = admissionScheduler;
        this.mmu = mmu;
        this.processAPI = processAPI;
    }

    public void compile(String text) {
        Logger.log(this, "CompilerAPI has received a request to compile [" + text + ".moss]");
        new Thread(() -> new Compiler(admissionScheduler, mmu, processAPI).compile(text)).start();
    }
}
