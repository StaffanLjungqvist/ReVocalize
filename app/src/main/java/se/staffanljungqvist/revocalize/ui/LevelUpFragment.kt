package se.staffanljungqvist.revocalize.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.delay
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.databinding.FragmentLevelUpBinding
import se.staffanljungqvist.revocalize.databinding.FragmentScoreBinding
import se.staffanljungqvist.revocalize.databinding.FragmentStartBinding
import se.staffanljungqvist.revocalize.viewmodels.IngameViewModel


class LevelUpFragment : Fragment() {

    private var _binding: FragmentLevelUpBinding? = null
    private val binding get() = _binding!!

    private val model : IngameViewModel by activityViewModels()

    private var postTop = -400
    private var posBottom = 500
    private var moveOutSpeed = 230
    private var moveInSpeed = 400

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLevelUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        move(binding.btnContinuteLevelUp, "down", true) {}
        move(binding.clStatistics, "left", true, 0, true) {
        }

        val levelUpPlayer = MediaPlayer.create(requireContext(), R.raw.perfect2).setOnPreparedListener {
           if (model.numberOfphrasesDone.value != 0) it.start()
        }

        model.audioReady.observe(viewLifecycleOwner) {
            if (it) {
                move(binding.btnContinuteLevelUp, "show", false, moveOutSpeed) {}
                move(binding.clStatistics, "show", false, 0, true){}
            }
        }




        view.findViewById<Button>(R.id.btnContinuteLevelUp).setOnClickListener {

            move(it, "down") {
                requireActivity().supportFragmentManager.popBackStack(null, 0)
                model.loadUI.value = true
                model.loadUI.value = false
                model.levelUp = false
                model.showSuccess.value = false
            }
        }

    }



    fun animateButton(button : Button, show: Boolean) {
        val duration = if (show) 200 else 100
        button.postDelayed({
            button.apply {
                alpha = if (show) 0f else 1f
                visibility = View.VISIBLE
                animate()
                    .alpha(if (show) 1f else 0f)
                    .setDuration(duration.toLong())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            button.isVisible = true
                        }
                    })
            }
        }, 300)
    }

    fun move(view: View, direction : String, hide : Boolean = false, delay : Int = 0, horisontal : Boolean = false, doThis: () -> Unit) {

        val mydirection = if (horisontal) "translationX" else "translationY"

        val moveTo = when(direction) {
            "up" -> postTop
            "down" -> posBottom
            "left" -> -2000
            "right" -> 2000
            else -> 0
        }

        var speed = if (moveTo == 0) moveInSpeed else moveOutSpeed
        if (hide) speed = 0

        ObjectAnimator.ofFloat(view, mydirection, moveTo.toFloat()).apply {
            startDelay = delay.toLong()
            duration = speed.toLong()
            start()
            addListener(onEnd = {
                doThis()
            }) {
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}