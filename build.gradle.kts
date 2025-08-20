plugins {
    kotlin("jvm") version "2.2.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-build-tools-api:2.3.255-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-build-tools-impl:2.3.255-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.3.255-SNAPSHOT")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("org.example.MainKt")
}