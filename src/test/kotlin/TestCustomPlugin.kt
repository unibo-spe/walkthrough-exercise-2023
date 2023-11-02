import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.contain
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File
import java.nio.file.Path
import java.nio.file.attribute.FileAttribute
import kotlin.io.path.absolutePathString
import kotlin.io.path.createFile
import kotlin.io.path.createTempDirectory

class TestCustomPlugin : StringSpec({

    "We should be able to launch Gradle from Gradle" {
        withinATemporaryDirectory {
            File(this, "settings.gradle.kts").createNewFile()
            val result = GradleRunner.create()
                .withProjectDir(this)
                .withArguments("tasks")
                .run()
            println(result.output)
            result.output shouldNotContain "BUILD FAILED"
            result.tasks.map { it.outcome }.forEach { it shouldBe TaskOutcome.SUCCESS }
        }
    }

    "The plugin extension should be openable" {
        withinATemporaryDirectory {
            val buildFile = File(this, "build.gradle.kts")
            buildFile.createNewFile()
            buildFile.writeText(
                """
                plugins {
                    id("it.unibo.spe.supergreetings")
                }

                greetings {
                    greetWith { "hello" }
                }
                """.trimIndent()
            )
            val result = GradleRunner.create()
                .withProjectDir(this)
                .withPluginClasspath(File("build/classpath").readLines().map { File(it) })
                .withArguments("greet")
                .run()
            println(result.output)
            result.output shouldContain "BUILD SUCCESS"
            result.tasks.map { it.outcome }.forEach { it shouldBe TaskOutcome.SUCCESS }
            result.output shouldContain "hello people"
        }
    }

}) {
    companion object {
        fun withinATemporaryDirectory(todos: File.() -> Unit): File = createTempDirectory("spe-tests")
            .also { println(it.absolutePathString()) }
            .run { toFile() }
            .apply(todos)
    }
}
