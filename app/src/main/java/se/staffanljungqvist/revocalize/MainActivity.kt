package se.staffanljungqvist.revocalize

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import se.staffanljungqvist.revocalize.databinding.ActivityMainBinding
import se.staffanljungqvist.revocalize.ui.*
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException

const val TAG = "revodebug"

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Kollar om ljudfilen har skapats/Det är första gången appen körs. Isåfall visas tutorial fragment
        val file = File(this.filesDir.toString() + "/myreq.wav")
        if(!file.exists()) {
            Log.d(TAG, "the audio file doesnt exists")
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, TutorialFragment()).addToBackStack(null).commit()
        }




/*        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, id)
            param(FirebaseAnalytics.Param.ITEM_NAME, name)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        }*/

        //Göm navigation bar och action bar
        supportActionBar?.hide()
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    class fetchData : Thread() {
        override fun run() {
            try {
                val url = ""

            } catch ( e : MalformedURLException) {
                e.printStackTrace()
            } catch (e : IOException) {
                e.printStackTrace()
            }
        }
    }




    override fun onBackPressed() {

        if(supportFragmentManager.fragments.first() is StartFragment)
        {
            super.onBackPressed()
        }

        if(supportFragmentManager.fragments.first() is InGameFragment)
        {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, ExitFragment()).addToBackStack(null)
                .commit()
        }
    }
}