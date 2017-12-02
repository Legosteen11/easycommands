package io.github.legosteen11.easycommands.exception

import io.github.legosteen11.easycommands.user.ICommandSender

interface IExceptionHandler {
    fun handleException(exception: Throwable, commandSender: ICommandSender)
}