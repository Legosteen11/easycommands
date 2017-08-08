package io.github.legosteen11.easycommands.annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Argument(val name: String, val optional: Boolean = false, val missingMessage: String = "") //TODO: Remove the need for the optional value and determine it based on whether the argument has a default value or not.