package io.github.legosteen11.easycommands.command

import io.github.legosteen11.easycommands.CommandHandler

abstract class SimpleCommand : ICommand {
    override lateinit var commandHandler: CommandHandler
}