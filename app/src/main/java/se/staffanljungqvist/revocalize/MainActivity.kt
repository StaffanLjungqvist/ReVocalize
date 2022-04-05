package se.staffanljungqvist.revocalize

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import se.staffanljungqvist.revocalize.databinding.ActivityMainBinding
import se.staffanljungqvist.revocalize.ui.*
import java.io.File

val TAG = "revodebug"

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val file = File(this.filesDir.toString() + "/myreq.wav")
      //  if(!file.exists()) {
            Log.d(TAG, "the audio file doesnt exists")
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, TutorialFragment()).addToBackStack(null).commit()
   //     }




/*        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, id)
            param(FirebaseAnalytics.Param.ITEM_NAME, name)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        }*/

        //Göm navigation bar och action bar
        getSupportActionBar()?.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }




    override fun onBackPressed() {

        if(supportFragmentManager.fragments.first() is StartFragment)
        {
            super.onBackPressed()
        }
        if(supportFragmentManager.fragments.first() is StageSelectFragment)
        {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, StartFragment()).commit()
        }
        if(supportFragmentManager.fragments.first() is InGameFragment)
        {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, ExitFragment()).addToBackStack(null)
                .commit()
        }
    }
}