package se.staffanljungqvist.revocalize.ui

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.databinding.FragmentScoreBinding
import se.staffanljungqvist.revocalize.viewmodels.IngameViewModel

class ScoreFragment : Fragment() {

    private val model: IngameViewModel by activityViewModels()

    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var failPlayer: MediaPlayer
    private lateinit var warningPlayer: MediaPlayer
    private lateinit var bonusPlayer: MediaPlayer

    private var points = 5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_score, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val llCircleRed = view.findViewById<LinearLayout>(R.id.llGuessesCircleRed)
        val llCircleGreen = view.findViewById<LinearLayout>(R.id.llGuessesCircleGreen)
        val tvScore = view.findViewById<TextView>(R.id.tvPoints)
        val tvPointMinus = view.findViewById<TextView>(R.id.tvPointMinus)
        val tvPointPlus = view.findViewById<TextView>(R.id.tvPointPlus)

        bonusPlayer = MediaPlayer.create(context, R.raw.good)
        failPlayer = MediaPlayer.create(requireContext(), R.raw.warning)
        warningPlayer = MediaPlayer.create(requireContext(), R.raw.fail)

        model.observedlevel.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.tvCurrentLevel).text = (it + 1).toString()
        }

        model.numberOfphrasesDone.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.tvCurrentPhrase).text = (it + 1).toString()
        }

        model.observedPoints.observe(viewLifecycleOwner) {

            if (it < points) {
                failPlayer!!.start()
                llCircleRed.apply {
                    alpha = 1f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(0f)
                        .setDuration(2000.toLong())
                        .setListener(null)
                }

                tvPointMinus.apply {
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

            if (it > points) {
                llCircleGreen.apply {
                    alpha = 1f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(0f)
                        .setDuration(2000.toLong())
                        .setListener(null)
                }
                tvPointPlus.apply {
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

            points = it
            tvScore.text = points.toString()
            if (it == 1) {
                view.findViewById<TextView>(R.id.tvLastGuess).isVisible = true
            //    warningPlayer.start()
            } else {
                view.findViewById<TextView>(R.id.tvLastGuess).isVisible = false
            }

        }






    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}