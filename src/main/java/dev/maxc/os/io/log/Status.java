package dev.maxc.os.io.log;

/**
 * @author Max Carter
 * @since 26/04/2020
 */
public enum Status {
    PFF,        //process file output
    OUT,         //process file outpit
    INFO,       //basic sys info
    DEBUG,      //performing a debugger test
    WARN,       //something is not right
    ERROR,      //something is wrong
    CRIT        //something is broken (critical error) - usually as a result of an exception being thrown
}
