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


class StartViewModel : ViewModel() {

    var stageList = listOf<StageModelClass>()

    val userDataLoaded: MutableLiveData<Boolean> by lazy {
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
        for (stage in stageList) {
            val record = sharedPref.getInt(stage.name, 0)
            stage.pointRecord = record
            if (record > 0) {
                stage.beatenWithRank = "BRONZE"
                stage.isComplete = true
            }
            if (record >= stage.pointsForGold) {
                stage.beatenWithRank = "GOLD"
            } else if (record >= stage.pointsForSilver) {
                stage.beatenWithRank = "SILVER"
            }
        }
        userDataLoaded.value = true
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