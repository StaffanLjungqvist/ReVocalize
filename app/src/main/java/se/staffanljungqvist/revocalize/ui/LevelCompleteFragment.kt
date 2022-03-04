package se.staffanljungqvist.revocalize.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.databinding.FragmentLevelCompleteBinding
import se.staffanljungqvist.revocalize.viewmodels.ViewModel


class LevelCompleteFragment : Fragment() {

    val model : ViewModel by activityViewModels()

    private var _binding: FragmentLevelCompleteBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLevelCompleteBinding.inflate(inflater, container, false)
        model.calculateScore()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.saveUserData(requireContext())

        if (model.newRecord) binding.tvNewBest.isVisible = true

        binding.tvCompleteGuesses.text = model.points.toString()
        binding.tvCompleteRank.text = model.rank

        val rankColor =
            when (model.rank) {
                "BRONZE" -> "#FF6C00"
                "SILVER" -> "#00E3FF"
                "GOLD" -> "#FFFF58"
                else -> {
                    "#4BEBFF"
                }
            }

        binding.tvCompleteRank.setTextColor(Color.parseColor(rankColor))

        binding.btnReturnMain.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, StartFragment()).commit()
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}