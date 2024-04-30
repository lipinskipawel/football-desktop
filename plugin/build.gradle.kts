plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "1.6.21"
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("app-builder") {
            id = "com.github.lipinskipawel.football-desktop.app-builder"
            implementationClass = "com.github.lipinskipawel.AppBuilderPlugin"
        }
    }
    version = "0.1.0"
    group = "com.github.lipinskipawel.football-desktop.app-builder"
}
