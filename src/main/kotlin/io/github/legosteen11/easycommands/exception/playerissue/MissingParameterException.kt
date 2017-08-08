package io.github.legosteen11.easycommands.exception.playerissue

import io.github.legosteen11.easycommands.command.ICommand
import kotlin.reflect.KClass

data class MissingParameterException(val parameter: String?, val command: KClass<out ICommand>) : Exception("Command parameter not found: $parameter")