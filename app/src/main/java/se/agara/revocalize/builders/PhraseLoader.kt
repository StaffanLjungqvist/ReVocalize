package se.agara.revocalize.builders

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import se.agara.revocalize.R
import se.agara.revocalize.models.AudioFile
import se.agara.revocalize.models.Phrase
import se.agara.revocalize.models.Slize

class PhraseLoader(val context: Context) {

    fun loadPhrase(level: Int): Phrase? {

        var duration : Int? = null
        var audioFile : AudioFile
        var slizes : List<Slize>


        try {
            var mediaPlayer = MediaPlayer.create(context, AudioList.audioFiles[level].file!!)
            duration = mediaPlayer.duration
            mediaPlayer.release()
        } catch (e: RuntimeException) {
            Log.e("kolla", "Kunde inte läsa in ljudfilen")
        }

        if (duration != null) {
            audioFile = AudioFile(AudioList.audioFiles[level].file, duration)
        } else {
            return null
        }

        slizes = SliceBuilder().makeSlices(duration, level)

        return Phrase(TextPhrases.textlist[level], audioFile, slizes)
    }
}




object AudioList {
    val audioFiles = listOf<AudioFile>(
        AudioFile(R.raw.guess),
        AudioFile(R.raw.happy),
        AudioFile(R.raw.believe),
        AudioFile(R.raw.great_things),
        AudioFile(R.raw.harvest),
        AudioFile(R.raw.teach_me),
        AudioFile(R.raw.courage)

    )
}

object TextPhrases {
    val textlist = listOf(
        "Guess what I am saying",
        "Whoever is happy will make others happy too.",
        "Believe you can and you are halfway, there.",
        "If you cannot do great things, do small things in a great way.",
        "Don't judge each day by the harvest you reap but by the seeds that you plant.",
        "Tell me and I forget. Teach me and I remember. Involve me and I learn.",

        "Success is not final; failure is not fatal: It is the courage to continue that counts"
    )
}