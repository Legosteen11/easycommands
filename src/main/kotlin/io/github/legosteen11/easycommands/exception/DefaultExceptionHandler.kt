package io.github.legosteen11.easycommands.exception

import io.github.legosteen11.easycommands.exception.playerissue.InvalidTypeException
import io.github.legosteen11.easycommands.exception.playerissue.MissingParameterException
import io.github.legosteen11.easycommands.messages.defaultexceptionhandler.DefaultExceptionHandlerMessages
import io.github.legosteen11.easycommands.messages.defaultexceptionhandler.IDefaultExceptionHandlerMessages
import io.github.legosteen11.easycommands.messages.simpleexceptionhandler.ISimpleExceptionHandlerMessages
import io.github.legosteen11.easycommands.messages.simpleexceptionhandler.SimpleExceptionHandlerMessages
import io.github.legosteen11.easycommands.user.ICommandSender

open class DefaultExceptionHandler(val handlerMessages: IDefaultExceptionHandlerMessages = DefaultExceptionHandlerMessages,
                                   alwaysExcecute: ((ICommandSender, Throwable) -> Unit)? = null,
                                   messages: ISimpleExceptionHandlerMessages = SimpleExceptionHandlerMessages,
                                   log: Boolean = true,
                                   handleUnhandledException: (Throwable, ICommandSender) -> Unit = { exception, commandSender ->
                                       commandSender.sendMessage(messages.EXCEPTION_UNHANDLED)
                                   }): SimpleExceptionHandler(
    alwaysExcecute, messages, log, handleUnhandledException
) {
    init {
        addExceptionHandler<InvalidTypeException> { throwable, iCommandSender -> iCommandSender.sendMessage(handlerMessages.invalidType(throwable as InvalidTypeException)) }
        addExceptionHandler<MissingParameterException> { throwable, iCommandSender -> iCommandSender.sendMessage(handlerMessages.missingParameter(throwable as MissingParameterException)) }
    }
}