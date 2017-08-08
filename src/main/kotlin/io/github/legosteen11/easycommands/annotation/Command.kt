package io.github.legosteen11.easycommands.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(val name: String, val documentation: String, val permissions: String = "")