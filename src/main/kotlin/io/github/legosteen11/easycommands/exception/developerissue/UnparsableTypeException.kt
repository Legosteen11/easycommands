package io.github.legosteen11.easycommands.exception.developerissue

import io.github.legosteen11.easycommands.command.ICommand
import kotlin.reflect.KClass
import kotlin.reflect.KType

data class UnparsableTypeException(val parameter: String, val command: KClass<out ICommand>, val type: KType) : Exception("")