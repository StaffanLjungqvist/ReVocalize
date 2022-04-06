package se.staffanljungqvist.revocalize.ui

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
    private var failPlayer: MediaPlayer? = null

    private var listenMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        slizeRecAdapter.fragment = this
        val stageIndex = arguments?.getInt("stage")
        val stageRecord = arguments?.getInt("score")
        model.loadStage(requireContext(), stageIndex!!, stageRecord!!)
        model.stageIndex = stageIndex

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, IntroFragment()).addToBackStack(null).commit()



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

        model.slizeIndex.observe(viewLifecycleOwner) {
            slizeRecAdapter.blinknumber = it
            slizeRecAdapter.notifyDataSetChanged()
            if (it != -1 && it != -2 && !model.isCorrect) playSlize(model.slices!![it])
            if (it == -2 && mediaPlayer != null) mediaPlayer?.pause()
        }


        model.doneIterating.observe(viewLifecycleOwner) {
            if (it) checkAnswer()
            slizeRecAdapter.blinknumber = -1
            slizeRecAdapter.notifyDataSetChanged()
        }

        model.audioReady.observe(viewLifecycleOwner) {
            if (it) binding.tvGuessesRemaining.text = model.points.toString()
        }

        binding.btnPlay.setOnClickListener {
            model.iterateSlices(model.slices!!)
            it.isVisible = false
        }

        binding.btnCheck.setOnClickListener {
            binding.btnCheck.visibility = View.INVISIBLE
            if (model.makeGuess()) {
                model.iterateSlices(model.slices!!)
                playFullPhrase()
            }
            model.iterateSlices(model.slices!!)
        }
    }

    private fun initializeUI() {
        makeSlices()
        binding.btnPlay.isVisible = true
        binding.tvSentence.text = model.currentPhrase.text.parentenses()
        binding.tvCurrentPhrase.text = model.phraseIndex.toString()
        binding.tvTotalPhrases.text = model.currentStage.phraseList.size.toString()
        binding.btnCheck.visibility = View.INVISIBLE


        listenMode = true
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
        listenMode = true
    }

    private fun loadAudio() {
        val audioFile = Uri.parse(requireContext().filesDir.toString() + "/myreq.wav")
        Log.d(TAG, "AA Uri omgord till File : $audioFile")
        mediaPlayer = MediaPlayer.create(context, audioFile)
        mediaPlayer!!.setOnPreparedListener {
            Log.d(TAG, "nu är mediaplayer skapad")
            initializeUI()
            model.audioReady.value = true
            model.audioReady.value = false
        }
    }

    private fun playSlize(slize: Slize) {
        mediaPlayer?.seekTo(slize.start)
        mediaPlayer?.start()
    }

    private fun playFullPhrase() {
        mediaPlayer?.seekTo(0)
        mediaPlayer?.start()
    }

    private fun checkAnswer() {

        binding.tvGuessesRemaining.text = model.points.toString()

        if (model.isCorrect) correctAnswer()
        else if(!listenMode) wrongAnswer()
        else {
            listenMode = false
            binding.btnCheck.isVisible = true
        }
    }

    private fun correctAnswer() {

        val bundle = Bundle()
        bundle.putString("trivia", model.currentPhrase.trivia)
        val theFragment = SuccessFragment()
        theFragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, theFragment).addToBackStack(null)
            .commit()

        loadNewPhrase()
    }

    private fun wrongAnswer() {
            failPlayer = MediaPlayer.create(requireContext(), R.raw.fail)
            failPlayer!!.start()

            binding.tvMinusPoint.apply {
                alpha = 1f
                visibility = View.VISIBLE
                animate()
                    .alpha(0f)
                    .setDuration(2000.toLong())
                    .setListener(null)
            }

            binding.llGuessesCircleRed.apply {
                alpha = 1f
                visibility = View.VISIBLE
                animate()
                    .alpha(0f)
                    .setDuration(2000.toLong())
                    .setListener(null)
            }

        if (model.)

        binding.btnCheck.isVisible = true
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
        return "\"" + this + "\""
    }

}