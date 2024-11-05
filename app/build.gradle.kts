plugins {
    application
    java
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.github.lipinskipawel.football-desktop.app-builder") version ("0.1.0")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(platform("com.github.lipinskipawel:football-platform:1.+"))

    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.github.lipinskipawel:game-engine:5.0.0")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha1")
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    implementation("com.google.code.gson:gson:2.8.9")

    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.assertj:assertj-core")
}

group = "com.github.lipinskipawel"
version = "1.0.0"
description = "football-desktop"

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.github.lipinskipawel.AppKt")
}

tasks.test {
    useJUnitPlatform()
}
