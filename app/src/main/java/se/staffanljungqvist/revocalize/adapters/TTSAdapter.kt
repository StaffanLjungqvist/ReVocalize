package se.staffanljungqvist.revocalize.adapters

import android.content.Context
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
    var testFile = File(context.filesDir.toString() + "/myreq.wav")
    val path = context.filesDir.toString() + "/myreq.wav"

    var ttsInitiated = MutableLiveData<Boolean>()
    var ttsAudiofileWritten = MutableLiveData<Boolean>()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {

            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                Log.e(TAG, "The language specified is not supported")
            }
            Log.d(TAG, "TTS initialiserades korrekt")
            if (testFile.exists()) {
                ttsInitiated.value = true
            } else {
                createAudioFile()
            }

        } else {
            Log.e(TAG, "TTS Initialization Failed")
        }
    }


//Todo : Lägg in i onInit?



        //Skapar en tom fil i minnet vilket data senare kommer att skrivas till.
     fun createAudioFile() {
        // Create audio file location
        val sddir = File(Environment.getExternalStorageDirectory().toString() + "/My File/")
        sddir.mkdir()
        mAudioFilename = sddir.absolutePath.toString() + "/" + mUtteranceID + ".wav"

        Log.d(TAG, "tts skapade fil : ${mAudioFilename}")
        ttsInitiated.value = true
    }


    //Tar text och gör om till en ljudfil, skrivs till skapade filen i minnet.
     fun saveToAudioFile(text : String) {

         //Tar ut alla röster tillgängliga i mobilen med engelsk språk, och sätter slumpmässigt till tts.

         val voicesAllEnglish = tts.voices.filter { it.locale.language == "en" }

        val voicesFiltered = voicesAllEnglish.filterNot { it.name.toLowerCase().contains("en-in") || it.name.toLowerCase().contains("en-ng")}

        Log.d(TAG, "Voices : ${voicesFiltered}")

         val voice = voicesFiltered.random()

         tts.setVoice(voice)
        Log.d(TAG, "Språk är satt till ${voice}")
        tts.setSpeechRate("0.7".toFloat())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


            val listener = ttsUtteranceListener()
            listener.ttsadapt = this
            tts.setOnUtteranceProgressListener(listener)


            //Skapar filen.

            tts!!.synthesizeToFile(text, null, testFile, mUtteranceID)

        } else {
            val hm = HashMap<String, String>()
            hm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,mUtteranceID)
            tts!!.synthesizeToFile("testing", hm, mAudioFilename)
            Log.d(TAG,"tts fulskrev  till" + mAudioFilename)
            ttsAudiofileWritten.value = true
        }


    }


    fun synthDone()
    {
        Log.d(TAG, "handler är färdig")
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                ttsAudiofileWritten.value = true
            }
        })
    }


    class ttsUtteranceListener : UtteranceProgressListener() {
        lateinit var ttsadapt : TTSAdapter
        override fun onStart(start : String) {
            Log.d(TAG, "TTS Startar synthesize file")
        }

        override fun onDone(uttranceID : String) {
            Log.d(TAG, "TTS syntesize file färdig.")
            ttsadapt.synthDone()
        }

        override fun onError(uttranceID : String) {
            TODO("Not yet implemented")
        }
    }
}





