package se.staffanljungqvist.revocalize

import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import se.staffanljungqvist.revocalize.builders.Colors
import se.staffanljungqvist.revocalize.builders.TextPhrases
import se.staffanljungqvist.revocalize.models.Phrase
import se.staffanljungqvist.revocalize.models.Slize

class ViewModel : ViewModel() {

    var level = 0
    var currentPhrase = Phrase(TextPhrases.textlist[level], listOf<Slize>())
    var isCorrect = false
    var guesses = 1


    fun loadPhrase() {
        currentPhrase = Phrase(TextPhrases.textlist[level], listOf<Slize>())
        if (level < TextPhrases.textlist.size) {
            level ++
        }
        guesses = 1
    }

    fun makeSlices(duration: Int): List<Slize> {
        //lista av sliceobjekt initialiseras
        val sliceList = mutableListOf<Slize>()
        var slizeDivisions: Int

        val numbers = listOf(5, 6, 7, 8).random()
        slizeDivisions = numbers
/*        when (level) {
            0 -> slizeDivisions = 5
            1 -> slizeDivisions = 5
            2 -> slizeDivisions = 6
            3 -> slizeDivisions = 6
            4 -> slizeDivisions = 7
            5 -> slizeDivisions = 8
            6 -> slizeDivisions = 8
            else -> slizeDivisions = 4
        }*/
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
        return superShuffle(sliceList)
    }


    fun superShuffle(list: MutableList<Slize>): List<Slize> {
        var superShuffled = false
        while (superShuffled == false) {
            superShuffled = true
            list.shuffle()
            for (i in 0..list.size) {
                if (i <= list.size - 2) {
                    if ((list[i].number + 1) == list[i + 1].number || list[0].number == 1) {
                        superShuffled = false
                    }
                }
            }
        }
        Log.d(se.staffanljungqvist.revocalize.adapters.TAG, "Blandade ${list.size} slizes till följande ordning ordning : ${list}")
        return list
    }





    fun checkIfCorrect(): Boolean {
        guesses++
        var sortedList = currentPhrase.slizes.sortedBy { it.number }
        Log.d("kolla", "listan i rätt ordning; ${sortedList}")

        if (sortedList.equals(currentPhrase.slizes)) {
            isCorrect = true
            Log.d("kolla", "Listan är i rätt ordning!")
            return true
        }
        return false
    }
}