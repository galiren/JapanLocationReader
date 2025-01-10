import com.diffplug.spotless.LineEnding

plugins {
    kotlin("jvm") version "2.0.21"
    id("com.gradleup.shadow") version "9.0.0-beta4"
    id("com.diffplug.spotless") version "7.0.1"
}

group = "me.galiren"
version = "0.0.1"

spotless {
    kotlin {
        target("**/*.kt")
        ktlint().editorConfigOverride(
            mapOf(
                "no-unused-imports" to true,
            ),
        )
        trimTrailingWhitespace()
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint().editorConfigOverride(
            mapOf(
                "no-unused-imports" to true,
            ),
        )
        trimTrailingWhitespace()
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }
}
repositories {
    mavenCentral()
}

tasks.named("jar", Jar::class) {
    manifest {
        attributes["Main-Class"] = "me.galiren.reader.MainKt"
    }
    enabled = false
}

tasks.named("shadowJar", com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
    archiveBaseName = "reader"
}

tasks.named("build") {
    dependsOn("spotlessApply")
    dependsOn("shadowJar")
}

dependencies {
    implementation("org.apache.pdfbox:pdfbox:3.0.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
