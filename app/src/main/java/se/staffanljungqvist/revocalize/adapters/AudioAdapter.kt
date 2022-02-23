package se.staffanljungqvist.revocalize.adapters

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.models.Slize

val TAG = "revodebug"

class AudioAdapter(var context : Context) {


    val successPlayer = MediaPlayer.create(context, R.raw.success)
    var audioFile = MutableLiveData<Uri>()
    var fileTransformedFromUriToFile = MutableLiveData<Boolean>()
    var mediaPlayer : MediaPlayer? = null

    //En mediaplayer instans skapas för att läsa av längden på ljudklippet, skickar tillbaka resultatet och förstörs sen.
    fun getDuration() : Int {
        if (audioFile != null) {
            val mediaPlayer = MediaPlayer.create(context, audioFile.value)
            val duration = mediaPlayer.duration
            mediaPlayer.release()
            Log.d(TAG, "Längden på ljudklippet : ${duration}")
            return duration
        } else {
            Log.d(TAG, "Audiofile not loaded")
            return 0
        }
    }

fun loadAudio(filePath : String) {
    audioFile.value = Uri.parse(filePath)
    Log.d(TAG, "AA Uri omgord till File : ${audioFile.value}")
    fileTransformedFromUriToFile.value = true
    mediaPlayer = MediaPlayer.create(context, audioFile.value)
}



    //Spelar upp en del av en ljudfil beroende på startpunkt och längd vilket den får av ett sliceobjekt.
    fun playSlize(slize : Slize? = null) {
        if (mediaPlayer != null) {
            "trying to play clip number ${slize?.number}, with the startposition ${slize?.start} and the length of ${slize?.length}"
            //Om slice som ska spelas upp är en del av en slicelista-uppspelning, så läggs 90 millisekunder till för att fylla upp luckor mellan slices.
            if (slize != null) {
                Log.d(
                    TAG,
                    "playing clip number ${slize.number}, with the startposition ${slize.start} and the length of ${slize.length}"
                )
                mediaPlayer!!.seekTo(slize.start)
                mediaPlayer!!.start()
                Handler().postDelayed({
                    mediaPlayer!!.pause()
                }, slize.length)

                //Om en ljudfil ska spelas upp i sin helhet så körs bara ljudklippet rakt av.
            }
        }
    }

    fun playAudio() {
        mediaPlayer!!.seekTo(0)
        mediaPlayer!!.start()
    }

    fun playSuccess() {
        successPlayer.start()
    }

    /*
Tar in en lista med slices i den ordning dom ligger i recycleview, och spelar upp en efter en
 */
    fun playSlices(slizes : List<Slize>){

        val mainHandler = Handler(Looper.getMainLooper())
        var sliceNumber = 0

        mainHandler.post(object : Runnable {
            override fun run() {

                playSlize(slizes[sliceNumber])

                if (sliceNumber < (slizes.size - 1)) {
                    sliceNumber += 1

                    mainHandler.postDelayed(this, slizes[0].length)
                }
            }
        })
    }
}