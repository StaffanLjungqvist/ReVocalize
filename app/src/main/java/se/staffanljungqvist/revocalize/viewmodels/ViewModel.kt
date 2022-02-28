package se.staffanljungqvist.revocalize.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.staffanljungqvist.revocalize.builders.Colors
import se.staffanljungqvist.revocalize.builders.Stages
import se.staffanljungqvist.revocalize.builders.TextPhrases
import se.staffanljungqvist.revocalize.models.Phrase
import se.staffanljungqvist.revocalize.models.Slize
import se.staffanljungqvist.revocalize.models.Stage

val TAG = "revodebug"

class ViewModel : ViewModel() {

    //TODO Sortera stagelist efter svårighetsgrad
    var stageList = Stages.StageList
    var currentStage: Stage = Stage("No Stage", "No Stage", 0, 0, 100)
    var phraseIndex = 0
    var slizes: List<Slize>? = null
    var currentPhrase = Phrase(TextPhrases.textlist[phraseIndex], 1, listOf<Slize>())
    var isCorrect = false
    var guessesUsed = 0
    var points = 100
    var rank = "BRONZE"
    var stageComplete = false
    var gameOver = false
    var bonus = 0
    var newRecord = false
    var toFragment = 0

    val phraseLoaded: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val audioReady: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    fun loadStage(stage: Stage) {
        Log.d(TAG, "Loading stage ${stage.name}")
        currentStage = stage
        points = currentStage.startingPoints
        phraseIndex = 0
        slizes = null
        stageComplete = false
        gameOver = false
        newRecord = false
    }

    fun loadPhrase() {
        guessesUsed = 0
        isCorrect = false
        if (currentStage != null) {
            if (phraseIndex < currentStage!!.phraseList.size) {
                Log.d(TAG, "Laddar in fras ${currentStage!!.phraseList[phraseIndex]}")
                currentPhrase = currentStage!!.phraseList[phraseIndex]
                phraseIndex++
            } else {
                stageComplete = true
            }

            phraseLoaded.value = true
            phraseLoaded.value = false

        } else {
            Log.d(TAG, "Kunde inte ladda fras")
        }
    }

    fun makeSlices(duration: Int) {
        //lista av sliceobjekt initialiseras
        val sliceList = mutableListOf<Slize>()
        var slizeDivisions = currentPhrase.slizediv
        val randomColors = Colors.colors.take(slizeDivisions).shuffled()
        val sliceLength = (duration / slizeDivisions)
        for (number in 1..slizeDivisions) {
            sliceList.add(
                Slize(
                    number,
                    (number - 1) * sliceLength,
                    sliceLength.toLong(),
                    randomColors[(number - 1)]
                )
            )
        }
        Log.d(
            se.staffanljungqvist.revocalize.adapters.TAG,
            "Skapade ${sliceList.size} slices med längd $sliceLength vardera"
        )
        slizes = superShuffle(sliceList)
    }


    fun superShuffle(list: MutableList<Slize>): List<Slize> {
        var superShuffled = false
        while (superShuffled == false) {
            superShuffled = true
            list.shuffle()
            for (i in 0..list.size) {
                if (i <= list.size - 2) {
                    if ((list[i].number + 1) == list[i + 1].number) {
                        superShuffled = false
                    }
                }
            }
        }
        Log.d(
            se.staffanljungqvist.revocalize.adapters.TAG,
            "Blandade ${list.size} slizes till följande ordning ordning : ${list}"
        )
        return list
    }


    fun makeGuess(): Boolean {
        var correct = false
        bonus = 0
        //Kollar om listan är i rätt ordning
        var sortedList = slizes!!.sortedBy { it.number }

        if (sortedList.equals(slizes)) {
            giveBonus()
            correct = true
            isCorrect = true

            //Kollar om det va sista frasen. Sätter isåfall stageComplete
            if (phraseIndex == currentStage!!.phraseList.size) {
                stageComplete = true
            }

            //Om gissningen är fel ökas antal gissningar, och ett poäng dras bort.
        } else {
            guessesUsed++
            points--
        }

        //Kollar om poängen är slut. Om så är fallet sätts gameOver
        if (points < 1) {
            Log.d(TAG, "Sätter game over till true")
            gameOver = true
        }

        Log.d(TAG, "Antal gissningar kvar : $points")
        return correct
    }


    fun calculateScore() {

        //Kollar om nuvarande poängen är bättre än poängrekordet. Ändrar därefter.
        if (points > currentStage.pointRecord || currentStage.pointRecord == 0) {
            Log.d(TAG, "Nytt rekord")
            newRecord = true
            currentStage.pointRecord = points
        }

        /*Bestämmer vilken rank som sätts beroende på nuvarandande ranks specifieringar
        Om ranken är högra än tidigare så sparas den över
        */
        if (points >= currentStage!!.pointsForGold) {
            rank = "GOLD"
        } else if (points >= currentStage!!.pointsForSilver) {
            rank = "SILVER"
        } else {
            rank = "BRONZE"
        }
        if (rank == "GOLD") {
            currentStage!!.beatenWithRank = rank
        } else if (rank == "SILVER" && currentStage!!.beatenWithRank != "GOLD") {
            currentStage!!.beatenWithRank = rank
        } else if (rank == "BRONZE" && currentStage!!.beatenWithRank != "SILVER") {
            currentStage!!.beatenWithRank = rank
        }
        currentStage!!.isComplete = true

    }

    fun giveBonus() {

        Log.d(TAG, "Guesses used är $guessesUsed")
        if (guessesUsed == 0) {

            when (slizes!!.size) {
                3 -> bonus = 1
                4 -> bonus = 1
                5 -> bonus = 1
                6 -> bonus = 1
                7 -> bonus = 5
                8 -> bonus = 6
            }
        }
        points += bonus
        toFragment = bonus
        Log.d(TAG, "Sätter bonus till $bonus")
    }
}