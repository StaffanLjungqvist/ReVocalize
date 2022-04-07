package se.staffanljungqvist.revocalize.viewmodels

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import org.json.JSONException
import se.staffanljungqvist.revocalize.Colors
import se.staffanljungqvist.revocalize.models.Phrase
import se.staffanljungqvist.revocalize.models.Phrases
import se.staffanljungqvist.revocalize.models.Slize
import se.staffanljungqvist.revocalize.models.Stages
import java.io.IOException
import java.nio.charset.Charset

const val TAG = "revodebug"

class IngameViewModel : ViewModel() {

    //TODO Sortera stagelist efter svårighetsgrad

    var phraseIndex = 1
    var slices: List<Slize>? = null
    var currentPhrase: Phrase = Phrase("This is the default testphrase")
    var isCorrect = false
    private var guessesUsed = 0
    var points = 5
    var gameOver = false
    var bonus = 0
    var newRecord = false
    var toFragment = 0
    val loopHandler = Handler(Looper.getMainLooper())
    var phraseList = listOf<Phrase>()
    var level = 1
    var slizeDivisions = 3

    val slizeIndex: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(-1)
    }

    private val phraseLoaded: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val audioReady: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val doneIterating: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    init {
        Log.d(TAG, "skapar en ingameViewModel")
    }

    fun loadPhraseList(context: Context) {
        try {
            val jsonString = getJSONFromAssets(context)!!
             var tempPhraseList = Gson().fromJson(jsonString, Phrases::class.java)
                phraseList = tempPhraseList.phraseList

            for (phrase in phraseList) {
                Log.d(TAG, phrase.text)
            }


         //   loadUserData(context)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun loadPhrase() {
        setDivisions()
        val minLength =
        when (slizeDivisions) {
            3 -> 0
            4 -> 40
            5 -> 80
            6 -> 100
            7 -> 120
            else -> 0
        }
        currentPhrase = phraseList.random()
        while (currentPhrase.text.length < minLength) {
            currentPhrase = phraseList.random()
            Log.d(TAG, "Längd på fras : ${currentPhrase.text.length}. Minsta längd är $minLength")
        }
        Log.d(TAG, "Kom ur loopen. Längd på fras : ${currentPhrase.text.length}")

        guessesUsed = 0
        isCorrect = false
        phraseLoaded.value = true
        phraseLoaded.value = false
    }

    fun setDivisions() {
        slizeDivisions = when (level) {
            in 0..1 -> 3
            in 2..3 -> 4
            in 4..5 -> 5
            in 6..7 -> 6
            in 16..19 -> 7
            else -> {
                2
            }
        }
    }

    fun makeSlices(duration: Int) {
        //lista av sliceobjekt initialiseras
        val sliceList = mutableListOf<Slize>()
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
        slices = superShuffle(sliceList)
        Log.d(TAG, "Skapade ${sliceList.size} slices med längd $sliceLength vardera")
    }


    private fun superShuffle(list: MutableList<Slize>): List<Slize> {
        var superShuffled = false
        while (!superShuffled) {
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
        val sortedList = slices!!.sortedBy { it.number }
        bonus = 0
        if (sortedList == slices) {
            giveBonus()
            correct = true
            phraseIndex ++
            isCorrect = true
        } else {
            guessesUsed++
            points--
        }
        if (points < 1) {
            gameOver = true
        }

        if (phraseIndex > (level * 3)) {
            level++
            Log.d(TAG, "Leveling up! New level : $level")

        }
        return correct
    }


    fun calculateScore(context: Context) {
        val sharedPref = context.getSharedPreferences("userScore", Context.MODE_PRIVATE)
        val userRecord = sharedPref.getInt("user_record", 0)
        //Kollar om nuvarande poängen är bättre än poängrekordet. Ändrar därefter.
        if (points > userRecord || userRecord == 0) {
            Log.d(TAG, "Nytt rekord; $points")
            newRecord = true
            saveUserData(context)
        }
    }

    private fun giveBonus() {
        if (guessesUsed == 0) bonus = 1
        points += bonus
        toFragment = bonus
        Log.d(TAG, "Sätter bonus till $bonus")
    }

    private fun saveUserData(context: Context) {
        if (newRecord) {
            Log.d(TAG, "Saving the new record : $points")
            val sharedPref = context.getSharedPreferences("userScore", Context.MODE_PRIVATE)
            val edit = sharedPref.edit()
            edit.putInt("user_record", points)
            edit.commit()
        }
    }


    fun iterateSlices(slizes: List<Slize>) {
        doneIterating.value = false
        var sliceNumber = -1
        Log.d("revodebugmodel", "Detta är den första slizen. borde vara noll $sliceNumber")
        slizeIndex.value = sliceNumber
        loopHandler.post(object : Runnable {
            override fun run() {
                if (sliceNumber < (slizes.size - 1)) {
                    sliceNumber += 1
                    slizeIndex.value = sliceNumber
                    loopHandler.postDelayed(this, slizes[sliceNumber].length)
                } else {
                    slizeIndex.value = -2
                    slizeIndex.value = -1
                    doneIterating.value = true
                }
            }
        })
    }

    private fun getJSONFromAssets(context: Context): String? {
        val json: String?
        val charset: Charset = Charsets.UTF_8
        try {
            val myjsonFile = context.assets.open("Stages.json")
            val size = myjsonFile.available()
            val buffer = ByteArray(size)
            myjsonFile.read(buffer)
            myjsonFile.close()
            json = String(buffer, charset)
        } catch (ex: IOException) {
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