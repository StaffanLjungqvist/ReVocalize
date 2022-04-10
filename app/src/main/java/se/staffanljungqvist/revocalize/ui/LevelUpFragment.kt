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
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.databinding.FragmentLevelUpBinding
import se.staffanljungqvist.revocalize.databinding.FragmentScoreBinding
import se.staffanljungqvist.revocalize.databinding.FragmentStartBinding
import se.staffanljungqvist.revocalize.viewmodels.IngameViewModel


class LevelUpFragment : Fragment() {

    private var _binding: FragmentLevelUpBinding? = null
    private val binding get() = _binding!!

    private val model : IngameViewModel by activityViewModels()

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

        val levelUpPlayer = MediaPlayer.create(requireContext(), R.raw.perfect2).setOnPreparedListener {
       //     it.start()
        }

        model.audioReady.observe(viewLifecycleOwner) {
            if (it) animateButton(binding.btnContinuteLevelUp, true)
        }




        view.findViewById<Button>(R.id.btnContinuteLevelUp).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack(null, 0)
            model.loadUI.value = true
            model.loadUI.value = false
        }

    }

    fun animate(view : View, position : Int, time : Int = 1000, doThis : () -> Unit) {
        ObjectAnimator.ofFloat(view, "translationY", position.toFloat()).apply {
            duration = time.toLong()
            start()
            addListener(onEnd = {
                doThis()
            }) {
            }
        }
    }


    fun animateButton(button : Button, show: Boolean) {
        val duration = if (show) 200 else 100
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}