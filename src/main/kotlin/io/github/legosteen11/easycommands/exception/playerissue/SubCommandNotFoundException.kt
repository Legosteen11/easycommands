package io.github.legosteen11.easycommands.exception.playerissue

import io.github.legosteen11.easycommands.command.CommandWrapper

class SubCommandNotFoundException(superCommand: CommandWrapper, subCommands: Array<CommandWrapper>): Exception()