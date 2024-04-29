pluginManagement {
    includeBuild("plugin")
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "football-desktop"

include("app")
