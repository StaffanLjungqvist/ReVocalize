package se.staffanljungqvist.revocalize.models

data class Level(
    val id : Int,
    val phraseList : List<Phrase> = listOf(
    )
)