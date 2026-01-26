plugins {
    id("com.github.ben-manes.versions") version "0.53.0"
    id("org.sonarqube") version "7.2.2.6593"
    application
    checkstyle
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass = "hexlet.code.App"
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

sonar {
    properties {
        property("sonar.projectKey", "MixsonV_java-project-72")
        property("sonar.organization", "mixsonv")
    }
}
