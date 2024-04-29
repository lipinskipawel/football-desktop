package com.github.lipinskipawel

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class AppBuilder : DefaultTask() {
    private val installer = project.layout.buildDirectory.get().dir("bundle").asFile

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
        logger.info("Copying jdk files to $installer")
        project.file(jdkDirectory)
            .listFiles()
            .first()
            .copyRecursively(project.file(installer).resolve("jdk"), overwrite = true)
        logger.info("Setting executable to java file")
        if (this.name == "linux") {
            makeExecutable("java")
        } else {
            makeExecutable("javaw")
        }
    }

    private fun makeExecutable(name: String) {
        try {
            project.file(installer)
                .resolve("jdk")
                .resolve("bin")
                .resolve(name)
                .setExecutable(true)
        } catch (ee: Exception) {
            logger.error(ee.toString())
        }
    }
}
