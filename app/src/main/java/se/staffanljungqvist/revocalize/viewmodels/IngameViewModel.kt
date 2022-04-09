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
import java.io.IOException
import java.nio.charset.Charset

const val TAG = "revodebug"

class IngameViewModel : ViewModel() {

    //TODO Sortera stagelist efter svårighetsgrad

    var phraseIndex = 0
    var slices: List<Slize>? = null
    var currentPhrase: Phrase = Phrase("This is the default testphrase")
    var isCorrect = false
    private var guessesUsed = 0
    var gameOver = false
    var bonus = 0
    var newRecord = false
    var toFragment = 0
    val loopHandler = Handler(Looper.getMainLooper())
    var phraseList = listOf<Phrase>()
    var slizeDivisions = 3
    var levelUp = false
    var points = 5

    val observedPoints: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(5)
    }

    val numberOfphrasesDone: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

    var level = 0
    val observedlevel: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

    val loadUI: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val slizeIndex: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val phraseLoaded: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val audioReady: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val doneIterating: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }




    fun loadPhraseList(context: Context) {
        try {
            val jsonString = getJSONFromAssets(context)!!
             var tempPhraseList = Gson().fromJson(jsonString, Phrases::class.java)
                phraseList = tempPhraseList.phraseList

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun loadPhrase() {
        val minLength: Int
        val maxLength: Int
        slizeDivisions = levels[level][phraseIndex]
        when (slizeDivisions) {
            3 -> {
                minLength = 0
                maxLength = 60
            }
            4 -> {
                minLength = 40
                maxLength = 80
            }
            5 -> {
                minLength = 80
                maxLength = 200
            }
            6 -> {
                minLength = 100
                maxLength = 200
            }
            7 -> {
                minLength = 120
                maxLength = 200
            }
            else -> {
                minLength = 0
                maxLength = 200
            }
        }
        currentPhrase = phraseList.random()
        while (currentPhrase.text.length > maxLength || currentPhrase.text.length < minLength ) {
            currentPhrase = phraseList.random()
        }
        guessesUsed = 0
        isCorrect = false
        phraseLoaded.value = true
        phraseLoaded.value = false
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

    fun checkAnswer(): Boolean {
        val sortedList = slices!!.sortedBy { it.number }
        return sortedList == slices
    }

    //Kollar om listan är i rätt ordning
    //Kollar om det va sista frasen. Sätter isåfall stageComplete
    //Om gissningen är fel ökas antal gissningar, och ett poäng dras bort.
    //Kollar om poängen är slut. Om så är fallet sätts gameOver
    fun makeGuess(): Boolean {
        var iscorrect: Boolean
        bonus = 0
        if (checkAnswer()) {
            Log.d("gamedebug", "right answer, guesses is now $guessesUsed")
            giveBonus()
            numberOfphrasesDone.value = numberOfphrasesDone.value?.plus(1)
            iscorrect = true
        } else {
            guessesUsed++
            Log.d("gamedebug", "wrong answer, guesses is now $guessesUsed")
            points--
            iscorrect = false
        }
        if (points < 1) {
            gameOver = true
        }
        observedPoints.value = points
        return iscorrect
    }

    fun advancePhrase() {
        Log.d("gamedebug", "advancing stage, guesses is now $guessesUsed")
        if (phraseIndex == 4) {
            levelUp = true
            level ++
            observedlevel.value = level
            Log.d(TAG, "Leveling up! New level : $level")
            phraseIndex = 0
        } else {
            phraseIndex++
        }
    }

    fun calculateScore(context: Context) {

        val sharedPref = context.getSharedPreferences("userScore", Context.MODE_PRIVATE)
        val userRecord = sharedPref.getInt("user_record", 0)
        val userScore = numberOfphrasesDone.value
        Log.d(TAG, "Game over. Antal fraser klarade : $userScore. Tidigare rekord: $userRecord")
        //Kollar om nuvarande poängen är bättre än poängrekordet. Ändrar därefter.
        if (userScore != null) {
            if (userScore + 1 > userRecord || userRecord == 0) {
                Log.d(TAG, "Nytt rekord; $userScore klarade fraser")
                newRecord = true
                saveUserData(context)
            }
        }
    }

    private fun giveBonus() {
        Log.d("gamedebug", "Giving bonus. guesses used : $guessesUsed")
        if (guessesUsed == 0) bonus = 1
        points += bonus
        toFragment = bonus
        Log.d(TAG, "Sätter bonus till $bonus")
        advancePhrase()
    }

    private fun saveUserData(context: Context) {
        val userScore = numberOfphrasesDone.value
        if (userScore != null) {
            if (newRecord) {
                Log.d(TAG, "Saving the new record : $userScore")
                val sharedPref = context.getSharedPreferences("userScore", Context.MODE_PRIVATE)
                val edit = sharedPref.edit()
                edit.putInt("user_record", userScore + 1)
                edit.commit()
            }
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

    var levels = listOf(
        listOf(3, 3, 3, 3, 3, 3),
        listOf(4, 4, 4, 4, 4, 4),
        listOf(5, 5, 5, 5, 5, 5),
        listOf(6, 6, 6, 6, 6, 6),
        listOf(7, 7, 7, 7, 7, 7),
        listOf(8, 8, 8, 8, 8, 8),
        listOf(3, 4, 5, 5, 5),
        listOf(3, 4, 5, 5, 6),
        listOf(4, 4, 5, 6, 6),
        listOf(4, 5, 5, 6, 6),
        listOf(4, 5, 6, 6, 6),
        listOf(4, 5, 6, 6, 7),
        listOf(5, 5, 6, 6, 7),
        listOf(4, 5, 5, 6, 6),
        listOf(4, 5, 5, 6, 7),
        listOf(4, 5, 6, 6, 7),
        listOf(4, 5, 6, 7, 7),
        listOf(5, 5, 6, 7, 7),
        listOf(5, 5, 6, 7, 8),
        listOf(5, 6, 6, 7, 8),
    )
}