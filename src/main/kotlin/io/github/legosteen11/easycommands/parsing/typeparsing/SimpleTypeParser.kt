package io.github.legosteen11.easycommands.parsing.typeparsing

import io.github.legosteen11.easycommands.command.ICommand
import io.github.legosteen11.easycommands.exception.playerissue.InvalidTypeException
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.withNullability

open class SimpleTypeParser: ITypeParser {
    private val parsers = hashMapOf<KType, Pair<(String) -> Any?, ((String) -> Array<String>)?>>()

    override fun isParsable(type: KType): Boolean = parsers.contains(type.withNullability(false))

    override fun parse(expectedType: KType, value: String, command: KClass<out ICommand>, parameter: String): Any = parsers[expectedType.withNullability(false)]?.first?.invoke(value)
            ?: throw InvalidTypeException(command, parameter, expectedType)

    override fun autocomplete(expectedType: KType, currentValue: String): Array<String> = parsers[expectedType.withNullability(false)]?.second?.invoke(currentValue) ?: emptyArray()

    /**
     * Add a parser and autocompleter for the given type
     *
     * @param type The type
     * @param parser The parser
     * @param autocomplete The autocompleter
     */
    fun addParser(type: KType, parser: (String) -> Any?, autocomplete: ((String) -> Array<String>)? = null) {
        parsers.put(type, Pair(parser, autocomplete))
    }

    /**
     * Add a parser and autocompleter for the given type
     *
     * @param parser The parser
     * @param autocomplete The autocompleter
     */
    inline fun <reified T> addParser(noinline parser: (String) -> Any?, noinline autocomplete: ((String) -> Array<String>)? = null) {
        addParser(T::class.createType(), parser, autocomplete)
    }
}