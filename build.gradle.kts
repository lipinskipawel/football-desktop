plugins {
    application
    java
    kotlin("jvm") version "1.5.0"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.github.lipinskipawel:game-engine:4.0.0")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha1")
    implementation("org.java-websocket:Java-WebSocket:1.5.2")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testImplementation("org.assertj:assertj-core:3.12.2")
}

group = "com.github.lipinskipawel"
version = "1.0.0"
description = "football-desktop"

java {
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_15
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        kotlinOptions.jvmTarget = "15"
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

application {
    mainClass.set("com.github.lipinskipawel.App")
}

tasks.register<AppBuilder>("linux") {
    dependsOn("shadowJar")
    runnableScriptName = "play.sh"
    contentOfRunnableScript = """
        #!/usr/bin/env bash
        
        ./jdk/bin/java -jar football-gui-game-1.0.0-all.jar
    """.trimIndent()
}

tasks.register<AppBuilder>("windows") {
    dependsOn("shadowJar")
    runnableScriptName = "play.bat"
    contentOfRunnableScript = """
        start jdk/bin/javaw -jar --enable-preview football-gui-game-1.0.0-all.jar
    """.trimIndent()
}

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
