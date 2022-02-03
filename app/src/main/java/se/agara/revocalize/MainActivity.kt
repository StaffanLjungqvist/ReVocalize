package se.agara.revocalize

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import se.agara.revocalize.adapters.AudioAdapter
import se.agara.revocalize.adapters.GameAdapter
import se.agara.revocalize.adapters.MyRecyclerAdapter
import se.agara.revocalize.builders.PhraseLoader
import se.agara.revocalize.models.Phrase
import java.util.*


val TAG = "kolla"


class MainActivity : AppCompatActivity() {

    private lateinit var myRecyclerView: RecyclerView
    private lateinit var myRecycleAdapter: MyRecyclerAdapter
    private lateinit var currentPhrase: Phrase
    private lateinit var audioHelper: AudioAdapter
    private lateinit var gameAdapter : GameAdapter
    private var level = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getSupportActionBar()?.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        gameAdapter = GameAdapter()

        //To do - safecast
        currentPhrase = PhraseLoader(this).loadPhrase(level)!!

        audioHelper = AudioAdapter(this, currentPhrase.audioFile.file)

        //Setting up recycler view
        myRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        myRecycleAdapter = MyRecyclerAdapter(this, currentPhrase.slizes, audioHelper)
        myRecyclerView.adapter = myRecycleAdapter
        myRecyclerView.layoutManager = GridLayoutManager(this, currentPhrase.slizes.size)

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

        findViewById<Button>(R.id.btnSkip).setOnClickListener {
            loadPhrase()
        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(myRecyclerView)


    }

    fun loadPhrase() {
        level = gameAdapter.advanceLevel()
        currentPhrase = PhraseLoader(this).loadPhrase(level)!!

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


    fun checkIfCorrect(): Boolean {

        var sortedList = currentPhrase.slizes.sortedBy { it.number }
        Log.d("kolla", "listan i rätt ordning; ${sortedList}")

        if (sortedList.equals(currentPhrase.slizes)) {
            audioHelper.playSuccess()
            findViewById<TextView>(R.id.tvSentence).text = "That is correct!"
            //    findViewById<Button>(R.id.btnCheck).isVisible = false
            findViewById<Button>(R.id.btnNext).isVisible = true
            Log.d("kolla", "Listan är i rätt ordning!")
            return true
        }
        return false
    }


}