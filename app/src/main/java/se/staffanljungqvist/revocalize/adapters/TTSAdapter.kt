package se.staffanljungqvist.revocalize.adapters

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import se.staffanljungqvist.revocalize.R
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



    init {
        testFile = File(path)
    }



     fun createAudioFile() {
        // Create audio file location
        val sddir = File(Environment.getExternalStorageDirectory().toString() + "/My File/")
        sddir.mkdir()
        mAudioFilename = sddir.absolutePath.toString() + "/" + mUtteranceID + ".wav"
        testFile = File(path)
        Log.d(TAG, "tts skapade fil : ${mAudioFilename}")
        audioFileCreated.value = true
    }

     fun saveToAudioFile(text : String) {

         val voices = tts.voices.filter { it.locale.language == "en" }
         val voice = voices.random()
         var local = voice.locale
         var country = local.language
         Log.d(TAG, country)

         Log.d(TAG, "Voices available : ${voice}")
         tts.setVoice(voice)
//AU, IN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            val listener = ttsUtteranceListener()
            tts.setOnUtteranceProgressListener(listener)
            tts.setSpeechRate("0.7".toFloat())
            tts!!.synthesizeToFile(text, null, testFile, mUtteranceID)

            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post(object : Runnable {
                var number = 0
                override fun run() {
                    if (number < 2) {
                        number++
                        Log.d(TAG, "Jag förhalar")
                        mainHandler.postDelayed(this, 2000)
                    } else {

                        Log.d(TAG, "handler är färdig")
                        audioFileWritten.value = true
                        Log.d(TAG, "tts Skrev till " + testFile.absolutePath)
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

     fun readAudioFile() {
        try {
            val mp = MediaPlayer.create(context, Uri.parse(path))
            Log.d(TAG, "playing audiofile from $path")
            mp.start()
        } catch (e: java.lang.Exception) {
            Log.d(TAG, "Something went wrong : ${e}")
        }
    }






    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {


            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                Log.e(TAG, "The language specified is not supported")
            }
            Log.d(TAG, "TTS I am now initialized")
            createAudioFile()
        } else {
            Log.e(TAG, "TTS Initialization Failed")
        }
    }


    fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    class ttsUtteranceListener : UtteranceProgressListener() {
        override fun onStart(start : String) {
            Log.d(TAG, "TTS Starting to parse file")
        }

        override fun onDone(uttranceID : String) {
            Log.d(TAG, "TTS file complete")

        }

        override fun onError(uttranceID : String) {
            TODO("Not yet implemented")
        }
    }
}





