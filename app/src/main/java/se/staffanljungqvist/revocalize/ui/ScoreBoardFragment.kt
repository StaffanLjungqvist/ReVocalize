package se.staffanljungqvist.revocalize.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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

    private var points = 0

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

        move(binding.llScoreCircle, "up", true) {}
        move(binding.llInventory, "up", true) {}

        model.numberOfphrasesDone.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.tvCurrentPhrase).text = (it + 1).toString()
            //   animateCircle(llCircleGreen)
        }

        model.observedTries.observe(viewLifecycleOwner) {
            if (it < points) {

                //      animateTryChange(binding.tvTriesNumberChange, false)
                animateCircle(binding.llGuessesCircleRed)
            }

            if (it > points) {
                //         animateTryChange(binding.tvTriesNumberChange, true)
                animateCircle(binding.llGuessesCircleGreen)
            }
            points = it
            binding.tvNumberOfTries.text = points.toString()

            if (it == 1) {
                binding.llGuessesCircleRed.isVisible = true
                binding.tvNumberOfTries.setTextColor(Color.parseColor("#FF0000"))
                //    warningPlayer.start()
            } else {

                binding.tvNumberOfTries.setTextColor(Color.parseColor("#000000"))
            }
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


        model.observedlevel.observe(viewLifecycleOwner) {
            binding.tvLevelNumber.text = (model.level + 1).toString()
        }

        model.audioReady.observe(viewLifecycleOwner) {
            if (model.phraseIndex != 0) {
                if (it) move(binding.llScoreCircle, "show", false, 1000) {}
            }
        }

        model.showSuccess.observe(viewLifecycleOwner) {
            if (it) {
                move(binding.llScoreCircle, "up", false, 300) {}
            }
        }

        model.loadUI.observe(viewLifecycleOwner) {
            if (it) move(binding.llScoreCircle, "show", false, 300) {}
        }

        model.showInventory.observe(viewLifecycleOwner) {
            binding.tvPwrTryNumber.text = model.powerTryAmount.toString()
            binding.tvPwrRemoveNumber.text = model.powerRemoveAmount.toString()
            binding.tvPwrClickNumber.text = model.powerClickAmount.toString()
            Log.d(TAG, "showInventory är satt till $it")
            if (it) {
                move(binding.llInventory, "show") {}
            } else {
                move(binding.llInventory, "up") {}
            }
        }

        model.showNewPower.observe(viewLifecycleOwner) {
            if (it) {

                binding.tvNewPower.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(1f)
                        .setDuration(200.toLong())
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                    animate()
                                        .setStartDelay(500.toLong())
                                        .alpha(0f)
                                        .setDuration(1000.toLong())
                            }
                        })
                }
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
            if (model.powersAvailable.value == true) {
                model.showInventory.value = model.showInventory.value != true
            }
        }

        binding.tvPowerClick.setOnClickListener {
            if (model.powersAvailable.value == true) {
                if (model.powerClickAmount != 0) {
                    model.usePowerUp(PowerUp.CLICK)
                    model.showInventory.value = false
                    binding.tvPwrClickNumber.text = model.powerClickAmount.toString()
                }
            }
        }

        binding.tvPowerRemove.setOnClickListener {
            if (model.powersAvailable.value == true) {
                if (model.powerRemoveAmount != 0) {
                    model.usePowerUp(PowerUp.REMOVE)
                    model.showInventory.value = false
                    binding.tvPwrRemoveNumber.text = model.powerRemoveAmount.toString()
                }

            }
        }

        binding.tvPowerTry.setOnClickListener {
            if (model.powersAvailable.value == true) {
                if (model.powerTryAmount != 0) {
                    model.usePowerUp(PowerUp.TRY)
                    model.showInventory.value = false
                    binding.tvPwrTryNumber.text = model.powerTryAmount.toString()
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