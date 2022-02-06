package se.agara.revocalize.builders

import se.agara.revocalize.R
import se.agara.revocalize.models.AudioFile

object AudioList {
    val audioFiles = listOf<AudioFile>(
        AudioFile(R.raw.happy),
        AudioFile(R.raw.believe),
        AudioFile(R.raw.great_things),
        AudioFile(R.raw.harvest),
        AudioFile(R.raw.courage),
        AudioFile(R.raw.teach_me)


    )
}

object TextPhrases {
    val textlist = listOf(
        "Whoever is happy will make others happy too.",
        "Believe you can and you are halfway, there.",
        "If you cannot do great things, do small things in a great way.",
        "Don't judge each day by the harvest you reap but by the seeds that you plant.",

        "Success is not final; failure is not fatal: It is the courage to continue that counts",
        "Tell me and I forget. Teach me and I remember. Involve me and I learn."
    )
}