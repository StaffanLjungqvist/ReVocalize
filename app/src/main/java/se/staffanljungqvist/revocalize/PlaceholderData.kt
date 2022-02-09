package se.staffanljungqvist.revocalize.builders

import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.models.AudioFile

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

object Colors {
    val colors = listOf(
        "#FFFF58",
        "#8F0DFF",
        "#E51919",
        "#FF6C00",
        "#FF4BF8",
        "#4BEBFF",
        "#38FF75",
        "#88FF00",
        "#FACA00",
        "#009FF8",
    )
}