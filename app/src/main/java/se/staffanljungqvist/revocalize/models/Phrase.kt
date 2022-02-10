package se.staffanljungqvist.revocalize.models

data class Phrase(

    val text : String,
    var slizes : List<Slize>,
    var audioFile : AudioFile? = null,

    )

data class AudioFile(
    var file : Int,
    var duration : Int = 0
)
