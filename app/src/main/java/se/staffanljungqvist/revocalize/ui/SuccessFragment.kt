package se.staffanljungqvist.revocalize.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.databinding.FragmentSuccessBinding
import se.staffanljungqvist.revocalize.viewmodels.ViewModel


class SuccessFragment : Fragment() {

    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!

    val model : ViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var bundle = arguments

        if (bundle != null) {
            var guessAmount = bundle.getString("phraseguessamount")!!
            binding.tvNumberOfTries.text = guessAmount
        } else {
            Log.d(TAG, "gick inte att skicka string")
        }




        if (model.phraseIndex == 1) {
            binding.llCorrect.isVisible = false
        }

        if (model.levelComplete) {
            binding.tvPhrasesLeft.text = "0"
        } else {
            binding.tvPhrasesLeft.text = (model.currentStage!!.phraseList.size + 1 - model.phraseIndex).toString()
        }

        binding.tvSuccessDifficulty.text = model.currentStage.difficulty

        binding.tvTotalGuesses.text = model.totalGuesses.toString()

        binding.tvToComplete.text = model.currentStage.guessesToComplete.toString()

        if (model.currentStage.guessRecord == 0) {
            binding.tvUserHighscore.text = "N/A"
        } else {
            binding.tvUserHighscore.text = model.currentStage.guessRecord.toString()
        }
        binding.tvGoldMax.text = model.currentStage.guessesForGold.toString()
        binding.tvSilverMax.text = model.currentStage.guessesForSilver.toString()


        model.audioReady.observe(requireActivity(), Observer {
            if (it) {
                view.findViewById<Button>(R.id.btnCloseContinue).isVisible = true
            }
        })

        view.findViewById<Button>(R.id.btnCloseContinue).setOnClickListener {

            if (model.levelComplete) {
                requireActivity().supportFragmentManager.popBackStack()
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, LevelCompleteFragment()).commit()
            }
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}