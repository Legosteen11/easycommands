package io.github.legosteen11.easycommands

import io.github.legosteen11.easycommands.annotation.Argument
import io.github.legosteen11.easycommands.annotation.Command
import io.github.legosteen11.easycommands.command.CommandWrapper
import io.github.legosteen11.easycommands.command.SimpleCommand
import io.github.legosteen11.easycommands.exception.DefaultExceptionHandler
import io.github.legosteen11.easycommands.exception.SimpleExceptionHandlerTest
import io.github.legosteen11.easycommands.exception.playerissue.SubCommandNotFoundException
import io.github.legosteen11.easycommands.parsing.EmptyCommand
import io.github.legosteen11.easycommands.parsing.HelloWorldCommand
import io.github.legosteen11.easycommands.parsing.MultiParamCommand
import io.github.legosteen11.easycommands.parsing.ParsingCommand
import io.github.legosteen11.easycommands.user.ICommandSender
import io.github.legosteen11.easycommands.user.TestCommandSender
import org.junit.Test
import kotlin.reflect.full.findAnnotation
import kotlin.test.assertEquals
import kotlin.test.fail

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
            commandSender.sendMessage("commandhandlertest:commands=${commandHandler.getCommands().joinToString()}")
        }
    }

    @Command("supercommandtest", "Super Command test")
    class SuperCommandTest(
            // no params
    ) : SimpleCommand() {
        override fun execute(commandSender: ICommandSender) {
            commandSender.sendMessage("RAN SUPERCOMMAND")
        }
    }

    @Command("subcommandtest1", "Sub command test 1")
    class SubCommandTest1(
            // empty
    ) : SimpleCommand() {
        override fun execute(commandSender: ICommandSender) {
            commandSender.sendMessage("RAN SUBCOMMAND1")
        }
    }

    @Command("subcommandtest2", "Sub")
    class SubCommandTest2(
            @Argument("test")
            val testValue: String,
            @Argument("test2")
            val testValue2: String
    ) : SimpleCommand() {
        override fun execute(commandSender: ICommandSender) {
            commandSender.sendMessage("subcommandtest2:$testValue;$testValue2")
        }
    }

    @Test
    fun onCommand() {
        var handledUnhandledException = false
        var threwSubCommandNotFoundException = false
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

        exceptionHandler.addExceptionHandler<SubCommandNotFoundException> { throwable, iCommandSender ->
            threwSubCommandNotFoundException = true
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

        // test supercommand
        commandHandler.addSuperCommand(SuperCommandTest::class, SubCommandTest1::class, SubCommandTest2::class)

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
        assert(commandSender.received("commandhandlertest:commands=${commandHandler.getCommands().joinToString()}"))

        commandHandler.onCommand(commandSender,"supercommandtest", arrayOf())
        assert(commandSender.received("RAN SUPERCOMMAND"))

        commandHandler.onCommand(commandSender, "supercommandtest", arrayOf("breakpls"))
        assert(threwSubCommandNotFoundException)

        commandHandler.onCommand(commandSender, "supercommandtest", arrayOf("subcommandtest1"))
        assert(commandSender.received("RAN SUBCOMMAND1"))

        commandHandler.onCommand(commandSender, "supercommandtest", arrayOf("subcommandtest2", "test1", "test2"))
        assert(commandSender.received("subcommandtest2:test1;test2"))

        assert(commandHandler.getCommands().map { it.getName() }.contains("supercommandtest"))
    }
}