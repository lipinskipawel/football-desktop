package com.github.lipinskipawel

import java.lang.management.ManagementFactory
import java.nio.file.Files
import java.nio.file.Path

class ThreadDumper {

    fun dumpThreads(filename: String) {
        val output = StringBuilder(System.lineSeparator())
        val threadMXBean = ManagementFactory.getThreadMXBean()
        threadMXBean
                .dumpAllThreads(true, false, 3)
                .map { it.toString() }
                .run { output.append(this) }
        Files.writeString(Path.of(filename), output)
    }
}