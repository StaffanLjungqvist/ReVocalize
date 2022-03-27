package se.staffanljungqvist.revocalize.ui

import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.adapters.SlizeRecAdapter
import se.staffanljungqvist.revocalize.adapters.TTSAdapter
import se.staffanljungqvist.revocalize.databinding.FragmentInGameBinding
import se.staffanljungqvist.revocalize.models.Slize
import se.staffanljungqvist.revocalize.viewmodels.IngameViewModel
import java.util.*


val TAG = "revodebug"

class InGameFragment : Fragment() {

    val model: IngameViewModel by activityViewModels()

    private var _binding: FragmentInGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var myRecyclerView: RecyclerView
    private var slizeRecAdapter: SlizeRecAdapter = SlizeRecAdapter()
    private lateinit var ttsAdapter: TTSAdapter

    var mediaPlayer: MediaPlayer? = null
    var failPlayer : MediaPlayer? = null

    private var listenMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        slizeRecAdapter.fragment = this
        val stageIndex = arguments?.getInt("stage")
        val stageRecord = arguments?.getInt("score")
        model.loadStage(requireContext(), stageIndex!!, stageRecord!!)
        model.stageIndex = stageIndex
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

/*        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, IntroFragment()).addToBackStack(null).commit()*/



        model.loadPhrase()

        ttsAdapter = TTSAdapter(requireContext())
        ttsAdapter.textPhrase = model.currentPhrase.text

        myRecyclerView = binding.rvSlizes
        myRecyclerView.adapter = slizeRecAdapter
        myRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        itemTouchHelper.attachToRecyclerView(myRecyclerView)


        ttsAdapter.ttsAudiofileWritten.observe(requireActivity(), androidx.lifecycle.Observer {
            if (it) loadAudio()
        })

        model.slizeIndex.observe(requireActivity(), androidx.lifecycle.Observer {
            slizeRecAdapter.blinknumber = it
            slizeRecAdapter.notifyDataSetChanged()

            if (it != -1 && it != -2 && !model.isCorrect) playSlize(model.slizes!![it])
            if (it == -2 && mediaPlayer != null) mediaPlayer?.pause()
        })


        //Lyssnar om användaren har gjort en gissning samt alla ljud har spelats upp.
        model.doneIterating.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) {
                slizeRecAdapter.blinknumber = -1
                slizeRecAdapter.notifyDataSetChanged()
                binding.tvGuessesRemaining.text = model.points.toString()

                if (model.gameOver) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .add(R.id.fragmentContainerView, GameOverFragment()).addToBackStack(null)
                        .commit()
                } else if (model.stageComplete) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .add(R.id.fragmentContainerView, LevelCompleteFragment())
                        .addToBackStack(null)
                        .commit()
                } else if (model.isCorrect) correctAnswer()
                else wrongAnswer()
            }
        })

        binding.btnPlay.setOnClickListener {
            model.interateSlizes(model.slizes!!)
            it.isVisible = false
        }

        binding.btnCheck.setOnClickListener {
            binding.btnCheck.visibility = View.INVISIBLE
            if (model.makeGuess()) {
                model.interateSlizes(model.slizes!!)
                playFullPhrase()
            }
            else model.interateSlizes(model.slizes!!)
        }
    }

    fun initializeUI() {
        makeSlices()
        binding.btnPlay.isVisible = true
        binding.tvSentence.text = model.currentPhrase.text.parentenses()
        binding.tvCurrentPhrase.text = model.phraseIndex.toString()
        binding.tvTotalPhrases.text = model.currentStage.phraseList.size.toString()
        binding.btnCheck.visibility = View.INVISIBLE
        binding.tvGuessesRemaining.text = model.points.toString()
        model.audioReady.value = true
        model.audioReady.value = false
    }

    fun makeSlices() {
        model.makeSlices(mediaPlayer!!.duration)
        slizeRecAdapter.slizes = model.slizes!!
        myRecyclerView.layoutManager = GridLayoutManager(requireContext(), model.slizes!!.size)
        slizeRecAdapter.notifyDataSetChanged()
    }

    fun loadNewPhrase() {
        model.loadPhrase()
        ttsAdapter.saveToAudioFile(model.currentPhrase.text)
        listenMode = true
    }

    fun loadAudio() {
        val audioFile = Uri.parse(requireContext().filesDir.toString() + "/myreq.wav")

        Log.d(TAG, "AA Uri omgord till File : ${audioFile}")

        mediaPlayer = MediaPlayer.create(context, audioFile)

        mediaPlayer!!.setOnPreparedListener(MediaPlayer.OnPreparedListener {

            Log.d(TAG, "nu är mediaplayer skapad")
            initializeUI()
        })
    }

    fun playSlize(slize: Slize) {
        mediaPlayer?.seekTo(slize.start)
        mediaPlayer?.start()
    }

    fun playFullPhrase() {
        mediaPlayer?.seekTo(0)
        mediaPlayer?.start()
    }

    fun correctAnswer() {
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, SuccessFragment()).addToBackStack(null)
            .commit()
        loadNewPhrase()
    }

    fun wrongAnswer() {
        if (!listenMode) {
            failPlayer = MediaPlayer.create(requireContext(), R.raw.fail)
            failPlayer!!.start()

            binding.llGuessesCircle.background.setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_ATOP)
            binding.tvGuessesRemaining.setTextColor(Color.parseColor("#FFFFFF"))

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                binding.llGuessesCircle.background.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP)
                binding.tvGuessesRemaining.setTextColor(Color.parseColor("#000000"))
            }, 500)
        }

        listenMode = false
        binding.btnCheck.isVisible = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "inGameFragment Destroyed")
        mediaPlayer?.stop()
        _binding = null
        slizeRecAdapter.blinkHandler.removeCallbacksAndMessages(null);
    }


    //Kod för byta plats på recyclerView viewholders med drag and drop.
    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT or
                        ItemTouchHelper.RIGHT or
                        ItemTouchHelper.START or
                        ItemTouchHelper.END, 0
            ){
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
                    Collections.swap(model.slizes, from, to)

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

    fun String.parentenses() : String{
        return "\"" + this + "\""
    }

}