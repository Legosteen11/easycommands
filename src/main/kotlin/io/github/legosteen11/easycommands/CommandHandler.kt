package io.github.legosteen11.easycommands

import io.github.legosteen11.easycommands.annotation.Command
import io.github.legosteen11.easycommands.command.CommandWrapper
import io.github.legosteen11.easycommands.command.ICommand
import io.github.legosteen11.easycommands.exception.IExceptionHandler
import io.github.legosteen11.easycommands.exception.developerissue.CommandNotFoundException
import io.github.legosteen11.easycommands.exception.developerissue.InvalidAnnotationException
import io.github.legosteen11.easycommands.exception.developerissue.MissingAnnotationException
import io.github.legosteen11.easycommands.exception.developerissue.UnparsableTypeException
import io.github.legosteen11.easycommands.exception.playerissue.SubCommandNotFoundException
import io.github.legosteen11.easycommands.parsing.CommandParser
import io.github.legosteen11.easycommands.parsing.typeparsing.DefaultTypeParser
import io.github.legosteen11.easycommands.parsing.typeparsing.ITypeParser
import io.github.legosteen11.easycommands.user.ICommandSender
import mu.KotlinLogging
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

open class CommandHandler(private val exceptionHandler: IExceptionHandler,
                     private val typeParser: ITypeParser = DefaultTypeParser,
                     private val alwaysExecute: ((ICommandSender, String, Array<String>) -> Unit)? = null,
                     private val log: Boolean = true) {
    private val commands = arrayListOf<CommandWrapper>()
    private val superCommands = hashMapOf<CommandWrapper, Array<CommandWrapper>>()
    private val logger = KotlinLogging.logger {  }

    /**
     * Handle the command
     *
     * @param commandSender The command sender
     * @param commandName The command name
     * @param parameters The command parameters
     */
    fun onCommand(commandSender: ICommandSender, commandName: String, parameters: Array<String>) {
        if(log)
            logger.info { "${commandSender.getName()} (${commandSender.getIdentifier()}) executed $commandName with arguments: ${parameters.joinToString()}." }

        alwaysExecute?.invoke(commandSender, commandName, parameters)

        runBlock(commandSender) {
            val command = getCommandByName(commandName)

            val parsedCommand = if(command == null) {
                val superCommand = getSuperCommandByName(commandName) ?: throw CommandNotFoundException(commandName)
                val subCommandName = parameters.getOrNull(0)

                if(subCommandName == null)
                    parseCommand(superCommand.first, emptyArray())
                else {
                    val subCommand = getCommandByName(parameters[0], superCommand.second.toList()) ?: throw SubCommandNotFoundException(superCommand.first, superCommand.second)

                    parseCommand(subCommand, parameters.drop(1).toTypedArray())
                }
            } else
                parseCommand(command, parameters)

            parsedCommand.execute(commandSender)
        }
    }

    private fun parseCommand(command: CommandWrapper, parameters: Array<String>): ICommand = CommandParser.parse(command.command, parameters, typeParser).apply {
        commandHandler = this@CommandHandler
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
    fun addCommands(vararg commands: KClass<out ICommand>) {
        commands.forEach { command ->
            // use this to check whether the command is parsable. It'll throw exceptions if it's not
            CommandParser.getParameters(command, typeParser)

            val commandWrapper = CommandWrapper(command, command.findAnnotation<Command>()!!)

            this.commands.add(commandWrapper)
        }
    }

    /**
     * Get all the commands that are registered
     *
     * @return The commands
     */
    fun getCommands(): Array<CommandWrapper> = commands.plus(superCommands.keys).toTypedArray()

    /**
     * Get an array of possible autocompleted results for parameters
     *
     * @param commandSender The command sender
     * @param commandName The command name
     * @param parameters The parameters
     *
     * @return The autocompleted values.
     */
    fun autoComplete(commandSender: ICommandSender, commandName: String, parameters: Array<String>): Array<String> {
        try {
            val command = getCommandByName(commandName) ?: return emptyArray()

            val parameterType = CommandParser.getParameters(command.command, typeParser).getOrNull(parameters.size-1)?.first?.type ?: return emptyArray()

            return typeParser.autocomplete(parameterType, parameters.last())
        } catch (e: Throwable) {
            return emptyArray()
        }
    }

    /**
     * Run code in a block that catches and handles exceptions.
     *
     * @param commandSender The commandsender
     * @param block The code block
     */
    fun runBlock(commandSender: ICommandSender, block: () -> Unit) {
        try {
            block()
        } catch (e: Throwable) {
            exceptionHandler.handleException(e, commandSender)
        }
    }

    /**
     * Add a supercommand with subcommands to listen for.
     *
     * @param superCommand The supercommand to add
     * @param commands The subcommands to add
     *
     * @throws InvalidAnnotationException Thrown when an annotation is not correctly configured (for example: the annotation says a field is optional, but there is no default parameter)
     * @throws MissingAnnotationException Thrown when there is a field or command without an annotation.
     * @throws UnparsableTypeException Thrown when you try to add a field with a type that is not (yet) parsable.
     */
    @Throws(InvalidAnnotationException::class, MissingAnnotationException::class, UnparsableTypeException::class)
    fun addSuperCommand(superCommand: KClass<out ICommand>, vararg commands: KClass<out ICommand>) {
        CommandParser.getParameters(superCommand, typeParser)
        val superCommandWrapper = CommandWrapper(superCommand, superCommand.findAnnotation<Command>()!!)

        val commandWrappers = arrayListOf<CommandWrapper>()

        commands.forEach { command ->
            CommandParser.getParameters(command, typeParser)

            commandWrappers.add(CommandWrapper(command, command.findAnnotation<Command>()!!))
        }

        superCommands.put(superCommandWrapper, commandWrappers.toTypedArray())
    }

    private fun getCommandByName(commandName: String, commandList: Iterable<CommandWrapper> = commands) = commandList.firstOrNull { it.getName().toLowerCase() == commandName.toLowerCase() }

    private fun getSuperCommandByName(commandName: String) = superCommands.map { it.key to it.value }.firstOrNull { it.first.getName().toLowerCase() == commandName.toLowerCase() }
}