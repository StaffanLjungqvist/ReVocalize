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


    val successPlayer = MediaPlayer.create(context, R.raw.perfect2)
    val perfectPlayer = MediaPlayer.create(context, R.raw.success)
    val failPlayer = MediaPlayer.create(context, R.raw.fail)
    val gameOverPlayer = MediaPlayer.create(context, R.raw.fail)
    val stageCompletePlayer = MediaPlayer.create(context, R.raw.perfect3)

    var audioFile = MutableLiveData<Uri>()
    var audioReady = MutableLiveData<Boolean>()
    var mediaPlayer : MediaPlayer? = null

    val loopHandler = Handler(Looper.getMainLooper())
    val pauseHandler = Handler(Looper.getMainLooper())

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


    mediaPlayer = MediaPlayer.create(context, audioFile.value)

    mediaPlayer!!.setOnPreparedListener( MediaPlayer.OnPreparedListener {
        audioReady.value = true
        audioReady.value = false
    })
}



    //Spelar upp en del av en ljudfil beroende på startpunkt och längd vilket den får av ett sliceobjekt.
    fun playSlize(slize : Slize) {

                Log.d(
                    TAG,
                    "playing clip number ${slize.number}, with the startposition ${slize.start} and the length of ${slize.length}"
                )
                mediaPlayer?.seekTo(slize.start)
                mediaPlayer?.start()

        Handler(Looper.getMainLooper()).postDelayed({
            // Your Code
            mediaPlayer?.pause()
        }, slize.length)

                //Om en ljudfil ska spelas upp i sin helhet så körs bara ljudklippet rakt av.
            }


    fun playEffect() {

    }

    fun playFullPhrase() {
        mediaPlayer?.seekTo(0)
        mediaPlayer?.start()
    }

    fun playSuccess() {
        successPlayer.start()
    }

    fun playPerfect() {
        perfectPlayer.start()
    }

    fun playFail(){
        failPlayer.start()
    }

    fun playStageComplete(){
        stageCompletePlayer.start()
    }

    fun playGameOver(){
        gameOverPlayer.start()
    }

      fun stopAll() {
           mediaPlayer?.pause()
           perfectPlayer.release()
           successPlayer.release()
           failPlayer.release()
           stageCompletePlayer.release()
           gameOverPlayer.release()
        loopHandler.removeCallbacksAndMessages(null)
        pauseHandler.removeCallbacksAndMessages(null)
    }




    /*
Tar in en lista med slices i den ordning dom ligger i recycleview, och spelar upp en efter en
 */
    fun playSlices(slizes : List<Slize>){

        var sliceNumber = 0

        loopHandler.post(object : Runnable {

            override fun run() {

                mediaPlayer?.seekTo(slizes[sliceNumber].start)
                mediaPlayer?.start()

                pauseHandler.postDelayed({
                    // Your Code
                    mediaPlayer?.pause()
                }, slizes[sliceNumber].length)

                if (sliceNumber < (slizes.size - 1)) {
                    sliceNumber += 1

                    loopHandler.postDelayed(this, slizes[0].length)
                }
            }
        })
    }
}
