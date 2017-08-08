package io.github.legosteen11.easycommands.command

import kotlin.reflect.KClass

/**
 * Use this interface to create a "super" command. A supercommand is a command with subcommands
 */
abstract class SuperCommand : ICommand {
    private val commands = arrayListOf<KClass<SimpleCommand>>()

    /**
     * Register a sub command
     *
     * @param subCommand The sub command.
     */
    fun registerSubCommand(subCommand: KClass<SimpleCommand>) {
        commands.add(subCommand)
    }
}