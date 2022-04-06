package se.staffanljungqvist.revocalize.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.staffanljungqvist.revocalize.adapters.StageRecAdapter
import se.staffanljungqvist.revocalize.databinding.FragmentStageSelectBinding
import se.staffanljungqvist.revocalize.viewmodels.StartViewModel


class StageSelectFragment : Fragment() {

    val model: StartViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView

    private var _binding: FragmentStageSelectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStageSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        model.loadStages(requireContext())
        //  model.loadUserData(requireContext())

        model.userDataLoaded.observe(requireActivity()) {
            recyclerView = binding.rvStageRecyclerView
            val stageAdapter = StageRecAdapter()
            stageAdapter.fragment = this
            recyclerView.adapter = stageAdapter
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}