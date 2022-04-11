package se.staffanljungqvist.revocalize.ui

import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
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

    private var postTop = -400
    private val posBottom = 500

    private var moveOutSpeed = 230
    private var moveInSpeed = 250

    private lateinit var successPlayer: MediaPlayer


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

        successPlayer = MediaPlayer.create(requireContext(), R.raw.success)

        move(binding.llSuccessText, "up", true){}

        textToShow = binding.llCorrect

        model.showSuccess.observe(viewLifecycleOwner) {

            if (it) {
                successPlayer.start()
                textToShow = if (model.bonus > 0) {
                    binding.llBonus
                } else {
                    binding.llCorrect
                }
                showSucces()
            } else if (it == false) {
                if (model.numberOfphrasesDone.value != 0) {
                    hideSuccess()
                }

            }
        }

        model.showLevelUp.observe(viewLifecycleOwner) {

        }

    }

    fun showSucces() {
        Log.d(TAG, "Showing success")
        binding.llSuccessText.isVisible = true
        textToShow.isVisible = true
        move(binding.llSuccessText, "show"){}
    }

    fun hideSuccess() {
        Log.d(TAG, "hiding success")
        move(binding.llSuccessText, "up"){
            binding.llBonus.isVisible = false
            binding.llCorrect.isVisible = false
            binding.llLevelUp.isVisible = false
            if (model.levelUp ) {
                textToShow = binding.llLevelUp
                model.showLevelUp.value = true
                showSucces()
            }
        }
    }


    fun move(view: View, direction : String, hide : Boolean = false, delay : Int = 0, doThis: () -> Unit) {
        val moveTo = when(direction) {
            "up" -> postTop
            "down" -> posBottom
            else -> 0
        }

        var speed = moveOutSpeed
        if (hide) speed = 0

        ObjectAnimator.ofFloat(view, "translationY", moveTo.toFloat()).apply {
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