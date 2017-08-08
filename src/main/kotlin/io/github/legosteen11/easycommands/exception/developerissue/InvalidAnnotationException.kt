package io.github.legosteen11.easycommands.exception.developerissue

data class InvalidAnnotationException(val name: String, val command: String?) : Exception("Invalid annotation for '$name' ${command?.let { "in '$command'" }}!")