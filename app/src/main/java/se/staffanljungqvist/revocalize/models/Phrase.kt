package se.staffanljungqvist.revocalize.models

data class Phrase(

    val text: String,
    val trivia: String? = null,
    val slizediv: Int,
    var slizes: List<Slize> = listOf(),
    var audioFile: AudioFile? = null,

    )

data class AudioFile(
    var file: Int,
    var duration: Int = 0
)
