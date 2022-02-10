package se.staffanljungqvist.revocalize.adapters

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.models.Slize
import java.io.File

val TAG = "revodebug"

class AudioAdapter(var context : Context) {


    val successPlayer = MediaPlayer.create(context, R.raw.success)
    var audioFile = MutableLiveData<Uri>()
    var fileLoaded = MutableLiveData<Boolean>()


    fun getDuration() : Int {
        if (audioFile != null) {
            val mediaPlayer = MediaPlayer.create(context, audioFile.value)
            val duration = mediaPlayer.duration
            mediaPlayer.release()
            return duration
        } else {
            Log.d(TAG, "Audiofile not loaded")
            return 0
        }
    }

fun loadAudio(filePath : String) {
    audioFile.value = Uri.parse(filePath)
    Log.d(TAG, "AA Parsing file to URI : ${audioFile.value}")
}



    //Spelar upp en del av en ljudfil beroende på startpunkt och längd vilket den får av ett sliceobjekt.
    fun playAudio(slize : Slize? = null, playAll : Boolean = false) {
        if (audioFile != null) {
            val mediaPlayer = MediaPlayer.create(context, audioFile.value)
            Log.d(TAG, "AA skapade mediaplayer från fil ${audioFile}")
            //Om slice som ska spelas upp är en del av en slicelista-uppspelning, så läggs 90 millisekunder till för att fylla upp luckor mellan slices.
            if (slize != null) {
                Log.d(
                    TAG,
                    "playing clip number ${slize.number}, with the startposition ${slize.start} and the length of ${slize.length}"
                )
                mediaPlayer.seekTo(slize.start)
                mediaPlayer.start()
                Handler().postDelayed({
                    mediaPlayer.pause()
                    mediaPlayer.release()
                }, slize.length + 90)

                //Om en ljudfil ska spelas upp i sin helhet så körs bara ljudklippet rakt av.
            } else {
                Log.d(TAG, "No slice. Playing the full clip")
                mediaPlayer.start()
            }

        }
    }

    fun playSuccess() {
        successPlayer.start()
    }

    /*
Tar in en lista med slices och spelar upp alla en efter en
 */
    fun playSlices(slizes : List<Slize>){

        val mainHandler = Handler(Looper.getMainLooper())
        var sliceNumber = 0

        mainHandler.post(object : Runnable {
            override fun run() {

                playAudio(slizes[sliceNumber], true)

                if (sliceNumber < (slizes.size - 1)) {
                    sliceNumber += 1

                    mainHandler.postDelayed(this, slizes[0].length)
                }
            }
        })
    }
}