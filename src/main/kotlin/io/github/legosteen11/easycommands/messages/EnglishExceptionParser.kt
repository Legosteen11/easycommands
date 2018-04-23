package io.github.legosteen11.easycommands.messages

import io.github.legosteen11.easycommands.exception.playerissue.InvalidTypeException
import io.github.legosteen11.easycommands.exception.playerissue.MissingParameterException
import kotlin.reflect.KType
import kotlin.reflect.full.createType

object EnglishExceptionParser : IExceptionParser {
    override fun parseInvalidTypeException(e: InvalidTypeException): String = "The argument for ${e.parameter} is not ${e.expectedType}."

    override fun parseMissingParameterException(e: MissingParameterException): String = "Could not find an argument for ${e.parameter} in ${e.command}."

    fun KType.getTypeString(): String = when(this) {
        Int::class.createType() -> "an integer (example: 21)"
        Double::class.createType() -> "a double (example: 3.14)"
        String::class.createType() -> "a piece of text (example: hello)"
        else -> toString()
    }
}