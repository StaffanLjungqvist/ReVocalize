package se.staffanljungqvist.revocalize.models

data class Phrase(

    val text : String,
    val slizediv : Int,
    var slizes : List<Slize> = listOf<Slize>(),
    var audioFile : AudioFile? = null,

    )

data class AudioFile(
    var file : Int,
    var duration : Int = 0
)
