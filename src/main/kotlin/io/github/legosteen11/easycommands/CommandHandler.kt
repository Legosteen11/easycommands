package io.github.legosteen11.easycommands

import io.github.legosteen11.easycommands.annotation.Command
import io.github.legosteen11.easycommands.command.CommandWrapper
import io.github.legosteen11.easycommands.command.ICommand
import io.github.legosteen11.easycommands.exception.developerissue.InvalidAnnotationException
import io.github.legosteen11.easycommands.exception.developerissue.MissingAnnotationException
import io.github.legosteen11.easycommands.exception.developerissue.UnparsableTypeException
import io.github.legosteen11.easycommands.exception.playerissue.MissingParameterException
import io.github.legosteen11.easycommands.exception.playerissue.InvalidTypeException
import io.github.legosteen11.easycommands.messages.EnglishExceptionParser
import io.github.legosteen11.easycommands.messages.IExceptionParser
import io.github.legosteen11.easycommands.parsing.CommandParser
import io.github.legosteen11.easycommands.parsing.DefaultTypeParser
import io.github.legosteen11.easycommands.parsing.ITypeParser
import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Use the commandhandler in your plugin to manage commands.
 *
 * @param exceptionParser The exception parser. By default this will be the english exception parser. You can create your own by extending IExceptionParser
 */
class CommandHandler(val exceptionParser: IExceptionParser = EnglishExceptionParser, val typeParser: ITypeParser = DefaultTypeParser) : CommandExecutor, TabCompleter {
    private val commands = arrayListOf<CommandWrapper>()

    override fun onTabComplete(commandSender: CommandSender?, bukkitCommand: org.bukkit.command.Command?, commandName: String, parameters: Array<String>): MutableList<String> {
        val command = getCommandByName(commandName) ?: throw IllegalArgumentException("CommandHandler was registered for the command $commandName, but there is no command with that name...")

        val commandParams = CommandParser.getParameters(command.command, typeParser)

        val currentParam = commandParams.getOrNull(parameters.size) ?: return mutableListOf() // return empty list of no param found

        return typeParser.autocomplete(currentParam.first.type, parameters.last()).toMutableList()
    }

    override fun onCommand(commandSender: CommandSender, bukkitCommand: org.bukkit.command.Command, commandName: String, parameters: Array<String>): Boolean {
        val command = getCommandByName(commandName) ?: throw IllegalArgumentException("CommandHandler was registered for the command $commandName, but there is no command with that name...")

        try {
            val parsedCommand = CommandParser.parse(command.command, parameters, typeParser)

            parsedCommand.execute(commandSender)
        } catch (e: MissingParameterException) {
            exceptionParser.parseMissingParameterException(e)
            return false
        } catch (e: InvalidTypeException) {
            exceptionParser.parseInvalidTypeException(e)
            return false
        }

        return true
    }

    /**
     * Add a command to listen for.
     *
     * @param commands The commands to add
     *
     * @throws InvalidAnnotationException Thrown when an annotation is not correctly configured (for example: the annotation says a field is optional, but there is no default parameter)
     * @throws MissingAnnotationException Thrown when there is a field or command without an annotation.
     * @throws UnparsableTypeException Thrown when you try to add a field with a type that is not (yet) parsable.
     */
    @Throws(InvalidAnnotationException::class, MissingAnnotationException::class, UnparsableTypeException::class)
    fun addCommand(vararg commands: KClass<out ICommand>) {
        commands.forEach { command ->
            // use this to check whether the command is parsable. It'll throw exceptions if it's not
            CommandParser.getParameters(command, typeParser)

            val commandWrapper = CommandWrapper(command, command.findAnnotation<Command>()!!)

            Bukkit.getPluginCommand(commandWrapper.getName())?.let {
                it.executor = this
                it.tabCompleter = this
            }

            this.commands.add(commandWrapper)
        }
    }

    private fun getCommandByName(commandName: String) = commands.firstOrNull { it.getName().toLowerCase() == commandName.toLowerCase() }
}