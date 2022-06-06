plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.intellij") version "1.4.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.tidalcycles"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.h0tk3y.betterParse:better-parse:0.4.3")
    implementation("com.illposed.osc:javaosc-core:0.8")
    implementation(kotlin("script-runtime"))

    testImplementation("org.assertj:assertj-core:3.22.0")
    testImplementation("org.awaitility:awaitility:4.1.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks.test {
    useJUnitPlatform()
}

// TODO: will this be useful anymore?
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("teapot.jar")
}

apply(plugin = "org.jetbrains.intellij")

intellij {
    version.set("2021.2.3")

    plugins.set(listOf("org.jetbrains.kotlin:212-1.6.0-release-799-IJ5457.46"))
}