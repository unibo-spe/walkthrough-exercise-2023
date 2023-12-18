import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    `java-gradle-plugin`
    alias(libs.plugins.detekt)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.plugin.publish)
    kotlin("jvm") version "1.9.10"
    signing
    alias(libs.plugins.publish.on.central)
    alias(libs.plugins.tasktree)
}

group = "org.danilopianini"

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
        create("") {
            id = "io.github.danysk.spe.supergreetings"
            implementationClass = "it.unibo.spe.greetings.GreetingsPlugin"
            displayName = "UniBo Software Process Engineering test plugin"
            description = "made with love just for the sake of learning how to do it"
            website.set("https://unibo-spe.github.io")
            vcsUrl.set("git:")
            tags.set(listOf("unibo", "spe", "greetings"))
        }
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

publishing {
    publications {
        withType<MavenPublication> {
            pom {
                developers {
                    developer {
                        name.set("Danilo Pianini")
                        email.set("danilo.pianini@gmail.com")
                        url.set("http://www.danilopianini.org/")
                    }
                }
            }
        }
    }
}

if (System.getenv("CI") == true.toString()) {
    signing {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
}

/*
plugins {
    id("it.unibo.supergreetings")
}

greetings {
//    greetWith { "ciao" }
}
*/
