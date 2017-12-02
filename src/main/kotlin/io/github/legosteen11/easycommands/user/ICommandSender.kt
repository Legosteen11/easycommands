package io.github.legosteen11.easycommands.user

interface ICommandSender {
    fun getName(): String

    fun getIdentifier(): String

    fun sendMessage(message: String)
}