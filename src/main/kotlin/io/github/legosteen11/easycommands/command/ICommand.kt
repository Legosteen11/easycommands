package io.github.legosteen11.easycommands.command

import org.bukkit.command.CommandSender

interface ICommand {
    fun execute(commandSender: CommandSender)
}