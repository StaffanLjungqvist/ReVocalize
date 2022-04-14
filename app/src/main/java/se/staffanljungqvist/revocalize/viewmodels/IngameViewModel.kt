package se.staffanljungqvist.revocalize.viewmodels

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONException
import se.staffanljungqvist.revocalize.Colors
import se.staffanljungqvist.revocalize.PowerUp
import se.staffanljungqvist.revocalize.models.Phrase
import se.staffanljungqvist.revocalize.models.Phrases
import se.staffanljungqvist.revocalize.models.Slize
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

const val TAG = "revodebug"

class IngameViewModel : ViewModel() {

    //TODO Sortera stagelist efter svårighetsgrad

    var phraseIndex = 0
    var slices: List<Slize>? = null
    var currentPhrase: Phrase = Phrase("PHRASETEXT MISSING")
    val bonusPowers = listOf(PowerUp.REMOVE, PowerUp.CLICK, PowerUp.TRY)
    var bonusIndex = 0

    private var guessesUsed = 0
    var bonus = false
    var levelUp = true
    var gameOver = false
    var newRecord = false
    var isCorrect = false
    val loopHandler = Handler(Looper.getMainLooper())
    var phraseList = listOf<Phrase>()
    var slizeDivisions = 3

    var tries = 3
    var startingTries = 3
    var powerPoints = 0
    var totalPoints = 0
    var points = 100
    var userHighScore = 0

    var levelUpIncrementPoints = 100
    var pointsToLevelUp = 200
    var gameComplete = false

    var powerTryAmount = -1
    var powerRemoveAmount = -1
    var powerClickAmount = -1

    val powers = listOf(PowerUp.TRY, PowerUp.REMOVE, PowerUp.CLICK)

