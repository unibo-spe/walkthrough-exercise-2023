import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    `java-gradle-plugin`
    id("io.gitlab.arturbosch.detekt").version("1.23.1")
    kotlin("jvm") version "1.9.10"
}

version = "0.1.0"

repositories {
    mavenCentral()
    maven {
        url = URI("https://idontexist.com")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        allWarningsAsErrors = true
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

gradlePlugin {
    plugins {
        create("it.unibo.spe.supergreetings")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    testImplementation(gradleTestKit()) // Test implementation: available for testing compile and runtime
    testImplementation(libs.bundles.kotest)
}

val generatePluginClasspath by tasks.registering {
    dependsOn(tasks.compileKotlin)
    doLast {
        val deps = configurations.runtimeClasspath.get().resolve().joinToString(separator = "\n") { it.absolutePath } +
            "\n${project.layout.buildDirectory.asFile.get().absolutePath}/classes/kotlin/main" +
            "\n${project.layout.buildDirectory.asFile.get().absolutePath}/resources/main"
        val classpathFile = File(project.layout.buildDirectory.asFile.get(), "classpath")
        classpathFile.writeText(deps)
    }
}

(12..20).forEach { version ->
    val newTestTask = tasks.register<Test>("testWithJvm$version") {
        javaLauncher.set(javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(version)
        })
    }
    tasks.check.configure {
        dependsOn(newTestTask)
    }
}

tasks.withType<Test>().configureEach {
    dependsOn(generatePluginClasspath)
    useJUnitPlatform()
    testLogging.showStandardStreams = true
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(11)
        // vendor.set(JvmVendorSpec.MICROSOFT)
        // implementation.set(JvmImplementation.J9)
    })
}


/*
plugins {
    id("it.unibo.supergreetings")
}

greetings {
//    greetWith { "ciao" }
}
*/
