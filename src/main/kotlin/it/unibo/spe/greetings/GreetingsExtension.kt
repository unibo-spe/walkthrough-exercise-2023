package it.unibo.spe.greetings

open class GreetingsExtension {

    internal var greetingGenerator: () -> String = { "ciao" }

    fun greetWith(mygreeting: () -> String) {
        greetingGenerator = mygreeting
    }


}
