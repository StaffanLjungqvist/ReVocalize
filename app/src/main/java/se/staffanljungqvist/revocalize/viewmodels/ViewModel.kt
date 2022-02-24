package se.staffanljungqvist.revocalize.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.staffanljungqvist.revocalize.builders.Colors
import se.staffanljungqvist.revocalize.builders.TextPhrases
import se.staffanljungqvist.revocalize.models.Stage
import se.staffanljungqvist.revocalize.models.Phrase
import se.staffanljungqvist.revocalize.models.Slize

val TAG = "revodebug"

class ViewModel : ViewModel() {

    var currentStage : Stage = Stage("No Stage", "No Stage", 0, 0,100)
    var phraseIndex = 0
    var slizes : List<Slize>? = null
    var currentPhrase = Phrase(TextPhrases.textlist[phraseIndex], 1, listOf<Slize>())
    var isCorrect = false
    var currentGuesses = 0
    var totalGuesses = 0
    var rank = "BRONZE"
    var levelComplete = false


    var gameOver = false

    val audioReady: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }



    fun reset() {
        phraseIndex = 0
        slizes = null
        currentPhrase = Phrase(TextPhrases.textlist[phraseIndex], 1, listOf<Slize>())
        isCorrect = false
        currentGuesses = 0
        totalGuesses = 0
        levelComplete = false
        gameOver = false
    }

    fun loadPhrase() {
        currentGuesses = 0
        if (currentStage != null) {
            if (phraseIndex < currentStage!!.phraseList.size) {
                currentPhrase = currentStage!!.phraseList[phraseIndex]
                phraseIndex++
            } else {
                levelComplete = true
            }
        } else {
            Log.d(TAG, "Kunde inte ladda fras")
        }
    }

    fun makeSlices(duration: Int){
        //lista av sliceobjekt initialiseras
        val sliceList = mutableListOf<Slize>()
        var slizeDivisions = currentPhrase.slizediv
        Log.d(TAG, "the current phrases divisions is : ${currentPhrase.slizediv}")

        val randomColors = Colors.colors.shuffled().take(slizeDivisions)
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
        Log.d(se.staffanljungqvist.revocalize.adapters.TAG, "Skapade ${sliceList.size} slices med längd $sliceLength vardera")
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
        Log.d(se.staffanljungqvist.revocalize.adapters.TAG, "Blandade ${list.size} slizes till följande ordning ordning : ${list}")
        return list
    }

    fun checkIfCorrect(): Boolean {
        currentGuesses++

        if (currentGuesses > currentStage.guessesToComplete) {
            gameOver = true
        }

        var sortedList = slizes!!.sortedBy { it.number }

        if (sortedList.equals(slizes)) {
            currentGuesses -= 1
            totalGuesses += currentGuesses
            isCorrect = true
            Log.d(TAG, "Totalt antal gissninger är ${totalGuesses}")
            return true
        }
        return false
    }

    fun calculateScore() {
        if (totalGuesses < currentStage.guessRecord || currentStage.guessRecord == 0) {
            currentStage.guessRecord = totalGuesses
        }

        if (totalGuesses <= currentStage!!.guessesForGold) {
            rank = "GOLD"
        } else if (totalGuesses <= currentStage!!.guessesForSilver) {
            rank = "SILVER"
        } else {
            rank = "BRONZE"
        }
        if (rank == "GOLD") {
            currentStage!!.beatenWithRank = rank
        } else if (rank == "SILVER" && currentStage!!.beatenWithRank != "GOLD") {
            currentStage!!.beatenWithRank = rank
        } else if (rank == "BRONZE" && currentStage!!.beatenWithRank != "SILVER"){
            currentStage!!.beatenWithRank = rank
        }
        currentStage!!.isComplete = true

    }
}