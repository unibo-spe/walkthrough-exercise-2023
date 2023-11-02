package it.unibo.spe.greetings

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class GreetingTask @Inject constructor(private val extension: GreetingsExtension)  : DefaultTask() {

    @TaskAction
    fun greetPeople() {
        println(extension.greetingGenerator() + " people!")
    }
}
