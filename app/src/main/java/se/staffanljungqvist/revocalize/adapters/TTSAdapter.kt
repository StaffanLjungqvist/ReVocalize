package se.staffanljungqvist.revocalize.adapters

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.MutableLiveData
import se.staffanljungqvist.revocalize.ui.TAG
import java.io.File
import java.util.*

class TTSAdapter(val context: Context) : TextToSpeech.OnInitListener {

    private var mAudioFilename = ""
    private val mUtteranceID = "totts"
    private var tts = TextToSpeech(context, this)
    var textPhrase = "Default textfrase"
    private var fileLocation = File(context.filesDir.toString() + "/myreq.wav")
    var ttsAudiofileWritten = MutableLiveData<Boolean>()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                Log.e(TAG, "The language specified is not supported")
            }
            if (fileLocation.exists()) {
                Log.d(TAG, "TTS initialiserades korrekt")
            } else {
                createAudioFile()
            }
        } else {
            Log.e(TAG, "TTS Initialization Failed")
        }
    }

    //Skapar en tom fil i minnet vilket data senare kommer att skrivas till.
    private fun createAudioFile() {
        // Create audio file location
        val sddir = File(Environment.getExternalStorageDirectory().toString() + "/My File/")
        sddir.mkdir()
        mAudioFilename = sddir.absolutePath.toString() + "/" + mUtteranceID + ".wav"

        Log.d(TAG, "tts skapade fil : $mAudioFilename")
        saveToAudioFile(textPhrase)
    }


    //Tar text och gör om till en ljudfil, skrivs till skapade filen i minnet.
    fun saveToAudioFile(text: String) {

        //Tar ut alla röster tillgängliga i mobilen med engelsk språk, och sätter slumpmässigt till tts.
        val voicesAllEnglish =
            tts.voices.filter { it.name.lowercase(Locale.getDefault()).contains("en") }
        val voicesFiltered = voicesAllEnglish.filterNot {
            it.name.lowercase(Locale.getDefault())
                .contains("en-in") || it.name.lowercase(Locale.getDefault())
                .contains("en-ng")
        }
        val voice = voicesFiltered.random()
        tts.voice = voice
        tts.setSpeechRate("0.8".toFloat())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val listener = ttsUtteranceListener()
            listener.ttsadapt = this
            tts.setOnUtteranceProgressListener(listener)

            //Skapar filen.
            tts.synthesizeToFile(text, null, fileLocation, mUtteranceID)

        } else {
            val hm = HashMap<String, String>()
            hm[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = mUtteranceID
            tts.synthesizeToFile("testing", hm, mAudioFilename)
            Log.d(TAG, "tts fulskrev  till $mAudioFilename")
            ttsAudiofileWritten.value = true
            ttsAudiofileWritten.value = false
        }
    }


    fun synthDone() {
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            Log.d(TAG, "handler är färdig")
            ttsAudiofileWritten.value = true
            ttsAudiofileWritten.value = false
        }
    }


    class ttsUtteranceListener : UtteranceProgressListener() {
        lateinit var ttsadapt: TTSAdapter
        override fun onStart(start: String) {
            Log.d(TAG, "TTS Startar synthesize file")
        }

        override fun onDone(uttranceID: String) {
            Log.d(TAG, "TTS syntesize file färdig.")
            ttsadapt.synthDone()
        }

        override fun onError(uttranceID: String) {
            TODO("Not yet implemented")
        }
    }
}





