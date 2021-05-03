plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

group = "com.github.lipinskipawel"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_15

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
