package se.staffanljungqvist.revocalize.ui

import android.graphics.Color
import android.graphics.PorterDuff
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
import se.staffanljungqvist.revocalize.viewmodels.IngameViewModel


class SuccessFragment : Fragment() {

    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!

    val modelIngame : IngameViewModel by activityViewModels()


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


        Log.d(TAG, "bonusen är ${modelIngame.bonus}")


        binding.tvGuessesRemaining.text = modelIngame.points.toString()
        binding.tvCurrentPhrase.text = (modelIngame.phraseIndex - 1).toString()
        binding.tvTotalPhrases.text = modelIngame.currentStage.phraseList.size.toString()

        if (modelIngame.bonus > 0) showBonus(modelIngame.bonus)

        modelIngame.audioReady.observe(requireActivity(), Observer {
            if (it) view.findViewById<Button>(R.id.btnCloseContinue).isVisible = true
        })

        view.findViewById<Button>(R.id.btnCloseContinue).setOnClickListener {
            if (modelIngame.stageComplete) {
                requireActivity().supportFragmentManager.popBackStack()
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, LevelCompleteFragment()).commit()
            }
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    fun showBonus(bonus : Int) {
        binding.llBonus.isVisible = true
        binding.tvBonus.text = modelIngame.toFragment.toString()
        binding.llGuessesCircle.background.setColorFilter(Color.parseColor("#4BEBFF"), PorterDuff.Mode.SRC_ATOP)

        when (bonus) {
                1 ->  binding.tvInfo.text = "GETTING IT CORRECT ON FIRST TRY WITH THREE SLIZES COUNTS AS ZERO!"
                2 -> binding.tvInfo.text = "GETTING IT CORRECT ON FIRST TRY WITH FOUR SLIZES GIVES AN EXTRA GUESS!"
                3 -> binding.tvInfo.text = "GETTING IT CORRECT ON FIRST TRY WITH FIVE SLIZES GIVES TWO EXTRA GUESSES!"
                4 -> binding.tvInfo.text = "GETTING IT CORRECT ON FIRST TRY WITH SIX SLIZES GIVES THREE EXTRA GUESSES!"
                5 -> binding.tvInfo.text = "GETTING IT CORRECT ON FIRST TRY WITH SEVEN SLIZES GIVES FOUR EXTRA GUESSES!"
                6 -> binding.tvInfo.text = "GETTING IT CORRECT ON FIRST TRY WITH EIGHT SLIZES GIVES FIVE EXTRA GUESSES!"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}