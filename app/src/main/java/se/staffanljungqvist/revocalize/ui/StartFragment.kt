package se.staffanljungqvist.revocalize.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.staffanljungqvist.revocalize.adapters.StageRecAdapter
import se.staffanljungqvist.revocalize.databinding.FragmentStartBinding
import se.staffanljungqvist.revocalize.viewmodels.IngameViewModel
import se.staffanljungqvist.revocalize.viewmodels.StartViewModel


class StartFragment : Fragment() {

    val model : StartViewModel by activityViewModels()

    private lateinit var recyclerView : RecyclerView

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


        model.loadStages(requireContext())
      //  model.loadUserData(requireContext())

        model.userDataLoaded.observe(requireActivity(), Observer {
            recyclerView = binding.rvStageRecyclerView
            var stageAdapter = StageRecAdapter()
            stageAdapter.fragment = this
            recyclerView.adapter = stageAdapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false )

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}