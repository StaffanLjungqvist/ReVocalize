package se.staffanljungqvist.revocalize.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.databinding.FragmentLevelCompleteBinding
import se.staffanljungqvist.revocalize.databinding.FragmentStartBinding
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCompleteGuesses.text = model.totalGuesses.toString()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}