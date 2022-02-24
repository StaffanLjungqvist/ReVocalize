package se.staffanljungqvist.revocalize.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.databinding.FragmentSuccessBinding
import se.staffanljungqvist.revocalize.viewmodels.ViewModel


class SuccessFragment : Fragment() {

    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!
    private var guesses = 0

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

            guesses = bundle.getString("phraseguessamount")!!.toInt()

            var slizes = bundle.getString("slizes")!!.toInt()

            if (guesses == 1) {
                giveBonus(slizes)

            } else {
                binding.tvNumberOfTries.text = guesses.toString()
                binding.tvGuessesRemaining.text = (model.guessAmount).toString()
            }


        } else {
            Log.d(TAG, "gick inte att skicka string")
        }




        if (model.phraseIndex == 1) {
            binding.llCorrect.isVisible = false
        }

      //  binding.tvNumberOfTries.text = model.currentGuesses.toString()




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

    fun giveBonus(slizes : Int) {
        Log.d(TAG, "Bonus with $slizes slizes!")
        var firstHalfText = ""
        var infoText = ""
        binding.tvSecondHalf.text = "PERFEKT!"
        binding.tvSecondHalf.setTextColor(Color.parseColor("#4BEBFF"))
        when (slizes) {
                3 -> {
                    infoText = "GETTING IT CORRECT ON FIRST TRY WITH THREE SLIZES COUNTS AS ZERO!"
                    model.guessAmount += 1
                    guesses -= 1
                }
                4 -> {
                    infoText = "GETTING IT CORRECT ON FIRST TRY WITH FOUR SLIZES GIVES AN EXTRA GUESS!"
                    model.guessAmount += 2
                    guesses -= 2
                }
            else -> {
                model.guessAmount -= 0
            }
        }
        binding.tvNumberOfTries.text = guesses.toString()
        binding.tvGuessesRemaining.text = (model.guessAmount).toString()
        binding.tvInfo.isVisible = true
        binding.tvInfo.text = infoText
        binding.tvFirstHalf.text = firstHalfText
        binding.tvNumberOfTries.text = guesses.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}