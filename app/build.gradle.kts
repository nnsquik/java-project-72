plugins {
    id("java")
    id("application")
    id("io.freefair.lombok") version "9.5.0"
    id("org.sonarqube") version "7.2.3.7755"
    id("com.gradleup.shadow") version "9.0.0"
    checkstyle
    jacoco
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.javalin:javalin-testtools:6.6.0")
    testImplementation("org.assertj:assertj-core:3.27.3")

    implementation("org.slf4j:slf4j-simple:2.0.17")

    implementation("io.javalin:javalin:6.6.0")

    implementation("io.javalin:javalin-rendering:6.6.0")
    implementation("gg.jte:jte:3.2.4")

    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.h2database:h2:2.3.232")
    implementation("org.postgresql:postgresql:42.7.4")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    environment("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;")
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

sonar {
    properties {
        property("sonar.projectKey", "nnsquik_java-project-72")
        property("sonar.organization", "nesquik")
        property("sonar.coverage.jacoco.xmlReportPaths",
            "${layout.buildDirectory.get()}/reports/jacoco/test/jacocoTestReport.xml")
    }
}

checkstyle {
    configFile = file("config/checkstyle/checkstyle.xml")
    configProperties = mapOf(
        "org.checkstyle.sun.suppressionfilter.config" to "${rootDir}/config/checkstyle/checkstyle-suppressions.xml"
    )
}

application {
    mainClass.set("hexlet.code.App")
}

tasks.shadowJar {
    archiveClassifier.set("")
    archiveVersion.set("")
    mergeServiceFiles()
}