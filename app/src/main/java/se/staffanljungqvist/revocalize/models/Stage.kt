package se.staffanljungqvist.revocalize.models

data class Stage(
    val name : String,
    val difficulty : String,
    val guessesForGold : Int,
    val guessesForSilver : Int,
    val GuessAmount : Int,
    var isComplete : Boolean = false,
    var beatenWithRank : String = "N/A",
    var guessRecord : Int = 0,
    val phraseList : List<Phrase> = listOf(
    )
)

data class Rank(
    val name : String,
    val color : String
)
