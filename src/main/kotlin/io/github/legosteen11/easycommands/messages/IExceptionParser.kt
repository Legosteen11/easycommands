package io.github.legosteen11.easycommands.messages

import io.github.legosteen11.easycommands.exception.playerissue.InvalidTypeException
import io.github.legosteen11.easycommands.exception.playerissue.MissingParameterException

interface IExceptionParser {
    /**
     * Get a string for the players to read for an InvalidTypeException.
     *
     * @param e The invalidTypeException
     *
     * @return Returns the message for the player
     */
    fun parseInvalidTypeException(e: InvalidTypeException): String

    /**
     * Get a string for the players to read for a MissingParameterException
     *
     * @param e The missingParameterException
     *
     * @return Returns the message for the player
     */
    fun parseMissingParameterException(e: MissingParameterException): String
}