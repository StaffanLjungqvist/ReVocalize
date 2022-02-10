package se.staffanljungqvist.revocalize.adapters

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.util.*

class TTSAdapter(val context : Context) : TextToSpeech.OnInitListener {

    private var mAudioFilename = ""
    private val mUtteranceID = "totts"
    private var tts = TextToSpeech(context, this)
    private val EXTERNAL_STORAGE_PERMISSION_CODE = 23
    var testFile : File
    var path = context.filesDir.toString() + "/myreq.wav"
    var audioFileCreated = MutableLiveData<Boolean>()
    var audioFileWritten = MutableLiveData<Boolean>()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {

            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                Log.e(TAG, "The language specified is not supported")
            }
            Log.d(TAG, "TTS är nu initialiserad")
            createAudioFile()
        } else {
            Log.e(TAG, "TTS Initialization Failed")
        }
    }


//Todo : Lägg in i onInit?
    init {
        testFile = File(path)
    }


        //Skapar en tom fil i minnet vilket data senare kommer att skrivas till.
     fun createAudioFile() {
        // Create audio file location
        val sddir = File(Environment.getExternalStorageDirectory().toString() + "/My File/")
        sddir.mkdir()
        mAudioFilename = sddir.absolutePath.toString() + "/" + mUtteranceID + ".wav"
        testFile = File(path)
        Log.d(TAG, "tts skapade fil : ${mAudioFilename}")
        audioFileCreated.value = true
    }


    //Tar text och gör om till en ljudfil, skrivs till skapade filen i minnet.
     fun saveToAudioFile(text : String) {

         //Tar ut alla röster tillgängliga i mobilen med engelsk språk, och sätter slumpmässigt till tts.
         val voices = tts.voices.filter { it.locale.language == "en" }
         val voice = voices.random()
         tts.setVoice(voice)
        tts.setSpeechRate("0.7".toFloat())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


            val listener = ttsUtteranceListener()
            tts.setOnUtteranceProgressListener(listener)


            //Skapar filen.
            //Todo : Obserera när ljudfilen är färdigprocesserad istället för statisk laddtid.
            tts!!.synthesizeToFile(text, null, testFile, mUtteranceID)

            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post(object : Runnable {
                var number = 0
                override fun run() {
                    if (number < 1) {
                        number++
                        Log.d(TAG, "Jag förhalar")
                        mainHandler.postDelayed(this, 4500)
                    } else {
                        Log.d(TAG, "handler är färdig")
                        Log.d(TAG, "tts Skrev till " + testFile.absolutePath)
                        audioFileWritten.value = true

                    }
                }
            })



        } else {
            val hm = HashMap<String, String>()
            hm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,mUtteranceID)
            tts!!.synthesizeToFile("testing", hm, mAudioFilename)
            Log.d(TAG,"tts fulskrev  till" + mAudioFilename)
            audioFileWritten.value = true
        }


    }







    //Todo : Få pli på denna
    class ttsUtteranceListener : UtteranceProgressListener() {
        override fun onStart(start : String) {
            Log.d(TAG, "TTS Startar synthesize file")
        }

        override fun onDone(uttranceID : String) {
            Log.d(TAG, "TTS syntesize file färdig.")
        }

        override fun onError(uttranceID : String) {
            TODO("Not yet implemented")
        }
    }

    //Används endast fil felsökning
    fun readAudioFile() {
        try {
            val mp = MediaPlayer.create(context, Uri.parse(path))
            Log.d(TAG, "playing audiofile from $path")
            mp.start()
        } catch (e: java.lang.Exception) {
            Log.d(TAG, "Something went wrong : ${e}")
        }
    }
}





