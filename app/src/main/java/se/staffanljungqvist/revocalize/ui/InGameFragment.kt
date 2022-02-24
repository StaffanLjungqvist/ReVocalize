package se.staffanljungqvist.revocalize.ui

import android.os.Bundle
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
import se.staffanljungqvist.revocalize.adapters.AudioAdapter
import se.staffanljungqvist.revocalize.adapters.SlizeRecAdapter
import se.staffanljungqvist.revocalize.adapters.TTSAdapter
import se.staffanljungqvist.revocalize.databinding.FragmentInGameBinding
import se.staffanljungqvist.revocalize.viewmodels.ViewModel
import java.util.*

val TAG = "revodebug"

class InGameFragment : Fragment() {

    val model: ViewModel by activityViewModels()

    private var _binding: FragmentInGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var myRecyclerView: RecyclerView
    private var slizeRecAdapter: SlizeRecAdapter = SlizeRecAdapter()
    lateinit var audioAdapter: AudioAdapter
    private lateinit var ttsAdapter: TTSAdapter
    var freeCheck = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        slizeRecAdapter.fragment = this
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

        audioAdapter = AudioAdapter(requireContext())
        ttsAdapter = TTSAdapter(requireContext())

        myRecyclerView = binding.rvSlizes
        myRecyclerView.adapter = slizeRecAdapter
        myRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)


        /*Observerar diverse  "är klar = true" booleans i diverse adaptrar för att vänta in färdiga processer
        och göra saker i rätt ordning.
        Todo : Finns ett bättre sätt?
         */
        ttsAdapter.ttsInitiated.observe(requireActivity(), androidx.lifecycle.Observer {
            ttsAdapter.saveToAudioFile(model.currentPhrase.text)
        })

        ttsAdapter.ttsAudiofileWritten.observe(requireActivity(), androidx.lifecycle.Observer {
            audioAdapter.loadAudio(ttsAdapter.path)
            model.audioReady.value = false
        })

        audioAdapter.fileTransformedFromUriToFile.observe(
            requireActivity(),
            androidx.lifecycle.Observer {
                Log.d(TAG, "ljudfil färdig")
                model.audioReady.value = true
                val duration = audioAdapter.getDuration()
                model.makeSlices(duration)
                slizeRecAdapter.slizes = model.slizes!!
                myRecyclerView.layoutManager =
                    GridLayoutManager(requireContext(), model.slizes!!.size)
                slizeRecAdapter.notifyDataSetChanged()
                binding.tvSentence.text = model.currentPhrase.text
                binding.tvCurrentPhrase.text = model.phraseIndex.toString()
                binding.tvTotalPhrases.text = model.currentStage.phraseList.size.toString()
            })

        slizeRecAdapter.hasChecked.observe(requireActivity(), androidx.lifecycle.Observer {

            if (model.isCorrect) {
                if (model.currentGuesses == 1) {
                    audioAdapter.playPerfect()
                }
                audioAdapter.playSuccess()
                model.isCorrect = false
                model.audioReady.value = false

                val bundle = Bundle()
                bundle.putString("phraseguessamount", model.currentGuesses.toString())
                bundle.putString("slizes", model.slizes!!.size.toString())

                var successFragment = SuccessFragment()

                successFragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView, successFragment).addToBackStack(null).commit()
                loadPhrase()
            } else if (model.gameOver) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView, GameOverFragment()).addToBackStack(null)
                    .commit()
            }
            binding.btnCheck.isVisible = true
        })

        binding.btnPlay.setOnClickListener {
            it.isVisible = false
            binding.btnCheck.isVisible = false
            slizeRecAdapter.runLight(model.slizes!!)
            audioAdapter.playSlices(model.slizes!!)
        }


        binding.btnCheck.setOnClickListener {
            it.isVisible = false
            slizeRecAdapter.runLight(model.slizes!!)
            if (model.checkIfCorrect()) {
                audioAdapter.playAudio()
            } else {
                audioAdapter.playSlices(model.slizes!!)
            }
        }


        //val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(myRecyclerView)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun loadPhrase() {
        model.loadPhrase()
        binding.tvSentence.text = model.currentPhrase.text
        ttsAdapter.saveToAudioFile(model.currentPhrase.text)
        slizeRecAdapter.slizes = model.slizes!!
        binding.btnPlay.isVisible = true
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

                override fun onMoved(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    fromPos: Int,
                    target: RecyclerView.ViewHolder,
                    toPos: Int,
                    x: Int,
                    y: Int
                ) {
                    super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
                }


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

}