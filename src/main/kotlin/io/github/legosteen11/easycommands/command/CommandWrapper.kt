package io.github.legosteen11.easycommands.command

import io.github.legosteen11.easycommands.annotation.Command
import kotlin.reflect.KClass

data class CommandWrapper(val command: KClass<out ICommand>, val annotation: Command) {
    /**
     * Get the name of this command
     *
     * @return The name of the command
     */
    fun getName(): String = annotation.name.toLowerCase()
}