plugins {
    id("com.github.ben-manes.versions") version "0.53.0"
    id("org.sonarqube") version "7.2.2.6593"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.lombok") version "8.13.1"
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
    implementation("org.apache.commons:commons-text:1.9")
    implementation("com.h2database:h2:2.3.232")
    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("gg.jte:jte:3.2.2")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("io.javalin:javalin:6.7.0")
    implementation("io.javalin:javalin-bundle:6.7.0")
    implementation("io.javalin:javalin-rendering:6.7.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")

    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    shadowJar {
        archiveBaseName.set("app")
        archiveClassifier.set("")
        archiveVersion.set("")
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
}

sonar {
    properties {
        property("sonar.projectKey", "MixsonV_java-project-72")
        property("sonar.organization", "mixsonv")
    }
}