    val observedTries: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(5)
    }

    val observedPowerPoints: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(2)
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

    val showSuccess: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val showLevelUp: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val showNewPower: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val showInventory: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val powersAvailable: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }


    val powerUpUsed: MutableLiveData<PowerUp> by lazy {
        MutableLiveData<PowerUp>()
    }

    val listenMode: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val playMode: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val clickMode: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }


    fun downloadPhrases(context: Context) {
        val url =
            "https://firebasestorage.googleapis.com/v0/b/revocalize-bf576.appspot.com/o/phrases.json?alt=media&token=3668417e-3ad4-4fd2-82e3-d1ffd1db073f"
        val queue = Volley.newRequestQueue(context)

        val stringRequest = VolleyUTF8EncodingStringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                loadPhraseList(response)
            }, Response.ErrorListener { error ->
                Log.d(TAG, error.toString())
            })
        queue.add(stringRequest)
    }

    private fun loadPhraseList(jsonString: String) {
        Log.d(TAG, "Försöker ladda")
        try {
            var tempPhraseList = Gson().fromJson(jsonString, Phrases::class.java)
            phraseList = tempPhraseList.phraseList
            loadPhrase()
        } catch (e: JSONException) {
            Log.d(TAG, "Nåt fel på jsonfil")
            e.printStackTrace()
        }
    }

    fun loadPhrase() {
        val div = getTextlengthAttributes()

        currentPhrase = phraseList.random()
        while (currentPhrase.text.length > div[1] || currentPhrase.text.length < div[0]) {
            currentPhrase = phraseList.random()
        }
        guessesUsed = 0
        tries = startingTries
        bonus = false
        points = 100

        observedTries.value = tries
        if (phraseIndex == 0) powerPoints += 2
        observedPowerPoints.value = powerPoints
        isCorrect = false
        Log.d(TAG, "Laddade in fras med storlek ${currentPhrase.text.length}")
        phraseLoaded.value = true
        Log.d(TAG, "Laddade in fras : ${currentPhrase.text}}")
        phraseLoaded.value = false
    }

    private fun advancePhrase() {
        Log.d(TAG, "advancing stage, guesses is now $guessesUsed")
        if (totalPoints >= pointsToLevelUp) {
            levelUp = true
            level++
            if (level == 5) {
                Log.d(TAG, "Sätter gamecomplete till true")
                gameComplete = true
                gameOver = true
                Log.d(TAG, "YOU BEAT THE GAME!")

            }
            setHelpers()
            observedlevel.value = level
            Log.d(TAG, "Leveling up! New level : $level")
            phraseIndex = 0
            pointsToLevelUp += levelUpIncrementPoints
        } else {
            levelUp = false
            phraseIndex++
        }
    }

    fun setHelpers() {

        powerTryAmount = 1

        powerClickAmount = when (level) {
            1 -> -1
            else -> 1
        }
        powerRemoveAmount = when (level) {
            1 -> -1
            2 -> -1
            else -> 1
        }
    }

    fun usePowerUp(powerUp: PowerUp) {
        when (powerUp) {
            PowerUp.REMOVE -> {
                if (powerRemoveAmount < 1) return
                slizeDivisions--
                powerRemoveAmount--
            }
            PowerUp.TRY -> {
                if (powerTryAmount < 1) return
                tries += 3
                observedTries.value = tries
                powerTryAmount--
            }
            PowerUp.CLICK -> {
                if (powerTryAmount < 1) return
                clickMode.value = true
                listenMode.value = false
                powerClickAmount--
            }
        }
        Log.d(TAG, "used helper ${powerUp}")
        powerUpUsed.value = powerUp
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

    fun checkAnswer(): Boolean {
        val sortedList = slices!!.sortedBy { it.number }
        return sortedList == slices
    }

    fun makeGuess(): Boolean {
        var iscorrect: Boolean
        if (checkAnswer()) {
            Log.d(TAG, "right answer, guesses is now $guessesUsed")
            if (guessesUsed == 0) {
                bonus = true
                points += 50
            }
            totalPoints += points
            advancePhrase()
            numberOfphrasesDone.value = numberOfphrasesDone.value?.plus(1)
            iscorrect = true
        } else {
            if (points > 25) points -= 25
            guessesUsed++
            Log.d(TAG, "wrong answer, guesses is now $guessesUsed")
            tries--
            iscorrect = false
        }
        if (tries < 1) {
            gameOver = true
        }
        observedTries.value = tries
        return iscorrect
    }

    fun giveBonus() {
        Log.d("gamedebug", "Giving bonus. guesses used : $guessesUsed")

        points += 50

        //Ger en random bonuspower
/*        bonusIndex = (0..2).random()
        showNewPower.value = true
        showNewPower.value = false
        when (bonusIndex) {
            0 -> powerRemoveAmount++
            1 -> powerClickAmount++
            2 -> powerTryAmount++
        }*/
    }

    fun calculateScore(context: Context) {
        val userRecord = getUserHighScore(context)
        val userScore = totalPoints
        Log.d(TAG, "Game over. Poäng : $userScore. Tidigare rekord: $userRecord")
        //Kollar om nuvarande poängen är bättre än poängrekordet. Ändrar därefter.
        if (userScore != null) {
            if (userScore > userRecord || userRecord == 0) {
                Log.d(TAG, "Nytt rekord; $userScore poäng")
                newRecord = true
                saveUserData(context)
            }
        }
    }

    fun getUserHighScore(context: Context): Int {
        val sharedPref = context.getSharedPreferences("userScore", Context.MODE_PRIVATE)
        userHighScore = sharedPref.getInt("user_record", 0)
        return userHighScore
    }

    private fun saveUserData(context: Context) {
        val userScore = totalPoints
        if (userScore != null) {
            if (newRecord) {
                Log.d(TAG, "Saving the new record : $userScore")
                val sharedPref = context.getSharedPreferences("userScore", Context.MODE_PRIVATE)
                val edit = sharedPref.edit()
                edit.putInt("user_record", userScore)
                edit.commit()
            }
        }
    }

    fun getTextlengthAttributes(): List<Int> {
        val minLength: Int
        val maxLength: Int


        slizeDivisions = 2
        when (slizeDivisions) {
            2 -> {
                minLength = 0
                maxLength = 60
            }
            3 -> {
                minLength = 0
                maxLength = 60
            }
            4 -> {
                minLength = 40
                maxLength = 100
            }
            5 -> {
                minLength = 80
                maxLength = 130
            }
            6 -> {
                minLength = 100
                maxLength = 130
            }
            7 -> {
                minLength = 100
                maxLength = 130
            }
            8 -> {
                minLength = 100
                maxLength = 130
            }
            else -> {
                minLength = 120
                maxLength = 300
            }
        }
        return listOf(minLength, maxLength)
    }

    fun iterateSlices(slizes: List<Slize>) {
        playMode.value = false
        doneIterating.value = false
        var sliceNumber = -1
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
                    playMode.value = true
                }
            }
        })
    }

    fun getJSONFromAssets(context: Context) {
        val json: String?
        val charset: Charset = Charsets.UTF_8
        try {
            val myjsonFile = context.assets.open("phrases.json")
            val size = myjsonFile.available()
            val buffer = ByteArray(size)
            myjsonFile.read(buffer)
            myjsonFile.close()
            json = String(buffer, charset)
            loadPhraseList(json)
        } catch (ex: IOException) {
            ex.printStackTrace()

        }

    }

    fun prepareText(text: String): String {
        val preparedtext = text.uppercase().trim()
        //    preparedtext.replace("Â ", "’")
        return preparedtext
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

    override fun onCleared() {
        Log.d(TAG, "destroying viewmodel")
        super.onCleared()
    }

}

class VolleyUTF8EncodingStringRequest(
    method: Int, url: String, private val mListener: Response.Listener<String>,
    errorListener: Response.ErrorListener
) : Request<String>(method, url, errorListener) {

    override fun deliverResponse(response: String) {
        mListener.onResponse(response)
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
        var parsed = ""

        val encoding = charset(HttpHeaderParser.parseCharset(response.headers))

        try {
            parsed = String(response.data, encoding)
            val bytes = parsed.toByteArray(encoding)
            parsed = String(bytes, charset("UTF-8"))

            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            return Response.error(ParseError(e))
        }
    }
}