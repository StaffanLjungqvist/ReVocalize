package se.staffanljungqvist.revocalize.models



//Todo Tillsätt Comparator Interface

data class Stages (
    val stageList : ArrayList<StageModelClass>
        )


data class StageModelClass(
    val id : Int,
    val name : String,
    val pointsForGold : Int,
    val pointsForSilver : Int,
    val startingPoints : Int,
    var isComplete : Boolean = false,
    var beatenWithRank : String = "N/A",
    val phraseList : List<Phrase> = listOf(),
)

