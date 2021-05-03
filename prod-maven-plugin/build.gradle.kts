plugins {
    java
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("commons-io:commons-io:2.6")
    testImplementation("org.apache.maven:maven-compat:3.6.1")
    testImplementation("junit:junit:4.12")
    testImplementation("org.apache.maven.plugin-testing:maven-plugin-testing-harness:3.3.0")
    compileOnly("org.apache.maven:maven-plugin-api:3.6.1")
    compileOnly("org.apache.maven:maven-core:3.6.1")
    compileOnly("org.apache.maven:maven-artifact:3.6.1")
    compileOnly("org.apache.maven.plugin-tools:maven-plugin-annotations:3.6.0")
}

group = "com.github.lipinskipawel"
version = "1.0.0-SNAPSHOT"
description = "prod-maven-plugin"
java.sourceCompatibility = JavaVersion.VERSION_11

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
