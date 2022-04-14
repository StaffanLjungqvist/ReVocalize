package se.staffanljungqvist.revocalize.ui

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.animation.addListener
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

    private lateinit var warningPlayer: MediaPlayer
    private lateinit var bonusPlayer: MediaPlayer

    private var postTop = -400
    private var posBottom = 500
    private var moveOutSpeed = 230
    private var moveInSpeed = 250

    private var tries = 3

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

        bonusPlayer = MediaPlayer.create(context, R.raw.good)
        warningPlayer = MediaPlayer.create(requireContext(), R.raw.fail)

        move(binding.llInventory, "up", true) {}



        model.numberOfphrasesDone.observe(viewLifecycleOwner) {
            if (it != 0) animateText(binding.tvPoints, "green", true)
            binding.tvPoints.text = model.totalPoints.toString()
            binding.tvPointsToNextLevel.text =
                ((model.levelUpIncrementPoints) * (model.level + 1)).toString()
            binding.tvNoHelpers.isVisible = model.level == 0
        }

        model.observedTries.observe(viewLifecycleOwner) {
            if (model.tries == 1) {
                binding.tvTriesLeft.setTextColor(Color.parseColor("#FF0000"))
                binding.tvTriesLeftNumber.setTextColor(Color.parseColor("#FF0000"))
            } else

            if (it < tries) {
                //      animateTryChange(binding.tvTriesNumberChange, false)
                animateText(binding.tvTriesLeft, "red", false)
                animateText(binding.tvTriesLeftNumber, "red", true)
            }

            if (it > tries) {
                //         animateTryChange(binding.tvTriesNumberChange, true)
                animateText(binding.tvTriesLeftNumber, "green", true)
                animateText(binding.tvTriesLeft, "green", false)
            }
            tries = it
            binding.tvTriesLeftNumber.text = tries.toString()
        }

        model.powersAvailable.observe(viewLifecycleOwner) {

        }
        model.listenMode.observe(viewLifecycleOwner) {
            Log.d(TAG, "listenMode är satt till $it")
        }
        model.clickMode.observe(viewLifecycleOwner) {
            Log.d(TAG, "clickMode är satt till $it")
        }
        model.playMode.observe(viewLifecycleOwner) {
            Log.d(TAG, "playMode är satt till $it")
        }


        model.audioReady.observe(viewLifecycleOwner) {
            showHelpers()
        }


        model.showInventory.observe(viewLifecycleOwner) {
            Log.d(TAG, "showInventory är satt till $it")
            if (it) {
                move(binding.llInventory, "show") {}
            } else {
                move(binding.llInventory, "up") {}
            }
        }


/*        model.observedPowerPoints.observe(viewLifecycleOwner) {
            if (it < powers) {
                animateTryChange(binding.tvPowerNumberChanged, false)
            }

            if (it > powers) {
                animateTryChange(binding.tvPowerNumberChanged, true)
            }
            powers = it
        }*/

        binding.llShowPowers.setOnClickListener {
            showHelpers()
            if (model.powersAvailable.value == true) {
                model.showInventory.value = model.showInventory.value != true
            }
        }

        binding.tvPowerClick.setOnClickListener {
            if (model.powersAvailable.value == true) {
                if (model.powerClickAmount != 0) {
                    model.usePowerUp(PowerUp.CLICK)
                    model.showInventory.value = false
                }
            }
        }

        binding.tvPowerRemove.setOnClickListener {
            if (model.powersAvailable.value == true) {
                if (model.powerRemoveAmount != 0) {
                    model.usePowerUp(PowerUp.REMOVE)
                    model.showInventory.value = false
                }

            }
        }

        binding.tvPowerTry.setOnClickListener {
            if (model.powersAvailable.value == true) {
                if (model.powerTryAmount != 0) {
                    model.usePowerUp(PowerUp.TRY)
                    model.showInventory.value = false
                }
            }
        }
    }

    fun move(
        view: View,
        direction: String,
        hide: Boolean = false,
        delay: Int = 0,
        doThis: () -> Unit
    ) {
        val moveTo = when (direction) {
            "up" -> postTop
            "down" -> posBottom
            else -> 0
        }

        var speed = if (moveTo == 0) moveInSpeed else moveOutSpeed
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


    fun showHelpers() {
        Log.d(TAG, "tryhelper amount is ${model.powerTryAmount}")
        binding.tvPowerTry.isVisible = model.powerTryAmount != -1
        binding.tvPowerClick.isVisible = model.powerClickAmount != -1
        binding.tvPowerRemove.isVisible = model.powerRemoveAmount != -1

        binding.tvPowerTry.alpha = if (model.powerTryAmount == 0) 0.5f else 1f
        binding.tvPowerClick.alpha = if (model.powerClickAmount == 0) 0.5f else 1f
        binding.tvPowerRemove.alpha = if (model.powerRemoveAmount == 0) 0.5f else 1f

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

    fun animateText(text: TextView, color: String, isNumber : Boolean) {
        val colorToChange = if (color == "red") Color.RED else Color.parseColor("#38FF75")
        val endColor = if (isNumber) "#FFFFFF" else "#B2B2B2"

            text.setTextColor(colorToChange)
            val animation = ObjectAnimator.ofObject(
                text,  // Object to animating
                "textColor",  // Property to animate
                ArgbEvaluator(),  // Interpolation function
                colorToChange,  // Start color
                Color.parseColor(endColor) // End color
            ).setDuration(1200)
            animation.start()

    }

    fun animateTryChange(view: TextView, tryAdded: Boolean) {
        view.text = if (tryAdded) "+" else "-"
        val textColor = if (tryAdded) "#38FF75" else "#FF0000"
        view.setTextColor(Color.parseColor(textColor))
        view.apply {
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