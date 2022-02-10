package se.staffanljungqvist.revocalize

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import se.staffanljungqvist.revocalize.adapters.AudioAdapter
import se.staffanljungqvist.revocalize.adapters.GameAdapter
import se.staffanljungqvist.revocalize.adapters.MyRecyclerAdapter
import se.staffanljungqvist.revocalize.adapters.TTSAdapter
import se.staffanljungqvist.revocalize.builders.TextPhrases
import se.staffanljungqvist.revocalize.models.Phrase
import se.staffanljungqvist.revocalize.models.Slize
import java.io.File
import java.util.*

val TAG = "revodebug"

class MainActivity : AppCompatActivity() {

    private lateinit var myRecyclerView: RecyclerView
    private lateinit var myRecycleAdapter: MyRecyclerAdapter
    private lateinit var currentPhrase: Phrase
    private lateinit var audioAdapter: AudioAdapter
    private lateinit var gameAdapter : GameAdapter
    private lateinit var ttsAdapter : TTSAdapter
    private var level = 0
    private var isCorrect = false
    private val EXTERNAL_STORAGE_PERMISSION_CODE = 23

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getSupportActionBar()?.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            EXTERNAL_STORAGE_PERMISSION_CODE
        )


        gameAdapter = GameAdapter(this)
        gameAdapter.loadPhrase()
        currentPhrase = Phrase(TextPhrases.textlist[level], listOf<Slize>())
        //
        audioAdapter = AudioAdapter(this)

        ttsAdapter = TTSAdapter(this)



        ttsAdapter.audioFileCreated.observe(this, androidx.lifecycle.Observer {
            Log.d(TAG, "hej hej")
            ttsAdapter.saveToAudioFile(currentPhrase.text)
        })
        ttsAdapter.audioFileWritten.observe(this, androidx.lifecycle.Observer {
            audioAdapter.loadAudio(ttsAdapter.path)
        })

        audioAdapter.audioFile.observe(this, androidx.lifecycle.Observer {
            val duration = audioAdapter.getDuration()
            Log.d(TAG, "the duration is ${duration}")
            currentPhrase.slizes = gameAdapter.makeSlices(duration)
            myRecycleAdapter.slizes = currentPhrase.slizes
            myRecyclerView.layoutManager = GridLayoutManager(this, currentPhrase.slizes.size)
            myRecycleAdapter.notifyDataSetChanged()
        })


        //Setting up recycler view
        myRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        myRecycleAdapter = MyRecyclerAdapter(this, audioAdapter)
        myRecyclerView.adapter = myRecycleAdapter
        var slizeSize = if (currentPhrase.slizes.size == 0) {1} else {currentPhrase.slizes.size}
        myRecyclerView.layoutManager = GridLayoutManager(this, 1)

        myRecycleAdapter.hasChecked.observe(this, androidx.lifecycle.Observer {
            if (isCorrect) {
                findViewById<TextView>(R.id.tvSentence).text = "That is correct!"
                findViewById<Button>(R.id.btnNext).isVisible = true
                audioAdapter.playSuccess()
                isCorrect = false
            } else {
                findViewById<Button>(R.id.btnCheck).isVisible = true
            }
        })

        findViewById<TextView>(R.id.tvSentence).text = currentPhrase.text


        //Knappar

        findViewById<Button>(R.id.btnCheck).setOnClickListener {

                myRecycleAdapter.runLight(currentPhrase.slizes)
            if (checkIfCorrect()) {
                audioAdapter.playAudio()
            } else {
                audioAdapter.playSlices(currentPhrase.slizes)
            }
        }

        findViewById<Button>(R.id.btnNext).setOnClickListener {
            loadPhrase()
        }


        //val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(myRecyclerView)
    }

    fun loadPhrase() {
        findViewById<Button>(R.id.btnCheck).isVisible = true
        gameAdapter.advanceLevel()
        gameAdapter.loadPhrase()
        currentPhrase = gameAdapter.currentPhrase
        Log.d(TAG, "nuvarande text är ${currentPhrase.text}")
        ttsAdapter.saveToAudioFile(currentPhrase.text)
        //To do, optimera release mediaplayer
       // audioAdapter = AudioAdapter(this, currentPhrase.audioFile.file)

        myRecycleAdapter.slizes = currentPhrase.slizes
        myRecycleAdapter.audioHelper = audioAdapter
        myRecyclerView.adapter = myRecycleAdapter
        myRecyclerView.layoutManager = GridLayoutManager(this, 1)


        findViewById<Button>(R.id.btnCheck).isVisible = true
        findViewById<Button>(R.id.btnNext).isVisible = false
        findViewById<TextView>(R.id.tvSentence).text = currentPhrase.text
    }

    fun checkIfCorrect(): Boolean {

        findViewById<Button>(R.id.btnCheck).isVisible = false
        var sortedList = currentPhrase.slizes.sortedBy { it.number }
        Log.d("kolla", "listan i rätt ordning; ${sortedList}")

        if (sortedList.equals(currentPhrase.slizes)) {
            isCorrect = true


            //    findViewById<Button>(R.id.btnCheck).isVisible = false

            Log.d("kolla", "Listan är i rätt ordning!")
            return true
        }
        return false
    }


    private val itemTouchHelper by lazy {
        // 1. Note that I am specifying all 4 directions.
        //    Specifying START and END also allows
        //    more organic dragging than just specifying UP and DOWN.

        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT or
                        ItemTouchHelper.RIGHT or
                        ItemTouchHelper.START or
                        ItemTouchHelper.END, 0) {

                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {

                    val adapter = myRecycleAdapter //recyclerView.adapter as MainRecyclerViewAdapter
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    // 2. Update the backing model. Custom implementation in
                    //    MainRecyclerViewAdapter. You need to implement
                    //    reordering of the backing model inside the method.
                    Collections.swap(currentPhrase.slizes, from, to)
                    
                    recyclerView.adapter?.notifyItemMoved(from, to)

                    return true
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                      direction: Int) {
                    // 4. Code block for horizontal swipe.
                    //    ItemTouchHelper handles horizontal swipe as well, but
                    //    it is not relevant with reordering. Ignoring here.
                }

                override fun isLongPressDragEnabled(): Boolean {
                    return true
                }
            }
        ItemTouchHelper(simpleItemTouchCallback)
    }

    fun startDragging(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }
}