plugins {
    kotlin("jvm") version "1.9.25"
}

group = "com.ggyool"
version = "0.0.1"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}