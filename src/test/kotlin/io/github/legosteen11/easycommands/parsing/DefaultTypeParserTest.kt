package io.github.legosteen11.easycommands.parsing

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.github.legosteen11.easycommands.command.SimpleCommand
import mockit.Mock
import mockit.MockUp
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.junit.Test

import org.junit.Assert.*
import kotlin.reflect.full.createType

class MockBukkit : MockUp<Bukkit>() {
    val player1: OfflinePlayer = mock {
        on { name } doReturn "player1"
    }
    val player2: OfflinePlayer = mock {
        on { name } doReturn "player2"
    }

    val mockServer: Server = mock {
        on { offlinePlayers } doReturn arrayOf(player1, player2)
    }

    @Mock
    fun getServer() = mockServer
}

class DefaultTypeParserTest {
    init {
        MockBukkit()
    }

    @Test
    fun isParsable() {
        assertEquals(true, DefaultTypeParser.isParsable(String::class.createType()))
        assertEquals(true, DefaultTypeParser.isParsable(Int::class.createType()))
        assertEquals(true, DefaultTypeParser.isParsable(Double::class.createType()))
        assertEquals(true, DefaultTypeParser.isParsable(OfflinePlayer::class.createType()))
    }

    @Test
    fun parse() {
        assertEquals(15.03, DefaultTypeParser.parse(Double::class.createType(), "15.03", SimpleCommand::class, "testparam"))
        assertEquals(16, DefaultTypeParser.parse(Int::class.createType(), "16", SimpleCommand::class, "testparam"))
        assertEquals("test", DefaultTypeParser.parse(String::class.createType(), "test", SimpleCommand::class, "testparam"))
    }

    @Test
    fun autocomplete() {
        assertEquals(arrayOf("player1", "player2"), DefaultTypeParser.autocomplete(OfflinePlayer::class.createType(), "pla"))
    }

}