package se.staffanljungqvist.revocalize.builders

import se.staffanljungqvist.revocalize.models.Stage
import se.staffanljungqvist.revocalize.models.Phrase
import se.staffanljungqvist.revocalize.models.Rank


object Stages {

val StageList = listOf(
    Stage(
        "\"UPLIFTING QUOTES\"",
        "EASY",
        10,
        7,
        5,

        phraseList = listOf<Phrase>(
            Phrase("Believe you can and you are halfway there.", 3),
            Phrase("Try to be a rainbow in someone else’s cloud.", 3),
          //  Phrase("Each day comes bearing its gifts. Untie the ribbon.", 3),
          //  Phrase("A problem is a chance for you to do your best.", 3),
        //    Phrase("The way to get started is to quit talking and begin doing.", 3),
        )
    ),

    /*
    Love doesn’t make the world go round. Love is what makes the ride worthwhile.
    Great opportunities to help others seldom come, but small ones surround us every day.
    */

    Stage(
        "\"MOVIE QUOTES\"",
        "EASY",
        10,
        7,
        5,
        phraseList = listOf<Phrase>(
            Phrase("Here's looking at you, kid.", 3),
            Phrase("I love the smell of napalm in the morning.", 3),
            Phrase("I'm going to make him an offer he can't refuse.", 3),
            Phrase("May the Force be with you.", 3),
            Phrase("Toto, I've got a feeling we're not in Kansas anymore.", 3),

            )
    ),

    Stage(
        "\"SONG LYRICS\"",
        "EASY",
        10,
        7,
        5,
        phraseList = listOf<Phrase>(
            Phrase("Just a small town girl, living in a lonely world", 3),
            Phrase("So when you’re near me, darling can’t you hear me S. O. S", 3),
            Phrase("Billie Jean is not my lover She’s just a girl who claims that I am the one", 3),
            Phrase("We are the champions, my friends And we’ll keep on fighting ’til the end", 3),
            Phrase("Pretty woman, walkin’ down the street Pretty woman the kind I like to meet", 3),
        )
    ),

    Stage(
        "\"UPLIFTING QUOTES 2\"",
        "MEDIUM",
        10,
        7,
        5,
        phraseList = listOf<Phrase>(
            Phrase("Happiness makes up in height for what it lacks in length.", 4),
            Phrase("Kindness is one thing you can’t give away. It always comes back.", 4),
            Phrase("Living is the art of getting used to what we didn’t expect.", 4),
            Phrase("Believe in yourself. Stay in your own lane. There’s only one you.", 4),
            Phrase("Life is what happens when you're busy making other plans", 4),
            Phrase("If you cannot do great things, do small things in a great way.", 4),
        )
    ),

    Stage(
        "\"MOVIE QUOTES 2\"",
        "MEDIUM",
        12,
        10,
        10,
        phraseList = listOf<Phrase>(
            Phrase("I love the smell of napalm in the morning.", 4),
            Phrase("Love means never having to say you're sorry.", 4),
            Phrase("Louis, I think this is the beginning of a beautiful friendship.", 4),
            Phrase("Mama always said life was like a box of chocolates. You never know what you're gonna get.", 4),
        )
    ),

    Stage(
        "\"SONG LYRICS 2\"",
        "MEDIUM",
        12,
        10,
        5,
        phraseList = listOf<Phrase>(
            Phrase("Do you ever feel, like a plastic bag, Drifting through the wind, Wanting to start again?", 4),
            Phrase("Getting born in the state of Mississippi, her papa was a copper, and her mama was a hippy", 4),
            Phrase(" Let’s go all the way tonight No regrets, just love We can dance until we die You and I We’ll be young forever", 4),
            Phrase("‘Cause I’m just a teenage dirtbag baby Yeah, I’m just a teenage dirtbag baby Listen to Iron Maiden, baby, with me, ooh", 4),
            Phrase("Don’t want to close my eyes I don’t want to fall asleep ‘Cause I’d miss you baby And I don’t want to miss a thing", 4),
        )
    ),


    Stage(
        "\"UPLIFTING QUOTES 3\"",
        "HARD",
        20,
        10,
        10,
        phraseList = listOf<Phrase>(
            Phrase("Believe in yourself. Stay in your own lane. There’s only one you.", 5),
            Phrase("Tell me and I forget. Teach me and I remember. Involve me and I learn.", 5),
            Phrase("If life were predictable it would cease to be life, and be without flavor.", 5),
            Phrase("There is only one certainty in life and that is that nothing is certain.", 5),
            Phrase("Don't judge each day by the harvest you reap but by the seeds that you plant.", 5),
        )
    ),
    Stage(
        "\"SONG LYRICS 3\"",
        "HARD",
        12,
        9,
        20,
        phraseList = listOf<Phrase>(
            Phrase("Don’t stop believin’ Hold on to the feelin’ Streetlights, people", 5),
            Phrase("I just want you for my own More than you could ever know Make my wish come true All I want for Christmas Is you You, baby", 5),
            Phrase(" Let’s go all the way tonight No regrets, just love We can dance until we die You and I We’ll be young forever", 5),
            Phrase("‘Cause I’m just a teenage dirtbag baby Yeah, I’m just a teenage dirtbag baby Listen to Iron Maiden, baby, with me, ooh", 5),
            Phrase("Do you ever feel, like a plastic bag, Drifting through the wind, Wanting to start again?", 5),
            Phrase("Sweet dreams are made of this Who am I to disagree? I travel the world And the seven seas, Everybody’s looking for something", 5),

            )
    ),
    Stage(
        "\"UPLIFTING QUOTES 4\"",
        "HARD",
        20,
        10,
        10,
        phraseList = listOf<Phrase>(
            Phrase("Love doesn’t make the world go round. Love is what makes the ride worthwhile.", 5),
            Phrase("The greatest glory in living lies not in never falling, but in rising every time we fall", 5),
            Phrase("Success is not final; failure is not fatal: It is the courage to continue that counts.", 5),
            Phrase("The greatest mistake you can make is to be continually fearing that you’ll make one.", 5),
            Phrase("Experience is not what happens to you; it is what you do with what happens to you.", 5),
        )
    ),

    Stage(
        "\"SONG LYRICS 4\"",
        "HARD",
        12,
        9,
        20,
        phraseList = listOf<Phrase>(
            Phrase(" I’m a Barbie girl, in a Barbie world Life in plastic, it’s fantastic You can brush my hair, undress me everywhere Imagination, life is your creation", 6),
            Phrase("Just a small town girl, living in a lonely world", 6),
            Phrase("Getting born in the state of Mississippi, her papa was a copper, and her mama was a hippy", 6),
            Phrase("I fell into a burning ring of fire. i went down down down and the flames went higher", 6),
            Phrase("Sweet dreams are made of this Who am I to disagree? I travel the world And the seven seas, Everybody’s looking for something", 6),

            )
    ),
    Stage(
        "\"SONG LYRICS LONG\"",
        "EASY",
        12,
        9,
        10,
        phraseList = listOf<Phrase>(
            Phrase("Just a small town girl, living in a lonely world", 3),
            Phrase("So when you’re near me, darling can’t you hear me S. O. S", 3),
            Phrase("Billie Jean is not my lover She’s just a girl who claims that I am the one", 4),
            Phrase("We are the champions, my friends And we’ll keep on fighting ’til the end", 4),
            Phrase("Pretty woman, walkin’ down the street Pretty woman the kind I like to meet", 4),
            Phrase("What you gon’ do with all that junk? All that junk inside your trunk", 4),
            Phrase("Getting born in the state of Mississippi, her papa was a copper, and her mama was a hippy", 5),
            Phrase("I fell into a burning ring of fire. i went down down down and the flames went higher", 5),
            Phrase("When I wake up yeah I know I’m gonna be, I’m gonna be the man who wakes up next to you", 5),
            Phrase("On a dark desert highway, cool wind in my hair, warm smell of colitas rising up through the air", 5),
            Phrase("Do you ever feel, like a plastic bag, Drifting through the wind, Wanting to start again?", 5),
            Phrase("Getting born in the state of Mississippi, her papa was a copper, and her mama was a hippy", 6),
            Phrase(" Let’s go all the way tonight No regrets, just love We can dance until we die You and I We’ll be young forever", 6),
            Phrase("‘Cause I’m just a teenage dirtbag baby Yeah, I’m just a teenage dirtbag baby Listen to Iron Maiden, baby, with me, ooh", 6),
            Phrase("Don’t want to close my eyes I don’t want to fall asleep ‘Cause I’d miss you baby And I don’t want to miss a thing", 7),
            Phrase("Sweet dreams are made of this Who am I to disagree? I travel the world And the seven seas, Everybody’s looking for something", 7),

            )
    ),
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
        "#FF4BF8",
        "#4BEBFF",
        "#38FF75",
        "#FFF384",
        "#DDA9FF",
        "#FF4B7C",
        "#9FFF94",
        "#87FFE6"


/*        "#FFFF58",
        "#8F0DFF",
        "#E51919",
        "#FF6C00",
        "#FF4BF8",
        "#4BEBFF",
        "#38FF75",
        "#88FF00",
        "#FACA00",
        "#009FF8",*/
    )
}
