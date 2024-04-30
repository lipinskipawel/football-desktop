package com.github.lipinskipawel

import com.github.lipinskipawel.AppBuilderExtension.OsName
import com.github.lipinskipawel.AppBuilderExtension.OsName.LINUX
import org.gradle.api.Plugin
import org.gradle.api.Project

class AppBuilderPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withId("com.github.johnrengelman.shadow") {
            val extension = project.extensions.create("appBuilder", AppBuilderExtension::class.java)
            extension.osName.convention(LINUX)

            createOsSpecificAppBuilderTask(project, extension)
        }
    }

    private fun createOsSpecificAppBuilderTask(project: Project, extension: AppBuilderExtension) {
        val osName = extension.osName.get()
        val taskName = osName.name.lowercase()
        project.tasks.register(taskName, AppBuilder::class.java) { task ->
            task.dependsOn("shadowJar")
            task.jdkDirectoryToolchain.set(extension.jdkDirectory)
            val (script, content) = osSpecificConfig(osName)
            task.runnableScriptName = script
            task.contentOfRunnableScript = content
        }
    }

    private data class OsSpecificConfig(val scriptName: String, val scriptContent: String)

    private fun osSpecificConfig(osName: OsName): OsSpecificConfig {
        return if (osName == LINUX) {
            linuxScript()
        } else {
            windowsScript()
        }
    }

    private fun linuxScript(): OsSpecificConfig {
        return OsSpecificConfig(
            "play.sh",
            """
            #!/usr/bin/env bash

            ./jdk/bin/java -jar football-gui-game-1.0.0-all.jar
        """.trimIndent()
        )
    }

    private fun windowsScript(): OsSpecificConfig {
        return OsSpecificConfig(
            "play.bat",
            """
            start jdk/bin/javaw -jar --enable-preview football-gui-game-1.0.0-all.jar
        """.trimIndent()
        )
    }
}
