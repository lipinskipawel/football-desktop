plugins {
    application
    java
    kotlin("jvm") version "1.6.21"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.github.lipinskipawel.football-desktop.app-builder") version ("0.1.0")
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
    implementation("com.github.lipinskipawel:game-engine:5.0.0")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha1")
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    implementation("com.google.code.gson:gson:2.8.9")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testImplementation("org.assertj:assertj-core:3.12.2")
}

group = "com.github.lipinskipawel"
version = "1.0.0"
description = "football-desktop"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        kotlinOptions.jvmTarget = "17"
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
    mainClass.set("com.github.lipinskipawel.AppKt")
}

tasks.test {
    useJUnitPlatform()
}

val launcher = javaToolchains.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(17))
}

appBuilder {
    jdkDirectory.set(launcher.map { it.metadata.installationPath })
}
