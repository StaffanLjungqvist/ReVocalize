package se.staffanljungqvist.revocalize.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.databinding.FragmentIntroBinding
import se.staffanljungqvist.revocalize.viewmodels.IngameViewModel


class IntroFragment : Fragment() {

    private val modelIngame: IngameViewModel by activityViewModels()

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




        val sharedPref = requireContext().getSharedPreferences("userScore", Context.MODE_PRIVATE)


        modelIngame.audioReady.observe(requireActivity()) {
            if (it) view.findViewById<Button>(R.id.btnStart).isVisible = true
        }

        binding.btnStart.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}