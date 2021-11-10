plugins {
    id("org.jetbrains.intellij") version "1.2.0"
    java
    kotlin("jvm") version "1.5.31"
}

group = "info.smart_tools.smartactors"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    // SmartActors utilities
    implementation("info.smart_tools.smartactors:feature-generator:0.1.2")
    implementation("info.smart_tools.smartactors:uploader:0.1.1")
    implementation("info.smart_tools.smartactors:builder:0.1.1")
    implementation("info.smart_tools.smartactors:ioc-viewer:0.1.0")

    implementation("com.intellij:forms_rt:7.0.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2021.1.3")
    pluginName.set("SmartActors IDEA Plugin")
}

tasks {
    patchPluginXml {
        changeNotes.set("""
            Add change notes here.<br>
            <em>most HTML tags may be used</em>        """.trimIndent())
    }
}
tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
