import com.github.gradle.node.npm.task.NpmTask

plugins {
    java
    id("io.quarkus")
    id("com.github.node-gradle.node") version "7.1.0"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val kotlinxSerializationVersion: String by project
val kotlinxDatetimeVersion: String by project
val kotestVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation(enforcedPlatform("org.dizitart:nitrite-bom:4.3.0"))

    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-rest-kotlin-serialization")
    implementation("io.quarkus:quarkus-undertow")

    // Kotlin and Serialization
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")

    // Nitrite Database
    implementation("org.dizitart:nitrite")
    implementation("org.dizitart:nitrite-mvstore-adapter")
    implementation("org.dizitart:potassium-nitrite")

    // CSV Parsing
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.10.0")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
}

group = "com.notwanoo"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

// Node.js and npm configuration
node {
    version.set("20.18.0") // Specify the Node.js version
    npmVersion.set("9.5.0") // Specify the npm version
    download.set(true) // Automatically download Node.js
    workDir.set(layout.buildDirectory.dir("nodejs"))
    npmWorkDir.set(layout.buildDirectory.dir("npm"))
}

val frontendDir = file("../../frontend/my-own-todo-app")

// Task to install frontend dependencies
val frontendNpmInstall by tasks.registering(NpmTask::class) {
    description = "Installs frontend dependencies using npm."
    group = "frontend"

    workingDir.set(frontendDir)
    args.set(listOf("install"))
}

// Task to build the frontend
val frontendNpmBuild by tasks.registering(NpmTask::class) {
    description = "Builds the frontend application using npm."
    group = "frontend"

    dependsOn(frontendNpmInstall)
    workingDir.set(frontendDir)
    args.set(listOf("run", "build"))
}

// Task to clean the frontend build
val frontendNpmClean by tasks.registering(NpmTask::class) {
    description = "Cleans the frontend build artifacts."
    group = "frontend"

    workingDir.set(frontendDir)
    args.set(listOf("run", "clean"))
}

val backendBuildResourcesDir: File = layout.buildDirectory.dir("resources/main/META-INF/resources").get().asFile

// Task to copy frontend build artifacts to backend build resources
val copyFrontendBuild by tasks.registering(Copy::class) {
    description = "Copies frontend build artifacts to backend build resources META-INF/resources."
    group = "build"

    dependsOn(frontendNpmBuild)

    from(frontendDir.resolve("build")) // Adjust if your frontend build output directory is different
    into(backendBuildResourcesDir)

    include("**/*") // Adjust patterns as needed

    // Clean the destination directory before copying to avoid stale files
    doFirst {
        if (backendBuildResourcesDir.exists()) {
            backendBuildResourcesDir.deleteRecursively()
        }
        backendBuildResourcesDir.mkdirs()
    }
}

// Make the processResources task depend on copying frontend build artifacts
tasks.named<ProcessResources>("processResources") {
    dependsOn(copyFrontendBuild)
}