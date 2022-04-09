package se.staffanljungqvist.revocalize.ui

import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    lateinit var fragment : Fragment

    private val model : IngameViewModel by activityViewModels()


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
        successPlayer.start()

        ObjectAnimator.ofFloat(binding.llSuccessText, "translationY", -1000f).apply {
            duration = 0
            start()
        }

        ObjectAnimator.ofFloat(binding.btnContinute, "translationX", -1000f).apply {
            duration = 0
            start()
        }



        animateIn()

        if (model.bonus > 0) showBonus()

        val trivia = arguments?.getString("trivia")

        if (trivia != null) {
            binding.tvTrivia.text = trivia
        }

        model.audioReady.observe(viewLifecycleOwner) {
            if (it) {
                animateButton()
            }
        }


        binding.btnContinute.setOnClickListener {
            it.isVisible = false
            animateOut()
        }
    }

    fun animateIn() {
        ObjectAnimator.ofFloat(binding.llSuccessText, "translationY", 0f).apply {
            duration = 300
            start()
            addListener(onEnd = {
            })
        }
    }

    fun animateButton() {
        ObjectAnimator.ofFloat(binding.btnContinute, "translationX", 0f).apply {
            duration = 300
            start()
        }
    }

    fun animateOut() {
        ObjectAnimator.ofFloat(binding.llSuccessText, "translationY", -1500f).apply {
            duration = 300
            start()
            addListener(onEnd = {
                requireActivity().supportFragmentManager.popBackStack()
                if (model.levelUp) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .add(R.id.fragmentContainerView, LevelUpFragment()).addToBackStack(null)
                        .commit()
                    model.levelUp = false
                } else {
                    model.loadUI.value = true
                    model.loadUI.value = false
                }
            }) {
            }
        }
    }


    fun showBonus() {
        binding.llBonus.isVisible = true
        binding.llCorrect.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}