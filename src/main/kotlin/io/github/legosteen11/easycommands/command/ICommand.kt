package io.github.legosteen11.easycommands.command

import io.github.legosteen11.easycommands.CommandHandler
import io.github.legosteen11.easycommands.user.ICommandSender

interface ICommand {
    fun execute(commandSender: ICommandSender)

    var commandHandler: CommandHandler
}