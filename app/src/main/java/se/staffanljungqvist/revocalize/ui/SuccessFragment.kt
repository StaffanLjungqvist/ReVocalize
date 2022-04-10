package se.staffanljungqvist.revocalize.ui

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

    private var postTop = -400
    private var duration = 200


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

        ObjectAnimator.ofFloat(binding.llSuccessText, "translationY", -200f).apply {
            duration = 0
            start()
        }

        textToShow = binding.llCorrect

        model.showSuccess.observe(viewLifecycleOwner) {
            if (it) {
                successPlayer.start()
                textToShow = if (model.bonus > 0) {
                    binding.llBonus
                } else {
                    binding.llCorrect
                }
                binding.llSuccessText.isVisible = true
                animPos(false){}
            } else if (it == false) {
                animPos(true){
                    binding.llBonus.isVisible = false
                    binding.llCorrect.isVisible = false
                }
            }
        }
    }


    fun animPos(out: Boolean, time : Int = duration, doThis: (out : Boolean) -> Unit) {
        textToShow.isVisible = true
        val position = if (out) postTop.toFloat() else 0f
        ObjectAnimator.ofFloat(binding.llSuccessText, "translationY", position).apply {
            duration = time.toLong()
            start()

            addListener(onEnd = {
                doThis(out)
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}