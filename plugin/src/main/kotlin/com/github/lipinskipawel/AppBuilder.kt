package com.github.lipinskipawel

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class AppBuilder : DefaultTask() {
    @Input
    var installerDirectory: String = project.projectDir.resolve("bundle").absolutePath

    @Input
    var runnableScriptName = "play"

    @Input
    var contentOfRunnableScript = ""

    @Input
    var jdkDirectory = project.rootDir.resolve("jdks").absolutePath

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
        logger.info("Creating installer directory: $installerDirectory")
        project.file(installerDirectory).mkdir()
    }

    private fun createExecutableScript() {
        logger.info("Creating $runnableScriptName file")
        val executable = project.file(installerDirectory).resolve(runnableScriptName)
        executable.createNewFile()
        executable.writeText(contentOfRunnableScript)
        executable.setExecutable(true)
    }

    private fun copyJarFileToInstallerDirectory() {
        val jarNameOfCompiledSourceCode = project.projectDir
            .resolve("build")
            .resolve("libs")
            .listFiles().first { !it.endsWith(".jar") }
        jarNameOfCompiledSourceCode
            .copyTo(project.file(installerDirectory).resolve(jarNameOfCompiledSourceCode.name), overwrite = true)
    }

    private fun moveJdkToInstallerDirectory() {
        logger.info("Copying jdk files to $installerDirectory")
        project.file(jdkDirectory)
            .listFiles()
            .first()
            .copyRecursively(project.file(installerDirectory).resolve("jdk"), overwrite = true)
        logger.info("Setting executable to java file")
        if (this.name == "linux") {
            makeExecutable("java")
        } else {
            makeExecutable("javaw")
        }
    }

    private fun makeExecutable(name: String) {
        try {
            project.file(installerDirectory)
                .resolve("jdk")
                .resolve("bin")
                .resolve(name)
                .setExecutable(true)
        } catch (ee: Exception) {
            logger.error(ee.toString())
        }
    }
}
