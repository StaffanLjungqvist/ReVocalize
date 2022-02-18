package se.staffanljungqvist.revocalize

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import se.staffanljungqvist.revocalize.adapters.AudioAdapter
import se.staffanljungqvist.revocalize.adapters.MyRecyclerAdapter
import se.staffanljungqvist.revocalize.adapters.TTSAdapter
import se.staffanljungqvist.revocalize.databinding.FragmentInGameBinding
import java.util.*


class InGameFragment : Fragment() {

    val model : ViewModel by viewModels()

    private var _binding: FragmentInGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var myRecyclerView: RecyclerView
    private var myRecycleAdapter: MyRecyclerAdapter = MyRecyclerAdapter()
    lateinit var audioAdapter: AudioAdapter
    private lateinit var ttsAdapter : TTSAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myRecycleAdapter.fragment = this
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

        model.loadPhrase()

        audioAdapter = AudioAdapter(requireContext())
        ttsAdapter = TTSAdapter(requireContext())

        myRecyclerView = binding.rvSlizes
        myRecyclerView.adapter = myRecycleAdapter
        myRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)



        /*Observerar diverse  "är klar = true" booleans i diverse adaptrar för att vänta in färdiga processer
        och göra saker i rätt ordning.
        Todo : Finns ett bättre sätt?
         */
        ttsAdapter.audioFileCreated.observe(requireActivity(), androidx.lifecycle.Observer {
            ttsAdapter.saveToAudioFile(model.currentPhrase.text)
        })

        ttsAdapter.audioFileWritten.observe(requireActivity(), androidx.lifecycle.Observer {
            audioAdapter.loadAudio(ttsAdapter.path)
        })

        audioAdapter.fileTransformedFromUriToFile.observe(requireActivity(), androidx.lifecycle.Observer {
            Log.d(TAG, "ljudfil färdig")
            val duration = audioAdapter.getDuration()
            model.currentPhrase.slizes = model.makeSlices(duration)
            myRecycleAdapter.slizes = model.currentPhrase.slizes
            myRecyclerView.layoutManager = GridLayoutManager(requireContext(), model.currentPhrase.slizes.size)
            myRecycleAdapter.notifyDataSetChanged()
            binding.rvSlizes.isVisible = true
            binding.tvSentence.isVisible = true
            binding.tvSentence.text = model.currentPhrase.text
            binding.btnCheck.isVisible = true
            binding.tvLoading.isVisible = false
        })

        myRecycleAdapter.hasChecked.observe(requireActivity(), androidx.lifecycle.Observer {
            if (model.isCorrect) {
                binding.tvSentence.isVisible = true
                binding.tvSentence.text = "That is correct!"
                binding.btnNext.isVisible = true
                binding.rvSlizes.isVisible = false
                binding.tvLoading.isVisible = false
                binding.btnCheck.isVisible = false
                audioAdapter.playSuccess()
                model.isCorrect = false
            } else {
                binding.btnCheck.isVisible = true
            }
        })

        //Knappar

        binding.btnCheck.setOnClickListener {
            it.isVisible = false
            binding.tvGuessAmount.text = model.guesses.toString()
            myRecycleAdapter.runLight(model.currentPhrase.slizes)
            if (model.checkIfCorrect()) {
                audioAdapter.playAudio()
            } else {
                audioAdapter.playSlices(model.currentPhrase.slizes)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun loadPhrase() {
        model.loadPhrase()
        binding.tvGuessAmount.text = model.guesses.toString()
        binding.tvSentence.text = model.currentPhrase.text
        ttsAdapter.saveToAudioFile(model.currentPhrase.text)
        myRecycleAdapter.slizes = model.currentPhrase.slizes
    }




    //Kod för byta plats på recyclerView viewholders med drag and drop.

    private val itemTouchHelper by lazy {

        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT or
                        ItemTouchHelper.RIGHT or
                        ItemTouchHelper.START or
                        ItemTouchHelper.END, 0) {

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



                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {


                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    // 2. Update the backing model. Custom implementation in
                    //    MainRecyclerViewAdapter. You need to implement
                    //    reordering of the backing model inside the method.
                    Collections.swap(model.currentPhrase.slizes, from, to)

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