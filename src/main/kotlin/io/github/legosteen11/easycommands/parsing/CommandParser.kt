package io.github.legosteen11.easycommands.parsing

import io.github.legosteen11.easycommands.annotation.Argument
import io.github.legosteen11.easycommands.command.ICommand
import io.github.legosteen11.easycommands.exception.developerissue.InvalidAnnotationException
import io.github.legosteen11.easycommands.exception.developerissue.MissingAnnotationException
import io.github.legosteen11.easycommands.exception.developerissue.UnparsableTypeException
import io.github.legosteen11.easycommands.exception.playerissue.MissingParameterException
import io.github.legosteen11.easycommands.parsing.typeparsing.BukkitTypeParser
import io.github.legosteen11.easycommands.parsing.typeparsing.ITypeParser
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

internal object CommandParser {
    /**
     * @exception MissingParameterException
     */
    @Throws(MissingParameterException::class, IllegalArgumentException::class)
    fun parse(command: KClass<out ICommand>, parameters: Array<String>, parser: ITypeParser = BukkitTypeParser): ICommand {
        val constructorParams = getParameters(command, parser)

        if(parameters.size < constructorParams.filter { !it.second.optional }.size) { // not enough argument
            val missingParam = constructorParams.subList(parameters.size, constructorParams.size).filter { !it.second.optional }.first().second
            throw MissingParameterException(missingParam.name, command)
        }

        val paramMap = hashMapOf<KParameter, Any?>()

        if(parameters.size >= constructorParams.size) {
            // all parameters or more present.
            constructorParams.forEachIndexed { index, constructorParam ->
                val valueString = if(constructorParams.size == index + 1)
                    parameters.copyOfRange(index, parameters.size).joinToString(" ")
                else
                    parameters[index]

                val value = parser.parse(constructorParam.first.type, valueString, command, constructorParam.second.name)

                paramMap.put(constructorParam.first, value)
            }
        } else {
            val missingParamsCount = parameters.size - constructorParams.filter { !it.second.optional }.size
            var paramsSkipped = 0
            var optionalsLeftToUse = if(missingParamsCount < 0) 0 else missingParamsCount

            constructorParams.forEachIndexed { index, constructorParam ->
                if((optionalsLeftToUse - missingParamsCount >= 0 && missingParamsCount != 0) || !constructorParam.second.optional) {
                    val valueString = parameters[index - paramsSkipped]

                    val value = parser.parse(constructorParam.first.type, valueString, command, constructorParam.second.name)

                    paramMap.put(constructorParam.first, value) // add the param and value to the map
                    if(constructorParam.second.optional)
                        optionalsLeftToUse--
                } else
                    paramsSkipped++
            }
        }

        return command.primaryConstructor?.callBy(paramMap) ?: throw IllegalArgumentException("Command has no primary constructor")
    }

    /**
     * Get the parameters for a command
     *
     * @param command The command class
     *
     * @return The parameters
     *
     * @throws InvalidAnnotationException Thrown when an annotation is not correctly configured (for example: the annotation says a field is optional, but there is no default parameter)
     * @throws MissingAnnotationException Thrown when there is a field or command without an annotation.
     * @throws UnparsableTypeException Thrown when you try to add a field with a type that is not (yet) parsable.
     */
    @Throws(InvalidAnnotationException::class, MissingAnnotationException::class, UnparsableTypeException::class)
    fun getParameters(command: KClass<out ICommand>, parser: ITypeParser): List<Pair<KParameter, Argument>> {
        val constructor = command.primaryConstructor ?: throw IllegalArgumentException("Command '${command.qualifiedName}' has no primary constructor")

        return constructor.parameters.map { it to (it.findAnnotation<Argument>() ?: throw MissingAnnotationException("${it.name} in ${command.qualifiedName}", Argument::class.qualifiedName)) }.apply {
            // check if there are any non-parsable types
            forEach {
                if(it.second.optional && !it.first.isOptional)
                    throw InvalidAnnotationException(it.second.name, command.qualifiedName)
                if(!parser.isParsable(it.first.type))
                    throw UnparsableTypeException(it.second.name, command, it.first.type)
            }
        }
    }
}