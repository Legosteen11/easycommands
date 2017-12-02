package io.github.legosteen11.easycommands.parsing.typeparsing

import io.github.legosteen11.easycommands.command.ICommand
import io.github.legosteen11.easycommands.exception.developerissue.UnparsableTypeException
import io.github.legosteen11.easycommands.exception.playerissue.InvalidTypeException
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface ITypeParser {
    /**
     * Check whether the type parser can parse this type
     *
     * @param type The type to check
     *
     * @return Returns true if the type can be parsed, returns false if it cannot be parsed
     */
    fun isParsable(type: KType): Boolean

    /**
     * Parse a value to a specific type.
     *
     * @param expectedType The expected return type
     * @param value The value
     *
     * @return Returns the parsed object
     *
     * @throws UnparsableTypeException Thrown when you try to parse a field with a type that is not (yet) parsable.
     * @throws InvalidTypeException Thrown when the type cannot be parsed because the type is invalid.
     */
    @Throws(UnparsableTypeException::class, InvalidTypeException::class)
    fun parse(expectedType: KType, value: String, command: KClass<out ICommand>, parameter: String): Any

    /**
     * Get a list of values for autocompletion.
     *
     * @param expectedType The expected type
     * @param currentValue The value that the user has currently typed
     *
     * @return Returns an array of strings containing the autocomplete options
     */
    fun autocomplete(expectedType: KType, currentValue: String): Array<String>
}