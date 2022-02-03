package se.agara.revocalize.builders

import android.util.Log
import se.agara.revocalize.models.Slize


val TAG = "kolla"

class SliceBuilder() {

    //Skapar och skickar tillbaka en lista av sliceobjekt med olika startpunkter
    fun makeSlices(duration: Int, level: Int): List<Slize> {


        //lista av sliceobjekt initialiseras
        val sliceList = mutableListOf<Slize>()

        var slizeDivisions : Int

        when (level) {
            0 -> slizeDivisions = 3
            1 -> slizeDivisions = 4
            2 -> slizeDivisions = 5
            3 -> slizeDivisions = 6
            4 -> slizeDivisions = 7
            5 -> slizeDivisions = 10
            6 -> slizeDivisions = 11
            else -> slizeDivisions = 4
        }

        val randomColors = SlizeColors.colors.shuffled().take(slizeDivisions)

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


    //Algoritm för att blanda slicelista, men att ingen slice hamnar i turordning efter varandra
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
        Log.d("kolla", "Made ${list.size} slizes in the following order : ${list}")
        return list
    }
}


object SlizeColors {

    val colors = listOf(
        "#FFFF00",
        "#8F0DFF",
        "#E51919",
        "#FF6C00",
        "#FF4BF8",
        "#4BEBFF",
        "#38FF75",
        "#FF8B71",
        "#FACA00",
        "#009FF8",
        "#53C275",
        "#009A16"
    )

}
