# MOSS
An Operating System Simulator, demonstrating how an operating system manages scheduling and memory subsystems. This was course for my Opersating Systems module and I received 100% for this project.


## GUI

![GUI GIF Preview](https://imgur.com/a/6cnEmEs)

The MOSS GUI is inspired by generic J.A.R.V.I.S static concepts, except the animations bring it to life as the OS user interface.  

A lot of calculus goes is implemented in the GUI:
* Making the components move in circular motion
* Each blue star in the background follows a unique centered parametric curve
* The core in the middle uses calculus to create a "breathing effect" with light and size as exhibiting factors

## Processor

The user can specify the amount of cores in the system, as well as customise their clock speeds which has a direct relation to the speed at which process files can be executed.

## Memory

The user can customise the memory subsystem with the following attributes:
* Memory allocated using paging
* Memory allocated using segmentation
* Allowing virtual memory
* Segment and allocation sizes
* Amount of cache memory between the memory subsystem and the CPU

## Compiler/Interpreter Hybrid

Process files are executed using a custom compiler/interpreter hybrid with automatic type inference which can perform BIDMAS operations and make use of the in built print function.
