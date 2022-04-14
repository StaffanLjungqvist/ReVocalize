package se.staffanljungqvist.revocalize.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import se.staffanljungqvist.revocalize.PowerUp
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.adapters.SlizeRecAdapter
import se.staffanljungqvist.revocalize.adapters.TTSAdapter
import se.staffanljungqvist.revocalize.databinding.FragmentInGameBinding
import se.staffanljungqvist.revocalize.models.Slize
import se.staffanljungqvist.revocalize.viewmodels.IngameViewModel
import java.util.*


const val TAG = "revodebug"

class InGameFragment : Fragment() {

    val model: IngameViewModel by activityViewModels()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var _binding: FragmentInGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var myRecyclerView: RecyclerView
    private var slizeRecAdapter: SlizeRecAdapter = SlizeRecAdapter()
    private lateinit var ttsAdapter: TTSAdapter

    var mediaPlayer: MediaPlayer? = null
    private lateinit var failPlayer: MediaPlayer
    private var postTop = -400
    private var posBottom = 500
    private var moveOutSpeed = 230
    private var moveInSpeed = 250


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        slizeRecAdapter.fragment = this
        val stageIndex = arguments?.getInt("stage")

        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_START) {
            param("stage index", stageIndex.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInGameBinding.inflate(inflater, container, false)

        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, SuccessFragment())
            .commit()

        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, ScoreBoardFragment())
            .commit()

