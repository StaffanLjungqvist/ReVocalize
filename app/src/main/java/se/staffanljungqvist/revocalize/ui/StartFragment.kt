package se.staffanljungqvist.revocalize.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.databinding.FragmentStartBinding


class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStageSelect.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, StageSelectFragment()).commit()
        }

        binding.btnInstructions.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, TutorialFragment()).addToBackStack(null).commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}