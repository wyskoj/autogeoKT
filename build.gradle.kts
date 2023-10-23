plugins {
    kotlin("jvm") version "1.9.0"
}

group = "org.wysko"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Multik
    implementation("org.jetbrains.kotlinx:multik-core:0.2.2")
    implementation("org.jetbrains.kotlinx:multik-default:0.2.2")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}