package se.staffanljungqvist.revocalize.ui

import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.databinding.FragmentSuccessBinding
import se.staffanljungqvist.revocalize.viewmodels.IngameViewModel


class SuccessFragment : Fragment() {

    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!

    private val modelIngame : IngameViewModel by activityViewModels()


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

        val successPlayer = MediaPlayer.create(context, R.raw.success)
        successPlayer.start()

        binding.tvGuessesRemaining.text = modelIngame.points.toString()
        binding.tvCurrentPhrase.text = (modelIngame.phraseIndex - 1).toString()
        binding.tvTotalPhrases.text = modelIngame.currentStage.phraseList.size.toString()



        if (modelIngame.bonus > 0) showBonus()
        val trivia = arguments?.getString("trivia")

        if (trivia != null) {
            binding.tvTrivia.isVisible = true
            binding.tvTrivia.text = trivia
        }


        modelIngame.audioReady.observe(viewLifecycleOwner) {
            if (it) view.findViewById<Button>(R.id.btnCloseContinue).isVisible = true
        }

        view.findViewById<Button>(R.id.btnCloseContinue).setOnClickListener {
            if (modelIngame.stageComplete) {

                Firebase.analytics.logEvent(FirebaseAnalytics.Event.LEVEL_END) {
                    param("stage index", arguments?.getInt("stage").toString())
                }

                requireActivity().supportFragmentManager.popBackStack()
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, LevelCompleteFragment()).commit()
            }
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    fun animateScore() {
        binding.tvGuessesRemainingWhite.text = binding.tvGuessesRemaining.text
        /*       binding.tvGuessesRemainingWhite.isVisible = true
               binding.tvGuessesRemainingWhite.apply {
                   alpha = 1f
                   visibility = View.VISIBLE

                   // Animate the content view to 100% opacity, and clear any animation
                   // listener set on the view.
                   animate()
                       .alpha(0f)
                       .setDuration(2000.toLong())
                       .setListener(null)
               }*/

        binding.tvMinusPoint.apply {
            alpha = 1f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(0f)
                .setDuration(2000.toLong())
                .setListener(null)
        }

        binding.llGuessesCircleRed.apply {
            alpha = 1f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(0f)
                .setDuration(2000.toLong())
                .setListener(null)
        }
    }

    fun showBonus() {
        animateScore()
        binding.llBonus.isVisible = true
        binding.llCorrect.isVisible = false
        binding.tvBonus.text = modelIngame.toFragment.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}