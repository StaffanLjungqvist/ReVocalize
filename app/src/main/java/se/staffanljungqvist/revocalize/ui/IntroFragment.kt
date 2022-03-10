package se.staffanljungqvist.revocalize.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.databinding.FragmentIntroBinding
import se.staffanljungqvist.revocalize.viewmodels.ViewModel


class IntroFragment : Fragment() {

    val model : ViewModel by activityViewModels()

    private var _binding: FragmentIntroBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var stage = model.currentStage

        binding.tvStageNumber.text = (model.stageList.indexOf(model.currentStage) + 1).toString()

        binding.tvStageName.text = stage.name
        binding.tvPhraseAmount.text = stage.phraseList.size.toString()
        binding.tvStartingPoints.text = stage.startingPoints.toString()
        binding.tvPointsForGold.text = stage.pointsForGold.toString()
        binding.tvPointsForSilver.text = stage.pointsForSilver.toString()
        binding.tvUserBest.text = stage.pointRecord.toString()

        model.audioReady.observe(requireActivity(), Observer {
            if (it) {
                view.findViewById<Button>(R.id.btnStart).isVisible = true
            }
        })

        binding.btnStart.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}