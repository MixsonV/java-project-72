plugins {
    id("com.github.ben-manes.versions") version "0.53.0"
    id("org.sonarqube") version "7.2.2.6593"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.lombok") version "8.13.1"
    id("gg.jte.gradle") version "3.2.2"
    application
    checkstyle
    jacoco
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
    implementation("org.jsoup:jsoup:1.16.1")
    implementation(platform("com.konghq:unirest-java-bom:4.5.1"))
    implementation("com.konghq:unirest-java-core")

    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

jte {
    sourceDirectory.set(file("src/main/resources/templates").toPath())
    targetDirectory.set(file("build/generated/jte").toPath())
    contentType.set(gg.jte.ContentType.Html)
    binaryStaticContent.set(false)
}

sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.dir("generated/jte"))
        }
    }
}

tasks {
    compileJava {
        dependsOn("generateJte")
    }

    shadowJar {
        archiveBaseName.set("app")
        archiveClassifier.set("")
        archiveVersion.set("")
        mergeServiceFiles()
        dependsOn("generateJte")
    }

    build {
        dependsOn(shadowJar)
    }
}

tasks.withType<Checkstyle> {
    exclude("**/generated/**")
    exclude("**/build/generated/**")
}

tasks.checkstyleMain {
    mustRunAfter(tasks.precompileJte)
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)

    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    classDirectories.setFrom(
        fileTree("build/classes/java/main") {
            exclude(
                "**/dto/**",
                "**/model/**",
                "**/util/**",
                "**/*Test.class",
                "**/*Tests.class"
            )
        }
    )
}

sonar {
    properties {
        property("sonar.projectKey", "MixsonV_java-project-72")
        property("sonar.organization", "mixsonv")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sources", "src/main/java")
        property("sonar.tests", "src/test/java")
        property("sonar.java.binaries", "build/classes/java/main")
        property("sonar.java.test.binaries", "build/classes/java/test")
        property("sonar.junit.reportPaths", "build/test-results/test")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.coverage.exclusions", "**/*Test.java,**/*Tests.java,**/dto/**,**/model/**,**/util/**")
    }
}

tasks.named("sonarqube") {
    dependsOn(tasks.jacocoTestReport)
}
