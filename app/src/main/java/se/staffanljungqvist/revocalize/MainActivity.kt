package se.staffanljungqvist.revocalize

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import se.staffanljungqvist.revocalize.adapters.AudioAdapter
import se.staffanljungqvist.revocalize.adapters.GameAdapter
import se.staffanljungqvist.revocalize.adapters.MyRecyclerAdapter
import se.staffanljungqvist.revocalize.models.Phrase
import java.util.*


val TAG = "kolla"
// hej från pc

//hej hej från mac

// osså från pc igen

class MainActivity : AppCompatActivity() {

    private lateinit var myRecyclerView: RecyclerView
    private lateinit var myRecycleAdapter: MyRecyclerAdapter
    private lateinit var currentPhrase: Phrase
    private lateinit var audioHelper: AudioAdapter
    private lateinit var gameAdapter : GameAdapter
    private var level = 0
    private var isCorrect = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getSupportActionBar()?.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        gameAdapter = GameAdapter(this)

        //To do - safecast
        currentPhrase = gameAdapter.loadPhrase(level)!!

        audioHelper = AudioAdapter(this, currentPhrase.audioFile.file)

        //Setting up recycler view
        myRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        myRecycleAdapter = MyRecyclerAdapter(this, currentPhrase.slizes, audioHelper)
        myRecyclerView.adapter = myRecycleAdapter
        myRecyclerView.layoutManager = GridLayoutManager(this, currentPhrase.slizes.size)

        myRecycleAdapter.hasChecked.observe(this, androidx.lifecycle.Observer {
            if (isCorrect) {
                findViewById<TextView>(R.id.tvSentence).text = "That is correct!"
                findViewById<Button>(R.id.btnNext).isVisible = true
                audioHelper.playSuccess()
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
                audioHelper.playAudio()
            } else {
                audioHelper.playSlices(currentPhrase.slizes)
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
        level = gameAdapter.advanceLevel()
        currentPhrase = gameAdapter.loadPhrase(level)!!

        //To do, optimera release mediaplayer
        audioHelper = AudioAdapter(this, currentPhrase.audioFile.file)

        myRecycleAdapter.slizes = currentPhrase.slizes
        myRecycleAdapter.audioHelper = audioHelper
        myRecyclerView.adapter = myRecycleAdapter
        myRecyclerView.layoutManager = GridLayoutManager(this, currentPhrase.slizes.size)


        findViewById<Button>(R.id.btnCheck).isVisible = true
        findViewById<Button>(R.id.btnNext).isVisible = false
        findViewById<TextView>(R.id.tvSentence).text = currentPhrase.text
    }

    /*
    private var simpleCallback = object : ItemTouchHelper.SimpleCallback(

        ItemTouchHelper.LEFT.or(
            ItemTouchHelper.RIGHT
        ), 0
    ) {


        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            var startPosition = viewHolder.adapterPosition
            var endPosition = target.adapterPosition


            Collections.swap(currentPhrase.slizes, startPosition, endPosition)


            recyclerView.adapter?.notifyItemMoved(startPosition, endPosition)

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            TODO("Not yet implemented")

        }
    }
    */

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