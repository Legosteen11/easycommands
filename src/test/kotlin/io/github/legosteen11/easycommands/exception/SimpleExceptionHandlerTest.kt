package io.github.legosteen11.easycommands.exception

import io.github.legosteen11.easycommands.user.TestCommandSender
import org.junit.Test

class SimpleExceptionHandlerTest {
    class TestException: Exception()
    class AnotherTestException(testValue: String): Exception()

    @Test
    fun handleException() {
        val handler = SimpleExceptionHandler()

        val commandSender = TestCommandSender()

        // test general exception
        val exception = Exception("test exception")
        handler.handleException(exception, commandSender)
        assert(commandSender.received(handler.messages.EXCEPTION_UNHANDLED))

        // test custom exception
        var exceptionHandled = false
        handler.addExceptionHandler<TestException> { throwable, iCommandSender ->
            exceptionHandled = true
        }
        handler.handleException(TestException(), commandSender)
        assert(exceptionHandled)

        // test another custom exception with value
        var anotherExceptionHandled = false
        handler.addExceptionHandler<AnotherTestException> { throwable, iCommandSender ->
            anotherExceptionHandled = true
        }
        handler.handleException(AnotherTestException("test"), commandSender)
        assert(anotherExceptionHandled)
    }
}