/*        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, InventoryFragment())
            .commit()*/

            showLevelUp()
        move(binding.tvLoading, "down", true) {
            move(binding.tvLoading, "show"){}
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ttsAdapter = TTSAdapter(requireContext())
        ttsAdapter.textPhrase = model.currentPhrase.text

        failPlayer = MediaPlayer.create(requireContext(), R.raw.warning)

        myRecyclerView = binding.rvSlizes
        myRecyclerView.adapter = slizeRecAdapter
        myRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        itemTouchHelper.attachToRecyclerView(myRecyclerView)

        ttsAdapter.ttsInitiated.observe(viewLifecycleOwner) {
            if (it) {
                model.downloadPhrases(requireContext())
              //  model.getJSONFromAssets(requireContext())
            }
        }

        model.phraseLoaded.observe(viewLifecycleOwner) {
            if (it) {
                model.audioReady.value = false
                ttsAdapter.saveToAudioFile(model.currentPhrase.text)
                move(binding.tvLoading, "show"){}
            }
        }

        ttsAdapter.ttsAudiofileWritten.observe(viewLifecycleOwner) {
            if (it) loadAudio()
        }

        model.audioReady.observe(viewLifecycleOwner) {
            if (it) {
                Log.d(TAG, "Audio är redo att spelas")
                makeSlices()
                model.listenMode.value = true
                binding.tvSentence.text = model.prepareText(model.currentPhrase.text)
                    move(binding.tvLoading, "down", false, 300) {
                        if (!model.levelUp) {
                        move(binding.rvSlizes, "show") {
                            model.showSuccess.value = false
                            move(binding.tvSentence, "show") {
                                model.powersAvailable.value = true
                                animateButton(binding.btnListen, true)
                                model.playMode.value = true
                            }
                        }
                    }
                }
            }
        }

        model.loadUI.observe(viewLifecycleOwner) {
            if (it) initializeUI()
        }

        model.slizeIndex.observe(viewLifecycleOwner) {
            val wrongAnswer = !model.checkAnswer()
            slizeRecAdapter.blinknumber = it
            slizeRecAdapter.notifyDataSetChanged()
            if (it != -1 && it != -2 && wrongAnswer) playSlize(model.slices!![it])
          //  if (it == -2 && mediaPlayer != null) mediaPlayer?.pause()
        }

        model.doneIterating.observe(viewLifecycleOwner) {
            if (it) {
                Log.d(TAG, "done iterating")
                if (model.listenMode.value == true) {
                    model.listenMode.value = false
                    animateButton(binding.btnCheck, true)
                } else {
                    if (model.makeGuess()) correctAnswer() else {
                        wrongAnswer()
                        model.powersAvailable.value = true
                        animateButton(binding.btnCheck, true)
                    }
                }

                slizeRecAdapter.blinknumber = -1
                slizeRecAdapter.notifyDataSetChanged()
            }
        }

        model.powerUpUsed.observe(viewLifecycleOwner) {
            when (it) {
                PowerUp.REMOVE -> {
                    move(binding.rvSlizes, "down") {
                        makeSlices()
                        move(binding.rvSlizes, "show"){}
                    }
                }
                PowerUp.CLICK -> {
                    model.listenMode.value = false
                }

            }
        }

        model.clickMode.observe(viewLifecycleOwner) {
            if (it) {
                if (binding.btnListen.isVisible) {
                    animateButton(binding.btnListen, false)
                } else if (binding.btnCheck.isVisible) {
                    animateButton(binding.btnCheck, false)
                }
                animateButton(binding.btnClickDone, true)
            }
        }

        model.showInventory.observe(viewLifecycleOwner) {
            if (it) {
                move(binding.tvSentence, "up"){}
            } else if (!it) {
                move(binding.tvSentence, "show"){}
            }

        }

        binding.btnListen.setOnClickListener {
            model.showInventory.value = false
            model.iterateSlices(model.slices!!)
            animateButton(binding.btnListen, false)
        }

        binding.btnCheck.setOnClickListener {
            model.showInventory.value = false
            animateButton(binding.btnCheck, false)
            if (model.checkAnswer()) {
                model.iterateSlices(model.slices!!)
                playFullPhrase()
            } else {
                model.iterateSlices(model.slices!!)
            }
            model.powersAvailable.value = false
        }

        binding.btnClickDone.setOnClickListener {
            model.showInventory.value = false
            model.clickMode.value = false
            animateButton(binding.btnClickDone, false)
            animateButton(binding.btnCheck, true)
        }
    }

    fun initializeUI() {
        Log.d(TAG, "Nollställer UI")
        binding.btnListen.isVisible = false
            move(binding.tvSentence, "show") {
                animateButton(binding.btnListen, true)
                model.powersAvailable.value = true
        }

            move(binding.rvSlizes, "show") {}

        move(binding.tvLoading, "down") {}
    }

    private fun makeSlices() {
        model.makeSlices(mediaPlayer!!.duration)
        slizeRecAdapter.slizes = model.slices!!
        myRecyclerView.layoutManager = GridLayoutManager(requireContext(), model.slices!!.size)
        slizeRecAdapter.notifyDataSetChanged()
    }

    private fun loadAudio() {
        val audioFile = Uri.parse(requireContext().filesDir.toString() + "/myreq.wav")
        Log.d(TAG, "AA Uri omgord till File : $audioFile")
        mediaPlayer = MediaPlayer.create(context, audioFile)
        mediaPlayer!!.setOnPreparedListener {
            it.seekTo(0)
            model.audioReady.value = true
        }
    }

    private fun correctAnswer() {
        move(binding.tvSentence, "up") {
            model.showSuccess.value = true
            move(binding.rvSlizes, "down", false, 300) {
                model.loadPhrase()
                if (model.levelUp) {
                    move(binding.rvSlizes, "down", false, 1000) {
                        model.showSuccess.value = false
                        if (model.gameComplete){
                            Log.d(TAG, "visar gameover fragment")
                            gameOver()
                        } else {
                            Log.d(TAG, "visar levelup fragment")
                            showLevelUp()
                        }

                    }
                }
            }
        }
    }

    private fun wrongAnswer() {
        failPlayer!!.start()
        if (model.gameOver) {
            gameOver()
        } else {
            binding.btnCheck.isVisible = true
            model.powersAvailable.value = true
        }
    }

    private fun gameOver() {
        model.calculateScore(requireContext())
        val bundle = Bundle()
        bundle.putInt("score", model.totalPoints)
        bundle.putBoolean("isRecord", model.newRecord)
        bundle.putInt("userRecord", model.userHighScore)
        bundle.putBoolean("gameBeat", model.gameComplete)
        Log.d(TAG, "Skickar med isRecord ${model.newRecord}")
        val theFragment = GameOverFragment()
        theFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, theFragment).addToBackStack(null)
            .commit()
        activity?.viewModelStore?.clear()
    }

    fun showLevelUp() {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView, LevelUpFragment()).addToBackStack(null)
                    .commit()
        move(binding.rvSlizes, "down", true) {}
        move(binding.tvSentence, "up", true) {}
    }

    fun move(view: View, direction : String, hide : Boolean = false, delay : Int = 0, doThis: () -> Unit) {
        val moveTo = when(direction) {
            "up" -> postTop
            "down" -> posBottom
            else -> 0
        }

        var speed = if (moveTo == 0) moveInSpeed else moveOutSpeed
        if (hide) speed = 0

        ObjectAnimator.ofFloat(view, "translationY", moveTo.toFloat()).apply {
            startDelay = delay.toLong()
            duration = speed.toLong()
            start()
            addListener(onEnd = {
                doThis()
            }) {
            }
        }
    }


    fun animateButton(button: Button, show: Boolean) {
        val duration = if (show) 200 else 100
        button.apply {
            alpha = if (show) 0f else 1f
            visibility = View.VISIBLE
            animate()
                .alpha(if (show) 1f else 0f)
                .setDuration(duration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        button.visibility = if (show) View.VISIBLE else View.INVISIBLE
                    }
                })
        }
    }

     fun playSlize(slize: Slize) {
        Log.d(TAG, "Playing a slize")
        mediaPlayer?.seekTo(slize.start)
        mediaPlayer?.start()
         val handler = Handler(Looper.getMainLooper())
         handler.postDelayed({
             mediaPlayer?.pause()
         }, slize.length)
    }

    private fun playFullPhrase() {

        Log.d(TAG, "Playing full phrase")
        mediaPlayer?.seekTo(0)
        mediaPlayer?.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "inGameFragment Destroyed")
        mediaPlayer?.stop()
        _binding = null
        slizeRecAdapter.blinkHandler.removeCallbacksAndMessages(null)
    }

    //Kod för byta plats på recyclerView viewholders med drag and drop.
    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT or
                        ItemTouchHelper.RIGHT or
                        ItemTouchHelper.START or
                        ItemTouchHelper.END, 0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    // 2. Update the backing model. Custom implementation in
                    //    MainRecyclerViewAdapter. You need to implement
                    //    reordering of the backing model inside the method.
                    Collections.swap(model.slices, from, to)

                    recyclerView.adapter?.notifyItemMoved(from, to)

                    return true
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
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