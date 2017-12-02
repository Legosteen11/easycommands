package io.github.legosteen11.easycommands.parsing

import io.github.legosteen11.easycommands.annotation.Argument
import io.github.legosteen11.easycommands.annotation.Command
import io.github.legosteen11.easycommands.command.SimpleCommand
import io.github.legosteen11.easycommands.user.ICommandSender
import org.junit.Test
import kotlin.reflect.full.createType
import kotlin.test.assertEquals

@Command("empty", "Empty command")
class EmptyCommand(
        // empty
) : SimpleCommand {
    var run = false

    override fun execute(commandSender: ICommandSender) {
        run = true
    }

}

@Command("helloworld", "Use this command to display your name!")
data class HelloWorldCommand(
        @Argument("name", optional = true)
        val name: String = "World"
): SimpleCommand {
    override fun execute(commandSender: ICommandSender) {
        commandSender.sendMessage("Hello, $name")
    }
}

@Command("multiparamcommand", "Use this command to test multiple commands.")
data class MultiParamCommand (
        @Argument("name", optional = true)
        val name: String = "default1",
        @Argument("secondName")
        val secondName: String,
        @Argument("thirdName", optional = true)
        val thirdName: String = "default3"
) : SimpleCommand {
    override fun execute(commandSender: ICommandSender) {
        commandSender.sendMessage("name = $name, secondName = $secondName, thirdName = $thirdName")
    }
}

@Command("parsingcommand", "Use this command to test parsing commands.")
data class ParsingCommand (
        @Argument("int")
        val age: Int,
        @Argument("double")
        val double: Double,
        @Argument("double2")
        val double2: Double
) : SimpleCommand {
    override fun execute(commandSender: ICommandSender) {
        commandSender.sendMessage("age = $age")
    }
}

class CommandParserTest {
    @Test
    fun parse() {
        // CommandParser.parse<HelloWorldCommand>(arrayOf()).execute(commandSender)
        /*
        assertEquals(HelloWorldCommand().toString(), CommandParser.parse<HelloWorldCommand>(arrayOf()).toString())
        assertEquals(HelloWorldCommand("Legosteen11").toString(), CommandParser.parse<HelloWorldCommand>(arrayOf("Legosteen11")).toString())
        assertEquals(HelloWorldCommand("Legosteen11, friends and contributors!").toString(), CommandParser.parse<HelloWorldCommand>(arrayOf("Legosteen11,", "friends", "and", "contributors!")).toString())

        assertEquals(MultiParamCommand(secondName = "second_name").toString(), CommandParser.parse<MultiParamCommand>(arrayOf("second_name")).toString())
        assertEquals(MultiParamCommand("first_name", "second_name", "third_name").toString(), CommandParser.parse<MultiParamCommand>(arrayOf("first_name", "second_name", "third_name")).toString())
        assertEquals(MultiParamCommand("first_name", "second_name").toString(), CommandParser.parse<MultiParamCommand>(arrayOf("first_name", "second_name")).toString())
        */

        assertEquals(EmptyCommand::class.createType(), CommandParser.parse(EmptyCommand::class, arrayOf())::class.createType())

        assertEquals(HelloWorldCommand().toString(), CommandParser.parse(HelloWorldCommand::class, arrayOf()).toString())
        assertEquals(HelloWorldCommand("Legosteen11").toString(), CommandParser.parse(HelloWorldCommand::class, arrayOf("Legosteen11")).toString())
        assertEquals(HelloWorldCommand("Legosteen11, friends and contributors!").toString(), CommandParser.parse(HelloWorldCommand::class, arrayOf("Legosteen11,", "friends", "and", "contributors!")).toString())

        assertEquals(MultiParamCommand(secondName = "second_name").toString(), CommandParser.parse(MultiParamCommand::class, arrayOf("second_name")).toString())
        assertEquals(MultiParamCommand("first_name", "second_name", "third_name").toString(), CommandParser.parse(MultiParamCommand::class, arrayOf("first_name", "second_name", "third_name")).toString())
        assertEquals(MultiParamCommand("first_name", "second_name").toString(), CommandParser.parse(MultiParamCommand::class, arrayOf("first_name", "second_name")).toString())

        assertEquals(ParsingCommand(16, 18.3, 17.0).toString(), CommandParser.parse(ParsingCommand::class, arrayOf("16", "18.3", "17")).toString())
    }

}