package se.staffanljungqvist.revocalize.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.staffanljungqvist.revocalize.builders.Colors
import se.staffanljungqvist.revocalize.builders.TextPhrases
import se.staffanljungqvist.revocalize.models.Level
import se.staffanljungqvist.revocalize.models.Phrase
import se.staffanljungqvist.revocalize.models.Slize

val TAG = "revodebug"

class ViewModel : ViewModel() {

    var currentStage : Int = 0
    var currentLevel : Level? = null
    var phraseIndex = 0
    var slizes : List<Slize>? = null


    val audioReady: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    var levelComplete = false



    var currentPhrase = Phrase(TextPhrases.textlist[phraseIndex], 1, listOf<Slize>())
    var isCorrect = false
    var currentGuesses = 0
    var totalGuesses = 0



    fun loadPhrase() {
        currentGuesses = 0
        if (currentLevel != null) {
            Log.d(TAG, "laddar in fras ${phraseIndex}")

            if (phraseIndex < currentLevel!!.phraseList.size) {
                levelComplete = false
                currentPhrase = currentLevel!!.phraseList[phraseIndex]
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

        val numbers = listOf(4, 5, 6).random()
       // slizeDivisions = numbers
/*
        when (level) {
            1 -> slizeDivisions = 3
            2 -> slizeDivisions = 4
            3 -> slizeDivisions = 5
            4 -> slizeDivisions = 6
            4 -> slizeDivisions = 5
            5 -> slizeDivisions = 5
            6 -> slizeDivisions = 6
            else -> slizeDivisions = 4
        }
*/
        val randomColors = Colors.colors.shuffled().take(slizeDivisions)
        val sliceLength = (duration / slizeDivisions)
        for (number in 1..slizeDivisions) {
            sliceList.add(
                Slize(
                    number,
                    (number - 1) * sliceLength,
                    sliceLength.toLong(),
                    randomColors[number - 1]
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
}