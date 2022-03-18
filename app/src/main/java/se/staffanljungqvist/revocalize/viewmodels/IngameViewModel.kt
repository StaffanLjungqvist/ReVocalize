package se.staffanljungqvist.revocalize.viewmodels

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import org.json.JSONException
import se.staffanljungqvist.revocalize.builders.Colors
import se.staffanljungqvist.revocalize.models.Phrase
import se.staffanljungqvist.revocalize.models.Slize
import se.staffanljungqvist.revocalize.models.StageModelClass
import se.staffanljungqvist.revocalize.models.Stages
import java.io.IOException
import java.nio.charset.Charset

val TAG = "revodebug"

class IngameViewModel : ViewModel() {

    //TODO Sortera stagelist efter svårighetsgrad

    var currentStage: StageModelClass = StageModelClass(0, "No Stage", 0, 0, 100)
    var stageIndex = 0
    var phraseIndex = 0
    var slizes: List<Slize>? = null
    var currentPhrase = Phrase("Text Not Available", 1, listOf<Slize>())
    var isCorrect = false
    var guessesUsed = 0
    var points = 100
    var rank = "BRONZE"
    var stageComplete = false
    var gameOver = false
    var bonus = 0
    var userRecord = 0
    var newRecord = false
    var toFragment = 0
    val loopHandler = Handler(Looper.getMainLooper())

    val slizeIndex: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(-1)
    }

    val phraseLoaded: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val audioReady: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val donePlaying: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    init {
        Log.d(TAG, "skapar en ingameViewModel")
    }



    fun loadStage(context : Context, stageId: Int, record : Int) {
        Log.d(TAG, "Loading stage number ${stageId}")
        try {
            val jsonString = getJSONFromAssets(context)!!
            Log.d(TAG, "Fick en json string : ${jsonString}")
            val stages = Gson().fromJson(jsonString, Stages::class.java)
            Log.d(TAG, "returnerar en lista med storleken ${stages.stageList.size}")
            currentStage = stages.stageList[stageIndex]
            Log.d(TAG, "Satte currentStage till ${stages.stageList[stageId].name}")

        }  catch (e: JSONException) {
            e.printStackTrace()
        }
        points = currentStage.startingPoints
        userRecord = record
    }

    fun loadPhrase() {
        guessesUsed = 0
        isCorrect = false

            if (phraseIndex < currentStage!!.phraseList.size) {
                Log.d(TAG, "Laddar in fras ${currentStage!!.phraseList[phraseIndex]}")
                currentPhrase = currentStage!!.phraseList[phraseIndex]
                phraseIndex++
            } else {
                stageComplete = true
            }
            phraseLoaded.value = true
            phraseLoaded.value = false

    }

    fun makeSlices(duration: Int) {
        //lista av sliceobjekt initialiseras
        val sliceList = mutableListOf<Slize>()
        var slizeDivisions = currentPhrase.slizediv
        val randomColors = Colors.colors.take(slizeDivisions).shuffled()
        val sliceLength = (duration / slizeDivisions)
        for (number in 1..slizeDivisions) {
            sliceList.add(
                Slize(
                    number,
                    (number - 1) * sliceLength,
                    sliceLength.toLong(),
                    randomColors[(number - 1)],
                )
            )
        }
        slizes = superShuffle(sliceList)
        Log.d(TAG, "Skapade ${sliceList.size} slices med längd $sliceLength vardera")
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
        return list
    }

    //Kollar om listan är i rätt ordning
    //Kollar om det va sista frasen. Sätter isåfall stageComplete
    //Om gissningen är fel ökas antal gissningar, och ett poäng dras bort.
    //Kollar om poängen är slut. Om så är fallet sätts gameOver
    fun makeGuess(): Boolean {
        var correct = false
        var sortedList = slizes!!.sortedBy { it.number }
        bonus = 0
        if (sortedList.equals(slizes)) {
            giveBonus()
            correct = true
            isCorrect = true
            if (phraseIndex == currentStage!!.phraseList.size) stageComplete = true
        } else {
            guessesUsed++
            points--
        }
        if (points < 1) gameOver = true
        return correct
    }


    fun calculateScore(context : Context) {
        //Kollar om nuvarande poängen är bättre än poängrekordet. Ändrar därefter.
        if (userRecord > currentStage.pointRecord || currentStage.pointRecord == 0) {
            Log.d(TAG, "Nytt rekord")
            newRecord = true
            saveUserData(context)
        }
        /*Bestämmer vilken rank som sätts beroende på nuvarandande ranks specifieringar
        Om ranken är högra än tidigare så sparas den över
        */
        if (points >= currentStage!!.pointsForGold) {
            rank = "GOLD"
        } else if (points >= currentStage!!.pointsForSilver) {
            rank = "SILVER"
        } else {
            rank = "BRONZE"
        }
        if (rank == "GOLD") {
            currentStage!!.beatenWithRank = rank
        } else if (rank == "SILVER" && currentStage!!.beatenWithRank != "GOLD") {
            currentStage!!.beatenWithRank = rank
        } else if (rank == "BRONZE" && currentStage!!.beatenWithRank != "SILVER") {
            currentStage!!.beatenWithRank = rank
        }
        currentStage!!.isComplete = true

    }

    fun giveBonus() {
        if (guessesUsed == 0) bonus = 1
        points += bonus
        toFragment = bonus
        Log.d(TAG, "Sätter bonus till $bonus")
    }

    fun saveUserData(context : Context) {
        val sharedPref = context.getSharedPreferences("userScore", Context.MODE_PRIVATE)
        var edit = sharedPref.edit()
        edit.putInt(currentStage.name, userRecord)
        edit.commit()
        Log.d(TAG, "Sparade poängen $points till nivån ${currentStage.name}")
    }


    fun playSlices(slizes : List<Slize>){
        donePlaying.value = false
        var sliceNumber = -1
        Log.d("revodebugmodel", "Detta är den första slizen. borde vara noll ${sliceNumber}")
        slizeIndex.value = sliceNumber

        loopHandler.post(object : Runnable {
            override fun run() {
                if (sliceNumber < (slizes.size - 1)) {
                    sliceNumber += 1
                    Log.d("revodebugmodel", "ändrade slize till ${sliceNumber}")
                    slizeIndex.value = sliceNumber
                    loopHandler.postDelayed(this, slizes[sliceNumber].length)
                } else {
                    slizeIndex.value = -2
                    slizeIndex.value = -1
                    Log.d("revodebugmodel", "ändrade slize till ${slizeIndex.value}")
                    donePlaying.value = true
                }
            }
        })
    }

    private fun getJSONFromAssets(context : Context) : String? {
        var json : String? = null
        val charset : Charset = Charsets.UTF_8
        try {
            val myjsonFile = context.assets.open("Stages.json")
            val size = myjsonFile.available()
            val buffer = ByteArray(size)
            myjsonFile.read(buffer)
            myjsonFile.close()
            json = String(buffer, charset)
        } catch (ex: IOException){
            ex.printStackTrace()
            return null
        }
        return json
    }

    override fun onCleared() {
        Log.d(TAG, "destroying viewmodel")
        super.onCleared()
    }
}