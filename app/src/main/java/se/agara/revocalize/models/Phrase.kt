package se.agara.revocalize.models

data class Phrase(

    val text : String,
    var audioFile : AudioFile,
    val slizes : List<Slize>,
)

data class AudioFile(
    var file : Int,
    var duration : Int = 0
)
