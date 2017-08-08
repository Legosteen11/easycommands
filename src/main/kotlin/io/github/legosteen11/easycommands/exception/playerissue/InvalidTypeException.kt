package io.github.legosteen11.easycommands.exception.playerissue

import io.github.legosteen11.easycommands.command.ICommand
import kotlin.reflect.KClass
import kotlin.reflect.KType

data class InvalidTypeException(val command: KClass<out ICommand>, val parameter: String, val expectedType: KType) : Exception("Expected $expectedType for $parameter in $command")