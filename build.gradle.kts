plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("org.jetbrains.compose") version "1.5.3"
}

group = "org.wysko"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Multik
    implementation("org.jetbrains.kotlinx:multik-core:0.2.2")
    implementation("org.jetbrains.kotlinx:multik-default:0.2.2")

    // Compose Multiplatform
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)

    // Compose Voyager
    val voyagerVersion = "1.0.0-rc08"
    implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
    implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")

    // Kotlin
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))

    // KotlinX Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // jMonkeyEngine
    implementation("org.jmonkeyengine:jme3-core:3.5.2-stable")
    implementation("org.jmonkeyengine:jme3-lwjgl3:3.5.2-stable")
    implementation("org.jmonkeyengine:jme3-desktop:3.5.2-stable")
    implementation("org.jmonkeyengine:jme3-plugins:3.5.2-stable")
    implementation("org.jmonkeyengine:jme3-effects:3.5.2-stable")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
