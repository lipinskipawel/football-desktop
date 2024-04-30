package com.github.lipinskipawel

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class AppBuilder : DefaultTask() {
    private val installer = project.layout.buildDirectory.get().dir("bundle").asFile

    @get:Input
    abstract val jdkDirectoryToolchain: DirectoryProperty

    @Input
    var runnableScriptName = "play"

    @Input
    var contentOfRunnableScript = ""

    @TaskAction
    fun build() {
        logger.info("$name tasks started")

        createInstallerDirectory()
        createExecutableScript()
        copyJarFileToInstallerDirectory()
        moveJdkToInstallerDirectory()

        logger.info("$name tasks stopped")
    }

    private fun createInstallerDirectory() {
        if (!installer.exists()) {
            installer.mkdirs()
        }
    }

    private fun createExecutableScript() {
        logger.info("Creating $runnableScriptName file")
        val executable = installer.resolve(runnableScriptName)
        executable.createNewFile()
        executable.writeText(contentOfRunnableScript)
        executable.setExecutable(true)
    }

    private fun copyJarFileToInstallerDirectory() {
        project.copy { it ->
            val shadowJar = installer
                .parentFile
                .resolve("libs")
                .listFiles()
                ?.first { it.name.endsWith(".jar") }
            it.from(shadowJar)
            it.into(installer)
        }
    }

    private fun moveJdkToInstallerDirectory() {
        val resolve = installer.resolve("jdk")
        logger.info("Copy jdk from toolchain to installer: $resolve")
        project.copy {
            it.from(jdkDirectoryToolchain)
            it.into(resolve)
        }
        logger.info("Setting executable to java file")
        val javaFilename: File = if (name == "linux") {
            resolve.resolve("bin").resolve("java")
        } else {
            resolve.resolve("bin").resolve("javaw")
        }
        javaFilename.setExecutable(true)
    }
}
