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
    implementation(project(":tinyK-frontend-cheesy"))

    implementation("com.github.ajalt.clikt:clikt:3.0.0-rc")

    testImplementation(kotlin("test-junit"))
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
application {
    mainClassName = "MainKt"
}