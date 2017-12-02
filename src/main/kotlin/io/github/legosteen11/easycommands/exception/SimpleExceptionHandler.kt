package io.github.legosteen11.easycommands.exception

import io.github.legosteen11.easycommands.messages.simpleexceptionhandler.ISimpleExceptionHandlerMessages
import io.github.legosteen11.easycommands.messages.simpleexceptionhandler.SimpleExceptionHandlerMessages
import io.github.legosteen11.easycommands.user.ICommandSender
import mu.KotlinLogging
import kotlin.reflect.KClass

open class SimpleExceptionHandler(private val alwaysExcecute: ((ICommandSender, Throwable) -> Unit)? = null,
                                  val messages: ISimpleExceptionHandlerMessages = SimpleExceptionHandlerMessages,
                                  private val log: Boolean = true,
                                  private val handleUnhandledException: (Throwable, ICommandSender) -> Unit = { exception, commandSender ->
                                      commandSender.sendMessage(messages.EXCEPTION_UNHANDLED)
                                  }): IExceptionHandler {
    private val logger = KotlinLogging.logger {  }

    private val exceptions = hashMapOf<KClass<out Throwable>, (Any: Throwable, ICommandSender) -> Unit>()

    override fun handleException(exception: Throwable, commandSender: ICommandSender) {
        if(log)
            logger.info { "${commandSender.getName()} (${commandSender.getIdentifier()}) threw exception ${exception.javaClass.canonicalName} (${exception.localizedMessage})" }

        alwaysExcecute?.invoke(commandSender, exception)

        val handler = exceptions[exception::class]

        if(handler == null) {
            unhandledException(commandSender, exception)
            return
        }

        try {
            handler(exception, commandSender)
        } catch (e: Throwable) {
            unhandledException(commandSender, exception)
        }
    }

    private fun unhandledException(commandSender: ICommandSender, exception: Throwable) {
        handleUnhandledException(exception, commandSender)
    }

    fun addExceptionHandlerWithType(exceptionType: KClass<out Throwable>, handle: (Throwable, ICommandSender) -> Unit) {
        exceptions.put(exceptionType, handle)
    }

    inline fun <reified T: Throwable> addExceptionHandler(crossinline handle: (Throwable, ICommandSender) -> Unit) {
        addExceptionHandlerWithType(T::class) { throwable, iCommandSender ->
            handle(throwable, iCommandSender)
        }
    }
}