package io.github.legosteen11.easycommands.messages.defaultexceptionhandler

import io.github.legosteen11.easycommands.exception.playerissue.InvalidTypeException
import io.github.legosteen11.easycommands.exception.playerissue.MissingParameterException
import org.bukkit.OfflinePlayer
import kotlin.reflect.KType
import kotlin.reflect.full.createType

object DefaultExceptionHandlerMessages: IDefaultExceptionHandlerMessages {
    override fun invalidType(e: InvalidTypeException): String = "The argument for ${e.parameter} is not ${e.expectedType.getTypeString()}."

    override fun missingParameter(e: MissingParameterException): String = "Could not find an argument for ${e.parameter}."

    private fun KType.getTypeString(): String = when(this) {
        Int::class.createType() -> "an integer (example: 21)"
        Double::class.createType() -> "a double (example: 3.14)"
        String::class.createType() -> "a piece of text (example: hello)"
        OfflinePlayer::class.createType() -> "a player"
        else -> toString()
    }
}