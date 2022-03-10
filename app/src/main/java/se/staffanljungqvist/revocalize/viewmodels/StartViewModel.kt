package se.staffanljungqvist.revocalize.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.staffanljungqvist.revocalize.builders.Stages

class StartViewModel : ViewModel() {

    var stageList = Stages.StageList

    val userDataLoaded: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    fun loadUserData(context : Context) {
        val sharedPref = context.getSharedPreferences("userScore", Context.MODE_PRIVATE)
        for (stage in Stages.StageList) {
            val points = sharedPref.getInt(stage.name, 0)
            stage.pointRecord = points
            if (points > 0) {
                stage.beatenWithRank = "BRONZE"
                stage.isComplete = true
            }
            if (points >= stage.pointsForGold) {
                stage.beatenWithRank = "GOLD"
            }
            else if (points >= stage.pointsForSilver) {
                stage.beatenWithRank = "SILVER"
            }
        }
        userDataLoaded.value = true
    }
}