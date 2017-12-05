package io.github.legosteen11.easycommands

import io.github.legosteen11.easycommands.annotation.Argument
import io.github.legosteen11.easycommands.annotation.Command
import io.github.legosteen11.easycommands.command.SimpleCommand
import io.github.legosteen11.easycommands.exception.DefaultExceptionHandler
import io.github.legosteen11.easycommands.exception.SimpleExceptionHandlerTest
import io.github.legosteen11.easycommands.parsing.EmptyCommand
import io.github.legosteen11.easycommands.parsing.HelloWorldCommand
import io.github.legosteen11.easycommands.parsing.MultiParamCommand
import io.github.legosteen11.easycommands.parsing.ParsingCommand
import io.github.legosteen11.easycommands.user.ICommandSender
import io.github.legosteen11.easycommands.user.TestCommandSender
import org.junit.Test

class CommandHandlerTest {
    @Command("testunhandledexception", "Test exception command")
    class TestUnhandledExceptionCommand(
            // empty
    ) : SimpleCommand() {
        override fun execute(commandSender: ICommandSender) {
            throw SimpleExceptionHandlerTest.TestException()
        }
    }

    @Command("testexception", "Test exception command")
    class TestTestExceptionCommand(
            @Argument("test")
            val testValue: String
    ) : SimpleCommand() {
        override fun execute(commandSender: ICommandSender) {
            throw SimpleExceptionHandlerTest.AnotherTestException(testValue)
        }
    }

    @Command("commandhandlertest", "Test getHandler")
    class TestHandlerCommand(
            // empty
    ) : SimpleCommand() {
        override fun execute(commandSender: ICommandSender) {
            commandSender.sendMessage("commands=${commandHandler.getCommands().joinToString()}")
        }
    }

    @Test
    fun onCommand() {
        var handledUnhandledException = false
        var executedDefaultExceptionMethod = false
        val exceptionHandler = DefaultExceptionHandler(handleUnhandledException = { _, _ ->
            handledUnhandledException = true
        }, alwaysExcecute = { iCommandSender, throwable ->
            executedDefaultExceptionMethod = true
        })
        val commandSender = TestCommandSender()

        exceptionHandler.addExceptionHandler<SimpleExceptionHandlerTest.AnotherTestException> { throwable, iCommandSender ->
            iCommandSender.sendMessage("handled exception")
        }

        var executedAlwaysRunMethod = false
        val commandHandler = CommandHandler(exceptionHandler, alwaysExecute = { _, _, _ -> executedAlwaysRunMethod = true })

        commandHandler.addCommands(
                EmptyCommand::class,
                HelloWorldCommand::class,
                MultiParamCommand::class,
                ParsingCommand::class,

                // test exceptions:
                TestUnhandledExceptionCommand::class,
                TestTestExceptionCommand::class,

                // test setting of commandHandler
                TestHandlerCommand::class
        )

        commandHandler.onCommand(commandSender, "helloworld", arrayOf())
        assert(commandSender.received("Hello, World"))
        assert(executedAlwaysRunMethod)
        commandHandler.onCommand(commandSender, "helloworld", arrayOf("test"))
        assert(commandSender.received("Hello, test"))

        commandHandler.onCommand(commandSender, "testunhandledexception", arrayOf())
        assert(handledUnhandledException)
        assert(executedDefaultExceptionMethod)

        commandHandler.onCommand(commandSender, "testexception", arrayOf("test"))
        assert(commandSender.received("handled exception"))

        commandHandler.onCommand(commandSender, "commandhandlertest", arrayOf())
        assert(commandSender.received("commands=${commandHandler.getCommands().joinToString()}"))
    }
}