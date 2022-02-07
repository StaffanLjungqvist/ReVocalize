package se.agara.revocalize.adapters

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import se.agara.revocalize.R
import se.agara.revocalize.builders.*
import se.agara.revocalize.models.AudioFile
import se.agara.revocalize.models.Phrase
import se.agara.revocalize.models.Slize

val TAG = "ReVoDebug"

class GameAdapter(val context : Context) {

    var level = 0

    fun advanceLevel() : Int{
        if (level < 5) {
            level ++
        }
        return level
    }

    fun loadPhrase(level: Int): Phrase? {
        var duration : Int? = null
        var audioFile : AudioFile
        var slizes : List<Slize>

        try {
            var mediaPlayer = MediaPlayer.create(context, AudioList.audioFiles[level].file!!)
            duration = mediaPlayer.duration
            mediaPlayer.release()
        } catch (e: RuntimeException) {
            Log.e(TAG, "Kunde inte läsa in ljudfilen")
        }
        if (duration != null) {
            audioFile = AudioFile(AudioList.audioFiles[level].file, duration)
        } else {
            return null
        }

        slizes = makeSlices(duration, level)
        return Phrase(TextPhrases.textlist[level], audioFile, slizes)
    }

    fun makeSlices(duration: Int, level: Int): List<Slize> {

        //lista av sliceobjekt initialiseras
        val sliceList = mutableListOf<Slize>()
        var slizeDivisions : Int

        when (level) {
            0 -> slizeDivisions = 5
            1 -> slizeDivisions = 5
            2 -> slizeDivisions = 6
            3 -> slizeDivisions = 6
            4 -> slizeDivisions = 7
            5 -> slizeDivisions = 8
            6 -> slizeDivisions = 8
            else -> slizeDivisions = 4
        }
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
        Log.d(TAG, "made ${sliceList.size} slices with the length of $sliceLength each")

        return superShuffle(sliceList)
    }

    fun superShuffle(list: MutableList<Slize>): MutableList<Slize> {

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
        Log.d(TAG, "Made ${list.size} slizes in the following order : ${list}")
        return list
    }
}


