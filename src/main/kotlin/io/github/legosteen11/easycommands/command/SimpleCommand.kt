package io.github.legosteen11.easycommands.command

import io.github.legosteen11.easycommands.CommandHandler
import io.github.legosteen11.easycommands.user.ICommandSender

abstract class SimpleCommand : ICommand {
    override lateinit var commandHandler: CommandHandler

    /**
     * Run code in a block that catches and handles exceptions.
     *
     * @param commandSender The commandsender
     * @param block The code block
     */
    fun runBlock(commandSender: ICommandSender, block: () -> Unit) {
        commandHandler.runBlock(commandSender, block)
    }
}