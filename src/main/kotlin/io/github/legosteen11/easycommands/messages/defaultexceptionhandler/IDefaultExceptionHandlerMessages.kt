package io.github.legosteen11.easycommands.messages.defaultexceptionhandler

import io.github.legosteen11.easycommands.exception.playerissue.InvalidTypeException
import io.github.legosteen11.easycommands.exception.playerissue.MissingParameterException

interface IDefaultExceptionHandlerMessages {
    fun invalidType(e: InvalidTypeException): String

    fun missingParameter(e: MissingParameterException): String
}