package io.github.legosteen11.easycommands.parsing

import io.github.legosteen11.easycommands.command.ICommand
import io.github.legosteen11.easycommands.exception.developerissue.UnparsableTypeException
import io.github.legosteen11.easycommands.exception.playerissue.InvalidTypeException
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

object DefaultTypeParser : ITypeParser {
    val parsable_types = arrayOf<KClass<out Any>>(
            String::class,
            Int::class,
            Double::class,
            OfflinePlayer::class
    )

    override fun isParsable(type: KType): Boolean = parsable_types.map { it.createType() }.contains(type)

    override fun parse(expectedType: KType, value: String, command: KClass<out ICommand>, parameter: String): Any {
        val parsedVal =  when(expectedType) {
            String::class.createType() -> value
            Int::class.createType() -> value.toIntOrNull()
            Double::class.createType() -> value.toDoubleOrNull()
            OfflinePlayer::class.createType() -> Bukkit.getServer().getOfflinePlayer(value)
            else -> throw UnparsableTypeException(parameter, command, expectedType)
        }

        return parsedVal ?: throw InvalidTypeException(command, parameter, expectedType)
    }

    override fun autocomplete(expectedType: KType, currentValue: String): Array<String> =
            when(expectedType) {
                OfflinePlayer::class.createType() -> Bukkit.getServer().offlinePlayers.filter { it.name.toLowerCase().contains(currentValue.toLowerCase()) }.map { it.name }.toTypedArray()
                else -> arrayOf()
            }
}