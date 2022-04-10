package se.staffanljungqvist.revocalize.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
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

    private var mediaPlayer: MediaPlayer? = null
    private var listenMode = true
    private var postTop = -400
    private var posBottom = 500
    private var duration = 300


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
            .add(R.id.fragmentContainerView, ScoreFragment())
            .commit()

        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, InventoryFragment())
            .commit()

        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, LevelUpFragment()).addToBackStack(null)
            .commit()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ttsAdapter = TTSAdapter(requireContext())
        ttsAdapter.textPhrase = model.currentPhrase.text

        myRecyclerView = binding.rvSlizes
        myRecyclerView.adapter = slizeRecAdapter
        myRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        itemTouchHelper.attachToRecyclerView(myRecyclerView)

        ttsAdapter.ttsInitiated.observe(viewLifecycleOwner) {
            if (it) model.downloadPhrases(requireContext())
        }

        model.phraseLoaded.observe(viewLifecycleOwner) {
            if (it) {
                ttsAdapter.saveToAudioFile(model.currentPhrase.text)
                animPos(binding.tvLoading, 0, duration) {}
            }
        }

        ttsAdapter.ttsAudiofileWritten.observe(viewLifecycleOwner) {
            if (it) loadAudio()
        }

        model.audioReady.observe(viewLifecycleOwner) {
            if (it) {
                Log.d(TAG, "Audio är redo att spelas")
                makeSlices()
                binding.tvSentence.text = model.prepareText()
                if (model.levelUp) {
                    showLevelUp()
                } else {
                    animPos(binding.tvLoading, posBottom, duration) {
                        animPos(binding.rvSlizes, 0, duration) {
                            model.showSuccess.value = false
                            animPos(binding.tvSentence, 0, duration, duration) {
                                animateButton(binding.btnListen, true)
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
            if (it == -2 && mediaPlayer != null) mediaPlayer?.pause()
        }


        model.doneIterating.observe(viewLifecycleOwner) {
            if (it) {
                Log.d(TAG, "done iterating")
                if (listenMode) {
                    listenMode = false
                    animateButton(binding.btnCheck, true)
                } else {
                    if (model.makeGuess()) correctAnswer() else {
                        wrongAnswer()
                        animateButton(binding.btnCheck, true)
                    }
                }
                slizeRecAdapter.blinknumber = -1
                slizeRecAdapter.notifyDataSetChanged()
            }
        }

        model.powerUpUsed.observe(viewLifecycleOwner) {
            when (it) {
                PowerUp.REMOVESLIZE -> {
                    animPos(binding.rvSlizes, posBottom, duration) {
                        makeSlices()
                        //    slizeRecAdapter.notifyDataSetChanged()
                        animPos(binding.rvSlizes, 0, duration) {}
                    }
                }
            }
        }

        binding.btnListen.setOnClickListener {
            model.iterateSlices(model.slices!!)
            listenMode = true
            animateButton(binding.btnListen, false)
        }

        binding.btnCheck.setOnClickListener {
            animateButton(binding.btnCheck, false)
            if (model.checkAnswer()) {
                model.iterateSlices(model.slices!!)
                playFullPhrase()
            } else {
                model.iterateSlices(model.slices!!)
            }
        }
    }

    fun initializeUI() {
        Log.d(TAG, "Nollställer UI")
        binding.btnListen.isVisible = false
        animPos(binding.tvSentence, postTop, 0) {
            animPos(binding.tvSentence, 0, duration) {
                animateButton(binding.btnListen, true)
            }
        }
        animPos(binding.rvSlizes, posBottom, 0) {
            animPos(binding.rvSlizes, 0, duration) {}
        }
        animPos(binding.tvLoading, posBottom, 0) {}
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
            model.audioReady.value = false
        }
    }


    private fun correctAnswer() {
        animPos(binding.tvSentence, postTop, duration) {
            model.showSuccess.value = true
            animPos(binding.rvSlizes, posBottom, duration, 500) {
                model.loadPhrase()
            }
        }
    }

    private fun wrongAnswer() {
        if (model.gameOver) {
            model.calculateScore(requireContext())
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, GameOverFragment()).addToBackStack(null)
                .commit()
            return
        }
        binding.btnCheck.isVisible = true
    }

    fun showLevelUp() {
        animPos(binding.tvLoading, posBottom, duration){
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView, LevelUpFragment()).addToBackStack(null)
                    .commit()
                model.levelUp = false
        }
        model.showSuccess.value = false
    }

    fun animPos(view: View, position: Int, time: Int, delay: Int = 0, doThis: () -> Unit) {
        ObjectAnimator.ofFloat(view, "translationY", position.toFloat()).apply {
            startDelay = delay.toLong()
            duration = if (position == 0) 300 else time.toLong()
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

    private fun playSlize(slize: Slize) {
        Log.d(TAG, "Playing a slize")
        mediaPlayer?.seekTo(slize.start)
        mediaPlayer?.start()
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