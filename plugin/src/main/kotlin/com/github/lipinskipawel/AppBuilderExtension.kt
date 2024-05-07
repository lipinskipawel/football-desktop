package com.github.lipinskipawel

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

interface AppBuilderExtension {
    val jdkDirectory: DirectoryProperty
    val osName: Property<OsName>

    enum class OsName {
        LINUX,
        WINDOWS;

        companion object {
            fun detectOsName(): OsName {
                return when (System.getProperty("os.name").lowercase()) {
                    "linux" -> LINUX
                    else -> WINDOWS
                }
            }
        }
    }
}
