import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
    application
}
repositories {
    mavenCentral()
}
dependencies {
    implementation(project(":tinyK-ast"))
    implementation(project(":tinyK-concept"))

    testImplementation(kotlin("test-junit"))
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}