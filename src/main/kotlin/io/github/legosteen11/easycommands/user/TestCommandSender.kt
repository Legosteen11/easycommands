package io.github.legosteen11.easycommands.user

class TestCommandSender: ICommandSender {
    val receivedMessages = arrayListOf<String>()

    override fun getName(): String = "test_command_sender"

    override fun getIdentifier(): String = "test_command_sender"

    override fun sendMessage(message: String) {
        receivedMessages.add(message)
    }

    fun received(message: String): Boolean = receivedMessages.contains(message)
}