package com.github.lipinskipawel

import org.gradle.api.Plugin
import org.gradle.api.Project

class AppBuilderPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("something")
    }
}