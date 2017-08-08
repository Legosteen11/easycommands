package io.github.legosteen11.easycommands.exception.developerissue

data class MissingAnnotationException(val name: String, val annotationNeeded: String?) : Exception("$name in does not have the correct annotation type ${annotationNeeded?.let { "($it)" } }.")