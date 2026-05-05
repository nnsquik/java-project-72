plugins {
    id("java")
    id("application")
    id("org.sonarqube") version "7.2.3.7755"
    id("com.gradleup.shadow") version "9.0.0"
    checkstyle
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val javalinVersion = "7.2.0"
val slf4jVersion = "2.0.17"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("io.javalin:javalin:$javalinVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
}

dependencyLocking {
    lockAllConfigurations()
}

tasks.test {
    useJUnitPlatform()
}

sonar {
    properties {
        property("sonar.projectKey", "nnsquik_java-project-72")
        property("sonar.organization", "nesquik")
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