package se.staffanljungqvist.revocalize.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import org.json.JSONException
import se.staffanljungqvist.revocalize.models.StageModelClass
import se.staffanljungqvist.revocalize.models.Stages
import java.io.IOException
import java.nio.charset.Charset


class StageSelectViewModel : ViewModel() {

    var stageList = listOf<StageModelClass>()

    var hasBeatenGame: Int = 0

    val userDataLoaded: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val theGameWasBeaten: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    fun loadStages(context: Context) {

        try {
            val jsonString = getJSONFromAssets(context)!!
            val stages = Gson().fromJson(jsonString, Stages::class.java)
            stageList = stages.stageList
            loadUserData(context)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun loadUserData(context: Context) {
        val sharedPref = context.getSharedPreferences("userScore", Context.MODE_PRIVATE)

        var stagesBeaten = 0
        var stagesBeatenWithSilver = 0
        var stagesBeatenWithGold = 0

        for (stage in stageList) {
            val record = sharedPref.getInt(stage.name, 0)
            if (record > 0) {
                stage.beatenWithRank = "BRONZE"
                stage.isComplete = true
                stagesBeaten += 1
            }
            if (record >= stage.pointsForGold) {
                stage.beatenWithRank = "GOLD"
                stagesBeatenWithGold += 1
            } else if (record >= stage.pointsForSilver) {
                stage.beatenWithRank = "SILVER"
                stagesBeatenWithSilver += 1
            }
        }

        if (stagesBeaten >= stageList.size) {
            if (stagesBeatenWithGold >= stageList.size) {
                hasBeatenGame = 3
            } else if ((stagesBeatenWithSilver + stagesBeatenWithGold) >= stageList.size ) {
                hasBeatenGame = 2
            } else  {
                hasBeatenGame = 1
            }
        }
        userDataLoaded.value = true

        val userBeatenRank = sharedPref.getInt("hasBeatenGameWith", 0)

        if (userBeatenRank == 3) {
            return
        }

        if (hasBeatenGame == 3) {
            val edit = sharedPref.edit()
            edit.putInt("hasBeatenGameWith", 3)
            edit.commit()
            theGameWasBeaten.value = true
            return
        }

        if (userBeatenRank == 2) {
            return
        }

        if (hasBeatenGame == 2) {
            val edit = sharedPref.edit()
            edit.putInt("hasBeatenGameWith", 2)
            edit.commit()
            theGameWasBeaten.value = true
            return
        }

        if (userBeatenRank == 1) {
            return
        }

        if (hasBeatenGame == 1) {
            val edit = sharedPref.edit()
            edit.putInt("hasBeatenGameWith", 1)
            edit.commit()
            theGameWasBeaten.value = true
            return
        }
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


}