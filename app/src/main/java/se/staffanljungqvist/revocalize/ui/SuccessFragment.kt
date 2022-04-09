package se.staffanljungqvist.revocalize.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.databinding.FragmentSuccessBinding
import se.staffanljungqvist.revocalize.viewmodels.IngameViewModel


class SuccessFragment : Fragment() {

    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!

    lateinit var fragment: Fragment

    private val model: IngameViewModel by activityViewModels()

    lateinit var textToShow: LinearLayout


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

        val successPlayer = MediaPlayer.create(requireContext(), R.raw.success)

        ObjectAnimator.ofFloat(binding.llSuccessText, "translationY", -500f).apply {
            duration = 0
            start()
        }

        textToShow = binding.llLevelUp
        animateText(false)



        model.answerCorrect.observe(viewLifecycleOwner) {
            if (it) {
                successPlayer.start()

                if (model.levelUp) binding.tvLevelUpLevel.text = (model.level + 1).toString()

                textToShow = if (model.levelUp) {
                    binding.llLevelUp
                } else if (model.bonus > 0) {
                    binding.llBonus
                } else {
                    binding.llCorrect
                }
                animateText(false)
            }
        }

        model.audioReady.observe(viewLifecycleOwner) {
            if (it) {
                animateText(true)
            }
        }


        binding.btnContinute.setOnClickListener {
            animateButton(false)
        }
    }


    fun animateButton(show: Boolean) {
        binding.btnContinute.apply {
            alpha = if (show) 0f else 1f
            visibility = View.VISIBLE
            animate()
                .alpha(if (show) 1f else 0f)
                .setDuration(300.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.btnContinute.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
        }
    }

    fun animateText(out: Boolean) {
        textToShow.isVisible = true
        val position = if (out) -500f else 0f
        ObjectAnimator.ofFloat(binding.llSuccessText, "translationY", position).apply {
            duration = if (out) 400 else 200
            startDelay = if (out) 400 else 200
            start()
            addListener(onEnd = {
                if (out) {
                    model.loadUI.value = true
                    model.loadUI.value = false
                    textToShow.visibility = View.GONE
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}