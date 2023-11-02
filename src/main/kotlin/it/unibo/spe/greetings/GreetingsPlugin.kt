package it.unibo.spe.greetings

import org.gradle.api.Plugin
import org.gradle.api.Project
import javax.inject.Inject

open class GreetingsPlugin @Inject constructor() : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create("greetings", GreetingsExtension::class.java)
        target.tasks.register("greet", GreetingTask::class.java, extension)
    }
}
