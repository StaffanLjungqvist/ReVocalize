package se.staffanljungqvist.revocalize.models


//Todo Tillsätt Comparator Interface
data class Stage(
    val name : String,
    val difficulty : String,
    val pointsForGold : Int,
    val pointsForSilver : Int,
    val startingPoints : Int,
    var isComplete : Boolean = false,
    var beatenWithRank : String = "N/A",
    var pointRecord : Int = 0,
    val phraseList : List<Phrase> = listOf(
    )
)

data class Rank(
    val name : String,
    val color : String
)
