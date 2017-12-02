package io.github.legosteen11.easycommands.parsing

import io.github.legosteen11.easycommands.command.SimpleCommand
import io.github.legosteen11.easycommands.parsing.typeparsing.DefaultTypeParser
import org.bukkit.OfflinePlayer
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.reflect.full.createType

class DefaultTypeParserTest {
    @Test
    fun isParsable() {
        assertEquals(true, DefaultTypeParser.isParsable(String::class.createType()))
        assertEquals(true, DefaultTypeParser.isParsable(Int::class.createType()))
        assertEquals(true, DefaultTypeParser.isParsable(Double::class.createType()))
        assertEquals(false, DefaultTypeParser.isParsable(OfflinePlayer::class.createType()))
    }

    @Test
    fun parse() {
        assertEquals(15.03, DefaultTypeParser.parse(Double::class.createType(), "15.03", SimpleCommand::class, "testparam"))
        assertEquals(16, DefaultTypeParser.parse(Int::class.createType(), "16", SimpleCommand::class, "testparam"))
        assertEquals("test", DefaultTypeParser.parse(String::class.createType(), "test", SimpleCommand::class, "testparam"))
    }

}