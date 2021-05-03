plugins {
    application
    java
    kotlin("jvm") version "1.4.0"
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation(project(":prod-maven-plugin"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.github.lipinskipawel:game-engine:4.0.0")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testImplementation("org.assertj:assertj-core:3.12.2")
}

group = "com.github.lipinskipawel"
version = "1.0.0"
description = "football-gui-game"
java.sourceCompatibility = JavaVersion.VERSION_15

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
