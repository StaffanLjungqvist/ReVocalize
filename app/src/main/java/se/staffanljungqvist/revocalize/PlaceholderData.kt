package se.staffanljungqvist.revocalize.builders

import se.staffanljungqvist.revocalize.models.Level
import se.staffanljungqvist.revocalize.models.Phrase


object episodes {

val episodeList = listOf(
    Level(
        1,
        phraseList = listOf<Phrase>(
            Phrase("Believe you can and you are halfway there.", 3),
            Phrase("The greatest glory in living lies not in never falling, but in rising every time we fall", 4),
            Phrase("If you cannot do great things, do small things in a great way.", 5),
            Phrase("Tell me and I forget. Teach me and I remember. Involve me and I learn.", 6)
        )
    ),
    Level(
        2,
        phraseList = listOf<Phrase>(
            Phrase("The way to get started is to quit talking and begin doing.", 4),
            Phrase("Life is what happens when you're busy making other plans", 5),
            Phrase("Don't judge each day by the harvest you reap but by the seeds that you plant.", 6),
            Phrase("Success is not final; failure is not fatal: It is the courage to continue that counts.", 7),
        )
    )
)
}



object TextPhrases {
    val textlist = listOf(
        "Believe you can and you are halfway there.",
        "The greatest glory in living lies not in never falling, but in rising every time we fall",
        "If you cannot do great things, do small things in a great way.",
        "Tell me and I forget. Teach me and I remember. Involve me and I learn.",

        //"The greatest glory in living lies not in never falling, but in rising every time we fall",
       // "Don't judge each day by the harvest you reap but by the seeds that you plant.",

        //"Success is not final; failure is not fatal: It is the courage to continue that counts",
        //"The way to get started is to quit talking and begin doing.",

      //
     //   "Your time is limited, so don't waste it living someone else's life. Don't be trapped by dogma – which is living with the results of other people's thinking",
     //   "If life were predictable it would cease to be life, and be without flavor.",
  //      "If you look at what you have in life, you'll always have more. If you look at what you don't have in life, you'll never have enough",
      //  "If you set your goals ridiculously high and it's a failure, you will fail above everyone else's success.",
    //    "Life is what happens when you're busy making other plans",

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