package se.staffanljungqvist.revocalize.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import se.staffanljungqvist.revocalize.PowerUp
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.databinding.FragmentScoreBinding
import se.staffanljungqvist.revocalize.viewmodels.IngameViewModel

class ScoreBoardFragment : Fragment() {

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
        _binding = FragmentScoreBinding.inflate(inflater, container, false)
        return binding.root
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


        model.numberOfphrasesDone.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.tvCurrentPhrase).text = (it + 1).toString()
        }

        model.observedTries.observe(viewLifecycleOwner) {
            if (it < points) {
                failPlayer!!.start()
                animateCircle(llCircleRed)
                animatePoints(tvPointMinus)
            }


            if (it > points) {
                animateCircle(llCircleGreen)
                animatePoints(tvPointPlus)
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

        model.observedPowerPoints.observe(viewLifecycleOwner) {
            binding.tvPowerPoints.text = it.toString()
        }


        binding.tvPowerRemove.setOnClickListener {
            Log.d(TAG, "TRyckte på try")
            model.usePowerUp(PowerUp.REMOVESLIZE)
        }

        binding.tvPowerTry.setOnClickListener {
            model.usePowerUp(PowerUp.EXTRATRY)
        }
    }


    fun animateCircle(circle: LinearLayout) {
        circle.apply {
            alpha = 1f
            visibility = View.VISIBLE
            animate()
                .alpha(0f)
                .setDuration(1000.toLong())
                .setListener(null)
        }
    }

    fun animatePoints(pointsText: TextView) {
        pointsText.apply {
            alpha = 1f
            visibility = View.VISIBLE
            animate()
                .alpha(0f)
                .setDuration(2000.toLong())
                .setListener(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}