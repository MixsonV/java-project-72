plugins {
    id("com.github.ben-manes.versions") version "0.53.0"
    id("org.sonarqube") version "7.2.2.6593"
    id("com.github.johnrengelman.shadow") version "8.1.1"
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

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "hexlet.code.App"
        )
    }
}

val junitBomVersion = "5.9.1"
val commonsIoVersion = "2.14.0"
val commonsLangVersion = "3.17.0"
val commonsTextVersion = "1.9"
val commonsCodecVersion = "1.18.0"

val javalinVersion = "6.6.0"
val jteVersion = "3.1.9"
val slf4jVersion = "2.0.17"

val jacksonVersion = "2.16.1"
val guavaVersion = "31.1-jre"

val h2databaseVersion = "2.2.220"
val postgresqlVersion = "42.3.3"
val hikariCpVersion = "5.0.1"
val jakartaServletVersion = "6.1.0"
val jakartaServletJspVersion = "3.0.2"
val jakartaServletJspJstlVersion = "3.0.1"

val unirestVersion = "3.13.6"
val jsoupVersion = "1.16.1"
val lombokVersion = "1.18.38"

val assertjCoreVersion = "3.27.3"
val mockWebServerVersion = "4.9.3"
val mockitoCoreVersion = "4.2.0"


dependencies {
    implementation("org.apache.commons:commons-lang3:$commonsLangVersion")
    implementation ("org.apache.commons:commons-text:$commonsTextVersion")
    implementation ("commons-codec:commons-codec:$commonsCodecVersion")
    implementation("commons-io:commons-io:$commonsIoVersion")

    implementation("io.javalin:javalin:$javalinVersion")
    implementation("io.javalin:javalin-bundle:$javalinVersion")
    implementation("io.javalin:javalin-rendering:$javalinVersion")
    implementation("gg.jte:jte:$jteVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation ("com.google.guava:guava:$guavaVersion")

    runtimeOnly("com.h2database:h2:$h2databaseVersion")
    implementation ("org.postgresql:postgresql:$postgresqlVersion")
    implementation("com.zaxxer:HikariCP:$hikariCpVersion")

    implementation("jakarta.servlet:jakarta.servlet-api:$jakartaServletVersion")
    implementation("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:$jakartaServletJspVersion")
    implementation("org.glassfish.web:jakarta.servlet.jsp.jstl:$jakartaServletJspJstlVersion")

    implementation ("com.konghq:unirest-java:$unirestVersion")
    implementation ("org.jsoup:jsoup:$jsoupVersion")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testImplementation("org.assertj:assertj-core:$assertjCoreVersion")
    testImplementation(platform("org.junit:junit-bom:$junitBomVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation ("com.squareup.okhttp3:mockwebserver:$mockWebServerVersion")
    testImplementation ("org.mockito:mockito-core:$mockitoCoreVersion")
    testImplementation ("org.mockito:mockito-inline:$mockitoCoreVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

jte {
    sourceDirectory.set(file("src/main/resources/templates").toPath())
    targetDirectory.set(file("build/generated/jte").toPath())
    contentType.set(gg.jte.ContentType.Html)
    binaryStaticContent.set(false)
}

checkstyle {
    toolVersion = "9.0"
    configDirectory.set(file("config/checkstyle"))
}

tasks.withType<Checkstyle>().configureEach {
    classpath = files("${project.rootDir}/src/test/java")
}

val myCheckstyleTest by tasks.registering(Checkstyle::class) {
    source("src/test/java")
    classpath = files()
    configFile = file("${project.rootDir}/config/checkstyle/checkstyle.xml")
    include("**/*.java")
    exclude("**/generated/**")
}

tasks.named("check") {
    dependsOn(myCheckstyleTest)
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
}

tasks.withType<Test> {
    finalizedBy(tasks.jacocoTestReport)
}

sonar {
    properties {
        property("sonar.projectKey", "MixsonV_java-project-72")
        property("sonar.organization", "mixsonv")
        property("sonar.host.url", "https://sonarcloud.io")
        property ("sonar.login", "${System.getenv("SONAR_TOKEN")}")
        property("sonar.java.binaries", "build/classes/java/main")
        property ("sonar.java.coveragePlugin", "jacoco")
        property ("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}
