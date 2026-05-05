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
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("com.h2database:h2:2.4.240")
    implementation("org.postgresql:postgresql:42.7.11")
    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")
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