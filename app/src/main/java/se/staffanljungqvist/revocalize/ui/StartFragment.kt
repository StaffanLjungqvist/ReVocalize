package se.staffanljungqvist.revocalize.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.builders.episodes
import se.staffanljungqvist.revocalize.databinding.FragmentStartBinding
import se.staffanljungqvist.revocalize.viewmodels.ViewModel


class StartFragment : Fragment() {

    val model : ViewModel by activityViewModels()

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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

        binding.tvStart.setOnClickListener {
            model.currentLevel = episodes.episodeList[1]
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, InGameFragment()).commit()
        }

        binding.tvStart2.setOnClickListener {
            model.currentLevel = episodes.episodeList[0]
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, InGameFragment()).commit()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}