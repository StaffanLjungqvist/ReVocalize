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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.loadPhraseList(requireContext())
        model.loadPhrase()

        ttsAdapter = TTSAdapter(requireContext())
        ttsAdapter.textPhrase = model.currentPhrase.text

        myRecyclerView = binding.rvSlizes
        myRecyclerView.adapter = slizeRecAdapter
        myRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        itemTouchHelper.attachToRecyclerView(myRecyclerView)

        ttsAdapter.ttsAudiofileWritten.observe(viewLifecycleOwner) {
            if (it) loadAudio()
        }

        model.loadUI.observe(viewLifecycleOwner) {
            if (it) {
                animateIn()
            }
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

    private fun initializeUI() {
        makeSlices()
        if (model.numberOfphrasesDone.value == 0) {
            animateIn()
        }
        binding.tvSentence.text = model.currentPhrase.text.parentenses()
    }

    private fun makeSlices() {
        model.makeSlices(mediaPlayer!!.duration)
        slizeRecAdapter.slizes = model.slices!!
        myRecyclerView.layoutManager = GridLayoutManager(requireContext(), model.slices!!.size)
        slizeRecAdapter.notifyDataSetChanged()
    }

    private fun loadNewPhrase() {
        model.loadPhrase()
        ttsAdapter.saveToAudioFile(model.currentPhrase.text)
    }

    private fun loadAudio() {
        val audioFile = Uri.parse(requireContext().filesDir.toString() + "/myreq.wav")
        Log.d(TAG, "AA Uri omgord till File : $audioFile")
        mediaPlayer = MediaPlayer.create(context, audioFile)
        mediaPlayer!!.setOnPreparedListener {
            it.seekTo(0);
            Log.d(TAG, "Audio är redo att spelas")
            initializeUI()
            ObjectAnimator.ofFloat(binding.tvLoading, "translationY", 500f).apply {
                duration = 300
                start()
            }
            model.audioReady.value = true
            model.audioReady.value = false
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

    private fun correctAnswer() {
        animateOut()
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

    fun animateOut() {
        ObjectAnimator.ofFloat(binding.rvSlizes, "translationY", 1000f).apply {
            duration = 1000
            start()
            addListener(onEnd = {
                loadNewPhrase()
                ObjectAnimator.ofFloat(binding.tvLoading, "translationY", 0f).apply {
                    duration = 300
                    start()
                }
            }) {
            }
        }

        ObjectAnimator.ofFloat(binding.tvSentence, "translationY", -500f).apply {
            duration = 400
            start()
        }


    }

    fun animateIn() {
        ObjectAnimator.ofFloat(binding.rvSlizes, "translationY", 0f).apply {
            duration = 500
            start()
        }

        ObjectAnimator.ofFloat(binding.tvSentence, "translationY", 0f).apply {
            duration = 600
            start()
            addListener(onEnd = {
                animateButton(binding.btnListen, true)
            })
        }
    }

    fun animateButton(button : Button, show: Boolean) {
        button.apply {
            alpha = if (show) 0f else 1f
            visibility = View.VISIBLE
            animate()
                .alpha(if (show) 1f else 0f)
                .setDuration(100.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        button.visibility = if (show) View.VISIBLE else View.INVISIBLE
                    }
                })
        }
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

    private fun String.parentenses(): String {
        val text = this.uppercase()
        return "\"" + text + "\""
    }

}