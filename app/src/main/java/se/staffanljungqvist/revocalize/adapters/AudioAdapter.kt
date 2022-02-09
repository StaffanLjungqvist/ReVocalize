package se.staffanljungqvist.revocalize.adapters

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.models.Slize



class AudioAdapter(var context : Context, val audioFile: Int) {

    val successPlayer = MediaPlayer.create(context, R.raw.success)


    //Spelar upp en del av en ljudfil beroende på startpunkt och längd vilket den får av ett sliceobjekt.
    fun playAudio(slize : Slize? = null, playAll : Boolean = false) {

        val mediaPlayer = MediaPlayer.create(context, audioFile)

        //Om slice som ska spelas upp är en del av en slicelista-uppspelning, så läggs 90 millisekunder till för att fylla upp luckor mellan slices.
        if (slize != null) {
            //    var length = if (playAll) (slice.length + 90) else slice.length
            Log.d("kolla", "playing clip number ${slize.number}, with the startposition ${slize.start} and the length of ${slize.length}")
            mediaPlayer.seekTo(slize.start)
            mediaPlayer.start()
            Handler().postDelayed({
                mediaPlayer.pause()
                mediaPlayer.release()
            }, slize.length + 90)

            //Om en ljudfil ska spelas upp i sin helhet så körs bara ljudklippet rakt av.
        } else {
            Log.d("kolla", "No slice. Playing the full clip")
            mediaPlayer.start()
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