plugins {
    application
    java
    kotlin("jvm") version "2.4.21"
    id("com.gradleup.shadow") version "9.6.1"
    id("com.github.lipinskipawel.football-desktop.app-builder") version ("0.1.0")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(platform("com.github.lipinskipawel:football-platform:1.+"))

    implementation("com.google.code.gson:gson:2.8.6")
    implementation("io.github.lipinskipawel:football-game-engine:7.0.2")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha1")
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    implementation("com.google.code.gson:gson:2.8.9")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.assertj:assertj-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

group = "com.github.lipinskipawel"
version = "1.0.0"
description = "football-desktop"

kotlin {
    jvmToolchain(25)
}

application {
    mainClass.set("com.github.lipinskipawel.AppKt")
}

tasks.test {
    useJUnitPlatform()
}
