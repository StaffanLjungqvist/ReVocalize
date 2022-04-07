package se.staffanljungqvist.revocalize.models


data class Phrases (
    val phraseList : ArrayList<Phrase>
)

data class Phrase(
    val text: String,
    val trivia: String? = null,
    var slizes: List<Slize> = listOf()
    )
