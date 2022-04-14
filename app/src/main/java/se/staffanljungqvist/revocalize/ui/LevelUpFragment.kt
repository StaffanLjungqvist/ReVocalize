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
        model.giveBonus()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        move(binding.btnContinuteLevelUp, "down", true) {}
        move(binding.clStatistics, "left", true, 0, true) {}
        move(binding.tvLevelUp, "up", true) {
            if (model.numberOfphrasesDone.value != 0) {
                move(binding.tvLevelUp, "show"){}
            }
        }
        move(binding.llNextLevel, "right", true, 0, true){}
        binding.tvNextLevelNumber.text = (model.level + 1).toString()

        showInfo()



        val levelUpPlayer = MediaPlayer.create(requireContext(), R.raw.perfect2).setOnPreparedListener {
           if (model.numberOfphrasesDone.value != 0) it.start()
        }

        model.audioReady.observe(viewLifecycleOwner) {
            if (it) {
                move(binding.btnContinuteLevelUp, "show", false, moveOutSpeed) {}
                move(binding.clStatistics, "show", false, 0, true){}
                move(binding.llNextLevel, "show", false, 0, true){}
            }
        }




        view.findViewById<Button>(R.id.btnContinuteLevelUp).setOnClickListener {

            move(binding.tvLevelUp, "up"){}
            move(binding.llNextLevel, "left", false, 0, true){}
            move(binding.clStatistics, "right", false, 0, true){}
            move(it, "down") {
                requireActivity().supportFragmentManager.popBackStack(null, 0)
                model.loadUI.value = true
                model.loadUI.value = false
                model.levelUp = false
                model.showSuccess.value = false
                model.playMode.value = true
            }
        }

    }


fun showInfo() {

    val text = when (model.level) {
        0 -> "PRESS PLAY TO HEAR THE PHRASE. IT IS IN THE WRONG ORDER! MOVE THE BLOCKS TO SORT IT. YOU GET 3 TRIES TO GET IT RIGHT. THE FIRST PLAYBACK IS FREE."
        1 -> "GOOD WORK! YOU GET A NEW HELPER. UNDER THE HELPER MENU, PRESS \"TRY+3\" TO GET 3 EXTRA TRIES. HELPERS CAN BE USED ONCE PER LEVEL."
        2 -> "WAY TO GO! YOU NOW GET ANOTHER HELPER. PRESS \"BLOCK-1\" TO SUBTRACT ONE DIVISION."
        3 -> "WELL DONE! YOU GET ANOTHER HELPER. PRESS \"TOUCH LISTEN\" TO GO IN TO TOUCH MODE, THE BLOCKS CAN'T BE MOVED BUT YOU CAN HEAR EACH SOUND BY PRESSING THEM. MEMORIZE THE ORDER AND PRESS \"TOUCH DONE\" WHEN READY."
        4 -> "WOW, YOU MADE IT FAR! YOU DON'T GET ANY NEW HELPERS FROM HERE ON SO MAKE THE MOST OF THE ONES YOU GOT!"
        else -> "GREAT JOB! KEEP GOING!"
    }

    binding.tvInfoMessage.text = text
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