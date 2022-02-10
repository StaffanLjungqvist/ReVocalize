package se.staffanljungqvist.revocalize

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
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
import se.staffanljungqvist.revocalize.databinding.ActivityMainBinding
import se.staffanljungqvist.revocalize.models.Phrase
import se.staffanljungqvist.revocalize.models.Slize
import java.util.*

val TAG = "revodebug"

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Todo: Lägg in logik i en viewModel

        //Göm navigation bar och action bar
        getSupportActionBar()?.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //Frågar om tillåtelse att läsa externt lagerutrymme
        //Todo : nödvändig? Släng?
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            EXTERNAL_STORAGE_PERMISSION_CODE
        )

        //Initiera + Konfigurera adaptrar.
        //Todo : Försök få in så mycket som möjligt i loadPhrase funktionen
        gameAdapter = GameAdapter(this)
        gameAdapter.loadPhrase()

        currentPhrase = Phrase(TextPhrases.textlist[level], listOf<Slize>())

        audioAdapter = AudioAdapter(this)
        ttsAdapter = TTSAdapter(this)

        myRecyclerView = binding.rvSlizes
        myRecycleAdapter = MyRecyclerAdapter(this, audioAdapter)
        myRecyclerView.adapter = myRecycleAdapter
        myRecyclerView.layoutManager = GridLayoutManager(this, 1)



        /*Observerar diverse  "är klar = true" booleans i diverse adaptrar för att vänta in färdiga processer
        och göra saker i rätt ordning.
        Todo : Finns ett bättre sätt?
         */
        ttsAdapter.audioFileCreated.observe(this, androidx.lifecycle.Observer {
            ttsAdapter.saveToAudioFile(currentPhrase.text)
        })

        ttsAdapter.audioFileWritten.observe(this, androidx.lifecycle.Observer {
            audioAdapter.loadAudio(ttsAdapter.path)
        })

        audioAdapter.fileTransformedFromUriToFile.observe(this, androidx.lifecycle.Observer {
            Log.d(TAG, "ljudfil färdig")
            val duration = audioAdapter.getDuration()
            currentPhrase.slizes = gameAdapter.makeSlices(duration)
            myRecycleAdapter.slizes = currentPhrase.slizes
            myRecyclerView.layoutManager = GridLayoutManager(this, currentPhrase.slizes.size)
            myRecycleAdapter.notifyDataSetChanged()
            binding.rvSlizes.isVisible = true
            binding.tvSentence.isVisible = true
            binding.tvSentence.text = currentPhrase.text
            binding.btnCheck.isVisible = true
            binding.tvLoading.isVisible = false
        })

        myRecycleAdapter.hasChecked.observe(this, androidx.lifecycle.Observer {
            if (isCorrect) {
                binding.tvSentence.isVisible = true
                binding.tvSentence.text = "That is correct!"
                binding.btnNext.isVisible = true
                binding.rvSlizes.isVisible = false
                binding.tvLoading.isVisible = false
                audioAdapter.playSuccess()
                isCorrect = false
            } else {
                binding.btnCheck.isVisible = true
            }
        })

        //Todo : Lägg in i loadPhrase()
        binding.tvSentence.text = currentPhrase.text

        //Knappar

        binding.btnCheck.setOnClickListener {
                myRecycleAdapter.runLight(currentPhrase.slizes)
            if (checkIfCorrect()) {
                audioAdapter.playAudio()
            } else {
                audioAdapter.playSlices(currentPhrase.slizes)
            }
        }

        binding.btnNext.setOnClickListener {
            binding.btnCheck.isVisible = false
            binding.tvLoading.isVisible = true
            binding.btnNext.isVisible = false
            binding.tvSentence.isVisible = false
            loadPhrase()
        }


        //val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(myRecyclerView)
    }

    //Todo : Fixa så att funktionen kan köras när activity startas, för att initiera alla lateinits.
    fun loadPhrase() {
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

    }

    fun checkIfCorrect(): Boolean {

        binding.btnCheck.isVisible = false
        var sortedList = currentPhrase.slizes.sortedBy { it.number }
        Log.d("kolla", "listan i rätt ordning; ${sortedList}")

        if (sortedList.equals(currentPhrase.slizes)) {
            isCorrect = true
            Log.d("kolla", "Listan är i rätt ordning!")
            return true
        }
        return false
    }


    //Kod för byta plats på recyclerView viewholders med drag and drop.

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